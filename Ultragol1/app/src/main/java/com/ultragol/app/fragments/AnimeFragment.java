package com.ultragol.app.fragments;

import com.ultragol.app.network.TmdbApi;

public class AnimeFragment extends CineBaseFragment {

    @Override protected String getFragmentTitle()  { return "🎌 Anime"; }
    @Override protected String getHeroPillLabel()  { return "ANIME"; }
    @Override protected String getCardTypeLabel()  { return "ANIME"; }

    @Override
    protected void loadAllSections() {
        int pink    = 0xFFE91E63;
        int orange  = 0xFFFF6B00;
        int blue    = 0xFF2196F3;
        int purple  = 0xFF9C27B0;
        int red     = 0xFFF44336;
        int teal    = 0xFF009688;

        loadHeroSection(
            "EN TENDENCIA", "ANIME DEL MOMENTO", pink,
            TmdbApi::fetchAnime
        );
        loadCardsSection(
            "TOP GLOBAL", "MEJOR CALIFICADOS", orange,
            TmdbApi::fetchTopAnime
        );
        loadCardsSection(
            "ACCIÓN & AVENTURA", "SHONEN", red,
            () -> TmdbApi.fetchAnimeByGenre(10759)
        );
        loadCardsSection(
            "ROMANCE", "SHOUJO & AMOR", pink,
            () -> TmdbApi.fetchAnimeByGenre(10749)
        );
        loadCardsSection(
            "FANTASÍA & MAGIA", "ISEKAI", purple,
            () -> TmdbApi.fetchAnimeByGenre(10765)
        );
        loadCardsSection(
            "MISTERIO & OSCURO", "THRILLER ANIME", blue,
            () -> TmdbApi.fetchAnimeByGenre(9648)
        );
    }
}
