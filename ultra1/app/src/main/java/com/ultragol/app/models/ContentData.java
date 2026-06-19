package com.ultragol.app.models;

import android.os.Handler;
import android.os.Looper;
import com.ultragol.app.network.StreamingApi;
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
    private static List<ContentItem> sportsCache;
    private static List<ContentItem> tvCache;

    private static final Handler main = new Handler(Looper.getMainLooper());

    private static void async(ThrowingSupplier supplier, List<ContentItem> fallback,
                               ContentCallback cb, CacheSetter setter) {
        new Thread(() -> {
            try {
                List<ContentItem> items = supplier.get();
                if (items == null || items.isEmpty()) {
                    main.post(() -> cb.onResult(fallback));
                    return;
                }
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

    public static void fetchLiveTV(ContentCallback cb) {
        if (tvCache != null) { cb.onResult(tvCache); return; }
        new Thread(() -> {
            try {
                List<StreamingApi.Channel> channels = StreamingApi.fetchChannels();
                List<ContentItem> list = new ArrayList<>();
                for (StreamingApi.Channel ch : channels) {
                    ContentItem item = new ContentItem(
                        ch.name, ch.category, "24/7", "HD", "", "",
                        ContentItem.TYPE_TV, false, true);
                    item.setStreamUrl(ch.url);
                    list.add(item);
                }
                if (!list.isEmpty()) {
                    tvCache = list;
                    main.post(() -> cb.onResult(list));
                } else {
                    main.post(() -> cb.onResult(staticLiveTV()));
                }
            } catch (Exception e) {
                main.post(() -> cb.onResult(staticLiveTV()));
            }
        }).start();
    }

    public static void fetchSportsChannels(ContentCallback cb) {
        if (sportsCache != null) { cb.onResult(sportsCache); return; }
        new Thread(() -> {
            List<ContentItem> list = buildFootballChannels();
            sportsCache = list;
            main.post(() -> cb.onResult(list));
        }).start();
    }

    private static List<ContentItem> buildFootballChannels() {
        List<ContentItem> list = new ArrayList<>();
        String[][] channels = {
            {"Futbol Canal 1",  "Deportes", "Canal 1",  "HD"},
            {"Futbol Canal 2",  "Deportes", "Canal 2",  "HD"},
            {"Futbol Canal 3",  "Deportes", "Canal 3",  "HD"},
            {"Futbol Canal 4",  "Deportes", "Canal 4",  "HD"},
            {"Futbol Canal 5",  "Deportes", "Canal 5",  "HD"},
            {"Futbol Canal 6",  "Deportes", "Canal 6",  "HD"},
            {"Futbol Canal 7",  "Deportes", "Canal 7",  "HD"},
            {"Futbol Canal 8",  "Deportes", "Canal 8",  "HD"},
            {"Futbol Canal 9",  "Deportes", "Canal 9",  "HD"},
            {"Futbol Canal 10", "Deportes", "Canal 10", "HD"},
        };
        for (int i = 0; i < channels.length; i++) {
            ContentItem item = new ContentItem(
                channels[i][0], channels[i][1], channels[i][2], channels[i][3],
                "", "Transmision de futbol en vivo",
                ContentItem.TYPE_SPORT, false, true);
            item.setStreamUrl(StreamingApi.getFootballStreamUrl(i + 1));
            list.add(item);
        }
        return list;
    }

    public static List<ContentItem> getSports() {
        return buildFootballChannels();
    }

    public static List<ContentItem> getLiveTV() {
        if (tvCache != null) return tvCache;
        return staticLiveTV();
    }

    private static List<ContentItem> staticLiveTV() {
        List<ContentItem> list = new ArrayList<>();
        String[][] channels = {
            {"ESPN",             "Deportes",       "24/7", "HD"},
            {"Fox Sports",       "Deportes",       "24/7", "HD"},
            {"TUDN",             "Futbol",         "24/7", "HD"},
            {"CNN Espanol",      "Noticias",       "24/7", "HD"},
            {"National Geo",     "Naturaleza",     "24/7", "4K"},
            {"Discovery",        "Documentales",   "24/7", "HD"},
            {"HBO",              "Entretenimiento","24/7", "HD"},
            {"Cartoon Network",  "Animacion",      "24/7", "HD"},
            {"BBC World",        "Noticias",       "24/7", "HD"},
            {"MTV",              "Musica",         "24/7", "HD"},
        };
        for (String[] ch : channels) {
            list.add(new ContentItem(ch[0], ch[1], ch[2], ch[3], "", "",
                ContentItem.TYPE_TV, false, true));
        }
        return list;
    }

    private static List<ContentItem> staticMovies() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Dune: Parte Dos",    "Sci-Fi",  "2024", "8.5", "", "La historia epica de Paul Atreides continua.", ContentItem.TYPE_MOVIE, true,  false));
        list.add(new ContentItem("Oppenheimer",        "Drama",   "2023", "8.9", "", "La historia del padre de la bomba atomica.",   ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Alien: Romulus",     "Terror",  "2024", "7.9", "", "Regresa el terror del espacio.",               ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Twisters",           "Accion",  "2024", "7.5", "", "Cazadores de tornados en accion.",             ContentItem.TYPE_MOVIE, false, false));
        return list;
    }

    private static List<ContentItem> staticSeries() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("The Last of Us T2",  "Drama",    "2025", "9.4", "", "Joel y Ellie regresan.",               ContentItem.TYPE_SERIES, true,  false));
        list.add(new ContentItem("House of the Dragon","Fantasia", "2024", "8.8", "", "La historia de los Targaryen.",        ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("Squid Game T3",      "Thriller", "2025", "9.2", "", "El juego mas peligroso regresa.",      ContentItem.TYPE_SERIES, true,  false));
        return list;
    }

    private static List<ContentItem> staticAnime() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Demon Slayer T5",    "Accion",      "2025", "9.1", "", "Tanjiro continua su camino.",             ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Jujutsu Kaisen T3",  "Sobrenatural","2025", "9.3", "", "La batalla contra las maldiciones.",      ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Solo Leveling T2",   "Fantasia",    "2025", "9.0", "", "Sung Jin-Woo regresa.",                   ContentItem.TYPE_ANIME, true, false));
        return list;
    }

    private static List<ContentItem> staticDoramas() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Crash Landing on You","Romance", "2020", "9.2", "", "Un romance entre dos mundos.",            ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Queen of Tears",      "Romance", "2024", "9.3", "", "Drama romantico coreano.",               ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Vincenzo",            "Crimen",  "2021", "9.1", "", "Un abogado mafioso italiano-coreano.",   ContentItem.TYPE_DORAMA, false, false));
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
