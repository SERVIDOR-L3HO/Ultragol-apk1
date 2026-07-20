package com.ultragol.app.network;

import com.ultragol.app.models.AdultVideoItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Multi-source adult content API.
 * Sources: RedTube Webmasters API + Pornhub Webmasters API
 * Both are public — no auth required.
 */
public class AdultContentApi {

    // ── Feed keys (used by AdultFragment chips) ───────────────────────────────
    public static final String FEED_TRENDING = "trending";
    public static final String FEED_NEWEST   = "newest";
    public static final String FEED_TOP      = "top";

    // ── Category keys ─────────────────────────────────────────────────────────
    public static final String CAT_AMATEUR   = "amateur";
    public static final String CAT_MILF      = "milf";
    public static final String CAT_LATINA    = "latina";
    public static final String CAT_ASIAN     = "asian";
    public static final String CAT_LESBIAN   = "lesbian";
    public static final String CAT_POV       = "pov";
    public static final String CAT_MATURE    = "mature";
    public static final String CAT_WEBCAM    = "webcam";
    public static final String CAT_HENTAI    = "hentai";
    public static final String CAT_THREESOME = "threesome";
    public static final String CAT_BIG_TITS  = "big_tits";
    public static final String CAT_ANAL      = "anal";
    public static final String CAT_MASSAGE   = "massage";
    public static final String CAT_ROMANTIC  = "romantic";

    // ── RedTube API ───────────────────────────────────────────────────────────
    private static final String RT_BASE = "https://api.redtube.com/?output=json";

    // RedTube category IDs
    private static int rtCatId(String key) {
        switch (key) {
            case CAT_AMATEUR:   return 6;
            case CAT_MILF:      return 20;
            case CAT_LATINA:    return 21;
            case CAT_ASIAN:     return 9;
            case CAT_LESBIAN:   return 3;
            case CAT_POV:       return 30;
            case CAT_MATURE:    return 16;
            case CAT_WEBCAM:    return 2291;
            case CAT_HENTAI:    return 15;
            case CAT_THREESOME: return 2211;
            case CAT_BIG_TITS:  return 1;
            case CAT_ANAL:      return 5;
            case CAT_MASSAGE:   return 51;
            case CAT_ROMANTIC:  return 50;
            default:            return -1;
        }
    }

