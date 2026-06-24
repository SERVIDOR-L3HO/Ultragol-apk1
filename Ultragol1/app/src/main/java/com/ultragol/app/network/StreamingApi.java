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
        public String embedUrl = "";
    }

    private static String fetch(String path) throws Exception {
        URL u = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(15000); c.setReadTimeout(20000);
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
        data.latino.addAll(parseServers(idiomas.optJSONObject("latino")));
        JSONObject esp = idiomas.optJSONObject("español");
        if (esp == null) esp = idiomas.optJSONObject("espanol");
        data.espanol.addAll(parseServers(esp));
        JSONObject sub = idiomas.optJSONObject("subtitulado");
        if (sub == null) sub = idiomas.optJSONObject("subtitles");
        data.subtitulado.addAll(parseServers(sub));
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
