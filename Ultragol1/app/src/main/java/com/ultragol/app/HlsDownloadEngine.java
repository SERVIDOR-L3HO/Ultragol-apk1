package com.ultragol.app;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Real HLS (m3u8) downloader: resolves master→media playlists, fetches every
 * segment (decrypting AES-128 ones), concatenates them into a single MPEG-TS
 * file, then remuxes that into a genuine standalone .mp4 saved to the public
 * Movies/ACTIONPLAY gallery folder via MediaStore — no re-encoding, just a
 * container remux via Android's own MediaExtractor/MediaMuxer, so no
 * third-party (e.g. FFmpeg) dependency is needed.
 */
public final class HlsDownloadEngine {

    private HlsDownloadEngine() {}

    public interface ProgressListener {
        void onProgress(int percent);
    }

    public static final class Result {
        public final boolean success;
        public final String contentUri;
        public final String error;
        Result(boolean success, String contentUri, String error) {
            this.success = success;
            this.contentUri = contentUri;
            this.error = error;
        }
    }

    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public static final String FOLDER_NAME = "ACTIONPLAY";

    /** Blocking — call from a background thread. Cleans up its own temp file. */
    public static Result download(Context ctx, String playlistUrl, String referer,
                                   String displayTitle, int contentType,
                                   ProgressListener listener) {
        File tempTs = null;
        try {
            String mediaPlaylistUrl = resolveMediaPlaylistUrl(playlistUrl, referer);
            String playlistText = fetchText(mediaPlaylistUrl, referer);
            String base = baseUrlOf(mediaPlaylistUrl);

            List<String> segmentUrls = new ArrayList<>();
            String keyUri = null;
            String ivHex  = null;
            long mediaSequence = 0;

            for (String raw : playlistText.split("\n")) {
                String line = raw.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("#EXT-X-KEY")) {
                    String uriAttr = extractAttr(line, "URI");
                    if (uriAttr != null) keyUri = resolveUrl(base, uriAttr);
                    ivHex = extractAttr(line, "IV");
                } else if (line.startsWith("#EXT-X-MEDIA-SEQUENCE")) {
                    try { mediaSequence = Long.parseLong(line.substring(line.indexOf(':') + 1).trim()); }
                    catch (Exception ignored) {}
                } else if (!line.startsWith("#")) {
                    segmentUrls.add(resolveUrl(base, line));
                }
            }
            if (segmentUrls.isEmpty()) {
                return new Result(false, null, "El manifiesto no tiene segmentos de video");
            }

            byte[] keyBytes = (keyUri != null) ? fetchBytes(keyUri, referer) : null;

            tempTs = File.createTempFile("dl_", ".ts", ctx.getCacheDir());
            try (FileOutputStream out = new FileOutputStream(tempTs)) {
                for (int i = 0; i < segmentUrls.size(); i++) {
                    byte[] segment = fetchBytesWithRetry(segmentUrls.get(i), referer, 2);
                    if (segment == null) {
                        return new Result(false, null, "Falló la descarga del segmento " + (i + 1)
                            + " de " + segmentUrls.size());
                    }
                    if (keyBytes != null) {
                        byte[] iv = (ivHex != null) ? hexToBytes(ivHex) : sequenceIv(mediaSequence + i);
                        segment = decryptAes128(segment, keyBytes, iv);
                    }
                    out.write(segment);
                    if (listener != null) {
                        // Reserve the last 8% of progress for the remux step below.
                        listener.onProgress((int) (((i + 1) * 92.0) / segmentUrls.size()));
                    }
                }
            }

            String filename = sanitizeFilename(displayTitle) + "_" + System.currentTimeMillis() + ".mp4";
            String contentUri = muxToMp4(ctx, tempTs, filename);

            // The playlist may have been an ad break rather than the title itself.
            // Check what actually got written and throw it away if it's too short
            // to be the real content, instead of leaving it in the user's gallery.
            long durationMs = StreamValidator.probeLocalDurationMs(ctx, contentUri);
            if (StreamValidator.isImplausiblyShort(contentType, durationMs)) {
                try { ctx.getContentResolver().delete(Uri.parse(contentUri), null, null); }
                catch (Exception ignored) {}
                return new Result(false, null,
                    "Ese servidor entregó un anuncio en vez del contenido. Prueba otro servidor.");
            }

            if (listener != null) listener.onProgress(100);
            return new Result(true, contentUri, null);

        } catch (Exception e) {
            return new Result(false, null, e.getMessage() != null ? e.getMessage() : "Error desconocido");
        } finally {
            if (tempTs != null) //noinspection ResultOfMethodCallIgnored
                tempTs.delete();
        }
    }

    // ── Playlist parsing ──────────────────────────────────────────────────────

    private static String resolveMediaPlaylistUrl(String url, String referer) throws IOException {
        String text = fetchText(url, referer);
        if (!text.contains("#EXT-X-STREAM-INF")) return url; // already a media playlist

        String base = baseUrlOf(url);
        String[] lines = text.split("\n");
        long bestBandwidth = -1;
        String bestUrl = null;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.startsWith("#EXT-X-STREAM-INF")) continue;
            long bandwidth = 0;
            String bw = extractAttr(line, "BANDWIDTH");
            if (bw != null) { try { bandwidth = Long.parseLong(bw); } catch (Exception ignored) {} }
            for (int j = i + 1; j < lines.length; j++) {
                String next = lines[j].trim();
                if (next.isEmpty() || next.startsWith("#")) continue;
                if (bandwidth >= bestBandwidth) { bestBandwidth = bandwidth; bestUrl = resolveUrl(base, next); }
                break;
            }
        }
        return bestUrl != null ? bestUrl : url;
    }

    private static final Pattern QUOTED_OR_BARE_VALUE =
        Pattern.compile("=(?:\"([^\"]*)\"|([^,\\s]+))");

    private static String extractAttr(String line, String attr) {
        int idx = line.indexOf(attr);
        if (idx < 0) return null;
        Matcher m = QUOTED_OR_BARE_VALUE.matcher(line.substring(idx + attr.length()));
        if (m.find() && m.start() == 0) {
            return m.group(1) != null ? m.group(1) : m.group(2);
        }
        return null;
    }

    private static String baseUrlOf(String url) {
        int idx = url.lastIndexOf('/');
        return idx >= 0 ? url.substring(0, idx + 1) : url;
    }

    private static String resolveUrl(String base, String maybeRelative) {
        if (maybeRelative.startsWith("http://") || maybeRelative.startsWith("https://")) return maybeRelative;
        if (maybeRelative.startsWith("//")) return "https:" + maybeRelative;
        try {
            return new URL(new URL(base), maybeRelative).toString();
        } catch (Exception e) {
            return base + maybeRelative;
        }
    }

    // ── AES-128 (HLS default cipher) ──────────────────────────────────────────

    private static byte[] hexToBytes(String hex) {
        String h = hex.startsWith("0x") || hex.startsWith("0X") ? hex.substring(2) : hex;
        int len = h.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(h.charAt(i), 16) << 4) + Character.digit(h.charAt(i + 1), 16));
        }
        return out;
    }

    private static byte[] sequenceIv(long sequence) {
        byte[] iv = new byte[16];
        for (int i = 15; i >= 0 && sequence != 0; i--) {
            iv[i] = (byte) (sequence & 0xFF);
            sequence >>>= 8;
        }
        return iv;
    }

    private static byte[] decryptAes128(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    // ── HTTP ───────────────────────────────────────────────────────────────

    private static String fetchText(String url, String referer) throws IOException {
        return new String(fetchBytes(url, referer), StandardCharsets.UTF_8);
    }

    private static byte[] fetchBytes(String url, String referer) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setRequestProperty("User-Agent", UA);
        if (referer != null && !referer.isEmpty()) conn.setRequestProperty("Referer", referer);
        conn.connect();
        int code = conn.getResponseCode();
        if (code != 200 && code != 206) throw new IOException("HTTP " + code);
        try (InputStream in = conn.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[16384];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
            return out.toByteArray();
        } finally {
            conn.disconnect();
        }
    }

    private static byte[] fetchBytesWithRetry(String url, String referer, int retries) {
        for (int attempt = 0; attempt <= retries; attempt++) {
            try {
                return fetchBytes(url, referer);
            } catch (Exception e) {
                if (attempt == retries) return null;
                try { Thread.sleep(500L * (attempt + 1)); } catch (InterruptedException ignored) {}
            }
        }
        return null;
    }

    // ── Container remux: raw .ts -> real, standalone .mp4 (no re-encode) ───────

    private static String muxToMp4(Context ctx, File tsFile, String filename) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

        Uri itemUri;
        File legacyFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + FOLDER_NAME);
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
            Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            itemUri = ctx.getContentResolver().insert(collection, values);
        } else {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), FOLDER_NAME);
            if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            legacyFile = new File(dir, filename);
            values.put(MediaStore.Video.Media.DATA, legacyFile.getAbsolutePath());
            itemUri = ctx.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }
        if (itemUri == null) throw new IOException("No se pudo crear el archivo en la galería");

        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(tsFile.getAbsolutePath());
            int trackCount = extractor.getTrackCount();
            int[] indexMap = new int[trackCount];

            try (ParcelFileDescriptor pfd = ctx.getContentResolver().openFileDescriptor(itemUri, "rw")) {
                if (pfd == null) throw new IOException("No se pudo abrir el destino de la descarga");
                MediaMuxer muxer = new MediaMuxer(pfd.getFileDescriptor(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                try {
                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat format = extractor.getTrackFormat(i);
                        String mime = format.getString(MediaFormat.KEY_MIME);
                        if (mime != null && (mime.startsWith("video/") || mime.startsWith("audio/"))) {
                            indexMap[i] = muxer.addTrack(format);
                            extractor.selectTrack(i);
                        } else {
                            indexMap[i] = -1;
                        }
                    }

                    muxer.start();

                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    while (true) {
                        int trackIndex = extractor.getSampleTrackIndex();
                        if (trackIndex < 0) break;
                        buffer.clear();
                        int sampleSize = extractor.readSampleData(buffer, 0);
                        if (sampleSize < 0) break;
                        int muxTrack = indexMap[trackIndex];
                        if (muxTrack >= 0) {
                            bufferInfo.offset = 0;
                            bufferInfo.size = sampleSize;
                            bufferInfo.presentationTimeUs = extractor.getSampleTime();
                            bufferInfo.flags = extractor.getSampleFlags();
                            muxer.writeSampleData(muxTrack, buffer, bufferInfo);
                        }
                        extractor.advance();
                    }
                    muxer.stop();
                } finally {
                    muxer.release();
                }
            }
        } catch (Exception e) {
            // Clean up the half-written MediaStore/legacy entry before propagating.
            try { ctx.getContentResolver().delete(itemUri, null, null); } catch (Exception ignored) {}
            throw (e instanceof IOException) ? (IOException) e : new IOException(e);
        } finally {
            extractor.release();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues done = new ContentValues();
            done.put(MediaStore.Video.Media.IS_PENDING, 0);
            ctx.getContentResolver().update(itemUri, done, null, null);
        } else if (legacyFile != null) {
            MediaScannerConnection.scanFile(ctx,
                new String[]{legacyFile.getAbsolutePath()}, new String[]{"video/mp4"}, null);
        }

        return itemUri.toString();
    }

    static String sanitizeFilename(String name) {
        if (name == null || name.isEmpty()) return "video";
        String clean = name.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ _\\-]", "").trim().replace(" ", "_");
        return clean.isEmpty() ? "video" : clean;
    }
}
