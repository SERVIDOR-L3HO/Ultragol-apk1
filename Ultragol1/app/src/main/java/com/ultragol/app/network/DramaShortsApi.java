package com.ultragol.app.network;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Proxy client for the /drama-shorts API endpoints served by this app's backend,
 * which in turn proxies Dailymotion.
 *
 * All methods are blocking — call from a background thread.
 */
public class DramaShortsApi {

    /** Base URL of the Dailymotion public API — called directly to avoid server round-trips. */
    private static final String DM_BASE   = "https://api.dailymotion.com";
    private static final String DM_FIELDS = "id,title,thumbnail_url,duration,views_total,owner.screenname";

    // ── Data model ────────────────────────────────────────────────────────────

    public static class VideoItem {
        public final String id, titulo, thumbnailUrl, canal;
        public final int    duracion, vistas;

        VideoItem(String id, String titulo, String thumbnailUrl,
                  String canal, int duracion, int vistas) {
            this.id           = id;
            this.titulo       = titulo;
            this.thumbnailUrl = thumbnailUrl;
            this.canal        = canal;
            this.duracion     = duracion;
            this.vistas       = vistas;
        }

        /** Full-screen Dailymotion embed URL for the player. */
        public String getEmbedUrl() {
            return "https://www.dailymotion.com/embed/video/" + id;
        }

        /** Human-readable duration, e.g. "3:42". */
        public String getDuracionStr() {
            int m = duracion / 60, s = duracion % 60;
            return m + ":" + (s < 10 ? "0" + s : s);
        }

        /** Human-readable view count, e.g. "1.2M". */
        public String getVistasStr() {
            if (vistas >= 1_000_000) return String.format("%.1fM", vistas / 1_000_000.0);
            if (vistas >= 1_000)     return String.format("%.0fK", vistas / 1_000.0);
            return String.valueOf(vistas);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static String fetch(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setConnectTimeout(12_000);
        c.setReadTimeout(15_000);
        if (c.getResponseCode() != 200) throw new Exception("HTTP " + c.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static VideoItem map(JSONObject v) {
        return new VideoItem(
            v.optString("id"),
            v.optString("title"),
            v.optString("thumbnail_url"),
            v.optString("owner.screenname"),
            v.optInt("duration", 0),
            v.optInt("views_total", 0)
        );
    }

    private static List<VideoItem> parseList(String json, String listKey) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray arr = root.optJSONArray(listKey != null ? listKey : "list");
        if (arr == null) arr = root.optJSONArray("list");
        if (arr == null) return Collections.emptyList();
        List<VideoItem> out = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try { out.add(map(arr.getJSONObject(i))); } catch (Exception ignored) {}
        }
        return out;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Recientes: recent drama shorts (randomly shuffled for the home row). */
    public static List<VideoItem> fetchRecientes(int page) throws Exception {
        String[] queries = {"drama corto", "k-drama short", "drama romántico", "short drama"};
        String q = queries[(int)(Math.random() * queries.length)];
        String json = fetch(DM_BASE + "/videos?search=" + java.net.URLEncoder.encode(q, "UTF-8")
            + "&fields=" + DM_FIELDS + "&limit=40&page=" + page + "&sort=recent");
        List<VideoItem> list = parseList(json, "list");
        Collections.shuffle(list);
        return list;
    }

    /** Buscar por título. */
    public static List<VideoItem> buscar(String titulo, String lang, int page) throws Exception {
        String json = fetch(DM_BASE + "/videos?search=" + java.net.URLEncoder.encode(titulo, "UTF-8")
            + "&language=" + lang + "&fields=" + DM_FIELDS + "&limit=30&page=" + page);
        return parseList(json, "list");
    }

    /** Videos populares (más vistas). */
    public static List<VideoItem> fetchPopulares(String lang, int page) throws Exception {
        String json = fetch(DM_BASE + "/videos?search=drama&language=" + lang
            + "&fields=" + DM_FIELDS + "&limit=30&page=" + page + "&sort=visited");
        return parseList(json, "list");
    }

    /** Tendencias (los que están subiendo). */
    public static List<VideoItem> fetchTendencias(String lang, int page) throws Exception {
        String json = fetch(DM_BASE + "/videos?search=drama&language=" + lang
            + "&fields=" + DM_FIELDS + "&limit=30&page=" + page + "&sort=trending");
        return parseList(json, "list");
    }

    /** Videos de un canal por username. */
    public static List<VideoItem> fetchCanal(String username, int page) throws Exception {
        String json = fetch(DM_BASE + "/user/" + username + "/videos?fields=" + DM_FIELDS
            + "&limit=30&page=" + page);
        return parseList(json, "list");
    }
}
