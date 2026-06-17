package com.ultragol.app.models;

import android.os.Handler;
import android.os.Looper;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;

public class ContentData {

    public interface ContentCallback {
        void onResult(List<ContentItem> items);
    }

    private static List<ContentItem> movieCache;
    private static List<ContentItem> seriesCache;
    private static List<ContentItem> animeCache;
    private static List<ContentItem> doramaCache;
    private static List<ContentItem> trendingCache;

    private static final Handler main = new Handler(Looper.getMainLooper());

    private static void async(ThrowingSupplier supplier, List<ContentItem> fallback,
                               ContentCallback cb, CacheSetter setter) {
        new Thread(() -> {
            try {
                List<ContentItem> items = supplier.get();
                setter.set(items);
                main.post(() -> cb.onResult(items));
            } catch (Exception e) {
                main.post(() -> cb.onResult(fallback));
            }
        }).start();
    }

    interface ThrowingSupplier { List<ContentItem> get() throws Exception; }
    interface CacheSetter { void set(List<ContentItem> items); }

    public static void fetchMovies(ContentCallback cb) {
        if (movieCache != null) { cb.onResult(movieCache); return; }
        async(TmdbApi::fetchMovies, staticMovies(), cb, items -> movieCache = items);
    }

    public static void fetchSeries(ContentCallback cb) {
        if (seriesCache != null) { cb.onResult(seriesCache); return; }
        async(TmdbApi::fetchSeries, staticSeries(), cb, items -> seriesCache = items);
    }

    public static void fetchAnime(ContentCallback cb) {
        if (animeCache != null) { cb.onResult(animeCache); return; }
        async(TmdbApi::fetchAnime, staticAnime(), cb, items -> animeCache = items);
    }

    public static void fetchDoramas(ContentCallback cb) {
        if (doramaCache != null) { cb.onResult(doramaCache); return; }
        async(TmdbApi::fetchDoramas, staticDoramas(), cb, items -> doramaCache = items);
    }

    public static void fetchTrending(ContentCallback cb) {
        if (trendingCache != null) { cb.onResult(trendingCache); return; }
        async(TmdbApi::fetchTrending, staticTrending(), cb, items -> trendingCache = items);
    }

    public static void fetchBanners(ContentCallback cb) {
        fetchTrending(items -> {
            List<ContentItem> banners = items.size() > 5 ? items.subList(0, 5) : items;
            cb.onResult(new ArrayList<>(banners));
        });
    }

    public static void fetchNewReleases(ContentCallback cb) {
        fetchTrending(items -> {
            List<ContentItem> newItems = new ArrayList<>();
            for (ContentItem item : items) {
                if (item.isNew()) newItems.add(item);
            }
            if (newItems.isEmpty()) newItems.addAll(items.subList(0, Math.min(6, items.size())));
            cb.onResult(newItems);
        });
    }

    public static List<ContentItem> getSports() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Real Madrid vs Barcelona", "La Liga", "HOY 20:00", "9.9", "", "El Clasico de La Liga espanola.", ContentItem.TYPE_SPORT, false, true));
        list.add(new ContentItem("Copa del Mundo 2026", "FIFA", "Jun 2026", "9.8", "", "El torneo mas grande del futbol mundial.", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("Champions League Final", "UEFA", "May 2025", "9.7", "", "La gran final de la UEFA.", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("NBA Finals 2025", "Baloncesto", "Jun 2025", "9.5", "", "La final de la NBA.", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("F1: GP Monaco 2025", "Formula 1", "May 2025", "9.2", "", "Gran Premio de Monaco.", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Copa America 2025", "Futbol", "2025", "9.4", "", "Copa America edicion 2025.", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("Canelo vs GGG IV", "Boxeo", "2025", "9.3", "", "Revancha de campeonato mundial.", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("UFC 310: Main Event", "MMA", "Dic 2024", "9.1", "", "Cartelera estelar de la UFC.", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Wimbledon 2025", "Tenis", "Jul 2025", "8.8", "", "El torneo de Grand Slam sobre cesped.", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Liga MX: Gran Final", "Futbol", "May 2025", "8.9", "", "La gran final del futbol mexicano.", ContentItem.TYPE_SPORT, false, false));
        return list;
    }

    public static List<ContentItem> getLiveTV() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("ESPN en Vivo", "Deportes", "24/7", "HD", "", "Canal deportivo internacional.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Fox Sports Live", "Deportes", "24/7", "HD", "", "Cobertura deportiva en vivo.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("TUDN en Directo", "Futbol", "24/7", "HD", "", "Tu canal de futbol.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("CNN en Espanol", "Noticias", "24/7", "HD", "", "Noticias internacionales.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("National Geographic", "Naturaleza", "24/7", "4K", "", "Documentales y naturaleza.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Discovery Channel", "Documentales", "24/7", "HD", "", "Ciencia y exploracion.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("HBO Max Live", "Entretenimiento", "24/7", "HD", "", "Series y peliculas premium.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Cartoon Network", "Animacion", "24/7", "HD", "", "Animacion para toda la familia.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("BBC World News", "Noticias", "24/7", "HD", "", "Noticias mundiales.", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("MTV Latinoamerica", "Musica", "24/7", "HD", "", "Musica y entretenimiento.", ContentItem.TYPE_TV, false, true));
        return list;
    }

    private static List<ContentItem> staticMovies() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Dune: Parte Dos", "Sci-Fi", "2024", "8.5", "", "La historia epica de Paul Atreides continua.", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("Oppenheimer", "Drama", "2023", "8.9", "", "La historia del padre de la bomba atomica.", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Alien: Romulus", "Terror", "2024", "7.9", "", "Regresa el terror del espacio.", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Twisters", "Accion", "2024", "7.5", "", "Cazadores de tornados en accion.", ContentItem.TYPE_MOVIE, false, false));
        return list;
    }

    private static List<ContentItem> staticSeries() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("The Last of Us T2", "Drama", "2025", "9.4", "", "Joel y Ellie regresan.", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("House of the Dragon", "Fantasia", "2024", "8.8", "", "La historia de los Targaryen.", ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("Squid Game T3", "Thriller", "2025", "9.2", "", "El juego mas peligroso regresa.", ContentItem.TYPE_SERIES, true, false));
        return list;
    }

    private static List<ContentItem> staticAnime() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Demon Slayer T5", "Accion", "2025", "9.1", "", "Tanjiro continua su camino.", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Jujutsu Kaisen T3", "Sobrenatural", "2025", "9.3", "", "La batalla contra las maldiciones.", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Solo Leveling T2", "Fantasia", "2025", "9.0", "", "Sung Jin-Woo regresa.", ContentItem.TYPE_ANIME, true, false));
        return list;
    }

    private static List<ContentItem> staticDoramas() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Crash Landing on You", "Romance", "2020", "9.2", "", "Un romance entre dos mundos.", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Queen of Tears", "Romance", "2024", "9.3", "", "Drama romantico coreano.", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Vincenzo", "Crimen", "2021", "9.1", "", "Un abogado mafioso italiano-coreano.", ContentItem.TYPE_DORAMA, false, false));
        return list;
    }

    private static List<ContentItem> staticTrending() {
        List<ContentItem> all = new ArrayList<>();
        all.addAll(staticMovies());
        all.addAll(staticSeries());
        all.addAll(staticAnime());
        return all;
    }
}
