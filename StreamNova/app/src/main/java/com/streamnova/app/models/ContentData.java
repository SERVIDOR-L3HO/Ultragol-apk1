package com.ultragol.app.models;

import java.util.ArrayList;
import java.util.List;

public class ContentData {

    public static List<ContentItem> getMovies() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Dune: Parte Dos", "Sci-Fi", "2024", "8.5", "🪐", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("Oppenheimer", "Drama", "2023", "8.9", "💥", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Avatar 3", "Aventura", "2025", "8.2", "🌿", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("The Batman 2", "Acción", "2025", "8.7", "🦇", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("Interstellar 2", "Sci-Fi", "2025", "9.1", "🌌", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("El Padrino IV", "Crimen", "2024", "8.4", "🌹", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Spider-Man: Nexo", "Acción", "2025", "8.8", "🕷️", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("Blade Runner 3", "Sci-Fi", "2024", "8.3", "🤖", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Mad Max: Wasteland", "Acción", "2025", "8.6", "🏜️", ContentItem.TYPE_MOVIE, true, false));
        list.add(new ContentItem("Joker: Folie à Deux", "Drama", "2024", "7.8", "🃏", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Alien: Romulus", "Terror", "2024", "7.9", "👾", ContentItem.TYPE_MOVIE, false, false));
        list.add(new ContentItem("Twisters", "Acción", "2024", "7.5", "🌪️", ContentItem.TYPE_MOVIE, false, false));
        return list;
    }

    public static List<ContentItem> getSeries() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("The Last of Us T2", "Drama", "2025", "9.4", "🍄", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("House of the Dragon", "Fantasía", "2024", "8.8", "🐉", ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("Squid Game T3", "Thriller", "2025", "9.2", "🟩", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Stranger Things 5", "Terror", "2025", "9.0", "🔦", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Breaking Bad: Origins", "Drama", "2024", "9.5", "⚗️", ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("The Witcher T4", "Fantasía", "2025", "8.1", "⚔️", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Black Mirror T7", "Sci-Fi", "2025", "8.7", "📱", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Wednesday T2", "Comedia", "2024", "8.4", "🖤", ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("Peaky Blinders: Film", "Crimen", "2024", "8.9", "🎩", ContentItem.TYPE_SERIES, false, false));
        list.add(new ContentItem("Dark T4", "Misterio", "2025", "9.3", "🌀", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Narcos: México T4", "Crimen", "2025", "8.8", "💊", ContentItem.TYPE_SERIES, true, false));
        list.add(new ContentItem("Succession T5", "Drama", "2024", "9.1", "💼", ContentItem.TYPE_SERIES, false, false));
        return list;
    }

    public static List<ContentItem> getAnime() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Demon Slayer T5", "Acción", "2025", "9.1", "🗡️", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Jujutsu Kaisen T3", "Sobrenatural", "2025", "9.3", "💜", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("One Piece: Egghead", "Aventura", "2024", "8.8", "⚓", ContentItem.TYPE_ANIME, false, false));
        list.add(new ContentItem("Attack on Titan: Final", "Drama", "2023", "9.7", "⚡", ContentItem.TYPE_ANIME, false, false));
        list.add(new ContentItem("Solo Leveling T2", "Fantasía", "2025", "9.0", "💎", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Vinland Saga T3", "Histórico", "2025", "9.2", "⚔️", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Blue Lock T2", "Deporte", "2024", "8.6", "🔷", ContentItem.TYPE_ANIME, false, false));
        list.add(new ContentItem("Chainsaw Man T2", "Horror", "2025", "8.9", "🪚", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Spy x Family T3", "Comedia", "2025", "8.7", "🕵️", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Frieren: El Viaje", "Fantasía", "2024", "9.4", "✨", ContentItem.TYPE_ANIME, false, false));
        list.add(new ContentItem("Hunter x Hunter Manga", "Aventura", "2025", "9.4", "🎯", ContentItem.TYPE_ANIME, true, false));
        list.add(new ContentItem("Haikyuu: Final", "Deporte", "2024", "9.0", "🏐", ContentItem.TYPE_ANIME, false, false));
        return list;
    }

    public static List<ContentItem> getDoramas() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Crash Landing on You", "Romance", "2020", "9.2", "🪂", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Goblin", "Romance", "2016", "9.0", "🌸", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Vincenzo", "Crimen", "2021", "9.1", "🌺", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Hotel Del Luna", "Fantasía", "2019", "8.7", "🌙", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Business Proposal", "Comedia", "2022", "8.6", "💼", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Queen of Tears", "Romance", "2024", "9.3", "💎", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Lovely Runner", "Romance", "2024", "9.1", "🏃", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Alchemy of Souls T3", "Histórico", "2025", "9.0", "🔮", ContentItem.TYPE_DORAMA, true, false));
        list.add(new ContentItem("My Love From the Star", "Romance", "2013", "8.8", "⭐", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Start-Up", "Romance", "2020", "8.5", "🚀", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Signal", "Thriller", "2016", "9.1", "📻", ContentItem.TYPE_DORAMA, false, false));
        list.add(new ContentItem("Itaewon Class", "Drama", "2020", "8.7", "🍷", ContentItem.TYPE_DORAMA, false, false));
        return list;
    }

    public static List<ContentItem> getSports() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("Real Madrid vs Barcelona", "La Liga", "HOY 20:00", "9.9", "⚽", ContentItem.TYPE_SPORT, false, true));
        list.add(new ContentItem("Copa del Mundo 2026", "FIFA", "Jun 2026", "9.8", "🏆", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("Champions League Final", "UEFA", "May 2025", "9.7", "🌟", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("NBA Finals 2025", "Baloncesto", "Jun 2025", "9.5", "🏀", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("F1: GP Mónaco 2025", "Fórmula 1", "May 2025", "9.2", "🏎️", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Copa América 2025", "Fútbol", "2025", "9.4", "🦁", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("Boxeo: Canelo vs GGG IV", "Boxeo", "2025", "9.3", "🥊", ContentItem.TYPE_SPORT, true, false));
        list.add(new ContentItem("UFC 310: Main Event", "MMA", "Dic 2024", "9.1", "🥊", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Wimbledon 2025", "Tenis", "Jul 2025", "8.8", "🎾", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Liga MX: Gran Final", "Fútbol", "May 2025", "8.9", "🦅", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Tour de France 2025", "Ciclismo", "Jul 2025", "8.5", "🚴", ContentItem.TYPE_SPORT, false, false));
        list.add(new ContentItem("Paris 2024: Highlights", "Olimpiadas", "2024", "9.0", "🥇", ContentItem.TYPE_SPORT, false, false));
        return list;
    }

    public static List<ContentItem> getLiveTV() {
        List<ContentItem> list = new ArrayList<>();
        list.add(new ContentItem("ESPN en Vivo", "Deportes", "24/7", "HD", "📺", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Fox Sports Live", "Deportes", "24/7", "HD", "🦊", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("TUDN en Directo", "Fútbol", "24/7", "HD", "⚽", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("CNN en Español", "Noticias", "24/7", "HD", "📰", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("National Geographic", "Naturaleza", "24/7", "4K", "🌍", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Discovery Channel", "Documentales", "24/7", "HD", "🔭", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("HBO Max Live", "Entretenimiento", "24/7", "HD", "🎭", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Cartoon Network", "Animación", "24/7", "HD", "🎨", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("BBC World News", "Noticias", "24/7", "HD", "🌐", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("MTV Latinoamérica", "Música", "24/7", "HD", "🎵", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("History Channel", "Historia", "24/7", "HD", "🏛️", ContentItem.TYPE_TV, false, true));
        list.add(new ContentItem("Animal Planet", "Naturaleza", "24/7", "HD", "🦁", ContentItem.TYPE_TV, false, true));
        return list;
    }

    public static List<ContentItem> getTrending() {
        List<ContentItem> items = new ArrayList<>();
        items.add(getSports().get(0));
        items.add(getMovies().get(0));
        items.add(getSeries().get(0));
        items.add(getAnime().get(0));
        items.add(getSports().get(1));
        items.add(getDoramas().get(0));
        items.add(getLiveTV().get(0));
        items.add(getMovies().get(1));
        return items;
    }

    public static List<ContentItem> getNewReleases() {
        List<ContentItem> items = new ArrayList<>();
        for (ContentItem item : getSports()) if (item.isNew() || item.isLive()) items.add(item);
        for (ContentItem item : getMovies()) if (item.isNew()) items.add(item);
        for (ContentItem item : getSeries()) if (item.isNew()) items.add(item);
        for (ContentItem item : getAnime()) if (item.isNew()) items.add(item);
        return items;
    }

    public static List<ContentItem> getHeroBanners() {
        List<ContentItem> items = new ArrayList<>();
        items.add(new ContentItem("Real Madrid vs Barcelona", "El Clásico más esperado del año en La Liga española", "HOY 20:00", "9.9", "⚽", ContentItem.TYPE_SPORT, false, true));
        items.add(new ContentItem("Copa del Mundo 2026", "El torneo más grande del fútbol mundial en México, EEUU y Canadá", "2026", "9.8", "🏆", ContentItem.TYPE_SPORT, true, false));
        items.add(new ContentItem("The Last of Us T2", "Joel y Ellie regresan en una aventura devastadora", "2025", "9.4", "🍄", ContentItem.TYPE_SERIES, true, false));
        items.add(new ContentItem("Champions League Final", "La gran final de la UEFA en Wembley", "May 2025", "9.7", "🌟", ContentItem.TYPE_SPORT, false, false));
        items.add(new ContentItem("Solo Leveling T2", "Sung Jin-Woo regresa más poderoso que nunca", "2025", "9.0", "💎", ContentItem.TYPE_ANIME, true, false));
        return items;
    }
}
