package com.ultragol.app.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.ultragol.app.models.ContentItem;
import com.ultragol.app.models.TvChannel;

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
 * Servicio IPTV para la API de A7X TV.
 *
 * Implementa caché en SharedPreferences con TTL de 1 hora.
 * Si el fetch falla y hay caché, usa los datos guardados (modo offline).
 */
public final class A7xIptvApi {

    private A7xIptvApi() {}

    // ── HTTP helper ───────────────────────────────────────────────────────────

    private static String fetch(Context ctx, String urlStr) throws Exception {
        String token = VpsAuthService.getValidAccessToken(ctx);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(20_000);
        conn.setRequestProperty("User-Agent",    A7xConstants.SECURE_USER_AGENT);
        conn.setRequestProperty("X-A7X-Client",  A7xConstants.SECURE_CLIENT_ID);
        conn.setRequestProperty("Content-Type",  "application/json");
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        int code = conn.getResponseCode();
        if (code == 401) {
            // Token inválido — limpiar
            VpsAuthService.clearVpsAccessToken(ctx);
            throw new Exception("HTTP 401 — sesión expirada");
        }
        if (code != 200) throw new Exception("HTTP " + code);

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"), 16384);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        conn.disconnect();
        return sb.toString();
    }

    // ── Caché ─────────────────────────────────────────────────────────────────

    private static SharedPreferences cache(Context ctx) {
        return ctx.getSharedPreferences(A7xConstants.CACHE_PREFS, Context.MODE_PRIVATE);
    }

    private static boolean isCacheValid(Context ctx) {
        long last = cache(ctx).getLong(A7xConstants.CACHE_KEY_LAST_REFRESH, 0L);
        return (System.currentTimeMillis() - last) < A7xConstants.CACHE_TTL_MS;
    }

    private static String readCache(Context ctx, String key) {
        return cache(ctx).getString(key, null);
    }

    private static void writeCache(Context ctx, String key, String json) {
        cache(ctx).edit()
                .putString(key, json)
                .putLong(A7xConstants.CACHE_KEY_LAST_REFRESH, System.currentTimeMillis())
                .apply();
    }

    public static void clearCache(Context ctx) {
        cache(ctx).edit().clear().apply();
    }

    // ── Canales en vivo ───────────────────────────────────────────────────────

    /**
     * Carga canales en vivo desde A7X. Usa caché si está vigente.
     */
    public static List<TvChannel> loadLiveChannels(Context ctx) throws Exception {
        String cacheKey = A7xConstants.CACHE_KEY_LIVE;
        String cached = readCache(ctx, cacheKey);

        if (isCacheValid(ctx) && cached != null) {
            return parseLiveChannels(cached);
        }

        try {
            String json = fetch(ctx, A7xConstants.BASE_URL + "?type=live");
            writeCache(ctx, cacheKey, json);
            return parseLiveChannels(json);
        } catch (Exception e) {
            if (cached != null) return parseLiveChannels(cached);
            throw e;
        }
    }

    /**
     * Carga canales en vivo filtrados por categoría.
     */
    public static List<TvChannel> loadLiveChannelsByCategory(Context ctx, String categoryId)
            throws Exception {
        String url = A7xConstants.BASE_URL + "?type=live&category_id=" + categoryId;
        try {
            String json = fetch(ctx, url);
            return parseLiveChannels(json);
        } catch (Exception e) {
            // Intentar desde caché general y filtrar
            String cached = readCache(ctx, A7xConstants.CACHE_KEY_LIVE);
            if (cached != null) {
                return filterByCategory(parseLiveChannels(cached), categoryId);
            }
            throw e;
        }
    }

