package com.ultragol.app.network;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StreamingApi {

    private static final String BASE = "https://ultragol-api-3--maricarmen43549.replit.app";

    public static class Server {
        public final String name;
        public final String url;
        public final String tipo;
        public Server(String name, String url, String tipo) {
            this.name = name;
            this.url  = url;
            this.tipo = tipo;
        }
    }

    public static class ServerData {
        public final List<Server> latino      = new ArrayList<>();
        public final List<Server> espanol     = new ArrayList<>();
        public final List<Server> subtitulado = new ArrayList<>();
        public String embedUrl = "";
    }

    public static class Channel {
        public final String name;
        public final String url;
        public final String category;
        public Channel(String name, String url, String category) {
            this.name     = name;
            this.url      = url;
            this.category = category;
        }
    }

    private static String fetch(String path) throws Exception {
        URL u = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(15000);
        c.setReadTimeout(20000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    public static ServerData fetchMovieServers(int tmdbId) throws Exception {
        String json = fetch("/api/unlimplay/m3u8/" + tmdbId);
        return parseServerData(json);
    }

    public static ServerData fetchSeriesServers(int tmdbId, int season, int episode) throws Exception {
        String json = fetch("/api/unlimplay/m3u8/tv/" + tmdbId + "/" + season + "/" + episode);
        return parseServerData(json);
    }

    private static List<Server> parseServers(JSONObject langObj, String fallbackLabel) {
        List<Server> list = new ArrayList<>();
        if (langObj == null) return list;
        JSONArray arr = langObj.optJSONArray("servidores");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject s = arr.getJSONObject(i);
                    String name = s.optString("nombre", "Servidor " + (i + 1));
                    String url  = s.optString("url", "");
                    String tipo = s.optString("tipo", "embed");
                    if (!url.isEmpty()) list.add(new Server(capitalize(name), url, tipo));
                } catch (Exception ignored) {}
            }
        }
        if (list.isEmpty()) {
            String emb = langObj.optString("embed_url", "");
            if (!emb.isEmpty()) list.add(new Server(fallbackLabel, emb, "embed"));
        }
        return list;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static ServerData parseServerData(String json) throws Exception {
        ServerData data = new ServerData();
        JSONObject root = new JSONObject(json);
        data.embedUrl = root.optString("embed_url", "");

        JSONObject idiomas = root.optJSONObject("idiomas");
        if (idiomas == null) {
            if (!data.embedUrl.isEmpty())
                data.latino.add(new Server("Principal", data.embedUrl, "embed"));
            return data;
        }

        data.latino.addAll(parseServers(idiomas.optJSONObject("latino"), "Latino"));

        JSONObject espObj = idiomas.optJSONObject("español");
        if (espObj == null) espObj = idiomas.optJSONObject("espanol");
        data.espanol.addAll(parseServers(espObj, "Español"));

        JSONObject subObj = idiomas.optJSONObject("subtitulado");
        if (subObj == null) subObj = idiomas.optJSONObject("subtitles");
        data.subtitulado.addAll(parseServers(subObj, "Subtitulado"));

        if (data.latino.isEmpty() && data.espanol.isEmpty() && data.subtitulado.isEmpty()
                && !data.embedUrl.isEmpty()) {
            data.latino.add(new Server("Principal", data.embedUrl, "embed"));
        }

        return data;
    }

    public static List<Channel> fetchChannels() throws Exception {
        String json = fetch("/canales");
        List<Channel> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String name = o.optString("nombre", o.optString("name", o.optString("canal", "Canal " + (i + 1))));
                String url  = o.optString("url", o.optString("stream", o.optString("link", "")));
                String cat  = o.optString("categoria", o.optString("category", "TV"));
                if (!url.isEmpty()) list.add(new Channel(name, url, cat));
            }
        } catch (Exception e) {
            try {
                JSONObject root = new JSONObject(json);
                JSONArray arr = root.optJSONArray("canales");
                if (arr == null) arr = root.optJSONArray("channels");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        String name = o.optString("nombre", o.optString("name", "Canal " + (i + 1)));
                        String url  = o.optString("url", o.optString("stream", ""));
                        String cat  = o.optString("categoria", "TV");
                        if (!url.isEmpty()) list.add(new Channel(name, url, cat));
                    }
                }
            } catch (Exception ignored) {}
        }
        return list;
    }

    public static String getFootballStreamUrl(int channel) {
        return BASE + "/gol-" + channel;
    }
}
