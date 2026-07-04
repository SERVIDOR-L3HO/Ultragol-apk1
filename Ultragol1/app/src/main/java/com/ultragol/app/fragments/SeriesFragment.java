package com.ultragol.app.fragments;

import com.ultragol.app.ProfileManager;
import com.ultragol.app.network.TmdbApi;

public class SeriesFragment extends CineBaseFragment {

    @Override protected String getFragmentTitle()  { return "📺 Series"; }
    @Override protected String getHeroPillLabel()  { return "SERIE"; }
    @Override protected String getCardTypeLabel()  { return "SERIE"; }

    @Override
    protected void loadAllSections() {
        // Detect kids profile
        boolean isKids = false;
        try {
            ProfileManager.Profile prof = ProfileManager.getCurrentProfile(requireContext());
            if (prof != null) isKids = prof.isKids;
        } catch (Exception ignored) {}

        if (isKids) {
            loadKidsSections();
            return;
        }

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

    private void loadKidsSections() {
        int orange  = 0xFFFF6B00;
        int yellow  = 0xFFFFD700;
        int teal    = 0xFF00BCD4;
        int green   = 0xFF4CAF50;
        int purple  = 0xFF9C27B0;

        loadHeroSection(
            "PARA NIÑOS", "SERIES INFANTILES", orange,
            TmdbApi::fetchKidsSeries
        );
        loadCardsSection(
            "DIBUJOS Y ANIMACIÓN", "SERIES ANIMADAS", yellow,
            TmdbApi::fetchKidsAnimationSeries
        );
        loadCardsSection(
            "DIVERSIÓN FAMILIAR", "PARA TODA LA FAMILIA", teal,
            () -> TmdbApi.fetchSeriesByGenre(10751)
        );
        loadCardsSection(
            "AVENTURAS", "ACCIÓN PARA NIÑOS", green,
            () -> TmdbApi.fetchSeriesByGenre(10762)
        );
        loadCardsSection(
            "COMEDIA", "SERIES DIVERTIDAS", purple,
            () -> TmdbApi.fetchSeriesByGenre(35)
        );
    }
}
