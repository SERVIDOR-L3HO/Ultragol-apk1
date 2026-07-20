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
 * RedTube Webmasters API wrapper.
 * Official API docs: https://www.redtube.com/api/docs
 * No authentication required for public endpoints.
 */
public class AdultContentApi {

    private static final String BASE = "https://api.redtube.com/?output=json";

    // ── Category IDs ───────────────────────────────────────────────────────────
    public static final int CAT_AMATEUR    =  6;
    public static final int CAT_MILF       = 20;
    public static final int CAT_LATINA     = 21;
    public static final int CAT_ASIAN      =  9;
    public static final int CAT_LESBIAN    =  3;
    public static final int CAT_POV        = 30;
    public static final int CAT_MATURE     = 16;
    public static final int CAT_BIG_TITS   =  1;
    public static final int CAT_ANAL       =  5;
    public static final int CAT_TEENS      =  2;
    public static final int CAT_THREESOME  = 2211;
    public static final int CAT_HENTAI     = 15;
    public static final int CAT_WEBCAM     = 2291;
    public static final int CAT_ROMANTIC   = 50;
    public static final int CAT_MASSAGE    = 51;

    private static String fetch(String path) throws Exception {
        URL url = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(12000);
        c.setReadTimeout(12000);
        BufferedReader br = new BufferedReader(
            new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static List<AdultVideoItem> parse(String json) {
        List<AdultVideoItem> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray videos = root.optJSONArray("videos");
            if (videos == null) return list;
            for (int i = 0; i < videos.length(); i++) {
                try {
                    JSONObject v = videos.getJSONObject(i).getJSONObject("video");
                    String id    = v.optString("video_id", "");
                    String title = v.optString("title", "Sin título");
                    // Use the best available thumb
                    String thumb = v.optString("default_thumb", "");
                    if (thumb.isEmpty()) thumb = v.optString("thumb", "");
                    // Try to get a larger thumb from thumbs array
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
                    if (embed.isEmpty() && !id.isEmpty()) {
                        embed = "https://embed.redtube.com/?id=" + id;
                    }
                    String pubDate  = v.optString("publish_date", "");
                    // Build tag string
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

    // ── Curated feeds ─────────────────────────────────────────────────────────

    /** Most viewed this week — main "Trending" row. */
    public static List<AdultVideoItem> fetchTrending() throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&ordering=mostviewed&period=week&thumbsize=big&page=1&count=20"));
    }

    /** Newest uploads. */
    public static List<AdultVideoItem> fetchNewest() throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&ordering=newest&thumbsize=big&page=1&count=20"));
    }

    /** Top rated all time. */
    public static List<AdultVideoItem> fetchTopRated() throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&ordering=toprated&thumbsize=big&page=1&count=20"));
    }

    /** Most viewed of all time. */
    public static List<AdultVideoItem> fetchMostViewed() throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&ordering=mostviewed&thumbsize=big&page=1&count=20"));
    }

    // ── Category feeds ────────────────────────────────────────────────────────

    public static List<AdultVideoItem> fetchByCategory(int categoryId) throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&category=" + categoryId
            + "&ordering=mostviewed&period=month&thumbsize=big&page=1&count=20"));
    }

    // ── Convenience category shortcuts ────────────────────────────────────────

    public static List<AdultVideoItem> fetchAmateur()   throws Exception { return fetchByCategory(CAT_AMATEUR); }
    public static List<AdultVideoItem> fetchMilf()      throws Exception { return fetchByCategory(CAT_MILF); }
    public static List<AdultVideoItem> fetchLatina()    throws Exception { return fetchByCategory(CAT_LATINA); }
    public static List<AdultVideoItem> fetchAsian()     throws Exception { return fetchByCategory(CAT_ASIAN); }
    public static List<AdultVideoItem> fetchLesbian()   throws Exception { return fetchByCategory(CAT_LESBIAN); }
    public static List<AdultVideoItem> fetchPOV()       throws Exception { return fetchByCategory(CAT_POV); }
    public static List<AdultVideoItem> fetchMature()    throws Exception { return fetchByCategory(CAT_MATURE); }
    public static List<AdultVideoItem> fetchHentai()    throws Exception { return fetchByCategory(CAT_HENTAI); }
    public static List<AdultVideoItem> fetchWebcam()    throws Exception { return fetchByCategory(CAT_WEBCAM); }
    public static List<AdultVideoItem> fetchRomantic()  throws Exception { return fetchByCategory(CAT_ROMANTIC); }

    // ── Search ────────────────────────────────────────────────────────────────

    public static List<AdultVideoItem> search(String query) throws Exception {
        String enc = java.net.URLEncoder.encode(query, "UTF-8");
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&search=" + enc
            + "&ordering=mostviewed&thumbsize=big&page=1&count=20"));
    }

    // ── Paginated fetch (for infinite scroll) ─────────────────────────────────

    public static List<AdultVideoItem> fetchPage(int page) throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&ordering=mostviewed&period=week&thumbsize=big&page=" + page + "&count=20"));
    }

    public static List<AdultVideoItem> fetchCategoryPage(int categoryId, int page) throws Exception {
        return parse(fetch("&data=redtube.Videos.searchVideos"
            + "&category=" + categoryId
            + "&ordering=mostviewed&thumbsize=big&page=" + page + "&count=20"));
    }
}
