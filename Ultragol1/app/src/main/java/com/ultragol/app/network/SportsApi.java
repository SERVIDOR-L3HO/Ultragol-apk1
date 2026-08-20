package com.ultragol.app.network;

import android.content.Context;
import android.util.Base64;

import com.ultragol.app.models.SportsHighlight;
import com.ultragol.app.models.SportsChannel;
import com.ultragol.app.models.SportsMatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Native client for the ultrago-xi.vercel.app sports API: match scores per
 * league, six independent live-stream-source feeds (gol-2..gol-7), and
 * highlight videos. Streams from different sources are attached to the
 * match they belong to by fuzzy team-name matching (ported from the same
 * algorithm the project's web client used), so a single fixture ends up
 * with every source's link listed as "Servidor 1", "Servidor 2", etc.
 */
public class SportsApi {

    private static final String BASE = "https://ultrago-xi.vercel.app";

    /** Blocking call for the full sports-channel catalogue. */
    public static List<SportsChannel> fetchSportsChannels() {
        List<SportsChannel> out = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(fetch("/canales/deportes"));
            JSONArray channels = root.optJSONArray("canales");
            if (channels == null) return out;
            for (int i = 0; i < channels.length(); i++) {
                JSONObject raw = channels.optJSONObject(i);
                if (raw == null) continue;
                SportsChannel channel = new SportsChannel();
                channel.id = raw.optString("id", "");
                channel.name = raw.optString("nombre", raw.optString("name", "Canal deportivo"));
                channel.country = raw.optString("pais", "Internacional");
                channel.countryCode = raw.optString("codigoPais", "");
                channel.flag = raw.optString("bandera", "");
                channel.logo = raw.optString("logo", "");
                JSONArray streams = raw.optJSONArray("streams");
                if (streams != null) {
                    for (int j = 0; j < streams.length(); j++) {
                        JSONObject stream = streams.optJSONObject(j);
                        String url = stream == null ? streams.optString(j, "") : stream.optString("url", "");
                        if (!url.isEmpty()) channel.streams.add(url);
                    }
                }
                if (!channel.primaryStream().isEmpty()) out.add(channel);
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** {display name, API path prefix} — prefix "" means the default (Liga MX) routes. */
    public static final String[][] LEAGUES = {
        {"Liga MX",    ""},
        {"Premier",    "premier"},
        {"La Liga",    "laliga"},
        {"Serie A",    "seriea"},
        {"Bundesliga", "bundesliga"},
        {"Ligue 1",    "ligue1"},
        {"Champions",  "champions"},
    };

    // ── Team alias / logo tables (bundled assets, loaded once) ───────────────

    private static Map<String, List<String>> aliasTable;
    private static Map<String, String> logoTable;

    private static synchronized void ensureTables(Context ctx) {
        if (aliasTable != null) return;
        Map<String, List<String>> aliases = new HashMap<>();
        Map<String, String> logos = new HashMap<>();
        try {
            JSONObject a = new JSONObject(readAsset(ctx, "sports/team_aliases.json"));
            Iterator<String> keys = a.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                JSONArray arr = a.getJSONArray(k);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) list.add(arr.getString(i));
                aliases.put(k, list);
            }
        } catch (Exception ignored) {}
        try {
            JSONObject l = new JSONObject(readAsset(ctx, "sports/team_logos.json"));
            Iterator<String> keys = l.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                logos.put(k, l.getString(k));
            }
        } catch (Exception ignored) {}
        aliasTable = aliases;
        logoTable  = logos;
    }

    private static String readAsset(Context ctx, String name) throws IOException {
        InputStream is = ctx.getApplicationContext().getAssets().open(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    // ── Name normalization / alias matching (ported from the project's web client) ──

    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        n = n.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.trim();
    }

    private static List<String> aliasesFor(Context ctx, String teamName) {
        ensureTables(ctx);
        String norm = normalize(teamName);
        for (Map.Entry<String, List<String>> e : aliasTable.entrySet()) {
            if (norm.equals(e.getKey()) || e.getValue().contains(norm)) return e.getValue();
        }
        for (Map.Entry<String, List<String>> e : aliasTable.entrySet()) {
            if (e.getKey().length() >= 5 && norm.contains(e.getKey())) return e.getValue();
            for (String v : e.getValue()) {
                if (v.length() >= 5 && norm.contains(v)) return e.getValue();
            }
        }
        return Collections.singletonList(norm);
    }

    public static String logoFor(Context ctx, String teamName) {
        ensureTables(ctx);
        String norm = normalize(teamName);
        String logo = logoTable.get(norm);
        if (logo != null) return logo;
        for (Map.Entry<String, String> e : logoTable.entrySet()) {
            if (norm.contains(e.getKey()) || e.getKey().contains(norm)) return e.getValue();
        }
        return "";
    }

    private static final Pattern VS_SPLIT =
        Pattern.compile("\\s+(?:vs?\\.?|×|x|-)\\s+", Pattern.CASE_INSENSITIVE);

    private static String[] teamsFromTitle(String titulo) {
        String norm = normalize(titulo);
        String[] parts = VS_SPLIT.split(norm, 2);
        if (parts.length >= 2) return new String[]{parts[0].trim(), parts[1].trim()};
        return new String[]{norm, ""};
    }

    /** True if eventoCandidate (a title from another source) refers to the same match as eventoRef. */
    private static boolean matchesEvento(Context ctx, String eventoRef, String eventoCandidate) {
        String ref  = normalize(eventoRef);
        String cand = normalize(eventoCandidate);
        if (ref.isEmpty() || cand.isEmpty()) return false;
        if (cand.equals(ref) || cand.contains(ref) || ref.contains(cand)) return true;

        String[] teams = teamsFromTitle(ref);
        if (teams[0].isEmpty() || teams[1].isEmpty()) return false;
        List<String> aliasA = aliasesFor(ctx, teams[0]);
        List<String> aliasB = aliasesFor(ctx, teams[1]);
        return containsAlias(aliasA, cand) && containsAlias(aliasB, cand);
    }

    private static boolean containsAlias(List<String> aliases, String cand) {
        for (String a : aliases) {
            if (a.length() >= 3 && cand.contains(a)) return true;
        }
        return false;
    }

    // ── HTTP ───────────────────────────────────────────────────────────────

    private static String fetch(String path) throws Exception {
        URL u = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(10000);
        c.setReadTimeout(12000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static String safeFetch(String path) {
        try { return fetch(path); } catch (Exception e) { return null; }
    }

    /** Stream URLs from these feeds are base64-encoded unless they already look like a URL. */
    private static String decodeStreamUrl(String url) {
        if (url == null || url.isEmpty() || url.startsWith("http")) return url;
        try {
            return new String(Base64.decode(url, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            return url;
        }
    }

    // ── Marcadores: match metadata (teams, score, status, time) ──────────────

    public static List<SportsMatch> fetchMarcadores(Context ctx, String leaguePrefix) throws Exception {
        String path = (leaguePrefix == null || leaguePrefix.isEmpty())
            ? "/marcadores" : "/" + leaguePrefix + "/marcadores";
        return parseMarcadores(ctx, fetch(path));
    }

    public static List<SportsMatch> fetchAllLeaguesMarcadores(Context ctx) throws Exception {
        return parseMarcadores(ctx, fetch("/todo-todas-las-ligas"));
    }

    private static List<SportsMatch> parseMarcadores(Context ctx, String json) throws Exception {
        List<SportsMatch> out = new ArrayList<>();
        JSONArray arr;
        try {
            JSONObject root = new JSONObject(json);
            arr = findArray(root, "partidos", "matches", "fixtures", "resultados", "marcadores", "data");
            if (arr == null) return out;
        } catch (Exception e) {
            arr = new JSONArray(json);
        }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject m = arr.getJSONObject(i);
                SportsMatch sm = new SportsMatch();
                sm.league   = m.optString("liga", m.optString("league", ""));
                sm.homeTeam = teamName(m, "local");
                sm.awayTeam = teamName(m, "visitante");
                String logoL = teamLogo(m, "local");
                String logoV = teamLogo(m, "visitante");
                sm.homeLogo = !logoL.isEmpty() ? logoL : logoFor(ctx, sm.homeTeam);
                sm.awayLogo = !logoV.isEmpty() ? logoV : logoFor(ctx, sm.awayTeam);
                sm.homeScore = teamScore(m, "local");
                sm.awayScore = teamScore(m, "visitante");
                sm.time   = m.optString("hora", m.optString("time", ""));
                sm.date   = m.optString("fecha", m.optString("date", ""));
                sm.minute = m.optString("minuto", m.optString("minute", ""));

                String estado = m.optString("estado", m.optString("status", "")).toLowerCase(Locale.ROOT);
                boolean isLive = estado.contains("vivo") || estado.contains("live")
                    || sm.minute.matches("\\d+.*");
                boolean isFinished = estado.contains("finaliz") || estado.contains("final")
                    || estado.contains("terminad") || estado.equals("ft");
                sm.status = isLive ? SportsMatch.STATUS_LIVE
                    : isFinished ? SportsMatch.STATUS_FINISHED
                    : SportsMatch.STATUS_UPCOMING;

                out.add(sm);
            } catch (Exception ignored) {}
        }
        return out;
    }

    private static String teamName(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"nombre", "name", "nombreCorto"}) {
                String n = team.optString(f, "");
                if (!n.isEmpty() && !n.equals("null")) return n;
            }
        }
        String flat = match.optString(key, "");
        if (!flat.startsWith("{") && !flat.isEmpty()) return flat;
        return key.equals("local") ? "Local" : "Visitante";
    }

    private static String teamScore(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            Object v = team.opt("marcador");
            if (v != null && !v.toString().equals("null") && !v.toString().isEmpty()) return v.toString();
        }
        boolean isLocal = key.equals("local");
        int v = match.optInt(isLocal ? "marcador_local" : "marcador_visitante",
                match.optInt(isLocal ? "score_home" : "score_away",
                match.optInt(isLocal ? "goles_local" : "goles_visitante", -1)));
        return v >= 0 ? String.valueOf(v) : "-";
    }

    private static String teamLogo(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"logo", "logo_url", "escudo", "badge"}) {
                String l = team.optString(f, "");
                if (!l.isEmpty() && !l.equals("null")) return l;
            }
        }
        return match.optString(key + "_logo", "");
    }

    private static JSONArray findArray(JSONObject root, String... keys) {
        for (String k : keys) {
            JSONArray arr = root.optJSONArray(k);
            if (arr != null && arr.length() > 0) return arr;
        }
        return null;
    }

    // ── Transmisiones: gol-2..gol-7 stream-source feeds ───────────────────────

    private static final class RawFeed {
        final String evento;
        final List<String[]> canales; // [0]=label [1]=already-decoded playable url
        RawFeed(String evento, List<String[]> canales) { this.evento = evento; this.canales = canales; }
    }

    /** Fetches and normalizes all 6 stream-source feeds in parallel. */
    private static List<RawFeed> fetchTransmisiones() {
        ExecutorService pool = Executors.newFixedThreadPool(6);
        List<Future<List<RawFeed>>> futures = new ArrayList<>();
        futures.add(pool.submit(() -> normalizeVoodc(safeFetch("/gol-2"))));
        futures.add(pool.submit(() -> normalizeE1link(safeFetch("/gol-3"))));
        futures.add(pool.submit(() -> normalizeCanalesUrl(safeFetch("/gol-4"))));
        futures.add(pool.submit(() -> normalizeDonromans(safeFetch("/gol-5"))));
        futures.add(pool.submit(() -> normalizeGol6(safeFetch("/gol-6"))));
        futures.add(pool.submit(() -> normalizeCanalesUrl(safeFetch("/gol-7"))));

        List<RawFeed> combined = new ArrayList<>();
        for (Future<List<RawFeed>> f : futures) {
            try { combined.addAll(f.get(15, TimeUnit.SECONDS)); } catch (Exception ignored) {}
        }
        pool.shutdown();
        return combined;
    }

    /** gol-2 (voodc): { transmisiones: [{ evento|titulo, deporte, url }] } — one channel per item. */
    private static List<RawFeed> normalizeVoodc(String json) {
        List<RawFeed> out = new ArrayList<>();
        if (json == null) return out;
        try {
            JSONArray arr = new JSONObject(json).optJSONArray("transmisiones");
            if (arr == null) return out;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.getJSONObject(i);
                String evento = t.optString("evento", t.optString("titulo", ""));
                String url = t.optString("url", "");
                if (evento.isEmpty() || url.isEmpty()) continue;
                List<String[]> canales = new ArrayList<>();
                canales.add(new String[]{t.optString("deporte", "Canal"), decodeStreamUrl(url)});
                out.add(new RawFeed(evento, canales));
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** gol-3 (e1link): { transmisiones: [{ titulo, enlacesDetalle|enlaces: [{nombre, url}] }] }. */
    private static List<RawFeed> normalizeE1link(String json) {
        List<RawFeed> out = new ArrayList<>();
        if (json == null) return out;
        try {
            JSONArray arr = new JSONObject(json).optJSONArray("transmisiones");
            if (arr == null) return out;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.getJSONObject(i);
                String evento = t.optString("titulo", t.optString("evento", ""));
                if (evento.isEmpty()) continue;
                JSONArray detalles = t.optJSONArray("enlacesDetalle");
                if (detalles == null) detalles = t.optJSONArray("enlaces");
                if (detalles == null) continue;
                List<String[]> canales = new ArrayList<>();
                for (int j = 0; j < detalles.length(); j++) {
                    JSONObject e = detalles.getJSONObject(j);
                    String url = e.optString("url", "");
                    if (url.isEmpty()) continue;
                    canales.add(new String[]{e.optString("nombre", "Canal"), decodeStreamUrl(url)});
                }
                if (!canales.isEmpty()) out.add(new RawFeed(evento, canales));
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** gol-4 / gol-7 (ftvhd / transmisiones7): { transmisiones: [{ evento|titulo, canales: [{nombre, url}] }] }. */
    private static List<RawFeed> normalizeCanalesUrl(String json) {
        List<RawFeed> out = new ArrayList<>();
        if (json == null) return out;
        try {
            JSONArray arr = new JSONObject(json).optJSONArray("transmisiones");
            if (arr == null) return out;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.getJSONObject(i);
                String evento = t.optString("evento", t.optString("titulo", ""));
                if (evento.isEmpty()) continue;
                JSONArray canalesArr = t.optJSONArray("canales");
                if (canalesArr == null) continue;
                List<String[]> canales = new ArrayList<>();
                for (int j = 0; j < canalesArr.length(); j++) {
                    JSONObject c = canalesArr.getJSONObject(j);
                    String url = c.optString("url", "");
                    if (url.isEmpty()) continue;
                    canales.add(new String[]{c.optString("nombre", "Canal"), decodeStreamUrl(url)});
                }
                if (!canales.isEmpty()) out.add(new RawFeed(evento, canales));
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** gol-5 (donromans): { matches: [{ title, league, links: [{type:'urls_list', data:[{match_url,...}]} | {urls:[...]}] }] }. */
    private static List<RawFeed> normalizeDonromans(String json) {
        List<RawFeed> out = new ArrayList<>();
        if (json == null) return out;
        try {
            JSONArray matches = new JSONObject(json).optJSONArray("matches");
            if (matches == null) return out;
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                String title = match.optString("title", "");
                if (title.isEmpty()) continue;
                JSONArray links = match.optJSONArray("links");
                if (links == null) continue;
                List<String[]> canales = new ArrayList<>();
                for (int j = 0; j < links.length(); j++) {
                    JSONObject linkGroup = links.getJSONObject(j);
                    if ("urls_list".equals(linkGroup.optString("type"))) {
                        JSONArray data = linkGroup.optJSONArray("data");
                        if (data != null) {
                            for (int k = 0; k < data.length(); k++) {
                                JSONObject stream = data.getJSONObject(k);
                                String url = stream.optString("match_url", "");
                                if (url.isEmpty()) continue;
                                String label = stream.optString("stream_source",
                                    stream.optString("platform", "Canal " + (k + 1)));
                                canales.add(new String[]{label, decodeStreamUrl(url)});
                            }
                        }
                    } else {
                        JSONArray urls = linkGroup.optJSONArray("urls");
                        if (urls != null) {
                            for (int k = 0; k < urls.length(); k++) {
                                String url = urls.optString(k, "");
                                if (url.isEmpty()) continue;
                                canales.add(new String[]{"Enlace " + (k + 1), decodeStreamUrl(url)});
                            }
                        }
                    }
                }
                if (!canales.isEmpty()) out.add(new RawFeed(title, canales));
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** gol-6: { transmisiones: [{ evento|titulo, canales:[{nombre,url}] | fuentes:[{fuente,url}] }] }. */
    private static List<RawFeed> normalizeGol6(String json) {
        List<RawFeed> out = new ArrayList<>();
        if (json == null) return out;
        try {
            JSONArray arr = new JSONObject(json).optJSONArray("transmisiones");
            if (arr == null) return out;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.getJSONObject(i);
                String evento = t.optString("evento", t.optString("titulo", ""));
                if (evento.isEmpty()) continue;
                List<String[]> canales = new ArrayList<>();
                JSONArray canalesArr = t.optJSONArray("canales");
                if (canalesArr != null && canalesArr.length() > 0) {
                    for (int j = 0; j < canalesArr.length(); j++) {
                        JSONObject c = canalesArr.getJSONObject(j);
                        String url = c.optString("url", "");
                        if (url.isEmpty()) continue;
                        canales.add(new String[]{c.optString("nombre", "Canal"), decodeStreamUrl(url)});
                    }
                } else {
                    JSONArray fuentes = t.optJSONArray("fuentes");
                    if (fuentes != null) {
                        for (int j = 0; j < fuentes.length(); j++) {
                            JSONObject f = fuentes.getJSONObject(j);
                            String url = f.optString("url", "");
                            if (url.isEmpty()) continue;
                            canales.add(new String[]{f.optString("fuente", "Canal"), decodeStreamUrl(url)});
                        }
                    }
                }
                if (!canales.isEmpty()) out.add(new RawFeed(evento, canales));
            }
        } catch (Exception ignored) {}
        return out;
    }

    /** Attaches every matching feed's channels onto each SportsMatch as "Servidor N". */
    private static void attachServers(Context ctx, List<SportsMatch> matches, List<RawFeed> feeds) {
        for (SportsMatch m : matches) {
            String ref = m.matchTitle();
            int n = 1;
            for (RawFeed feed : feeds) {
                if (!matchesEvento(ctx, ref, feed.evento)) continue;
                for (String[] canal : feed.canales) {
                    boolean hasLabel = canal[0] != null && !canal[0].isEmpty()
                        && !canal[0].equalsIgnoreCase("Canal");
                    String label = "Servidor " + n + (hasLabel ? " · " + canal[0] : "");
                    m.servers.add(new String[]{label, canal[1]});
                    n++;
                }
            }
        }
    }

    /**
     * Loads a league's matches and attaches playable servers to each one.
     * Blocking — call from a background thread.
     */
    public static List<SportsMatch> loadLeague(Context ctx, String leaguePrefix) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<List<SportsMatch>> matchesF = pool.submit(() -> fetchMarcadores(ctx, leaguePrefix));
        Future<List<RawFeed>> feedsF = pool.submit(SportsApi::fetchTransmisiones);

        List<SportsMatch> matches = new ArrayList<>();
        List<RawFeed> feeds = new ArrayList<>();
        try { matches = matchesF.get(20, TimeUnit.SECONDS); } catch (Exception ignored) {}
        try { feeds = feedsF.get(20, TimeUnit.SECONDS); } catch (Exception ignored) {}
        pool.shutdown();

        attachServers(ctx, matches, feeds);
        return matches;
    }

    /** Same as loadLeague but across every league at once (for "Todos los partidos"). Blocking. */
    public static List<SportsMatch> loadAllLeagues(Context ctx) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<List<SportsMatch>> matchesF = pool.submit(() -> fetchAllLeaguesMarcadores(ctx));
        Future<List<RawFeed>> feedsF = pool.submit(SportsApi::fetchTransmisiones);

        List<SportsMatch> matches = new ArrayList<>();
        List<RawFeed> feeds = new ArrayList<>();
        try { matches = matchesF.get(20, TimeUnit.SECONDS); } catch (Exception ignored) {}
        try { feeds = feedsF.get(20, TimeUnit.SECONDS); } catch (Exception ignored) {}
        pool.shutdown();

        attachServers(ctx, matches, feeds);
        return matches;
    }

    // ── Highlights / replays ──────────────────────────────────────────────────

    /** Blocking — call from a background thread. */
    public static List<SportsHighlight> fetchHighlights() {
        List<SportsHighlight> out = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(fetch("/videos"));
            JSONObject categorias = root.optJSONObject("categorias");
            if (categorias == null) return out;
            addHighlights(out, categorias.optJSONArray("mejoresMomentos"), "Mejores momentos");
            addHighlights(out, categorias.optJSONArray("resumenes"), "Resumen");
            addHighlights(out, categorias.optJSONArray("goles"), "Gol");
        } catch (Exception ignored) {}
        return out;
    }

    private static void addHighlights(List<SportsHighlight> out, JSONArray arr, String category) {
        if (arr == null) return;
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject v = arr.getJSONObject(i);
                String url = v.optString("urlEmbed",
                    v.optString("url", v.optString("videoUrl", v.optString("link", ""))));
                if (url.isEmpty()) continue;
                String title = v.optString("titulo", v.optString("title", "Video sin título"));
                String thumb = v.optString("thumbnail", v.optString("imagen", ""));
                out.add(new SportsHighlight(title, thumb, url, category));
            } catch (Exception ignored) {}
        }
    }
}
