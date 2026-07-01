package com.ultragol.app.fragments;

import com.ultragol.app.network.TmdbApi;

public class DoramasFragment extends CineBaseFragment {

    @Override protected String getFragmentTitle()  { return "🎭 Doramas"; }
    @Override protected String getHeroPillLabel()  { return "DORAMA"; }
    @Override protected String getCardTypeLabel()  { return "DORAMA"; }

    @Override
    protected void loadAllSections() {
        int teal    = 0xFF26A69A;
        int pink    = 0xFFE91E63;
        int blue    = 0xFF2196F3;
        int purple  = 0xFF9C27B0;
        int orange  = 0xFFFF6B00;
        int cyan    = 0xFF00BCD4;

        loadHeroSection(
            "EN TENDENCIA", "K-DRAMA DEL MOMENTO", teal,
            TmdbApi::fetchDoramas
        );
        loadCardsSection(
            "ROMANCE", "AMOR COREANO", pink,
            () -> TmdbApi.fetchDoramasByGenre(10749)
        );
        loadCardsSection(
            "ACCIÓN", "DRAMA DE ACCIÓN", blue,
            () -> TmdbApi.fetchDoramasByGenre(28)
        );
        loadCardsSection(
            "DRAMA & EMOCIONES", "DRAMA COREANO", teal,
            () -> TmdbApi.fetchDoramasByGenre(18)
        );
        loadCardsSection(
            "CHINO", "C-DRAMA", purple,
            () -> TmdbApi.fetchDoramasByCountry("CN")
        );
        loadCardsSection(
            "JAPONÉS", "J-DRAMA", orange,
            () -> TmdbApi.fetchDoramasByCountry("JP")
        );
        loadCardsSection(
            "SUSPENSO", "CRIMEN & MISTERIO", cyan,
            () -> TmdbApi.fetchDoramasByGenre(9648)
        );
    }
}
