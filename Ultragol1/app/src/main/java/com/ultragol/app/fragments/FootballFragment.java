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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FootballFragment extends Fragment {

    private static final String API = "https://ultragol-api-3--maricarmen43549.replit.app";

    private static final String[][] LEAGUES = {
        {"⚡", "EN VIVO",     "__live__"},
        {"🇲🇽", "Liga MX",    ""},
        {"🏴", "Premier",     "premier/"},
        {"🇪🇸", "La Liga",    "laliga/"},
        {"🇮🇹", "Serie A",    "seriea/"},
        {"🇩🇪", "Bundesliga", "bundesliga/"},
        {"🇫🇷", "Ligue 1",    "ligue1/"},
        {"🌍", "Todas",       "__todas__"},
    };

    private static final String[][] SECTIONS = {
        {"Marcadores",   "marcadores"},
        {"Tabla",        "tabla"},
        {"Goleadores",   "goleadores"},
        {"Calendario",   "calendario"},
        {"Alineaciones", "alineaciones"},
        {"Highlights",   "mejores-momentos"},
        {"Noticias",     "noticias"},
    };

    private static final String[][] LIGAMX_SECTIONS = {
        {"Marcadores",   "marcadores"},
        {"Tabla",        "tabla"},
        {"Goleadores",   "goleadores"},
        {"Calendario",   "calendario"},
        {"Alineaciones", "alineaciones"},
        {"Equipos",      "equipos"},
        {"Highlights",   "videos"},
        {"Noticias",     "noticias"},
    };

    private int selLeague  = 0;
    private int selSection = 0;

    private LinearLayout leagueTabBar;
    private LinearLayout sectionTabBar;
    private View         sectionTabScroll;
    private LinearLayout contentContainer;
    private ProgressBar  progressBar;
    private TextView     errorView;
    private ScrollView   contentScroll;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_football, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        leagueTabBar     = view.findViewById(R.id.leagueTabBar);
        sectionTabBar    = view.findViewById(R.id.sectionTabBar);
        sectionTabScroll = view.findViewById(R.id.sectionTabScroll);
        contentContainer = view.findViewById(R.id.footballContent);
        progressBar      = view.findViewById(R.id.footballLoading);
        errorView        = view.findViewById(R.id.footballError);
        contentScroll    = view.findViewById(R.id.footballScroll);
        buildLeagueTabs();
        selectLeague(0);
    }

    // ─── League tabs ─────────────────────────────────────────────────────────────

    private void buildLeagueTabs() {
        leagueTabBar.removeAllViews();
        for (int i = 0; i < LEAGUES.length; i++) {
            final int idx = i;
            String[] l = LEAGUES[i];

            LinearLayout tab = new LinearLayout(requireContext());
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(dp(12), dp(4), dp(12), dp(4));
            tab.setClickable(true);
            tab.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(60), MATCH_PARENT);
            lp.setMarginEnd(dp(6));
            tab.setLayoutParams(lp);

            TextView emoji = new TextView(requireContext());
            emoji.setText(l[0]);
            emoji.setTextSize(22);
            emoji.setGravity(Gravity.CENTER);

            TextView label = new TextView(requireContext());
            label.setText(l[1]);
            label.setTextSize(10);
            label.setGravity(Gravity.CENTER);
            label.setTypeface(null, Typeface.BOLD);
            label.setLetterSpacing(0.02f);
            label.setMaxLines(1);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            tlp.setMargins(0, dp(3), 0, 0);
            label.setLayoutParams(tlp);

            tab.addView(emoji);
            tab.addView(label);
            tab.setTag(idx);
            tab.setOnClickListener(v -> selectLeague(idx));
            leagueTabBar.addView(tab);
        }
    }

    private void selectLeague(int idx) {
        selLeague  = idx;
        selSection = 0;
        for (int i = 0; i < leagueTabBar.getChildCount(); i++) {
            View tab = leagueTabBar.getChildAt(i);
            boolean active = (i == idx);
            tab.setBackgroundResource(active
                ? R.drawable.sport_league_tab_active
                : R.drawable.sport_league_tab_inactive);
            if (tab instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) tab;
                if (ll.getChildCount() >= 2) {
                    if (ll.getChildAt(0) instanceof TextView)
                        ((TextView) ll.getChildAt(0)).setTextColor(active ? 0xFFFFFFFF : 0xBBFFFFFF);
                    if (ll.getChildAt(1) instanceof TextView)
                        ((TextView) ll.getChildAt(1)).setTextColor(active ? 0xFFFFFFFF : 0x66FFFFFF);
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

    // ─── Section chips ────────────────────────────────────────────────────────────

    private void buildSectionTabs(int leagueIdx) {
        sectionTabBar.removeAllViews();
        String[][] sections = (leagueIdx == 1) ? LIGAMX_SECTIONS : SECTIONS;
        for (int i = 0; i < sections.length; i++) {
            final int idx = i;
            TextView chip = new TextView(requireContext());
            chip.setText(sections[i][0]);
            chip.setTextSize(12);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setPadding(dp(14), dp(5), dp(14), dp(5));
            chip.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lp.setMarginEnd(dp(7));
            lp.gravity = Gravity.CENTER_VERTICAL;
            chip.setLayoutParams(lp);
            chip.setClickable(true);
            chip.setFocusable(true);
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
            v.setBackgroundResource(active
                ? R.drawable.sport_section_chip_active
                : R.drawable.sport_section_chip_inactive);
            if (v instanceof TextView)
                ((TextView) v).setTextColor(active ? 0xFFFFFFFF : 0x88FFFFFF);
        }
    }

    private void loadSection(int leagueIdx, int sectionIdx) {
        String prefix = LEAGUES[leagueIdx][2];
        String[][] sections = (leagueIdx == 1) ? LIGAMX_SECTIONS : SECTIONS;
        String key  = sections[sectionIdx][1];
        loadUrl("/" + prefix + key);
    }

    // ─── EN VIVO ─────────────────────────────────────────────────────────────────

    private void loadLiveStreams() {
        showLoading();
        contentContainer.removeAllViews();
        addSectionHeader("📡", "Transmisiones en Vivo");

        String[][] streams = {
            {"Gol 1", "1", "/gol-1"},
            {"Gol 2", "2", "/gol-2"},
            {"Gol 3", "3", "/gol-3"},
            {"Gol 4", "4", "/gol-4"},
            {"Gol 5", "5", "/gol-5"},
            {"Gol 6", "6", "/gol-6"},
        };

        LinearLayout grid = new LinearLayout(requireContext());
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams gLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        gLp.setMargins(dp(12), 0, dp(12), dp(4));
        grid.setLayoutParams(gLp);

        for (int i = 0; i < streams.length; i += 2) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            rLp.setMargins(0, 0, 0, dp(10));
            row.setLayoutParams(rLp);
            row.addView(makeStreamButton(streams[i][0], streams[i][1], streams[i][2]));
            if (i + 1 < streams.length)
                row.addView(makeStreamButton(streams[i+1][0], streams[i+1][1], streams[i+1][2]));
            grid.addView(row);
        }
        contentContainer.addView(grid);
        hideLoading();

        addSectionHeader("📺", "Canales IPTV");
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

    private View makeStreamButton(String name, String number, String path) {
        LinearLayout btn = new LinearLayout(requireContext());
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setBackgroundResource(R.drawable.sport_stream_btn);
        btn.setClickable(true);
        btn.setFocusable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(90), 1f);
        lp.setMarginEnd(dp(8));
        btn.setLayoutParams(lp);
        btn.setPadding(dp(8), dp(12), dp(8), dp(12));

        TextView icon = new TextView(requireContext());
        icon.setText("▶");
        icon.setTextColor(0xFFFFFFFF);
        icon.setTextSize(24);
        icon.setGravity(Gravity.CENTER);

        TextView label = new TextView(requireContext());
        label.setText("⚽  Gol " + number);
        label.setTextColor(0xFFFFFFFF);
        label.setTextSize(14);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        llp.setMargins(0, dp(4), 0, 0);
        label.setLayoutParams(llp);

        TextView sub = new TextView(requireContext());
        sub.setText("• EN VIVO");
        sub.setTextColor(0xBBFFFFFF);
        sub.setTextSize(9);
        sub.setLetterSpacing(0.12f);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        slp.setMargins(0, dp(2), 0, 0);
        sub.setLayoutParams(slp);

        btn.addView(icon);
        btn.addView(label);
        btn.addView(sub);

        btn.setOnClickListener(v -> {
            btn.animate().alpha(0.5f).setDuration(100).start();
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String json = fetch(path);
                    String url  = extractUrl(json);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        btn.animate().alpha(1f).setDuration(200).start();
                        if (url != null && !url.isEmpty()) {
                            Intent intent = new Intent(requireContext(), PlayerActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", "Gol " + number + " ⚽");
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), "Stream no disponible", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        btn.animate().alpha(1f).setDuration(200).start();
                        Toast.makeText(requireContext(), "Error al cargar stream", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
        return btn;
    }

    // ─── Generic loader ───────────────────────────────────────────────────────────

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

    // ─── JSON Dispatcher ─────────────────────────────────────────────────────────

    private void renderJson(String path, String json) {
        contentContainer.removeAllViews();
        hideError();
        try {
            JSONObject root = new JSONObject(json);
            if      (path.contains("marcadores"))              renderMarcadores(root);
            else if (path.contains("tabla"))                   renderTabla(root);
            else if (path.contains("goleadores"))              renderGoleadores(root);
            else if (path.contains("calendario"))              renderCalendario(root);
            else if (path.contains("alineaciones"))            renderAlineaciones(root);
            else if (path.contains("videos") || path.contains("momentos")) renderHighlights(root);
            else if (path.contains("noticias"))                renderNoticias(root);
            else if (path.contains("equipos"))                 renderEquipos(root);
            else if (path.contains("canales"))                 renderCanales(json);
            else                                               renderGeneric(root);
        } catch (Exception e) {
            try {
                JSONArray arr = new JSONArray(json);
                renderArray(arr);
            } catch (Exception e2) {
                showError("Formato de respuesta desconocido");
            }
        }
    }

    // ─── Section Renderers ────────────────────────────────────────────────────────

    private void renderMarcadores(JSONObject root) {
        addSectionHeader("⚽", "Marcadores");
        JSONArray arr = findArray(root, "partidos","matches","fixtures","resultados","marcadores","data");
        if (arr == null || arr.length() == 0) {
            addErrorCard("No hay marcadores disponibles");
            return;
        }
        for (int i = 0; i < arr.length(); i++) {
            try { addMatchCard(arr.getJSONObject(i)); } catch (Exception ignored) {}
        }
    }

    private void renderTabla(JSONObject root) {
        addSectionHeader("📊", "Tabla de Posiciones");
        JSONArray arr = findArray(root, "tabla","posiciones","standings","teams","data","equipos");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin datos de tabla"); return; }
        addTableHeader(new String[]{"#", "Equipo", "PJ", "G", "E", "P", "Pts"});
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject t   = arr.getJSONObject(i);
                int pos        = t.optInt("posicion", t.optInt("pos", t.optInt("rank", i + 1)));
                String eq      = t.optString("equipo", t.optString("team", t.optString("nombre", t.optString("name","—"))));
                int pj         = t.optInt("pj",      t.optInt("played",     t.optInt("gamesPlayed", 0)));
                int g          = t.optInt("g",        t.optInt("won",        t.optInt("wins",        0)));
                int e          = t.optInt("e",        t.optInt("drawn",      t.optInt("draws",       0)));
                int p          = t.optInt("p",        t.optInt("lost",       t.optInt("losses",      0)));
                int pts        = t.optInt("puntos",   t.optInt("points",     t.optInt("pts",         0)));
                addTableRow(new String[]{
                    String.valueOf(pos), eq, String.valueOf(pj),
                    String.valueOf(g), String.valueOf(e), String.valueOf(p), String.valueOf(pts)
                }, i % 2 == 0, pos <= 4);
            } catch (Exception ignored) {}
        }
    }

    private void renderGoleadores(JSONObject root) {
        addSectionHeader("👟", "Goleadores");
        JSONArray arr = findArray(root, "goleadores","scorers","jugadores","data","players");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin datos de goleadores"); return; }
        addTableHeader(new String[]{"#", "Jugador", "Equipo", "⚽"});
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject p = arr.getJSONObject(i);
                String nombre = p.optString("jugador", p.optString("player", p.optString("nombre", p.optString("name","—"))));
                String equipo = p.optString("equipo",  p.optString("team",   p.optString("club",   "—")));
                int goles     = p.optInt("goles", p.optInt("goals", p.optInt("scored", 0)));
                addTableRow(new String[]{String.valueOf(i+1), nombre, equipo, String.valueOf(goles)},
                    i % 2 == 0, i == 0);
            } catch (Exception ignored) {}
        }
    }

    private void renderCalendario(JSONObject root) {
        addSectionHeader("📅", "Calendario");
        JSONArray arr = findArray(root, "partidos","fixtures","calendario","matches","data");
        if (arr == null || arr.length() == 0) { addErrorCard("No hay partidos programados"); return; }
        for (int i = 0; i < arr.length(); i++) {
            try { addMatchCard(arr.getJSONObject(i)); } catch (Exception ignored) {}
        }
    }

    private void renderAlineaciones(JSONObject root) {
        addSectionHeader("📋", "Alineaciones");
        JSONArray arr = findArray(root, "partidos","matches","alineaciones","equipos","data");
        if (arr == null) { renderGeneric(root); return; }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject m   = arr.getJSONObject(i);
                String nameL   = getTeamName(m, "local");
                String nameV   = getTeamName(m, "visitante");
                LinearLayout card = makeGlassCard(false);
                card.setPadding(dp(16), dp(14), dp(16), dp(14));
                TextView tv = new TextView(requireContext());
                tv.setText("🏠  " + nameL + "   vs   " + nameV + "  ✈");
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(14);
                tv.setTypeface(null, Typeface.BOLD);
                card.addView(tv);
                contentContainer.addView(card);
            } catch (Exception ignored) {}
        }
    }

    private void renderHighlights(JSONObject root) {
        addSectionHeader("🎬", "Highlights");
        JSONArray arr = findArray(root, "videos","highlights","clips","data","items","results");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin highlights disponibles"); return; }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject v = arr.getJSONObject(i);
                String title = v.optString("titulo", v.optString("title", v.optString("nombre","Video "+(i+1))));
                String url   = v.optString("url",    v.optString("link",  v.optString("video_url","")));
                if (url.isEmpty()) continue;
                addVideoCard(title, url);
            } catch (Exception ignored) {}
        }
    }

    private void renderNoticias(JSONObject root) {
        addSectionHeader("📰", "Noticias");
        JSONArray arr = findArray(root, "noticias","news","articles","data","items");
        if (arr == null || arr.length() == 0) { addErrorCard("Sin noticias disponibles"); return; }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject n = arr.getJSONObject(i);
                String titulo = n.optString("titulo", n.optString("title",   n.optString("headline","Noticia")));
                String fuente = n.optString("fuente", n.optString("source",  n.optString("author",  "")));
                String fecha  = n.optString("fecha",  n.optString("date",    n.optString("published","")));
                String sub    = (fuente.isEmpty() ? "" : fuente) + (fecha.isEmpty() ? "" : " · " + fecha);
                addNewsCard(titulo, sub);
            } catch (Exception ignored) {}
        }
    }

    private void renderEquipos(JSONObject root) {
        addSectionHeader("🏆", "Equipos");
        JSONArray arr = findArray(root, "equipos","teams","data","clubs");
        if (arr == null || arr.length() == 0) { renderGeneric(root); return; }
        LinearLayout grid = new LinearLayout(requireContext());
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams gLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        gLp.setMargins(dp(12), 0, dp(12), 0);
        grid.setLayoutParams(gLp);
        for (int i = 0; i < arr.length(); i += 2) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
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
        card.setBackgroundResource(R.drawable.sport_match_card);
        card.setPadding(dp(8), dp(14), dp(8), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(80), 1f);
        lp.setMarginEnd(dp(6));
        card.setLayoutParams(lp);

        String nombre = t != null ? t.optString("nombre", t.optString("name", t.optString("equipo","—"))) : "—";
        String logo   = t != null ? t.optString("escudo", t.optString("logo", t.optString("badge", ""))) : "";

        if (!logo.isEmpty() && isAdded()) {
            ImageView img = new ImageView(requireContext());
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(dp(40), dp(40));
            ilp.gravity = Gravity.CENTER_HORIZONTAL;
            img.setLayoutParams(ilp);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(logo)
                 .apply(new RequestOptions().transform(new RoundedCorners(dp(8))))
                 .placeholder(R.drawable.ic_channel_placeholder)
                 .error(R.drawable.ic_channel_placeholder)
                 .into(img);
            card.addView(img);
        } else {
            TextView emojiTv = new TextView(requireContext());
            emojiTv.setText("⚽");
            emojiTv.setTextSize(22);
            emojiTv.setGravity(Gravity.CENTER);
            card.addView(emojiTv);
        }

        TextView nameTv = new TextView(requireContext());
        nameTv.setText(nombre);
        nameTv.setTextColor(0xDDFFFFFF);
        nameTv.setTextSize(12);
        nameTv.setGravity(Gravity.CENTER);
        nameTv.setMaxLines(2);
        nameTv.setPadding(0, dp(4), 0, 0);
        card.addView(nameTv);
        return card;
    }

    private void renderCanales(String json) {
        try {
            JSONArray arr;
            try {
                JSONObject root = new JSONObject(json);
                arr = findArray(root, "canales","channels","data","items");
                if (arr == null) arr = new JSONArray(json);
            } catch (Exception e) {
                arr = new JSONArray(json);
            }
            if (arr.length() == 0) { addErrorCard("Sin canales IPTV"); return; }
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject c  = arr.getJSONObject(i);
                    String nombre = c.optString("nombre", c.optString("name",       "Canal "+(i+1)));
                    String bandera= c.optString("bandera",c.optString("flag",       "📺"));
                    String pUrl   = c.optString("player_url", c.optString("url",    ""));
                    String pais   = c.optString("pais",   c.optString("country",    ""));
                    if (pUrl.isEmpty()) continue;
                    addChannelCard(bandera + "  " + nombre, pais, pUrl);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            addErrorCard("Error al parsear canales");
        }
    }

    private void renderGeneric(JSONObject root) {
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
                        addSectionHeader("📋", cap(key));
                        for (int i = 0; i < Math.min(arr.length(), 30); i++) {
                            Object item = arr.get(i);
                            if (item instanceof JSONObject) addGenericCard((JSONObject) item);
                            else addInfoCard(item.toString());
                        }
                    }
                } else if (val instanceof JSONObject && !key.equals("meta")) {
                    found = true;
                    addSectionHeader("📋", cap(key));
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
                if (item instanceof JSONObject) addMatchCard((JSONObject) item);
                else addInfoCard(item.toString());
            } catch (Exception ignored) {}
        }
    }

    // ─── Match Card (main UI piece) ───────────────────────────────────────────────

    private void addMatchCard(JSONObject match) {
        String nameL  = getTeamName(match, "local");
        String nameV  = getTeamName(match, "visitante");
        String logoL  = getTeamLogo(match, "local");
        String logoV  = getTeamLogo(match, "visitante");
        String gL     = getTeamScore(match, "local");
        String gV     = getTeamScore(match, "visitante");
        String estado = match.optString("estado",  match.optString("status",  ""));
        String fecha  = match.optString("fecha",   match.optString("date",    ""));
        String hora   = match.optString("hora",    match.optString("time",    ""));
        String minuto = match.optString("minuto",  match.optString("minute",  ""));

        boolean isLive  = estado.toLowerCase().contains("vivo") ||
                          estado.toLowerCase().contains("live") ||
                          minuto.matches("\\d+.*");
        boolean hasScore = !gL.equals("-") && !gV.equals("-");

        LinearLayout card = makeGlassCard(isLive);
        card.setPadding(dp(14), dp(13), dp(14), dp(13));

        // Status bar (live indicator or match state)
        if (!estado.isEmpty() || !minuto.isEmpty()) {
            LinearLayout statusBar = new LinearLayout(requireContext());
            statusBar.setOrientation(LinearLayout.HORIZONTAL);
            statusBar.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams sbLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            sbLp.setMargins(0, 0, 0, dp(8));
            statusBar.setLayoutParams(sbLp);

            if (isLive) {
                // Pulsing green dot
                View dot = new View(requireContext());
                LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(dp(7), dp(7));
                dLp.gravity   = Gravity.CENTER_VERTICAL;
                dLp.setMarginEnd(dp(5));
                dot.setLayoutParams(dLp);
                dot.setBackgroundResource(R.drawable.badge_live);
                statusBar.addView(dot);
            }

            TextView badge = new TextView(requireContext());
            badge.setText(isLive ? "EN VIVO" : estado.toUpperCase());
            badge.setTextColor(isLive ? 0xFF4CAF50 : 0x77FFFFFF);
            badge.setTextSize(10);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setLetterSpacing(0.1f);
            badge.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
            statusBar.addView(badge);

            if (!minuto.isEmpty()) {
                TextView tvMin = new TextView(requireContext());
                tvMin.setText(minuto);
                tvMin.setTextColor(isLive ? 0xFFFF6B00 : 0x77FFFFFF);
                tvMin.setTextSize(11);
                tvMin.setTypeface(null, Typeface.BOLD);
                statusBar.addView(tvMin);
            }
            card.addView(statusBar);
        }

        // Score row: [logo + name] [score] [name + logo]
        LinearLayout scoreRow = new LinearLayout(requireContext());
        scoreRow.setOrientation(LinearLayout.HORIZONTAL);
        scoreRow.setGravity(Gravity.CENTER_VERTICAL);
        scoreRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dp(72)));

        // Left team
        LinearLayout leftTeam = makeTeamBlock(nameL, logoL, Gravity.START);
        leftTeam.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, 3f));

        // Score center
        LinearLayout center = new LinearLayout(requireContext());
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER);
        center.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, 2f));

        TextView tvScore = new TextView(requireContext());
        if (hasScore) {
            tvScore.setText(gL + "  —  " + gV);
            tvScore.setTextColor(0xFFFF6B00);
            tvScore.setTextSize(24);
            tvScore.setTypeface(null, Typeface.BOLD);
        } else {
            tvScore.setText("vs");
            tvScore.setTextColor(0x44FFFFFF);
            tvScore.setTextSize(18);
            tvScore.setTypeface(null, Typeface.BOLD);
        }
        tvScore.setGravity(Gravity.CENTER);
        center.addView(tvScore);

        if (!fecha.isEmpty() || !hora.isEmpty()) {
            String timeStr = fecha + (hora.isEmpty() ? "" : "\n" + hora);
            TextView tvTime = new TextView(requireContext());
            tvTime.setText(timeStr);
            tvTime.setTextColor(0x55FFFFFF);
            tvTime.setTextSize(10);
            tvTime.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            tlp.setMargins(0, dp(4), 0, 0);
            tvTime.setLayoutParams(tlp);
            center.addView(tvTime);
        }

        // Right team
        LinearLayout rightTeam = makeTeamBlock(nameV, logoV, Gravity.END);
        rightTeam.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, 3f));

        scoreRow.addView(leftTeam);
        scoreRow.addView(center);
        scoreRow.addView(rightTeam);
        card.addView(scoreRow);
        contentContainer.addView(card);
    }

    private LinearLayout makeTeamBlock(String name, String logoUrl, int textGravity) {
        LinearLayout block = new LinearLayout(requireContext());
        block.setOrientation(LinearLayout.VERTICAL);
        block.setGravity(Gravity.CENTER);

        if (!logoUrl.isEmpty() && isAdded()) {
            ImageView img = new ImageView(requireContext());
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(dp(42), dp(42));
            ilp.gravity = Gravity.CENTER_HORIZONTAL;
            img.setLayoutParams(ilp);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(logoUrl)
                 .apply(new RequestOptions().transform(new RoundedCorners(dp(6))))
                 .placeholder(R.drawable.ic_channel_placeholder)
                 .error(R.drawable.ic_channel_placeholder)
                 .into(img);
            block.addView(img);
        }

        TextView tvName = new TextView(requireContext());
        tvName.setText(name);
        tvName.setTextColor(0xEEFFFFFF);
        tvName.setTextSize(12);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setGravity(Gravity.CENTER);
        tvName.setMaxLines(2);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        nlp.setMargins(0, dp(4), 0, 0);
        tvName.setLayoutParams(nlp);
        block.addView(tvName);
        return block;
    }

    // ─── Card / UI builders ───────────────────────────────────────────────────────

    private void addSectionHeader(String icon, String text) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(dp(16), dp(18), dp(16), dp(10));
        row.setLayoutParams(lp);

        TextView tvIcon = new TextView(requireContext());
        tvIcon.setText(icon);
        tvIcon.setTextSize(16);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        ilp.setMarginEnd(dp(8));
        tvIcon.setLayoutParams(ilp);
        row.addView(tvIcon);

        TextView tvText = new TextView(requireContext());
        tvText.setText(text);
        tvText.setTextColor(0xFFFFFFFF);
        tvText.setTextSize(15);
        tvText.setTypeface(null, Typeface.BOLD);
        tvText.setLetterSpacing(0.03f);
        tvText.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        row.addView(tvText);

        // Orange accent line
        View accent = new View(requireContext());
        accent.setBackgroundColor(0x40FF6B00);
        accent.setLayoutParams(new LinearLayout.LayoutParams(dp(3), dp(20)));
        row.addView(accent);

        contentContainer.addView(row);
    }

    private void addTableHeader(String[] cols) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(0x22FF6B00);
        row.setPadding(dp(10), dp(8), dp(10), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), 0);
        row.setLayoutParams(lp);
        int[] weights = getColWeights(cols.length);
        for (int i = 0; i < cols.length; i++) {
            TextView tv = new TextView(requireContext());
            tv.setText(cols[i]);
            tv.setTextColor(0xBBFF6B00);
            tv.setTextSize(11);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setLetterSpacing(0.05f);
            tv.setGravity(i == 1 ? Gravity.START : Gravity.CENTER);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, weights[i]));
            row.addView(tv);
        }
        contentContainer.addView(row);
    }

    private void addTableRow(String[] cols, boolean alt, boolean highlight) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        int bg = highlight ? 0x15FF6B00 : (alt ? 0x08FFFFFF : 0x00000000);
        row.setBackgroundColor(bg);
        row.setPadding(dp(10), dp(11), dp(10), dp(11));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), 0);
        row.setLayoutParams(lp);
        int[] weights = getColWeights(cols.length);
        for (int i = 0; i < cols.length; i++) {
            TextView tv = new TextView(requireContext());
            tv.setText(cols[i]);
            tv.setTextColor(i == 0 && highlight ? 0xFFFF6B00 :
                            i == 1 ? 0xFFFFFFFF : 0xAAFFFFFF);
            tv.setTextSize(i == 1 ? 13 : 12);
            if (i == 1 || (i == 0 && highlight)) tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(i == 1 ? Gravity.START : Gravity.CENTER);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, weights[i]));
            row.addView(tv);
        }
        contentContainer.addView(row);
    }

    private int[] getColWeights(int count) {
        if (count == 7) return new int[]{1, 4, 1, 1, 1, 1, 1};
        if (count == 4) return new int[]{1, 4, 3, 2};
        int[] w = new int[count]; for (int i = 0; i < count; i++) w[i] = 1; return w;
    }

    private void addVideoCard(String title, String url) {
        LinearLayout card = makeGlassCard(false);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setClickable(true);
        card.setFocusable(true);

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
        tv.setTextColor(0xEEFFFFFF);
        tv.setTextSize(13);
        tv.setMaxLines(2);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

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
        LinearLayout card = makeGlassCard(false);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams clp = (LinearLayout.LayoutParams) card.getLayoutParams();
        clp.setMargins(dp(12), 0, dp(12), dp(7));
        card.setLayoutParams(clp);

        LinearLayout textBlock = new LinearLayout(requireContext());
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

        TextView tvName = new TextView(requireContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(13);
        tvName.setTypeface(null, Typeface.BOLD);
        textBlock.addView(tvName);

        if (!country.isEmpty()) {
            TextView tvCountry = new TextView(requireContext());
            tvCountry.setText(country);
            tvCountry.setTextColor(0x66FFFFFF);
            tvCountry.setTextSize(11);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            cp.setMargins(0, dp(2), 0, 0);
            tvCountry.setLayoutParams(cp);
            textBlock.addView(tvCountry);
        }

        TextView tvPlay = new TextView(requireContext());
        tvPlay.setText("▶  VER");
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
        LinearLayout card = makeGlassCard(false);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(14);
        tvTitle.setMaxLines(3);
        card.addView(tvTitle);
        if (!sub.trim().isEmpty()) {
            TextView tvSub = new TextView(requireContext());
            tvSub.setText(sub.trim());
            tvSub.setTextColor(0x55FFFFFF);
            tvSub.setTextSize(11);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            sp.setMargins(0, dp(5), 0, 0);
            tvSub.setLayoutParams(sp);
            card.addView(tvSub);
        }
        contentContainer.addView(card);
    }

    private void addGenericCard(JSONObject obj) {
        LinearLayout card = makeGlassCard(false);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        StringBuilder sb = new StringBuilder();
        java.util.Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String k = keys.next();
            try {
                Object v = obj.get(k);
                if (!(v instanceof JSONObject) && !(v instanceof JSONArray))
                    sb.append(cap(k)).append(":  ").append(v).append("\n");
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
        LinearLayout card = makeGlassCard(false);
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
        tv.setTextColor(0x55FFFFFF);
        tv.setTextSize(13);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(dp(16), dp(24), dp(16), dp(24));
        tv.setLayoutParams(lp);
        contentContainer.addView(tv);
    }

    private LinearLayout makeGlassCard(boolean live) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(live ? R.drawable.sport_match_live_card : R.drawable.sport_match_card);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(dp(12), 0, dp(12), dp(10));
        card.setLayoutParams(lp);
        return card;
    }

    // ─── JSON helpers ─────────────────────────────────────────────────────────────

    /** Extracts team name: handles nested obj {nombre,name,nombreCorto} or flat string */
    private String getTeamName(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            String n = team.optString("nombre", "");
            if (!n.isEmpty() && !n.equals("null")) return n;
            n = team.optString("name", "");
            if (!n.isEmpty() && !n.equals("null")) return n;
            n = team.optString("nombreCorto", "");
            if (!n.isEmpty() && !n.equals("null")) return n;
        }
        String flat = match.optString(key, "");
        if (!flat.startsWith("{") && !flat.isEmpty()) return flat;
        return key.equals("local") ? "Local" : "Visitante";
    }

    /** Extracts team score from nested obj or flat field */
    private String getTeamScore(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            Object m = team.opt("marcador");
            if (m != null && !m.toString().equals("null") && !m.toString().isEmpty())
                return m.toString();
        }
        boolean isLocal = key.equals("local");
        int v = match.optInt(isLocal ? "marcador_local"  : "marcador_visitante",
                match.optInt(isLocal ? "score_home"      : "score_away",
                match.optInt(isLocal ? "goles_local"     : "goles_visitante", -1)));
        return v >= 0 ? String.valueOf(v) : "-";
    }

    /** Extracts team logo URL from nested obj */
    private String getTeamLogo(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            String logo = team.optString("logo", team.optString("logo_url", team.optString("escudo","")));
            if (!logo.isEmpty() && !logo.equals("null")) return logo;
        }
        return match.optString(key + "_logo", "");
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
                         obj.optString("hls", obj.optString("m3u8","" ))))));
            if (!url.isEmpty()) return url;
            java.util.Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                Object v = obj.opt(keys.next());
                if (v instanceof String && ((String) v).startsWith("http")) return (String) v;
            }
        } catch (Exception ignored) {}
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

    // ─── State helpers ────────────────────────────────────────────────────────────

    private void showLoading() { if (progressBar != null) progressBar.setVisibility(View.VISIBLE); }
    private void hideLoading() { if (progressBar != null) progressBar.setVisibility(View.GONE);    }
    private void showError(String msg) {
        if (errorView != null) { errorView.setText(msg); errorView.setVisibility(View.VISIBLE); }
    }
    private void hideError() {
        if (errorView != null) errorView.setVisibility(View.GONE);
    }
}