    private static String httpGet(String fullUrl) throws Exception {
        URL url = new URL(fullUrl);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(14000);
        c.setReadTimeout(14000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(
            new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    // ── RedTube parsers & fetchers ────────────────────────────────────────────

    private static List<AdultVideoItem> parseRT(String json) {
        List<AdultVideoItem> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray videos = root.optJSONArray("videos");
            if (videos == null) return list;
            for (int i = 0; i < videos.length(); i++) {
                try {
                    JSONObject v = videos.getJSONObject(i).getJSONObject("video");
                    String id       = v.optString("video_id", "");
                    String title    = v.optString("title", "Sin título");
                    String thumb    = v.optString("default_thumb", "");
                    if (thumb.isEmpty()) thumb = v.optString("thumb", "");
                    JSONArray thumbs = v.optJSONArray("thumbs");
                    if (thumbs != null) {
                        for (int j = thumbs.length() - 1; j >= 0; j--) {
                            JSONObject t = thumbs.optJSONObject(j);
                            if (t != null) {
                                String ts = t.optString("src", "");
                                if (!ts.isEmpty()) { thumb = ts; break; }
                            }
                        }
                    }
                    String duration = v.optString("duration", "0:00");
                    String views    = v.optString("views", "0");
                    String rating   = String.valueOf((int) v.optDouble("rating", 0.0));
                    String embed    = v.optString("embed_url", "");
                    if (embed.isEmpty() && !id.isEmpty())
                        embed = "https://embed.redtube.com/?id=" + id;
                    String pubDate  = v.optString("publish_date", "");
                    StringBuilder tagSb = new StringBuilder();
                    JSONArray tags = v.optJSONArray("tags");
                    if (tags != null) {
                        for (int t = 0; t < Math.min(tags.length(), 3); t++) {
                            JSONObject tag = tags.optJSONObject(t);
                            if (tag != null) {
                                if (tagSb.length() > 0) tagSb.append(" · ");
                                tagSb.append(tag.optString("tag_name", ""));
                            }
                        }
                    }
                    list.add(new AdultVideoItem(id, title, thumb, duration,
                        views, rating, embed, pubDate, tagSb.toString()));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static List<AdultVideoItem> rtFeed(String ordering, String period, int page)
            throws Exception {
        String url = RT_BASE
            + "&data=redtube.Videos.searchVideos"
            + "&ordering=" + ordering
            + (period != null ? "&period=" + period : "")
            + "&thumbsize=big&page=" + page + "&count=20";
        return parseRT(httpGet(url));
    }

    private static List<AdultVideoItem> rtCategory(int catId, int page) throws Exception {
        String url = RT_BASE
            + "&data=redtube.Videos.searchVideos"
            + "&category=" + catId
            + "&ordering=mostviewed&thumbsize=big&page=" + page + "&count=20";
        return parseRT(httpGet(url));
    }

    // ── Pornhub Webmasters API ────────────────────────────────────────────────
    private static final String PH_BASE = "https://www.pornhub.com/webmasters";

    // Pornhub category IDs
    private static String phCatId(String key) {
        switch (key) {
            case CAT_AMATEUR:   return "1";
            case CAT_MILF:      return "217";
            case CAT_LATINA:    return "28";
            case CAT_ASIAN:     return "6";
            case CAT_LESBIAN:   return "27";
            case CAT_POV:       return "241";
            case CAT_MATURE:    return "44";
            case CAT_WEBCAM:    return "105";
            case CAT_HENTAI:    return "148";
            case CAT_THREESOME: return "53";
            case CAT_BIG_TITS:  return "8";
            case CAT_ANAL:      return "2";
            case CAT_MASSAGE:   return "81";
            case CAT_ROMANTIC:  return "172";
            default:            return null;
        }
    }

    private static List<AdultVideoItem> parsePH(String json) {
        List<AdultVideoItem> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray videos = root.optJSONArray("videos");
            if (videos == null) return list;
            for (int i = 0; i < videos.length(); i++) {
                try {
                    JSONObject v = videos.getJSONObject(i);
                    String id    = v.optString("video_id", "");
                    String title = v.optString("title", "Sin título");
                    String thumb = v.optString("thumb", "");
                    // PH duration is in seconds
                    int secs     = v.optInt("duration", 0);
                    String duration = String.format("%d:%02d", secs / 60, secs % 60);
                    String views = String.valueOf(v.optLong("views", 0));
                    String rating = String.valueOf((int) v.optDouble("rating", 0.0));
                    String embed = v.optString("embed", "");
                    if (embed.isEmpty() && !id.isEmpty())
                        embed = "https://www.pornhub.com/embed/" + id;
                    String pubDate = v.optString("publish_date", "");
                    // PH tags can be array of strings or objects
                    StringBuilder tagSb = new StringBuilder();
                    JSONArray tags = v.optJSONArray("tags");
                    if (tags != null) {
                        for (int t = 0; t < Math.min(tags.length(), 3); t++) {
                            String tag = tags.optString(t, "");
                            if (!tag.isEmpty()) {
                                if (tagSb.length() > 0) tagSb.append(" · ");
                                tagSb.append(tag);
                            }
                        }
                    }
                    list.add(new AdultVideoItem(id, title, thumb, duration,
                        views, rating, embed, pubDate, tagSb.toString()));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static List<AdultVideoItem> phFeed(String ordering, int page) throws Exception {
        String url = PH_BASE + "/search?search=&ordering=" + ordering
            + "&thumbsize=medium_q&page=" + page;
        return parsePH(httpGet(url));
    }

    private static List<AdultVideoItem> phCategory(String catId, int page) throws Exception {
        String url = PH_BASE + "/search?search=&categories_id[]=" + catId
            + "&ordering=mostviewed&thumbsize=medium_q&page=" + page;
        return parsePH(httpGet(url));
    }

    // ── Unified fetch with fallback ───────────────────────────────────────────

    /**
     * Main entry point. Tries RedTube first; falls back to Pornhub if empty.
     * @param key  One of FEED_* or CAT_* constants.
     * @param page 1-based page number.
     */
    public static List<AdultVideoItem> fetch(String key, int page) {
        // Try source 1: RedTube
        try {
            List<AdultVideoItem> r = fetchRT(key, page);
            if (!r.isEmpty()) return r;
        } catch (Exception ignored) {}

        // Try source 2: Pornhub
        try {
            List<AdultVideoItem> r = fetchPH(key, page);
            if (!r.isEmpty()) return r;
        } catch (Exception ignored) {}

        return new ArrayList<>();
    }

    private static List<AdultVideoItem> fetchRT(String key, int page) throws Exception {
        switch (key) {
            case FEED_TRENDING: return rtFeed("mostviewed", "week",  page);
            case FEED_NEWEST:   return rtFeed("newest",     null,    page);
            case FEED_TOP:      return rtFeed("toprated",   null,    page);
            default:
                int catId = rtCatId(key);
                if (catId < 0) throw new Exception("Unknown key: " + key);
                return rtCategory(catId, page);
        }
    }

    private static List<AdultVideoItem> fetchPH(String key, int page) throws Exception {
        switch (key) {
            case FEED_TRENDING: return phFeed("mostviewed", page);
            case FEED_NEWEST:   return phFeed("newest",     page);
            case FEED_TOP:      return phFeed("rating",     page);
            default:
                String phId = phCatId(key);
                if (phId == null) throw new Exception("Unknown key: " + key);
                return phCategory(phId, page);
        }
    }

    // ── Search (tries both sources) ───────────────────────────────────────────

    public static List<AdultVideoItem> search(String query) {
        String enc;
        try { enc = java.net.URLEncoder.encode(query, "UTF-8"); }
        catch (Exception e) { enc = query; }

        // Try RedTube search
        try {
            String url = RT_BASE + "&data=redtube.Videos.searchVideos"
                + "&search=" + enc + "&ordering=mostviewed&thumbsize=big&page=1&count=20";
            List<AdultVideoItem> r = parseRT(httpGet(url));
            if (!r.isEmpty()) return r;
        } catch (Exception ignored) {}

        // Try PH search
        try {
            String url = PH_BASE + "/search?search=" + enc
                + "&ordering=mostviewed&thumbsize=medium_q&page=1";
            return parsePH(httpGet(url));
        } catch (Exception ignored) {}

        return new ArrayList<>();
    }
}
