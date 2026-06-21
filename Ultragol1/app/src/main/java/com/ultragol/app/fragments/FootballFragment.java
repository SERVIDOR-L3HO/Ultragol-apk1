package com.ultragol.app.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class FootballFragment extends Fragment {

    private static final String API = "https://ultragol-api-3--maricarmen43549.replit.app";

    // {emoji, label, apiPrefix}
    // apiPrefix "" = Liga MX (root paths), else "premier/", "laliga/", etc.
    private static final String[][] LEAGUES = {
        {"⚡", "EN VIVO",    "__live__"},
        {"🇲🇽", "Liga MX",   ""},
        {"🏴", "Premier",    "premier/"},
        {"🇪🇸", "La Liga",   "laliga/"},
        {"🇮🇹", "Serie A",   "seriea/"},
        {"🇩🇪", "Bundesliga","bundesliga/"},
        {"🇫🇷", "Ligue 1",   "ligue1/"},
        {"🌍", "Todas",      "__todas__"},
    };

    private static final String[][] SECTIONS = {
        {"Marcadores","marcadores"},
        {"Tabla",     "tabla"},
        {"Goleadores","goleadores"},
        {"Calendario","calendario"},
        {"Alineaciones","alineaciones"},
        {"Highlights","mejores-momentos"},
        {"Noticias",  "noticias"},
    };

    private static final String[][] LIGAMX_SECTIONS = {
        {"Marcadores",  "marcadores"},
        {"Tabla",       "tabla"},
        {"Goleadores",  "goleadores"},
        {"Calendario",  "calendario"},
        {"Alineaciones","alineaciones"},
        {"Equipos",     "equipos"},
        {"Highlights",  "videos"},
        {"Noticias",    "noticias"},
    };

    private int selLeague = 0;
    private int selSection = 0;

    private LinearLayout leagueTabBar;
    private LinearLayout sectionTabBar;
    private View sectionTabScroll;
    private LinearLayout contentContainer;
    private ProgressBar progressBar;
    private TextView errorView;
    private ScrollView contentScroll;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_football, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        leagueTabBar    = view.findViewById(R.id.leagueTabBar);
        sectionTabBar   = view.findViewById(R.id.sectionTabBar);
        sectionTabScroll= view.findViewById(R.id.sectionTabScroll);
        contentContainer= view.findViewById(R.id.footballContent);
        progressBar     = view.findViewById(R.id.footballLoading);
        errorView       = view.findViewById(R.id.footballError);
        contentScroll   = view.findViewById(R.id.footballScroll);

        buildLeagueTabs();
        selectLeague(0);
    }

    // ─── League tabs ────────────────────────────────────────────────────────────

    private void buildLeagueTabs() {
        leagueTabBar.removeAllViews();
        for (int i = 0; i < LEAGUES.length; i++) {
            final int idx = i;
            String[] l = LEAGUES[i];
            LinearLayout tab = new LinearLayout(requireContext());
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(dp(14), dp(8), dp(14), dp(8));
            tab.setClickable(true); tab.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMarginEnd(dp(4));
            tab.setLayoutParams(lp);

            TextView emoji = new TextView(requireContext());
            emoji.setText(l[0]);
            emoji.setTextSize(20);
            emoji.setGravity(Gravity.CENTER);

            TextView label = new TextView(requireContext());
            label.setText(l[1]);
            label.setTextSize(11);
            label.setGravity(Gravity.CENTER);
            label.setTypeface(null, Typeface.BOLD);
            label.setLetterSpacing(0.03f);

            tab.addView(emoji);
            tab.addView(label);
            tab.setTag(idx);
            tab.setOnClickListener(v -> selectLeague(idx));
            leagueTabBar.addView(tab);
        }
    }

    private void selectLeague(int idx) {
        selLeague = idx;
        selSection = 0;
        // Update tab highlight
        for (int i = 0; i < leagueTabBar.getChildCount(); i++) {
            View tab = leagueTabBar.getChildAt(i);
            boolean active = (i == idx);
            tab.setBackgroundResource(active ? R.drawable.tab_active : android.R.color.transparent);
            if (tab instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) tab;
                if (ll.getChildCount() >= 2 && ll.getChildAt(1) instanceof TextView) {
                    ((TextView) ll.getChildAt(1)).setTextColor(active ? 0xFFFFFFFF : 0x88FFFFFF);
                }
            }
        }

        String prefix = LEAGUES[idx][2];
        if ("__live__".equals(prefix)) {
            if (sectionTabScroll != null) sectionTabScroll.setVisibility(View.GONE);
            loadLiveStreams();
        } else if ("__todas__".equals(prefix)) {
            if (sectionTabScroll != null) sectionTabScroll.setVisibility(View.GONE);
            loadUrl("/resultados/todas-las-ligas");
        } else {
            buildSectionTabs(idx);
            if (sectionTabScroll != null) sectionTabScroll.setVisibility(View.VISIBLE);
            loadSection(idx, 0);
        }
    }

    // ─── Section tabs ────────────────────────────────────────────────────────────

    private void buildSectionTabs(int leagueIdx) {
        sectionTabBar.removeAllViews();
        String[][] sections = (leagueIdx == 1) ? LIGAMX_SECTIONS : SECTIONS;
        for (int i = 0; i < sections.length; i++) {
            final int idx = i;
            TextView chip = new TextView(requireContext());
            chip.setText(sections[i][0]);
            chip.setTextSize(12);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            chip.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(6));
            lp.gravity = Gravity.CENTER_VERTICAL;
            chip.setLayoutParams(lp);
            chip.setClickable(true); chip.setFocusable(true);
            chip.setTag(idx);
            chip.setOnClickListener(v -> {
                selSection = idx;
                updateSectionChips();
                loadSection(selLeague, idx);
            });
            sectionTabBar.addView(chip);
        }
        updateSectionChips();
    }

    private void updateSectionChips() {
        for (int i = 0; i < sectionTabBar.getChildCount(); i++) {
            View v = sectionTabBar.getChildAt(i);
            boolean active = (i == selSection);
            v.setBackgroundResource(active ? R.drawable.tab_active : R.drawable.tab_inactive);
            if (v instanceof TextView) ((TextView) v).setTextColor(active ? 0xFFFFFFFF : 0x99FFFFFF);
        }
    }

    private void loadSection(int leagueIdx, int sectionIdx) {
        String prefix = LEAGUES[leagueIdx][2];
        String[][] sections = (leagueIdx == 1) ? LIGAMX_SECTIONS : SECTIONS;
        String sectionKey = sections[sectionIdx][1];
        String path = "/" + prefix + sectionKey;
        loadUrl(path);
    }

    // ─── EN VIVO ─────────────────────────────────────────────────────────────────

    private void loadLiveStreams() {
        showLoading();
        contentContainer.removeAllViews();

        // Section header
        addSectionHeader("📡 Transmisiones en Vivo");

        // Gol 1–6 buttons (static — open stream directly)
        String[][] streams = {
            {"Gol 1 ⚽", "/gol-1"},
            {"Gol 2 ⚽", "/gol-2"},
            {"Gol 3 ⚽", "/gol-3"},
            {"Gol 4 ⚽", "/gol-4"},
            {"Gol 5 ⚽", "/gol-5"},
            {"Gol 6 ⚽", "/gol-6"},
        };

        // 2-column grid of stream buttons
        LinearLayout grid = new LinearLayout(requireContext());
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setPadding(dp(12), 0, dp(12), 0);
        for (int i = 0; i < streams.length; i += 2) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 0, 0, dp(10));
            row.setLayoutParams(rowLp);
            row.addView(makeStreamButton(streams[i][0], streams[i][1]));
            if (i + 1 < streams.length)
                row.addView(makeStreamButton(streams[i+1][0], streams[i+1][1]));
            grid.addView(row);
        }
        contentContainer.addView(grid);

        // IPTV Channels from /canales
        addSectionHeader("📺 Canales IPTV");
        hideLoading();

        // Load IPTV channels async
        showLoading();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = fetch("/canales");
                new Handler(Looper.getMainLooper()).post(() -> {
                    hideLoading();
                    renderCanales(json);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    hideLoading();
                    addErrorCard("No se pudieron cargar los canales");
                });
            }
        });
    }

    private View makeStreamButton(String name, String path) {
        LinearLayout btn = new LinearLayout(requireContext());
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setBackgroundResource(R.drawable.server_row_active);
        btn.setClickable(true); btn.setFocusable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(80), 1f);
        lp.setMarginEnd(dp(5));
        btn.setLayoutParams(lp);
        btn.setPadding(dp(8), dp(12), dp(8), dp(12));

        TextView icon = new TextView(requireContext());
        icon.setText("▶");
        icon.setTextColor(0xFFFF6B00);
        icon.setTextSize(22);
        icon.setGravity(Gravity.CENTER);

        TextView label = new TextView(requireContext());
        label.setText(name);
        label.setTextColor(0xFFFFFFFF);
        label.setTextSize(13);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER);

        TextView sub = new TextView(requireContext());
        sub.setText("EN VIVO");
        sub.setTextColor(0xAAFF6B00);
        sub.setTextSize(10);
        sub.setLetterSpacing(0.1f);
        sub.setGravity(Gravity.CENTER);

        btn.addView(icon);
        btn.addView(label);
        btn.addView(sub);

        // Fetch stream URL async on click
        btn.setOnClickListener(v -> {
            btn.setAlpha(0.6f);
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String json = fetch(path);
                    String url = extractUrl(json);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        btn.setAlpha(1f);
                        if (url != null && !url.isEmpty()) {
                            Intent intent = new Intent(requireContext(), PlayerActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", name);
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), "Stream no disponible", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        btn.setAlpha(1f);
                        Toast.makeText(requireContext(), "Error al cargar stream", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
        return btn;
    }

    // ─── Generic URL loader ───────────────────────────────────────────────────────

    private void loadUrl(String path) {
        showLoading();
        contentContainer.removeAllViews();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = fetch(path);
                new Handler(Looper.getMainLooper()).post(() -> {
                    hideLoading();
                    renderJson(path, json);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    hideLoading();
                    showError("Sin conexión o sin datos disponibles");
                });
            }
        });
    }

    // ─── JSON Renderers ───────────────────────────────────────────────────────────

    private void renderJson(String path, String json) {
        contentContainer.removeAllViews();
        hideError();
        try {
            JSONObject root = new JSONObject(json);
            // Try to detect type by path
            if (path.contains("tabla") || path.contains("posicion")) {
                renderTabla(root);
            } else if (path.contains("marcadores")) {
                renderMarcadores(root);
            } else if (path.contains("goleadores")) {
                renderGoleadores(root);
            } else if (path.contains("calendario")) {
                renderCalendario(root);
            } else if (path.contains("alineaciones")) {
                renderAlineaciones(root);
            } else if (path.contains("videos") || path.contains("momentos")) {
                renderHighlights(root);
            } else if (path.contains("noticias")) {
                renderNoticias(root);
            } else if (path.contains("equipos")) {
                renderEquipos(root);
            } else if (path.contains("canales")) {
                renderCanales(json);
            } else if (path.contains("todas-las-ligas") || path.contains("estadisticas")) {
                renderGeneric(root);
            } else {
                renderGeneric(root);
            }
        } catch (Exception e) {
            try {
                JSONArray arr = new JSONArray(json);
                renderArray(arr);
            } catch (Exception e2) {
                showError("Formato de respuesta desconocido");
            }
        }
    }

    private void renderTabla(JSONObject root) {
        // Header
        addSectionHeader("📊 Tabla de Posiciones");
        addTableHeader(new String[]{"#", "Equipo", "PJ", "G", "E", "P", "Pts"});

        JSONArray arr = findArray(root, "tabla","posiciones","standings","teams","data");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin datos de tabla"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject t = arr.getJSONObject(i);
                int pos   = t.optInt("posicion", t.optInt("pos", t.optInt("rank", i + 1)));
                String eq = t.optString("equipo", t.optString("team", t.optString("nombre", t.optString("name", "—"))));
                int pj    = t.optInt("pj", t.optInt("played", t.optInt("gamesPlayed", 0)));
                int g     = t.optInt("g",  t.optInt("won",    t.optInt("wins",   0)));
                int e     = t.optInt("e",  t.optInt("drawn",  t.optInt("draws",  0)));
                int p     = t.optInt("p",  t.optInt("lost",   t.optInt("losses", 0)));
                int pts   = t.optInt("puntos", t.optInt("points", t.optInt("pts", 0)));
                addTableRow(new String[]{String.valueOf(pos), eq, String.valueOf(pj),
                    String.valueOf(g), String.valueOf(e), String.valueOf(p), String.valueOf(pts)},
                    i % 2 == 0);
            } catch (Exception ignored) {}
        }
    }

    private void renderMarcadores(JSONObject root) {
        addSectionHeader("⚽ Marcadores");
        JSONArray arr = findArray(root, "partidos","matches","fixtures","resultados","marcadores","data");
        if (arr == null || arr.length() == 0) { addErrorCard("No hay marcadores disponibles"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject m = arr.getJSONObject(i);
                String local = m.optString("local",      m.optString("home",      m.optString("equipo_local",     "Local")));
                String visit = m.optString("visitante",  m.optString("away",      m.optString("equipo_visitante", "Visitante")));
                String gL    = String.valueOf(m.optInt("marcador_local",   m.optInt("score_home",  m.optInt("goles_local",   -1))));
                String gV    = String.valueOf(m.optInt("marcador_visitante",m.optInt("score_away", m.optInt("goles_visitante",-1))));
                String estado= m.optString("estado",   m.optString("status",  m.optString("estado_partido", "")));
                String minuto= m.optString("minuto",   m.optString("minute",  ""));
                String fecha = m.optString("fecha",    m.optString("date",    m.optString("hora", "")));
                addMatchCard(local, visit, gL, gV, estado, minuto.isEmpty() ? fecha : minuto);
            } catch (Exception ignored) {}
        }
    }

    private void renderGoleadores(JSONObject root) {
        addSectionHeader("👟 Goleadores");
        addTableHeader(new String[]{"#", "Jugador", "Equipo", "Goles"});
        JSONArray arr = findArray(root, "goleadores","scorers","jugadores","data","players");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin datos de goleadores"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject p = arr.getJSONObject(i);
                String nombre = p.optString("jugador",    p.optString("player", p.optString("nombre", p.optString("name", "—"))));
                String equipo = p.optString("equipo",     p.optString("team",   p.optString("club",   "—")));
                int goles     = p.optInt("goles",         p.optInt("goals",     p.optInt("scored", 0)));
                addTableRow(new String[]{String.valueOf(i+1), nombre, equipo, String.valueOf(goles)}, i % 2 == 0);
            } catch (Exception ignored) {}
        }
    }

    private void renderCalendario(JSONObject root) {
        addSectionHeader("📅 Calendario");
        JSONArray arr = findArray(root, "partidos","fixtures","calendario","matches","data");
        if (arr == null || arr.length() == 0) { addErrorCard("No hay partidos programados"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject m = arr.getJSONObject(i);
                String local = m.optString("local",     m.optString("home",  "Local"));
                String visit = m.optString("visitante", m.optString("away",  "Visitante"));
                String fecha = m.optString("fecha",     m.optString("date",  m.optString("day", "")));
                String hora  = m.optString("hora",      m.optString("time",  m.optString("hour", "")));
                addMatchCard(local, visit, "—", "—", fecha, hora);
            } catch (Exception ignored) {}
        }
    }

    private void renderAlineaciones(JSONObject root) {
        addSectionHeader("📋 Alineaciones");
        // Try to find team data
        JSONArray arr = findArray(root, "partidos","matches","alineaciones","equipos","data");
        if (arr == null) {
            // Try to render as generic key-value
            renderGeneric(root);
            return;
        }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject m = arr.getJSONObject(i);
                String local = m.optString("local", m.optString("home", "Equipo Local"));
                String visit = m.optString("visitante", m.optString("away", "Equipo Visitante"));
                LinearLayout card = makeCard();
                TextView tv = new TextView(requireContext());
                tv.setText("🏠 " + local + "  vs  ✈ " + visit);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(14);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setPadding(dp(16), dp(12), dp(16), dp(12));
                card.addView(tv);
                contentContainer.addView(card);
            } catch (Exception ignored) {}
        }
    }

    private void renderHighlights(JSONObject root) {
        addSectionHeader("🎬 Highlights");
        JSONArray arr = findArray(root, "videos","highlights","clips","data","items","results");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin highlights disponibles"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject v = arr.getJSONObject(i);
                String title = v.optString("titulo",  v.optString("title",   v.optString("nombre", "Video " + (i+1))));
                String url   = v.optString("url",     v.optString("link",    v.optString("video_url", "")));
                String thumb = v.optString("thumbnail", v.optString("imagen", ""));
                if (url.isEmpty()) continue;
                addVideoCard(title, url);
            } catch (Exception ignored) {}
        }
    }

    private void renderNoticias(JSONObject root) {
        addSectionHeader("📰 Noticias");
        JSONArray arr = findArray(root, "noticias","news","articles","data","items");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin noticias disponibles"); return; }

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject n = arr.getJSONObject(i);
                String titulo = n.optString("titulo", n.optString("title",   n.optString("headline", "Noticia")));
                String fuente = n.optString("fuente", n.optString("source",  n.optString("author",   "")));
                String fecha  = n.optString("fecha",  n.optString("date",    n.optString("published","")));
                addNewsCard(titulo, fuente + (fecha.isEmpty() ? "" : " · " + fecha));
            } catch (Exception ignored) {}
        }
    }

    private void renderEquipos(JSONObject root) {
        addSectionHeader("🏆 Equipos");
        JSONArray arr = findArray(root, "equipos","teams","data","clubs");
        if (arr == null || arr.length() == 0) { renderGeneric(root); return; }

        LinearLayout grid = new LinearLayout(requireContext());
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setPadding(dp(12), 0, dp(12), 0);
        for (int i = 0; i < arr.length(); i += 2) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rLp.setMargins(0, 0, 0, dp(8));
            row.setLayoutParams(rLp);
            row.addView(makeTeamCard(arr.optJSONObject(i)));
            if (i + 1 < arr.length()) row.addView(makeTeamCard(arr.optJSONObject(i + 1)));
            grid.addView(row);
        }
        contentContainer.addView(grid);
    }

    private View makeTeamCard(JSONObject t) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundResource(R.drawable.server_row);
        card.setPadding(dp(8), dp(14), dp(8), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(70), 1f);
        lp.setMarginEnd(dp(6));
        card.setLayoutParams(lp);

        String nombre = t != null ? t.optString("nombre", t.optString("name", t.optString("equipo", "—"))) : "—";
        String escudo = t != null ? t.optString("escudo", t.optString("logo",  t.optString("badge", ""))) : "";

        TextView tv = new TextView(requireContext());
        tv.setText(escudo.isEmpty() ? "⚽" : escudo);
        tv.setTextSize(22); tv.setGravity(Gravity.CENTER);

        TextView name = new TextView(requireContext());
        name.setText(nombre);
        name.setTextColor(0xDDFFFFFF);
        name.setTextSize(12);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);

        card.addView(tv);
        card.addView(name);
        return card;
    }

    private void renderCanales(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray arr = findArray(root, "canales","channels","data","items");
            if (arr == null) arr = new JSONArray(json);
            if (arr.length() == 0) { addErrorCard("Sin canales IPTV"); return; }

            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject c = arr.getJSONObject(i);
                    String nombre = c.optString("nombre", c.optString("name", "Canal " + (i+1)));
                    String bandera= c.optString("bandera", c.optString("flag", "📺"));
                    String pUrl   = c.optString("player_url", c.optString("url", ""));
                    String pais   = c.optString("pais", c.optString("country", ""));
                    if (pUrl.isEmpty()) continue;
                    addChannelCard(bandera + " " + nombre, pais, pUrl);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            addErrorCard("Error al parsear canales");
        }
    }

    private void renderGeneric(JSONObject root) {
        // Render all top-level arrays/objects as sections
        java.util.Iterator<String> keys = root.keys();
        boolean found = false;
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object val = root.get(key);
                if (val instanceof JSONArray) {
                    JSONArray arr = (JSONArray) val;
                    if (arr.length() > 0) {
                        found = true;
                        addSectionHeader(cap(key));
                        for (int i = 0; i < Math.min(arr.length(), 30); i++) {
                            Object item = arr.get(i);
                            if (item instanceof JSONObject) {
                                addGenericCard((JSONObject) item);
                            } else {
                                addInfoCard(item.toString());
                            }
                        }
                    }
                } else if (val instanceof JSONObject && !key.equals("meta")) {
                    found = true;
                    addSectionHeader(cap(key));
                    addGenericCard((JSONObject) val);
                }
            } catch (Exception ignored) {}
        }
        if (!found) addErrorCard("Sin datos disponibles");
    }

    private void renderArray(JSONArray arr) {
        for (int i = 0; i < Math.min(arr.length(), 50); i++) {
            try {
                Object item = arr.get(i);
                if (item instanceof JSONObject) addGenericCard((JSONObject) item);
                else addInfoCard(item.toString());
            } catch (Exception ignored) {}
        }
    }

    // ─── Card builders ────────────────────────────────────────────────────────────

    private void addSectionHeader(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(15);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setLetterSpacing(0.03f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(16), dp(16), dp(16), dp(8));
        tv.setLayoutParams(lp);
        contentContainer.addView(tv);
    }

    private void addTableHeader(String[] cols) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(0x22FF6B00);
        row.setPadding(dp(8), dp(8), dp(8), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), 0);
        row.setLayoutParams(lp);
        int[] weights = getColWeights(cols.length);
        for (int i = 0; i < cols.length; i++) {
            TextView tv = new TextView(requireContext());
            tv.setText(cols[i]);
            tv.setTextColor(0xAAFF6B00);
            tv.setTextSize(11);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(i == 1 ? Gravity.START : Gravity.CENTER);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weights[i]));
            row.addView(tv);
        }
        contentContainer.addView(row);
    }

    private void addTableRow(String[] cols, boolean alt) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(alt ? 0x08FFFFFF : 0x00000000);
        row.setPadding(dp(8), dp(10), dp(8), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), 0);
        row.setLayoutParams(lp);
        int[] weights = getColWeights(cols.length);
        for (int i = 0; i < cols.length; i++) {
            TextView tv = new TextView(requireContext());
            tv.setText(cols[i]);
            tv.setTextColor(i == 1 ? 0xFFFFFFFF : 0xBBFFFFFF);
            tv.setTextSize(i == 1 ? 13 : 12);
            if (i == 1) tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(i == 1 ? Gravity.START : Gravity.CENTER);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weights[i]));
            row.addView(tv);
        }
        contentContainer.addView(row);
    }

    private int[] getColWeights(int count) {
        if (count == 7) return new int[]{1, 4, 1, 1, 1, 1, 1};
        if (count == 4) return new int[]{1, 4, 3, 2};
        int[] w = new int[count];
        for (int i = 0; i < count; i++) w[i] = 1;
        return w;
    }

    private void addMatchCard(String local, String visit, String gL, String gV, String estado, String extra) {
        LinearLayout card = makeCard();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        // Estado badge
        if (!estado.isEmpty()) {
            TextView badge = new TextView(requireContext());
            boolean live = estado.toLowerCase().contains("vivo") || estado.toLowerCase().contains("live")
                        || estado.toLowerCase().contains("live") || estado.matches("\\d+.*'");
            badge.setText(live ? "● EN VIVO" : estado.toUpperCase());
            badge.setTextColor(live ? 0xFF4CAF50 : 0x88FFFFFF);
            badge.setTextSize(10);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setLetterSpacing(0.08f);
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bp.setMargins(0, 0, 0, dp(6));
            badge.setLayoutParams(bp);
            card.addView(badge);
        }

        // Score row
        LinearLayout scoreRow = new LinearLayout(requireContext());
        scoreRow.setOrientation(LinearLayout.HORIZONTAL);
        scoreRow.setGravity(Gravity.CENTER_VERTICAL);
        scoreRow.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvLocal = new TextView(requireContext());
        tvLocal.setText(local);
        tvLocal.setTextColor(0xFFFFFFFF);
        tvLocal.setTextSize(14);
        tvLocal.setTypeface(null, Typeface.BOLD);
        tvLocal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));

        TextView tvScore = new TextView(requireContext());
        boolean hasScore = !gL.equals("-1") && !gV.equals("-1");
        tvScore.setText(hasScore ? gL + " - " + gV : extra);
        tvScore.setTextColor(hasScore ? 0xFFFF6B00 : 0x88FFFFFF);
        tvScore.setTextSize(hasScore ? 18 : 12);
        if (hasScore) tvScore.setTypeface(null, Typeface.BOLD);
        tvScore.setGravity(Gravity.CENTER);
        tvScore.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

        TextView tvVisit = new TextView(requireContext());
        tvVisit.setText(visit);
        tvVisit.setTextColor(0xFFFFFFFF);
        tvVisit.setTextSize(14);
        tvVisit.setTypeface(null, Typeface.BOLD);
        tvVisit.setGravity(Gravity.END);
        tvVisit.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));

        scoreRow.addView(tvLocal);
        scoreRow.addView(tvScore);
        scoreRow.addView(tvVisit);
        card.addView(scoreRow);

        if (hasScore && !extra.isEmpty()) {
            TextView tvExtra = new TextView(requireContext());
            tvExtra.setText(extra);
            tvExtra.setTextColor(0x66FFFFFF);
            tvExtra.setTextSize(11);
            tvExtra.setGravity(Gravity.CENTER);
            tvExtra.setPadding(0, dp(4), 0, 0);
            card.addView(tvExtra);
        }
        contentContainer.addView(card);
    }

    private void addVideoCard(String title, String url) {
        LinearLayout card = makeCard();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setClickable(true); card.setFocusable(true);

        TextView icon = new TextView(requireContext());
        icon.setText("▶");
        icon.setTextColor(0xFFFF6B00);
        icon.setTextSize(22);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(40), dp(40));
        ip.setMarginEnd(dp(12));
        icon.setLayoutParams(ip);
        icon.setGravity(Gravity.CENTER);

        TextView tv = new TextView(requireContext());
        tv.setText(title);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(13);
        tv.setMaxLines(2);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        card.addView(icon);
        card.addView(tv);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        });
        contentContainer.addView(card);
    }

    private void addChannelCard(String name, String country, String url) {
        LinearLayout card = makeCard();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        card.setClickable(true); card.setFocusable(true);

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(dp(12), 0, dp(12), dp(6));
        card.setLayoutParams(clp);

        LinearLayout textBlock = new LinearLayout(requireContext());
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(requireContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(13);
        tvName.setTypeface(null, Typeface.BOLD);

        textBlock.addView(tvName);
        if (!country.isEmpty()) {
            TextView tvCountry = new TextView(requireContext());
            tvCountry.setText(country);
            tvCountry.setTextColor(0x88FFFFFF);
            tvCountry.setTextSize(11);
            textBlock.addView(tvCountry);
        }

        TextView tvPlay = new TextView(requireContext());
        tvPlay.setText("▶ VER");
        tvPlay.setTextColor(0xFFFF6B00);
        tvPlay.setTextSize(12);
        tvPlay.setTypeface(null, Typeface.BOLD);

        card.addView(textBlock);
        card.addView(tvPlay);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", name);
            startActivity(intent);
        });
        contentContainer.addView(card);
    }

    private void addNewsCard(String title, String sub) {
        LinearLayout card = makeCard();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(14);
        tvTitle.setMaxLines(3);
        card.addView(tvTitle);
        if (!sub.trim().isEmpty()) {
            TextView tvSub = new TextView(requireContext());
            tvSub.setText(sub);
            tvSub.setTextColor(0x66FFFFFF);
            tvSub.setTextSize(11);
            tvSub.setPadding(0, dp(4), 0, 0);
            card.addView(tvSub);
        }
        contentContainer.addView(card);
    }

    private void addGenericCard(JSONObject obj) {
        LinearLayout card = makeCard();
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        StringBuilder sb = new StringBuilder();
        java.util.Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String k = keys.next();
            try {
                Object v = obj.get(k);
                if (!(v instanceof JSONObject) && !(v instanceof JSONArray)) {
                    sb.append(cap(k)).append(": ").append(v).append("\n");
                }
            } catch (Exception ignored) {}
        }
        if (sb.length() > 0) {
            TextView tv = new TextView(requireContext());
            tv.setText(sb.toString().trim());
            tv.setTextColor(0xCCFFFFFF);
            tv.setTextSize(13);
            card.addView(tv);
            contentContainer.addView(card);
        }
    }

    private void addInfoCard(String text) {
        LinearLayout card = makeCard();
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(0xCCFFFFFF);
        tv.setTextSize(13);
        card.addView(tv);
        contentContainer.addView(card);
    }

    private void addErrorCard(String msg) {
        TextView tv = new TextView(requireContext());
        tv.setText(msg);
        tv.setTextColor(0x66FFFFFF);
        tv.setTextSize(13);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(16), dp(24), dp(16), dp(24));
        tv.setLayoutParams(lp);
        contentContainer.addView(tv);
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.server_row);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), dp(8));
        card.setLayoutParams(lp);
        return card;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────────

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        if (errorView != null) { errorView.setText(msg); errorView.setVisibility(View.VISIBLE); }
    }

    private void hideError() {
        if (errorView != null) errorView.setVisibility(View.GONE);
    }

    private JSONArray findArray(JSONObject root, String... keys) {
        for (String k : keys) {
            JSONArray arr = root.optJSONArray(k);
            if (arr != null && arr.length() > 0) return arr;
        }
        return null;
    }

    private String extractUrl(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String url = obj.optString("url", obj.optString("stream_url",
                         obj.optString("embed_url", obj.optString("player_url",
                         obj.optString("hls", obj.optString("m3u8", ""))))));
            if (!url.isEmpty()) return url;
            // fallback: scan all string values for http
            java.util.Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                Object v = obj.opt(keys.next());
                if (v instanceof String && ((String) v).startsWith("http")) return (String) v;
            }
        } catch (Exception ignored) {}
        // If JSON is just a URL string
        if (json.trim().startsWith("http")) return json.trim();
        return "";
    }

    private String fetch(String path) throws Exception {
        URL url = new URL(API + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(12000);
        c.setReadTimeout(15000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).replace("_", " ");
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}
