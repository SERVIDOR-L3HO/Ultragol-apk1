package com.ultragol.app.network;

import com.ultragol.app.models.Channel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StreamingApi {
    private static final String BASE = "https://ultrago-xi.vercel.app";

    public static class Server {
        public final String name, url, tipo;
        public Server(String name, String url, String tipo) {
            this.name = name; this.url = url; this.tipo = tipo;
        }
    }

    public static class ServerData {
        public final List<Server> latino      = new ArrayList<>();
        public final List<Server> espanol     = new ArrayList<>();
        public final List<Server> subtitulado = new ArrayList<>();
        public final List<Server> english     = new ArrayList<>(); // excluded by default; last resort only
        public String embedUrl = "";
    }

    private static String fetch(String path) throws Exception {
        URL u = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(12000); c.setReadTimeout(16000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close(); return sb.toString();
    }

    private static List<Server> parseServers(JSONObject langObj) {
        List<Server> list = new ArrayList<>();
        if (langObj == null) return list;
        JSONArray arr = langObj.optJSONArray("servidores");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject s = arr.getJSONObject(i);
                    String url = s.optString("url", "");
                    if (!url.isEmpty()) list.add(new Server(
                        cap(s.optString("nombre", "Srv " + (i+1))),
                        url, s.optString("tipo", "embed")));
                } catch (Exception ignored) {}
            }
        }
        if (list.isEmpty()) {
            String emb = langObj.optString("embed_url", "");
            if (!emb.isEmpty()) list.add(new Server("Principal", emb, "embed"));
        }
        return list;
    }

    private static String cap(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static ServerData fetchMovieServers(int tmdbId) throws Exception {
        return parseServerData(fetch("/api/unlimplay/m3u8/" + tmdbId));
    }

    public static ServerData fetchSeriesServers(int tmdbId, int season, int ep) throws Exception {
        return parseServerData(fetch("/api/unlimplay/m3u8-all/tv/" + tmdbId + "/" + season + "/" + ep));
    }

    public static ServerData parseServerData(String json) throws Exception {
        ServerData data = new ServerData();
        JSONObject root = new JSONObject(json);
        data.embedUrl = root.optString("embed_url", "");
        JSONObject idiomas = root.optJSONObject("idiomas");
        if (idiomas == null) {
            if (!data.embedUrl.isEmpty()) data.latino.add(new Server("Principal", data.embedUrl, "embed"));
            return data;
        }

        // Iterate all keys — the API may use "Español Latino", "Latino", "español", etc.
        java.util.Iterator<String> keys = idiomas.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String k = key.toLowerCase(java.util.Locale.ROOT)
                          .replace("á","a").replace("é","e")
                          .replace("í","i").replace("ó","o").replace("ú","u");
            JSONObject langObj = idiomas.optJSONObject(key);
            if (langObj == null) continue;
            List<Server> parsed = parseServers(langObj);
            if (parsed.isEmpty()) continue;
            if (k.contains("sub") || k.contains("vose") || k.contains("dub")) {
                data.subtitulado.addAll(parsed);
            } else if (k.contains("espa") || k.contains("cast") || k.contains("spain")) {
                data.espanol.addAll(parsed);
            } else if (k.contains("ingl") || k.contains("english") || k.equals("en")
                    || k.startsWith("eng")) {
                // English — keep separate, only used as absolute last resort
                data.english.addAll(parsed);
            } else {
                // "latino", "Español Latino", "lat", unknown — default to latino
                data.latino.addAll(parsed);
            }
        }

        if (data.latino.isEmpty() && data.espanol.isEmpty() && data.subtitulado.isEmpty()
                && !data.embedUrl.isEmpty())
            data.latino.add(new Server("Principal", data.embedUrl, "embed"));
        return data;
    }

    public static List<Channel> fetchSportsChannels() throws Exception {
        String json = fetch("/canales?categoria=sports&limit=100");
        return parseChannels(json);
    }

    public static List<Channel> fetchAllChannels(String categoria) throws Exception {
        String path = categoria != null && !categoria.isEmpty()
            ? "/canales?categoria=" + categoria + "&limit=80"
            : "/canales?limit=80";
        return parseChannels(fetch(path));
    }

    private static List<Channel> parseChannels(String json) {
        List<Channel> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.optJSONArray("canales");
            if (arr == null) arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject o = arr.getJSONObject(i);
                    String id   = o.optString("id", String.valueOf(i));
                    String name = o.optString("nombre", o.optString("name", "Canal " + (i+1)));
                    String country = o.optString("pais", "");
                    String flag = o.optString("bandera", "📺");
                    String logo = o.optString("logo", "");
                    String playerUrl = o.optString("player_url", "");
                    JSONArray cats = o.optJSONArray("categorias");
                    String cat = (cats != null && cats.length() > 0) ? cats.optString(0, "TV") : "TV";
                    if (!playerUrl.isEmpty()) list.add(new Channel(id, name, country, flag, logo, playerUrl, cat));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return list;
    }
}
