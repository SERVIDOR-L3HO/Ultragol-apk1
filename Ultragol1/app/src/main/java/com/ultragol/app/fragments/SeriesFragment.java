package com.ultragol.app.fragments;

import com.ultragol.app.network.TmdbApi;

public class SeriesFragment extends CineBaseFragment {

    @Override protected String getFragmentTitle()  { return "📺 Series"; }
    @Override protected String getHeroPillLabel()  { return "SERIE"; }
    @Override protected String getCardTypeLabel()  { return "SERIE"; }

    @Override
    protected void loadAllSections() {
        int cyan    = 0xFF00BCD4;
        int green   = 0xFF4CAF50;
        int pink    = 0xFFE91E63;
        int yellow  = 0xFFFFBB00;
        int blue    = 0xFF2196F3;
        int purple  = 0xFF9C27B0;
        int orange  = 0xFFFF6B00;
        int teal    = 0xFF009688;

        loadHeroSection(
            "EN TENDENCIA", "SERIES DEL MOMENTO", cyan,
            TmdbApi::fetchSeries
        );
        loadCardsSection(
            "PRODUCCIÓN LATINA", "SERIES EN ESPAÑOL", green,
            TmdbApi::fetchSeriesSpanish
        );
        loadCardsSection(
            "TOP GLOBAL", "MEJOR CALIFICADAS", yellow,
            TmdbApi::fetchTopSeries
        );
        loadCardsSection(
            "EMOCIONES", "DRAMA INTERNACIONAL", pink,
            () -> TmdbApi.fetchSeriesByGenre(18)
        );
        loadCardsSection(
            "ENTRETENIMIENTO", "COMEDIA", orange,
            () -> TmdbApi.fetchSeriesByGenre(35)
        );
        loadCardsSection(
            "MISTERIO", "CRIMEN & POLICÍACA", blue,
            () -> TmdbApi.fetchSeriesByGenre(80)
        );
        loadCardsSection(
            "FUTURO", "CIENCIA FICCIÓN", purple,
            () -> TmdbApi.fetchSeriesByGenre(878)
        );
        loadCardsSection(
            "ARTE EN MOVIMIENTO", "ANIMACIÓN", teal,
            () -> TmdbApi.fetchSeriesByGenre(16)
        );
    }
}
