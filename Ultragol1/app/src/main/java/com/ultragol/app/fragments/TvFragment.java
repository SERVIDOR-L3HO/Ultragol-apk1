package com.ultragol.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ultragol.app.MediaActivity;
import com.ultragol.app.R;
import com.ultragol.app.adapters.TvAdapter;
import com.ultragol.app.models.TvChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TvFragment extends Fragment {

    // ── Categorías del menú ───────────────────────────────────────────────────
    private static final List<String> CATEGORIES = Arrays.asList(
            TvChannel.CAT_TODOS,
            TvChannel.CAT_NOTICIAS,
            TvChannel.CAT_DEPORTES,
            TvChannel.CAT_ENTRETENIMIENTO,
            TvChannel.CAT_MUSICA,
            TvChannel.CAT_DOCUMENTALES,
            TvChannel.CAT_INFANTIL,
            TvChannel.CAT_NEGOCIOS,
            TvChannel.CAT_CIENCIA
    );

    /** Canales de respaldo siempre disponibles — accesibles desde otras clases. */
    public static TvChannel[] getFallbackChannels() { return FALLBACK; }

    /**
     * Caché global de canales cargados — accesible desde SearchActivity.
     * Se puebla progresivamente conforme el fragment carga fuentes M3U.
     */
    private static final List<TvChannel> CHANNEL_CACHE = new ArrayList<>();
    public static List<TvChannel> getCachedChannels() {
        synchronized (CHANNEL_CACHE) { return new ArrayList<>(CHANNEL_CACHE); }
    }

    // ── Canales de respaldo (siempre disponibles, streams conocidos) ──────────
    private static final TvChannel[] FALLBACK = {
        // Noticias internacionales
        ch("DW Español",      "https://dwamdstream104.akamaized.net/hls/live/2015530/dwstream104/index.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/DW_Espa%C3%B1ol.svg/200px-DW_Espa%C3%B1ol.svg.png",
           TvChannel.CAT_NOTICIAS, "DE"),
        ch("France 24 ES",    "https://f24hls-i.akamaihd.net/hls/live/976956/F24_ES_LO_HLS/master.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8b/France_24_logo.svg/200px-France_24_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "FR"),
        ch("France 24 EN",    "https://f24hls-i.akamaihd.net/hls/live/976932/F24_EN_LO_HLS/master.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8b/France_24_logo.svg/200px-France_24_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "FR"),
        ch("Al Jazeera EN",   "https://live-hls-web-aje.getaj.net/AJE/01.m3u8",
           "https://upload.wikimedia.org/wikipedia/en/thumb/f/f2/Aljazeera_eng.svg/200px-Aljazeera_eng.svg.png",
           TvChannel.CAT_NOTICIAS, "QA"),
        ch("DW English",      "https://dwamdstream106.akamaized.net/hls/live/2015530/dwstream106/index.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/DW_logo.svg/200px-DW_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "DE"),
        ch("CGTN Español",    "https://news.cgtn.com/resource/live/spanish/8VXBVBVh.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/CGTN_logo.svg/200px-CGTN_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "CN"),
        ch("CGTN English",    "https://news.cgtn.com/resource/live/english/8VXBVBVh.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/CGTN_logo.svg/200px-CGTN_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "CN"),
        ch("Euronews EN",     "https://rakuten-euronews-1-gb.samsung.wurl.tv/manifest/playlist.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Euronews_2022_logo.svg/200px-Euronews_2022_logo.svg.png",
           TvChannel.CAT_NOTICIAS, "FR"),
        // Ciencia
        ch("NASA TV",         "https://nasa-i.akamaihd.net/hls/live/253565/NASA-NTV1-HLS/master.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/200px-NASA_logo.svg.png",
           TvChannel.CAT_CIENCIA, "US"),
        ch("NASA TV UHD",     "https://nasa-i.akamaihd.net/hls/live/253566/NASA-NTV3-HLS/master.m3u8",
           "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/200px-NASA_logo.svg.png",
           TvChannel.CAT_CIENCIA, "US"),
        // Entretenimiento / Música
        ch("Rakuten Action",  "https://rakuten-rakutenactionmovieschannel-1-us.samsung.wurl.tv/manifest/playlist.m3u8",
           "",
           TvChannel.CAT_ENTRETENIMIENTO, "US"),
        ch("Rakuten Drama",   "https://rakuten-rakutendramas-1-us.samsung.wurl.tv/manifest/playlist.m3u8",
           "",
           TvChannel.CAT_ENTRETENIMIENTO, "US"),
    };

    // ── Fuentes IPTV remotas (iptv-org + otros agregadores) ──────────────────
    private static final String[] M3U_SOURCES = {
        // Por categoría
        "https://iptv-org.github.io/iptv/categories/news.m3u",
        "https://iptv-org.github.io/iptv/categories/sports.m3u",
        "https://iptv-org.github.io/iptv/categories/entertainment.m3u",
        "https://iptv-org.github.io/iptv/categories/music.m3u",
        "https://iptv-org.github.io/iptv/categories/documentary.m3u",
        "https://iptv-org.github.io/iptv/categories/kids.m3u",
        "https://iptv-org.github.io/iptv/categories/general.m3u",
        "https://iptv-org.github.io/iptv/categories/science.m3u",
        "https://iptv-org.github.io/iptv/categories/business.m3u",
        "https://iptv-org.github.io/iptv/categories/travel.m3u",
        "https://iptv-org.github.io/iptv/categories/cooking.m3u",
        "https://iptv-org.github.io/iptv/categories/religious.m3u",
        // Por idioma español + inglés
        "https://iptv-org.github.io/iptv/languages/spa.m3u",
        "https://iptv-org.github.io/iptv/languages/por.m3u",
        // LATAM y España por país
        "https://iptv-org.github.io/iptv/countries/mx.m3u",
        "https://iptv-org.github.io/iptv/countries/ar.m3u",
        "https://iptv-org.github.io/iptv/countries/co.m3u",
        "https://iptv-org.github.io/iptv/countries/es.m3u",
        "https://iptv-org.github.io/iptv/countries/pe.m3u",
        "https://iptv-org.github.io/iptv/countries/ve.m3u",
        "https://iptv-org.github.io/iptv/countries/cl.m3u",
        "https://iptv-org.github.io/iptv/countries/ec.m3u",
        "https://iptv-org.github.io/iptv/countries/bo.m3u",
        "https://iptv-org.github.io/iptv/countries/py.m3u",
        "https://iptv-org.github.io/iptv/countries/uy.m3u",
        "https://iptv-org.github.io/iptv/countries/cr.m3u",
        "https://iptv-org.github.io/iptv/countries/do.m3u",
        "https://iptv-org.github.io/iptv/countries/gt.m3u",
        "https://iptv-org.github.io/iptv/countries/us.m3u",
        "https://iptv-org.github.io/iptv/countries/br.m3u",
        "https://iptv-org.github.io/iptv/countries/fr.m3u",
        "https://iptv-org.github.io/iptv/countries/de.m3u",
        "https://iptv-org.github.io/iptv/countries/it.m3u",
        "https://iptv-org.github.io/iptv/countries/gb.m3u",
        "https://iptv-org.github.io/iptv/countries/pt.m3u",
    };

    private static final int MAX_PER_SOURCE = 50; // límite por fuente

    // ── Estado ────────────────────────────────────────────────────────────────
    private TvAdapter adapter;
    private String selectedCategory = TvChannel.CAT_TODOS;
    private final List<TvChannel> allChannels = new ArrayList<>();
    // executor se crea en onViewCreated y se destruye en onDestroyView para
    // soportar correctamente la recreación de la vista (back stack, rotaciones).
    private ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Crear executor fresco en cada recreación de la vista
        executor = Executors.newFixedThreadPool(8);
        // Limpiar canales previos para evitar duplicados en rotaciones / back-stack
        synchronized (allChannels) { allChannels.clear(); }

        RecyclerView rv = view.findViewById(R.id.rvTvChannels);
        adapter = new TvAdapter(requireContext(), CATEGORIES);

        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(llm);

        // Divider sutil entre filas
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        if (requireContext().getDrawable(com.ultragol.app.R.drawable.tv_row_divider) != null) {
            divider.setDrawable(requireContext().getDrawable(com.ultragol.app.R.drawable.tv_row_divider));
        }
        rv.addItemDecoration(divider);
        rv.setAdapter(adapter);

        adapter.setOnChannelClickListener(this::playChannel);
        adapter.setOnCategoryClickListener(cat -> {
            selectedCategory = cat;
            adapter.setSelectedCategory(cat);
            applyFilter();
        });

        // Mostrar canales de respaldo inmediatamente + poblar caché global
        synchronized (allChannels) {
            for (TvChannel ch : FALLBACK) allChannels.add(ch);
        }
        synchronized (CHANNEL_CACHE) {
            if (CHANNEL_CACHE.isEmpty()) {
                for (TvChannel ch : FALLBACK) CHANNEL_CACHE.add(ch);
            }
        }
        applyFilter();

        // Cargar fuentes IPTV en background
        loadRemoteChannels();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        mainHandler.removeCallbacksAndMessages(null);
    }

    // ── Reproducción ──────────────────────────────────────────────────────────

    private void playChannel(TvChannel ch) {
        Intent intent = new Intent(requireContext(), MediaActivity.class);
        intent.putExtra("url",      ch.url);
        intent.putExtra("title",    ch.name);
        intent.putExtra("is_m3u8",  ch.url.contains(".m3u8") || ch.url.contains("m3u"));
        intent.putExtra("referer",  "");
        startActivity(intent);
    }

    // ── Filtrado ──────────────────────────────────────────────────────────────

    private void applyFilter() {
        List<TvChannel> filtered = new ArrayList<>();
        synchronized (allChannels) {
            for (TvChannel ch : allChannels) {
                if (ch.isAlive &&
                    (selectedCategory.equals(TvChannel.CAT_TODOS) ||
                     ch.category.equals(selectedCategory))) {
                    filtered.add(ch);
                }
            }
        }
        adapter.setChannels(filtered);
    }

    // ── Carga remota de M3U ───────────────────────────────────────────────────

    private void loadRemoteChannels() {
        for (String src : M3U_SOURCES) {
            executor.execute(() -> {
                List<TvChannel> parsed = fetchAndParse(src, MAX_PER_SOURCE);
                if (parsed.isEmpty()) return;

                // Verificar cada canal en paralelo
                List<TvChannel> verified = new ArrayList<>();
                List<Runnable> checks = new ArrayList<>();
                Object lock = new Object();

                for (TvChannel ch : parsed) {
                    checks.add(() -> {
                        if (checkStream(ch.url)) {
                            synchronized (lock) { verified.add(ch); }
                        }
                    });
                }

                // Ejecutar verificaciones concurrentes
                List<Thread> threads = new ArrayList<>();
                for (Runnable r : checks) {
                    Thread t = new Thread(r);
                    t.start();
                    threads.add(t);
                }
                for (Thread t : threads) {
                    try { t.join(6000); } catch (InterruptedException ignored) {}
                }

                if (!verified.isEmpty() && isAdded()) {
                    // Deduplicar por URL
                    List<TvChannel> newChannels = new ArrayList<>();
                    synchronized (allChannels) {
                        LinkedHashSet<String> existingUrls = new LinkedHashSet<>();
                        for (TvChannel c : allChannels) existingUrls.add(c.url);
                        for (TvChannel c : verified) {
                            if (!existingUrls.contains(c.url)) {
                                existingUrls.add(c.url);
                                allChannels.add(c);
                                newChannels.add(c);
                            }
                        }
                    }
                    if (!newChannels.isEmpty()) {
                        // Añadir también al caché global para SearchActivity
                        synchronized (CHANNEL_CACHE) {
                            LinkedHashSet<String> cacheUrls = new LinkedHashSet<>();
                            for (TvChannel c : CHANNEL_CACHE) cacheUrls.add(c.url);
                            for (TvChannel c : newChannels) {
                                if (!cacheUrls.contains(c.url)) CHANNEL_CACHE.add(c);
                            }
                        }
                        mainHandler.post(this::applyFilter);
                    }
                }
            });
        }
    }

    // ── Parser de M3U ─────────────────────────────────────────────────────────

    private List<TvChannel> fetchAndParse(String src, int limit) {
        List<TvChannel> result = new ArrayList<>();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(src).openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(20_000);
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Android; Mobile) AppleWebKit/537.36");
            conn.connect();
            if (conn.getResponseCode() != 200) { conn.disconnect(); return result; }

            BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"), 16384);

            String name = null, logo = null, group = null, country = null;
            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < limit) {
                line = line.trim();
                if (line.startsWith("#EXTINF")) {
                    name    = extractAttr(line, "tvg-name",  extractAfterComma(line));
                    logo    = extractAttr(line, "tvg-logo",  "");
                    group   = extractAttr(line, "group-title","");
                    country = extractAttr(line, "tvg-country","");
                    if (country.isEmpty()) country = extractAttr(line, "tvg-id", "").replaceAll("[^A-Z]", "");
                } else if (!line.startsWith("#") && !line.isEmpty() && name != null) {
                    if (line.startsWith("http")) {
                        String cat = TvChannel.mapGroup(group);
                        TvChannel ch = new TvChannel(name, line, logo, cat,
                            country.length() >= 2 ? country.substring(0,2).toUpperCase() : "");
                        result.add(ch);
                        count++;
                    }
                    name = null; logo = null; group = null; country = null;
                }
            }
            br.close();
            conn.disconnect();
        } catch (Exception ignored) {}
        return result;
    }

    private String extractAttr(String line, String attr, String def) {
        String search = attr + "=\"";
        int start = line.indexOf(search);
        if (start < 0) return def;
        start += search.length();
        int end = line.indexOf('"', start);
        return end > start ? line.substring(start, end) : def;
    }

    private String extractAfterComma(String line) {
        int idx = line.lastIndexOf(',');
        return idx >= 0 && idx < line.length() - 1 ? line.substring(idx + 1).trim() : "";
    }

    // ── Verificación de stream ─────────────────────────────────────────────────

    /**
     * Intenta conectar al stream y leer los primeros bytes del manifiesto M3U8.
     * Retorna true si la respuesta contiene "#EXT" (manifiesto HLS válido) o
     * si el servidor responde con 200/302 (puede ser TS directo).
     */
    private boolean checkStream(String streamUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(streamUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Android; Mobile) AppleWebKit/537.36");
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                // Leer primeros 512 bytes para validar que es M3U8 real
                byte[] buf = new byte[512];
                int read = conn.getInputStream().read(buf);
                conn.disconnect();
                if (read > 0) {
                    String header = new String(buf, 0, read, "UTF-8");
                    return header.contains("#EXT") || header.contains("http");
                }
                return false;
            }
            conn.disconnect();
            return code == 302 || code == 301;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static TvChannel ch(String n, String u, String l, String c, String co) {
        return new TvChannel(n, u, l, c, co);
    }
}
