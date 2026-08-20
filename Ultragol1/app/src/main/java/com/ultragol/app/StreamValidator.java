package com.ultragol.app;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.ultragol.app.models.ContentItem;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Rejects ad / placeholder / test videos that some embeds and stream APIs serve
 * instead of the real title.
 *
 * Two independent layers, because neither alone is enough:
 *
 *  1. URL fingerprints — catches the well-known public demo clips by name/host.
 *     Cheap, but only works for decoys we already know about.
 *
 *  2. Plausibility checks on the actual media — a feature film is never six
 *     minutes long or twenty megabytes big, whatever the URL looks like. This
 *     is the layer that catches *unknown* decoys, including re-encoded or
 *     shortened cuts of a known clip that fingerprinting misses.
 */
public final class StreamValidator {

    private StreamValidator() {}

    /** Well-known public test/demo/placeholder videos some ad slots or lazy embeds serve. */
    private static final String[] DECOY_URL_SIGNATURES = {
        "bigbuckbunny", "big_buck_bunny", "big-buck-bunny", "mov_bbb", "bbb_sunflower",
        "sintel", "tearsofsteel", "tears_of_steel", "elephantsdream", "elephants_dream",
        "jellyfish.mp4", "forbiggerescapes", "forbiggerfun", "forbiggerjoyrides",
        "forbiggerblazes", "forbiggermeltdowns", "subaru_outback", "volkswagen_gti",
        "sample-videos.com", "samplelib.com", "file-examples.com", "learningcontainer.com",
        "commondatastorage.googleapis.com", "gtv-videos-bucket",
        "test-videos.co.uk", "html5demos.com", "media.w3.org", "w3schools.com",
        "vjs.zencdn.net", "download.blender.org", "peach.blender.org",
        "sample.mp4", "sample_video", "testvideo", "test_video", "placeholder.mp4"
    };

    /** True for URLs that match a known public placeholder/test clip. */
    public static boolean isKnownDecoyUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        String lower = url.toLowerCase(Locale.ROOT);
        for (String sig : DECOY_URL_SIGNATURES) {
            if (lower.contains(sig)) return true;
        }
        return false;
    }

    // ── Plausibility thresholds ───────────────────────────────────────────────

    /**
     * Shortest runtime that could plausibly be real content of this type, in seconds.
     * Deliberately far below any genuine title so real content is never rejected:
     * the shortest feature films run about 70 minutes, and the check trips at 20.
     * Returns 0 for live content, whose duration is unknown or unbounded.
     */
    public static int minPlausibleDurationSec(int contentType) {
        switch (contentType) {
            case ContentItem.TYPE_MOVIE:
                return 20 * 60;
            case ContentItem.TYPE_SERIES:
            case ContentItem.TYPE_ANIME:
            case ContentItem.TYPE_DORAMA:
                return 4 * 60;   // some anime shorts genuinely run ~5 min
            default:
                return 0;        // TYPE_SPORT / TYPE_TV are live — no duration to check
        }
    }

    /** Smallest plausible file size in bytes, or 0 when no size check applies. */
    public static long minPlausibleBytes(int contentType) {
        switch (contentType) {
            case ContentItem.TYPE_MOVIE:
                return 60L * 1024 * 1024;
            case ContentItem.TYPE_SERIES:
            case ContentItem.TYPE_ANIME:
            case ContentItem.TYPE_DORAMA:
                return 15L * 1024 * 1024;
            default:
                return 0;
        }
    }

    /** True when a measured duration is too short to be the requested content. */
    public static boolean isImplausiblyShort(int contentType, long durationMs) {
        if (durationMs <= 0) return false;               // unknown duration — can't judge
        int minSec = minPlausibleDurationSec(contentType);
        if (minSec <= 0) return false;                   // live content — no check
        return (durationMs / 1000) < minSec;
    }

    /** True when a known content length is too small to be the requested content. */
    public static boolean isImplausiblySmall(int contentType, long bytes) {
        if (bytes <= 0) return false;                    // unknown size — can't judge
        long minBytes = minPlausibleBytes(contentType);
        if (minBytes <= 0) return false;
        return bytes < minBytes;
    }

    // ── Probes ────────────────────────────────────────────────────────────────

    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /**
     * Content-Length of a remote file, or 0 when the server won't say.
     * Tries HEAD first, then a one-byte ranged GET for servers that reject HEAD.
     * Blocking — call from a background thread.
     */
    public static long probeContentLength(String url, String referer) {
        long viaHead = requestLength(url, referer, true);
        if (viaHead > 0) return viaHead;
        return requestLength(url, referer, false);
    }

    private static long requestLength(String url, String referer, boolean head) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod(head ? "HEAD" : "GET");
            conn.setRequestProperty("User-Agent", UA);
            if (referer != null && !referer.isEmpty()) conn.setRequestProperty("Referer", referer);
            if (!head) conn.setRequestProperty("Range", "bytes=0-0");
            conn.connect();

            if (head) {
                long len = conn.getContentLengthLong();
                return len > 0 ? len : 0;
            }
            // 206 Partial Content → "Content-Range: bytes 0-0/12345678"
            String range = conn.getHeaderField("Content-Range");
            if (range != null) {
                int slash = range.lastIndexOf('/');
                if (slash >= 0 && slash + 1 < range.length()) {
                    try { return Long.parseLong(range.substring(slash + 1).trim()); }
                    catch (NumberFormatException ignored) {}
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** Duration in ms of a video already saved on the device, or 0 when unreadable. */
    public static long probeLocalDurationMs(Context ctx, String uriOrPath) {
        if (uriOrPath == null || uriOrPath.isEmpty()) return 0;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (uriOrPath.startsWith("content://")) {
                mmr.setDataSource(ctx, Uri.parse(uriOrPath));
            } else {
                mmr.setDataSource(uriOrPath);
            }
            String v = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return v != null ? Long.parseLong(v) : 0;
        } catch (Exception e) {
            return 0;
        } finally {
            try { mmr.release(); } catch (Exception ignored) {}
        }
    }
}
