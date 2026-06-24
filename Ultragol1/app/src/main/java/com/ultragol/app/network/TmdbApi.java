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
    private static final String BASE = "https://api.themoviedb.org/3";
    public  static final String IMG_W  = "https://image.tmdb.org/t/p/w342";
    public  static final String IMG_BG = "https://image.tmdb.org/t/p/w780";
    private static final String BEARER =
        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmQ5YTgzNGQ0NDEzNzAwYjQ5MWNjMjY4OTIxNDdhYSIsIm5iZiI6MTc1MjQ1NjQ4My4zNDUsInN1YiI6IjY4NzQ1ZDIzNjIwNzU1OWUwNDVhZTRjMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mm-GBMnPS_WUAslIwTiewd6khCIFIqR4XDBqTlT9Yx0";

    private static String fetch(String path) throws Exception {
        URL url = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Authorization", "Bearer " + BEARER);
        c.setRequestProperty("accept", "application/json");
        c.setConnectTimeout(12000); c.setReadTimeout(12000);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close(); return sb.toString();
    }

    private static String genre(JSONArray ids) {
        if (ids == null || ids.length() == 0) return "Drama";
        switch (ids.optInt(0, 18)) {
            case 28: case 10759: return "Acción"; case 12: return "Aventura";
            case 16: return "Animación"; case 35: return "Comedia";
            case 80: return "Crimen"; case 99: return "Documental";
            case 18: return "Drama"; case 10751: return "Familia";
            case 14: case 10765: return "Fantasía"; case 27: return "Terror";
            case 9648: return "Misterio"; case 10749: return "Romance";
            case 878: return "Sci-Fi"; case 53: return "Thriller";
            default: return "Drama";
        }
    }

    private static String year(String d) { return (d != null && d.length() >= 4) ? d.substring(0,4) : "2024"; }
    private static String rating(double v) { return String.format("%.1f", v); }
    private static String poster(String p) { return (p == null || p.isEmpty()) ? "" : IMG_W + p; }
    private static String backdrop(String p) { return (p == null || p.isEmpty()) ? "" : IMG_BG + p; }

    private static List<ContentItem> parse(JSONArray arr, int type) {
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                boolean mov = o.has("title");
                String title = mov ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int id = o.optInt("id", 0);
                String g = genre(o.optJSONArray("genre_ids"));
                String y = year(o.optString(mov ? "release_date" : "first_air_date"));
                String r = rating(o.optDouble("vote_average", 7.0));
                String post = poster(o.optString("poster_path"));
                String back = backdrop(o.optString("backdrop_path"));
                String ov = o.optString("overview", "");
                boolean isNew = false;
                try { isNew = Integer.parseInt(y) >= 2024; } catch (Exception ignored) {}
                ContentItem item = new ContentItem(title, g, y, r, post, ov, type, isNew, false);
                item.setTmdbId(id); item.setBackdropUrl(back);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static List<ContentItem> fetchTrending() throws Exception {
        JSONArray arr = new JSONObject(fetch("/trending/all/week?language=es-MX")).getJSONArray("results");
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < arr.length() && list.size() < 10; i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                String mt = o.optString("media_type", "movie");
                if ("person".equals(mt)) continue;
                boolean mov = "movie".equals(mt);
                String title = mov ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int id = o.optInt("id", 0);
                String g = genre(o.optJSONArray("genre_ids"));
                String y = year(o.optString(mov ? "release_date" : "first_air_date"));
                String r = rating(o.optDouble("vote_average", 7.0));
                String post = poster(o.optString("poster_path"));
                String back = backdrop(o.optString("backdrop_path"));
                String ov = o.optString("overview", "");
                int type = mov ? ContentItem.TYPE_MOVIE : ContentItem.TYPE_SERIES;
                boolean isNew = false;
                try { isNew = Integer.parseInt(y) >= 2024; } catch (Exception ignored) {}
                ContentItem item = new ContentItem(title, g, y, r, post, ov, type, isNew, false);
                item.setTmdbId(id); item.setBackdropUrl(back);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static List<ContentItem> fetchMovies() throws Exception {
        return parse(new JSONObject(fetch("/discover/movie?sort_by=popularity.desc&language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_MOVIE);
    }
    public static List<ContentItem> fetchTopMovies() throws Exception {
        return parse(new JSONObject(fetch("/movie/top_rated?language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_MOVIE);
    }
    public static List<ContentItem> fetchSeries() throws Exception {
        return parse(new JSONObject(fetch("/discover/tv?sort_by=popularity.desc&language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_SERIES);
    }
    public static List<ContentItem> fetchAnime() throws Exception {
        return parse(new JSONObject(fetch("/discover/tv?with_genres=16&with_origin_country=JP&sort_by=popularity.desc&language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_ANIME);
    }
    public static List<ContentItem> fetchDoramas() throws Exception {
        return parse(new JSONObject(fetch("/discover/tv?with_origin_country=KR&sort_by=popularity.desc&language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_DORAMA);
    }
    public static List<ContentItem> fetchNewMovies() throws Exception {
        return parse(new JSONObject(fetch("/movie/now_playing?language=es-MX&page=1")).getJSONArray("results"), ContentItem.TYPE_MOVIE);
    }

    public static List<ContentItem> fetchByProvider(int providerId, String mediaType) throws Exception {
        int type = "movie".equals(mediaType) ? ContentItem.TYPE_MOVIE : ContentItem.TYPE_SERIES;
        String path = "/discover/" + mediaType
            + "?with_watch_providers=" + providerId
            + "&watch_region=MX"
            + "&sort_by=popularity.desc"
            + "&language=es-MX"
            + "&page=1";
        return parse(new JSONObject(fetch(path)).getJSONArray("results"), type);
    }
    // ── Episode data ──────────────────────────────────────────────────────────
    public static class EpisodeInfo {
        public final int season, number, runtime;
        public final String name, overview, stillUrl;
        public EpisodeInfo(int season, int number, String name, String overview, String stillUrl, int runtime) {
            this.season = season; this.number = number; this.name = name;
            this.overview = overview; this.stillUrl = stillUrl; this.runtime = runtime;
        }
    }

    public static List<EpisodeInfo> fetchSeasonEpisodes(int seriesId, int season) throws Exception {
        JSONObject root = new JSONObject(fetch("/tv/" + seriesId + "/season/" + season + "?language=es-MX"));
        JSONArray arr = root.optJSONArray("episodes");
        List<EpisodeInfo> list = new ArrayList<>();
        if (arr == null) return list;
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                int epNum = o.optInt("episode_number", i + 1);
                String name = o.optString("name", "Episodio " + epNum);
                String overview = o.optString("overview", "");
                String stillPath = o.optString("still_path", "");
                String stillUrl = stillPath.isEmpty() ? "" : "https://image.tmdb.org/t/p/w300" + stillPath;
                int runtime = o.optInt("runtime", 0);
                list.add(new EpisodeInfo(season, epNum, name, overview, stillUrl, runtime));
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static int fetchSeriesSeasonCount(int seriesId) throws Exception {
        JSONObject root = new JSONObject(fetch("/tv/" + seriesId + "?language=es-MX"));
        return Math.max(1, root.optInt("number_of_seasons", 1));
    }

    public static List<ContentItem> searchMulti(String query) throws Exception {
        String enc = java.net.URLEncoder.encode(query, "UTF-8");
        JSONArray arr = new JSONObject(fetch("/search/multi?query=" + enc + "&language=es-MX&page=1")).getJSONArray("results");
        List<ContentItem> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                String mt = o.optString("media_type", "movie");
                if ("person".equals(mt)) continue;
                boolean mov = "movie".equals(mt);
                String title = mov ? o.optString("title") : o.optString("name");
                if (title == null || title.isEmpty()) continue;
                int id = o.optInt("id", 0);
                String g = genre(o.optJSONArray("genre_ids"));
                String y = year(o.optString(mov ? "release_date" : "first_air_date"));
                String r = rating(o.optDouble("vote_average", 7.0));
                String post = poster(o.optString("poster_path"));
                String back = backdrop(o.optString("backdrop_path"));
                String ov = o.optString("overview", "");
                int type = mov ? ContentItem.TYPE_MOVIE : ContentItem.TYPE_SERIES;
                ContentItem item = new ContentItem(title, g, y, r, post, ov, type, false, false);
                item.setTmdbId(id); item.setBackdropUrl(back);
                list.add(item);
            } catch (Exception ignored) {}
        }
        return list;
    }
}
