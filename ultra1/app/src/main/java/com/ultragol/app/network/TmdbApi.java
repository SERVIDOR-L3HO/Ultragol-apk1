package com.ultragol.app.network;

import com.ultragol.app.models.ContentItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TmdbApi {

    private static final String BASE_URL  = "https://api.themoviedb.org/3";
    public  static final String IMAGE_BASE = "https://image.tmdb.org/t/p/w342";
    private static final String BEARER    =
        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmQ5YTgzNGQ0NDEzNzAwYjQ5MWNjMjY4OTIxNDdhYSIsIm5iZiI6MTc1MjQ1NjQ4My4zNDUsInN1YiI6IjY4NzQ1ZDIzNjIwNzU1OWUwNDVhZTRjMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mm-GBMnPS_WUAslIwTiewd6khCIFIqR4XDBqTlT9Yx0";

    private static String fetch(String path) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Authorization", "Bearer " + BEARER);
        c.setRequestProperty("accept", "application/json");
        c.setConnectTimeout(12000);
        c.setReadTimeout(12000);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static String genreName(JSONArray ids) {
        if (ids == null || ids.length() == 0) return "Drama";
        switch (ids.optInt(0, 18)) {
            case 28: case 10759: return "Acción";
            case 12: return "Aventura";
            case 16: return "Animación";
            case 35: return "Comedia";
            case 80: return "Crimen";
            case 99: return "Documental";
            case 18: return "Drama";
            case 10751: return "Familia";
            case 14: case 10765: return "Fantasía";
            case 36: return "Histórico";
            case 27: return "Terror";
            case 9648: return "Misterio";
            case 10749: return "Romance";
            case 878: return "Sci-Fi";
            case 53: return "Thriller";
            case 10752: return "Bélico";
            default: return "Drama";
        }
    }

    private static String year(String date) {
        if (date == null || date.length() < 4) return "2024";
        return date.substring(0, 4);
    }

    private static String rating(double val) {
        return String.format("%.1f", val);
    }

    private static String poster(String path) {
        if (path == null || path.isEmpty()) return "";
        return IMAGE_BASE + path;
    }

    private static List<ContentItem> parseResults(JSONArray results, int type) {
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject o = results.getJSONObject(i);
                boolean isMovie = o.has("title");
                String title = isMovie ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int tmdbId   = o.optInt("id", 0);
                String genre = genreName(o.optJSONArray("genre_ids"));
                String dateField = isMovie ? "release_date" : "first_air_date";
                String yr    = year(o.optString(dateField));
                String rat   = rating(o.optDouble("vote_average", 7.0));
                String posterUrl = poster(o.optString("poster_path"));
                String overview  = o.optString("overview", "");
                boolean isNew = false;
                try { isNew = Integer.parseInt(yr) >= 2024; }
                catch (NumberFormatException ignored) {}
                ContentItem item = new ContentItem(title, genre, yr, rat, posterUrl, overview, type, isNew, false);
                item.setTmdbId(tmdbId);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static List<ContentItem> fetchMovies() throws Exception {
        String json = fetch("/discover/movie?sort_by=popularity.desc&language=es-MX&page=1");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        return parseResults(results, ContentItem.TYPE_MOVIE);
    }

    public static List<ContentItem> fetchSeries() throws Exception {
        String json = fetch("/discover/tv?sort_by=popularity.desc&language=es-MX&page=1");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        return parseResults(results, ContentItem.TYPE_SERIES);
    }

    public static List<ContentItem> fetchAnime() throws Exception {
        String json = fetch("/discover/tv?with_genres=16&with_origin_country=JP&sort_by=popularity.desc&language=es-MX&page=1");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        return parseResults(results, ContentItem.TYPE_ANIME);
    }

    public static List<ContentItem> fetchDoramas() throws Exception {
        String json = fetch("/discover/tv?with_origin_country=KR&sort_by=popularity.desc&language=es-MX&page=1");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        return parseResults(results, ContentItem.TYPE_DORAMA);
    }

    public static List<ContentItem> fetchTrending() throws Exception {
        String json = fetch("/trending/all/week?language=es-MX");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < results.length() && list.size() < 12; i++) {
            try {
                JSONObject o = results.getJSONObject(i);
                String mediaType = o.optString("media_type", "movie");
                if (mediaType.equals("person")) continue;
                boolean isMovie  = mediaType.equals("movie");
                String title     = isMovie ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int tmdbId       = o.optInt("id", 0);
                String genre     = genreName(o.optJSONArray("genre_ids"));
                String dateField = isMovie ? "release_date" : "first_air_date";
                String yr        = year(o.optString(dateField));
                String rat       = rating(o.optDouble("vote_average", 7.0));
                String posterUrl = poster(o.optString("poster_path"));
                String overview  = o.optString("overview", "");
                int type         = isMovie ? ContentItem.TYPE_MOVIE : ContentItem.TYPE_SERIES;
                boolean isNew    = false;
                try { isNew = Integer.parseInt(yr) >= 2024; } catch (Exception ignored) {}
                ContentItem item = new ContentItem(title, genre, yr, rat, posterUrl, overview, type, isNew, false);
                item.setTmdbId(tmdbId);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static List<ContentItem> searchMulti(String query) throws Exception {
        String encoded = java.net.URLEncoder.encode(query, "UTF-8");
        String json    = fetch("/search/multi?query=" + encoded + "&language=es-MX&page=1");
        JSONArray results = new JSONObject(json).getJSONArray("results");
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject o = results.getJSONObject(i);
                String mediaType = o.optString("media_type", "movie");
                if (mediaType.equals("person")) continue;
                boolean isMovie  = mediaType.equals("movie");
                String title     = isMovie ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int tmdbId       = o.optInt("id", 0);
                String genre     = genreName(o.optJSONArray("genre_ids"));
                String dateField = isMovie ? "release_date" : "first_air_date";
                String yr        = year(o.optString(dateField));
                String rat       = rating(o.optDouble("vote_average", 7.0));
                String posterUrl = poster(o.optString("poster_path"));
                String overview  = o.optString("overview", "");
                int type         = isMovie ? ContentItem.TYPE_MOVIE : ContentItem.TYPE_SERIES;
                ContentItem item = new ContentItem(title, genre, yr, rat, posterUrl, overview, type, false, false);
                item.setTmdbId(tmdbId);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }
}
