package com.ultragol.app.fragments;

import com.ultragol.app.ProfileManager;
import com.ultragol.app.network.A7xIptvApi;
import com.ultragol.app.network.TmdbApi;

/**
 * Pantalla de series — usa A7X TV IPTV como fuente principal.
 * Si A7X falla, cae a TMDB como respaldo.
 * En perfil infantil carga sólo series aptas para niños.
 */
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
        } else {
            loadNormalSections();
        }
    }

    private void loadKidsSections() {
        int teal   = 0xFF009688;
        int yellow = 0xFFFFBB00;
        int pink   = 0xFFE91E63;
        int green  = 0xFF4CAF50;

        loadHeroSection(
            "PARA NIÑOS", "SERIES ANIMADAS", teal,
            () -> TmdbApi.fetchKidsAnimationSeries()
        );
        loadCardsSection(
            "FAMILIA", "SERIES FAMILIARES", yellow,
            () -> TmdbApi.fetchKidsSeries()
        );
        loadCardsSection(
            "ARTE EN MOVIMIENTO", "ANIMACIÓN INFANTIL", pink,
            () -> TmdbApi.fetchSeriesByGenre(16)
        );
        loadCardsSection(
            "AVENTURA", "PARA TODA LA FAMILIA", green,
            () -> TmdbApi.fetchSeriesByGenre(10751)
        );
    }

    private void loadNormalSections() {
        int cyan   = 0xFF00BCD4;
        int green  = 0xFF4CAF50;
        int pink   = 0xFFE91E63;
        int yellow = 0xFFFFBB00;
        int blue   = 0xFF2196F3;
        int purple = 0xFF9C27B0;
        int orange = 0xFFFF6B00;
        int teal   = 0xFF009688;

        // Fuente principal: A7X IPTV
        loadHeroSection(
            "EN TENDENCIA", "SERIES DEL MOMENTO", cyan,
            () -> {
                try {
                    java.util.List<com.ultragol.app.models.ContentItem> a7xSeries =
                        A7xIptvApi.loadSeries(requireContext());
                    if (!a7xSeries.isEmpty()) return a7xSeries;
                } catch (Exception ignored) {}
                // Respaldo TMDB
                return TmdbApi.fetchSeries();
            }
        );

        loadCardsSection(
            "VOD A7X", "SERIES POPULARES", green,
            () -> {
                try {
                    java.util.List<com.ultragol.app.models.ContentItem> result =
                        A7xIptvApi.loadSeries(requireContext());
                    if (result.size() > 10) return result.subList(10, Math.min(30, result.size()));
                    if (!result.isEmpty()) return result;
                } catch (Exception ignored) {}
                return TmdbApi.fetchSeriesSpanish();
            }
        );

        loadCardsSection(
            "TOP GLOBAL", "MEJOR CALIFICADAS", yellow,
            () -> {
                try {
                    java.util.List<com.ultragol.app.models.ContentItem> result =
                        A7xIptvApi.loadSeries(requireContext());
                    if (result.size() > 30) return result.subList(30, Math.min(50, result.size()));
                } catch (Exception ignored) {}
                return TmdbApi.fetchTopSeries();
            }
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