    private static List<TvChannel> parseLiveChannels(String json) {
        List<TvChannel> list = new ArrayList<>();
        try {
            JSONArray arr = resolveArray(json);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject o = arr.getJSONObject(i);
                    String name     = o.optString("name", "Canal " + (i + 1));
                    String url      = o.optString("stream_url", "");
                    String logo     = o.optString("stream_icon", "");
                    String catName  = o.optString("category_name", "TV");
                    if (!url.isEmpty()) {
                        TvChannel ch = new TvChannel(name, url, logo,
                                TvChannel.mapGroup(catName), "");
                        list.add(ch);
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static List<TvChannel> filterByCategory(List<TvChannel> all, String categoryId) {
        // Filtra por categoría si el nombre coincide
        List<TvChannel> result = new ArrayList<>();
        for (TvChannel ch : all) {
            if (ch.category != null && ch.category.equalsIgnoreCase(categoryId)) {
                result.add(ch);
            }
        }
        return result.isEmpty() ? all : result;
    }

    // ── Películas (VOD) ───────────────────────────────────────────────────────

    /**
     * Carga películas VOD desde A7X. Usa caché si está vigente.
     */
    public static List<ContentItem> loadMovies(Context ctx) throws Exception {
        String cacheKey = A7xConstants.CACHE_KEY_VOD;
        String cached = readCache(ctx, cacheKey);

        if (isCacheValid(ctx) && cached != null) {
            return parseMovies(cached);
        }

        try {
            String json = fetch(ctx, A7xConstants.BASE_URL + "?type=vod");
            writeCache(ctx, cacheKey, json);
            return parseMovies(json);
        } catch (Exception e) {
            if (cached != null) return parseMovies(cached);
            throw e;
        }
    }

    /**
     * Carga películas filtradas por categoría.
     */
    public static List<ContentItem> loadMoviesByCategory(Context ctx, String categoryId)
            throws Exception {
        String url = A7xConstants.BASE_URL + "?type=vod&category_id=" + categoryId;
        try {
            String json = fetch(ctx, url);
            return parseMovies(json);
        } catch (Exception e) {
            String cached = readCache(ctx, A7xConstants.CACHE_KEY_VOD);
            if (cached != null) return parseMovies(cached);
            throw e;
        }
    }

    private static List<ContentItem> parseMovies(String json) {
        List<ContentItem> list = new ArrayList<>();
        try {
            JSONArray arr = resolveArray(json);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject o = arr.getJSONObject(i);
                    String name  = o.optString("name", o.optString("title", ""));
                    String url   = o.optString("stream_url", "");
                    String id    = o.optString("stream_id",
                            o.optString("id", String.valueOf(i)));
                    String poster = o.optString("stream_icon",
                            o.optString("cover", ""));
                    String backdrop = o.optString("backdrop_path", "");
                    String genre = o.optString("genre",
                            o.optString("category_name", "Drama"));
                    String year  = o.optString("year",
                            o.optString("release_date", "2024"));
                    String rating = o.optString("rating",
                            o.optString("vote_average", "7.0"));
                    String overview = o.optString("plot",
                            o.optString("description", ""));
                    // Omitir películas sin URL reproducible
                    if (name.isEmpty() || url.isEmpty()) continue;

                    ContentItem item = new ContentItem(
                            name, genre, year, rating, poster, overview,
                            ContentItem.TYPE_MOVIE, false, false);
                    item.setStreamUrl(url);
                    item.setBackdropUrl(backdrop);
                    item.setA7xSource(true);   // ← marcar como fuente A7X
                    list.add(item);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ── Series ────────────────────────────────────────────────────────────────

    /**
     * Carga series desde A7X. Usa caché si está vigente.
     */
    public static List<ContentItem> loadSeries(Context ctx) throws Exception {
        String cacheKey = A7xConstants.CACHE_KEY_SERIES;
        String cached = readCache(ctx, cacheKey);

        if (isCacheValid(ctx) && cached != null) {
            return parseSeries(cached);
        }

        try {
            String json = fetch(ctx, A7xConstants.BASE_URL + "?type=series");
            writeCache(ctx, cacheKey, json);
            return parseSeries(json);
        } catch (Exception e) {
            if (cached != null) return parseSeries(cached);
            throw e;
        }
    }

    /**
     * Carga series filtradas por categoría.
     */
    public static List<ContentItem> loadSeriesByCategory(Context ctx, String categoryId)
            throws Exception {
        String url = A7xConstants.BASE_URL + "?type=series&category_id=" + categoryId;
        try {
            String json = fetch(ctx, url);
            return parseSeries(json);
        } catch (Exception e) {
            String cached = readCache(ctx, A7xConstants.CACHE_KEY_SERIES);
            if (cached != null) return parseSeries(cached);
            throw e;
        }
    }

    /**
     * Carga el detalle completo de una serie (temporadas y episodios).
     */
    public static JSONObject loadSeriesDetail(Context ctx, String seriesId) throws Exception {
        String url = A7xConstants.BASE_URL + "?type=series&series_id=" + seriesId;
        String json = fetch(ctx, url);
        // Puede venir como array con 1 elemento o como objeto directo
        if (json.trim().startsWith("[")) {
            JSONArray arr = new JSONArray(json);
            return arr.length() > 0 ? arr.getJSONObject(0) : new JSONObject();
        }
        return new JSONObject(json);
    }

    private static List<ContentItem> parseSeries(String json) {
        List<ContentItem> list = new ArrayList<>();
        try {
            JSONArray arr = resolveArray(json);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject o = arr.getJSONObject(i);
                    String name  = o.optString("name", o.optString("title", ""));
                    String poster = o.optString("cover",
                            o.optString("stream_icon", ""));
                    String backdrop = o.optString("backdrop_path", "");
                    String genre = o.optString("genre", "Drama");
                    String year  = o.optString("year", "2024");
                    String rating = o.optString("rating", "7.0");
                    String overview = o.optString("plot", "");
                    String id    = o.optString("id", "");
                    if (name.isEmpty()) continue;

                    // stream_url para series es el ID para luego cargar detalle
                    ContentItem item = new ContentItem(
                            name, genre, year, rating, poster, overview,
                            ContentItem.TYPE_SERIES, false, false);
                    item.setStreamUrl("a7x://series/" + id); // URL interna
                    item.setBackdropUrl(backdrop);
                    item.setA7xSource(true);   // ← marcar como fuente A7X
                    list.add(item);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ── Recomendaciones del home ──────────────────────────────────────────────

    /**
     * Mezcla aleatoria de hasta 20 ítems entre películas y series para el home.
     * Usa caché si está disponible.
     */
    public static List<ContentItem> loadHomeRecommendations(Context ctx) {
        List<ContentItem> combined = new ArrayList<>();
        try {
            List<ContentItem> movies = loadMovies(ctx);
            List<ContentItem> series = loadSeries(ctx);
            combined.addAll(movies);
            combined.addAll(series);
            Collections.shuffle(combined);
            return combined.subList(0, Math.min(20, combined.size()));
        } catch (Exception e) {
            return combined;
        }
    }

    // ── Helper: resolver array desde diferentes formatos de respuesta ─────────

    private static JSONArray resolveArray(String json) throws Exception {
        String trimmed = json.trim();
        if (trimmed.startsWith("[")) {
            return new JSONArray(trimmed);
        }
        // Objeto con campo "data", "items", "results", o "transmisiones"
        JSONObject root = new JSONObject(trimmed);
        for (String key : new String[]{"data", "items", "results", "channels",
                                       "movies", "series", "transmisiones"}) {
            if (root.has(key)) {
                Object val = root.get(key);
                if (val instanceof JSONArray) return (JSONArray) val;
            }
        }
        // Si no hay array reconocido, devolver array vacío
        return new JSONArray();
    }
}
