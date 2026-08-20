package com.ultragol.app.network;

import com.ultragol.app.models.ContentItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Anime-only API client.
 *
 * Anime uses a slug, not a TMDB id. Keeping this client separate prevents
 * anime identifiers from accidentally reaching TMDB or the regular stream API.
 */
public final class AnimeApi {
    private static final String BASE = "https://ultrago-xi.vercel.app";

    private AnimeApi() {}

    private static String fetch(String path) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + path).openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(12000);
        c.setReadTimeout(16000);
        int code = c.getResponseCode();
        if (code < 200 || code >= 300) throw new Exception("Anime API HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) out.append(line);
        br.close();
        return out.toString();
    }

    public static List<ContentItem> search(String query) throws Exception {
        String encoded = URLEncoder.encode(query.trim(), "UTF-8");
        JSONObject root = new JSONObject(fetch("/api/anime/buscar?q=" + encoded));
        JSONArray results = root.optJSONArray("resultados");
        List<ContentItem> items = new ArrayList<>();
        if (results == null) return items;

        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.optJSONObject(i);
            if (o == null) continue;
            String title = o.optString("titulo", "").trim();
            String slug = o.optString("slug", "").trim();
            if (title.isEmpty() || slug.isEmpty()) continue;
            String type = o.optString("tipo", "serie");
            if ("pelicula".equalsIgnoreCase(type)) continue;

            int year = o.optInt("anio", 0);
            double rating = o.optDouble("rating", 0);
            ContentItem item = new ContentItem(
                    title, "Anime", year > 0 ? String.valueOf(year) : "",
                    rating > 0 ? String.format(java.util.Locale.US, "%.1f", rating) : "",
                    o.optString("poster", ""), "", ContentItem.TYPE_ANIME, false, false);
            item.setAnimeSlug(slug);
            items.add(item);
        }
        return items;
    }

    public static AnimeDetail fetchDetail(String slug) throws Exception {
        JSONObject root = new JSONObject(fetch("/api/anime/" + encodePathPart(slug)));
        AnimeDetail detail = new AnimeDetail();
        detail.title = root.optString("titulo", "");
        detail.slug = root.optString("slug", slug);
        detail.poster = root.optString("poster", "");
        JSONArray seasons = root.optJSONArray("temporadas");
        if (seasons != null) {
            for (int i = 0; i < seasons.length(); i++) {
                JSONObject season = seasons.optJSONObject(i);
                if (season != null) detail.seasons.add(season.optInt("numero", i + 1));
            }
        }
        detail.totalEpisodes = root.optInt("total_episodios", 0);
        return detail;
    }

    public static int fetchSeasonCount(String slug) throws Exception {
        AnimeDetail detail = fetchDetail(slug);
        return Math.max(1, detail.seasons.size());
    }

    public static int fetchSeasonCount(ContentItem item) throws Exception {
        return fetchSeasonCount(resolveSlug(item));
    }

    public static List<TmdbApi.EpisodeInfo> fetchSeasonEpisodes(String slug, int season)
            throws Exception {
        List<TmdbApi.EpisodeInfo> episodes = new ArrayList<>();
        JSONObject root = new JSONObject(fetch("/api/anime/" + encodePathPart(slug)));
        JSONArray seasons = root.optJSONArray("temporadas");
        if (seasons == null) return episodes;

        for (int i = 0; i < seasons.length(); i++) {
            JSONObject s = seasons.optJSONObject(i);
            if (s == null || s.optInt("numero", -1) != season) continue;
            JSONArray eps = s.optJSONArray("episodios");
            if (eps == null) return episodes;
            for (int j = 0; j < eps.length(); j++) {
                JSONObject ep = eps.optJSONObject(j);
                if (ep == null) continue;
                int number = ep.optInt("numero", j + 1);
                String title = ep.optString("titulo", "");
                if (title.isEmpty() || "null".equalsIgnoreCase(title)) {
                    title = "Episodio " + number;
                }
                episodes.add(new TmdbApi.EpisodeInfo(
                        season, number, title, "", ep.optString("poster", ""), 0));
            }
            break;
        }
        return episodes;
    }

    public static List<TmdbApi.EpisodeInfo> fetchSeasonEpisodes(ContentItem item, int season)
            throws Exception {
        return fetchSeasonEpisodes(resolveSlug(item), season);
    }

    public static StreamingApi.ServerData fetchEpisodeServers(String slug, int season, int episode)
            throws Exception {
        JSONObject root = new JSONObject(fetch("/api/anime/" + encodePathPart(slug)
                + "/temporada/" + season + "/episodio/" + episode));
        StreamingApi.ServerData data = new StreamingApi.ServerData();
        JSONArray servers = root.optJSONArray("servidores");
        if (servers != null) {
            for (int i = 0; i < servers.length(); i++) {
                JSONObject server = servers.optJSONObject(i);
                if (server == null) continue;
                String url = server.optString("url", "");
                if (url.isEmpty()) continue;
                String name = server.optString("nombre", "Servidor " + (i + 1));
                String type = server.optString("tipo", "servidor");
                String language = server.optString("idioma", "");
                StreamingApi.Server parsed = new StreamingApi.Server(name, url, type);
                String lang = language.toLowerCase(java.util.Locale.ROOT);
                if (lang.contains("lat") || lang.contains("español")) data.latino.add(parsed);
                else if (lang.contains("sub") || lang.contains("japon")) data.subtitulado.add(parsed);
                else data.latino.add(parsed);
            }
        }
        String fallback = root.optString("url_animejara", "");
        if (data.latino.isEmpty() && data.subtitulado.isEmpty() && !fallback.isEmpty()) {
            data.latino.add(new StreamingApi.Server("AnimeJara", fallback, "embed"));
        }
        return data;
    }

    public static StreamingApi.ServerData fetchEpisodeServers(ContentItem item, int season,
                                                              int episode) throws Exception {
        return fetchEpisodeServers(resolveSlug(item), season, episode);
    }

    private static String resolveSlug(ContentItem item) throws Exception {
        String slug = item.getAnimeSlug();
        if (!slug.isEmpty()) return slug;
        List<ContentItem> matches = search(item.getTitle());
        String wanted = item.getTitle().trim().toLowerCase(java.util.Locale.ROOT);
        for (ContentItem match : matches) {
            if (match.getTitle().trim().toLowerCase(java.util.Locale.ROOT).equals(wanted)) {
                item.setAnimeSlug(match.getAnimeSlug());
                return match.getAnimeSlug();
            }
        }
        if (!matches.isEmpty()) {
            item.setAnimeSlug(matches.get(0).getAnimeSlug());
            return matches.get(0).getAnimeSlug();
        }
        throw new Exception("Anime no encontrado: " + item.getTitle());
    }

    private static String encodePathPart(String value) {
        return value.replace("/", "%2F").replace(" ", "%20");
    }

    public static final class AnimeDetail {
        public String title = "";
        public String slug = "";
        public String poster = "";
        public int totalEpisodes;
        public final List<Integer> seasons = new ArrayList<>();
    }
}