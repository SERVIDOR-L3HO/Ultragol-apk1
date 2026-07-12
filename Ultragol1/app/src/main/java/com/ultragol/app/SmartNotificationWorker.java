package com.ultragol.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SmartNotificationWorker — Motor automático de notificaciones con imagen grande.
 * Se ejecuta periódicamente vía WorkManager y elige un tipo creativo de notificación.
 */
public class SmartNotificationWorker extends Worker {

    private static final String TAG = "SmartNotifWorker";

    // Canales
    public static final String CH_CONTENT  = "ap_content";   // películas / series
    public static final String CH_SPORTS   = "ap_sports";    // deportes en vivo
    public static final String CH_PROMO    = "ap_promo";     // promociones / estrenos
    public static final String CH_SERVER   = "ap_server";    // notificaciones del servidor

    // TMDB
    private static final String TMDB_BASE   = "https://api.themoviedb.org/3";
    private static final String TMDB_IMG    = "https://image.tmdb.org/t/p/w780";
    private static final String TMDB_BEARER =
        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmQ5YTgzNGQ0NDEzNzAwYjQ5MWNjMjY4OTIxNDdhYSIsIm5iZiI6MTc1MjQ1NjQ4My4zNDUsInN1YiI6IjY4NzQ1ZDIzNjIwNzU1OWUwNDVhZTRjMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mm-GBMnPS_WUAslIwTiewd6khCIFIqR4XDBqTlT9Yx0";

    // Tipos de notificación
    private static final int TYPE_TRENDING     = 0;
    private static final int TYPE_NEW_RELEASE  = 1;
    private static final int TYPE_RECOMMENDATION = 2;
    private static final int TYPE_TOP_RATED    = 3;
    private static final int TYPE_SERIES       = 4;
    private static final int TYPE_SPORTS_BAIT  = 5;
    private static final int TYPES_COUNT       = 6;

    private static final String PREFS        = "ap_smart_notif";
    private static final String KEY_LAST_IDS = "last_shown_ids";
    private static final String KEY_SEQ      = "notif_seq";

    private final Random rng = new Random();

    public SmartNotificationWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        createChannels(ctx);

        // Verificar permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) return Result.success();
        }

        // Elegir tipo aleatorio
        int type = rng.nextInt(TYPES_COUNT);
        try {
            switch (type) {
                case TYPE_TRENDING:       showTrendingMovie(ctx);      break;
                case TYPE_NEW_RELEASE:    showNewRelease(ctx);         break;
                case TYPE_RECOMMENDATION: showRecommendation(ctx);     break;
                case TYPE_TOP_RATED:      showTopRated(ctx);           break;
                case TYPE_SERIES:         showTrendingSeries(ctx);     break;
                case TYPE_SPORTS_BAIT:    showSportsBait(ctx);         break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generando notificación tipo " + type, e);
        }

        // También revisar notificaciones del servidor
        try { NotificationChecker.check(ctx); } catch (Exception ignored) {}

        return Result.success();
    }

    // ─────────────────── Tipos de notificación ───────────────────────────────

    private void showTrendingMovie(Context ctx) throws Exception {
        JSONArray items = tmdbFetch("/trending/movie/day?language=es-MX");
        ContentSnippet c = pickRandom(items, true);
        if (c == null) return;

        String[] titles = {
            "🔥 Lo que todo el mundo está viendo",
            "🎬 Tendencia ahora mismo",
            "🌟 La película del momento",
            "🎥 No puedes perderte esto"
        };
        String[] bodies = {
            c.title + " — el mundo entero ya habló. ¿Y tú?",
            "\"" + c.title + "\" está rompiendo récords. Descubre por qué.",
            c.title + " — " + (c.overview.length() > 80 ? c.overview.substring(0, 80) + "…" : c.overview),
            "Millones ya la vieron. " + c.title + " te espera ahora."
        };
        int pick = rng.nextInt(titles.length);
        post(ctx, CH_CONTENT, titles[pick], bodies[pick], c.imageUrl, "▶ Ver ahora", "❤ Guardar");
    }

    private void showNewRelease(Context ctx) throws Exception {
        JSONArray items = tmdbFetch("/movie/now_playing?language=es-MX&region=MX");
        ContentSnippet c = pickRandom(items, true);
        if (c == null) return;

        String[] titles = {
            "✨ Estreno de la semana",
            "🆕 Acaba de llegar",
            "🎉 Nuevo en Action Play",
            "🍿 Estreno imperdible"
        };
        String body = "\"" + c.title + "\" ya está disponible. " +
            (c.overview.length() > 70 ? c.overview.substring(0, 70) + "…" : c.overview);
        post(ctx, CH_PROMO, titles[rng.nextInt(titles.length)], body, c.imageUrl, "▶ Ver ahora", "📥 Descargar");
    }

    private void showRecommendation(Context ctx) throws Exception {
        // Géneros variados para recomendaciones personalizadas
        int[] genreIds = {28, 35, 18, 27, 878, 10749, 80, 12};
        String[] genreNames = {"Acción", "Comedia", "Drama", "Terror", "Sci-Fi", "Romance", "Crimen", "Aventura"};
        int idx = rng.nextInt(genreIds.length);
        JSONArray items = tmdbFetch("/discover/movie?language=es-MX&sort_by=popularity.desc&with_genres=" + genreIds[idx] + "&vote_average.gte=7");
        ContentSnippet c = pickRandom(items, true);
        if (c == null) return;

        String[] hooks = {
            "Él eligió el poder. Tú eliges verlo.",
            "Una historia que no olvidarás fácilmente.",
            "Nadie vio esto venir. ¿Estás listo?",
            "El mundo cambió para ellos. Descubre cómo.",
            "Algunos secretos son mejores en pantalla grande.",
            "Una sola decisión lo cambió todo.",
            "La mejor de " + genreNames[idx] + " que verás este año."
        };
        String title = "🎭 Recomendado · " + genreNames[idx];
        String body  = hooks[rng.nextInt(hooks.length)] + " — " + c.title;
        post(ctx, CH_CONTENT, title, body, c.imageUrl, "▶ Ver ahora", "❤ Guardar");
    }

    private void showTopRated(Context ctx) throws Exception {
        JSONArray items = tmdbFetch("/movie/top_rated?language=es-MX");
        ContentSnippet c = pickRandom(items, false);
        if (c == null) return;

        String[] titles = {
            "⭐ Clásico que debes ver",
            "🏆 De las mejor calificadas",
            "👑 Obra maestra disponible",
            "💎 Imprescindible en streaming"
        };
        String body = c.title + " · " + c.rating + "★  — " +
            (c.overview.length() > 75 ? c.overview.substring(0, 75) + "…" : c.overview);
        post(ctx, CH_CONTENT, titles[rng.nextInt(titles.length)], body, c.imageUrl, "▶ Ver ahora", "ℹ Más info");
    }

    private void showTrendingSeries(Context ctx) throws Exception {
        JSONArray items = tmdbFetch("/trending/tv/day?language=es-MX");
        ContentSnippet c = pickRandom(items, true);
        if (c == null) return;

        String[] titles = {
            "📺 Serie que está dando de qué hablar",
            "🔥 Serie tendencia hoy",
            "📡 No pares de ver — nueva temporada",
            "🎬 Binge-watch de la semana"
        };
        String[] bodies = {
            "\"" + c.title + "\" — empieza hoy y no podrás parar.",
            c.title + " está en boca de todos. Descubre por qué.",
            "Una temporada completa lista para ti: " + c.title,
            c.title + " — " + (c.overview.length() > 80 ? c.overview.substring(0, 80) + "…" : c.overview)
        };
        int pick = rng.nextInt(titles.length);
        post(ctx, CH_CONTENT, titles[pick], bodies[pick], c.imageUrl, "▶ Empezar", "❤ Mi lista");
    }

    private void showSportsBait(Context ctx) throws Exception {
        // Sin llamada a API — mensajes creativos de deportes
        String[][] sports = {
            {"⚽ Fútbol en vivo", "Los mejores goles y partidos en tiempo real. ¡Conéctate ahora!"},
            {"🏀 NBA · Playoffs en directo", "La emoción del basketball a un toque. Entra y vívelo."},
            {"🎾 Tenis · Grand Slam", "Cada punto vale un campeonato. No te quedes fuera."},
            {"🏎️ Fórmula 1 · Gran Premio", "Velocidad, estrategia y adrenalina. En vivo ahora."},
            {"🥊 Boxeo · Combate estelar", "El ring te espera. ¿Quién ganará esta noche?"},
            {"🏈 NFL · Partido de temporada", "Touchdowns, defensas épicas y mucho más. ¡En vivo!"},
            {"⚽ Champions League", "La élite del fútbol mundial está en Action Play. Entra ya."}
        };
        int pick = rng.nextInt(sports.length);
        // Para deportes usamos null imagen → notificación sin imagen grande (más rápida)
        post(ctx, CH_SPORTS, sports[pick][0], sports[pick][1], null, "▶ Ver en vivo", "📅 Agenda");
    }

    // ─────────────────── Helpers ─────────────────────────────────────────────

    private void post(Context ctx, String channelId,
                      String title, String body, String imageUrl,
                      String action1, String action2) {

        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int seq = prefs.getInt(KEY_SEQ, 1000);
        prefs.edit().putInt(KEY_SEQ, seq + 1).apply();

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(ctx, seq, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setColor(0xFF1565C0)  // Azul Action Play
            .addAction(android.R.drawable.ic_media_play, action1, pi)
            .addAction(android.R.drawable.btn_star_big_off, action2, pi);

        // Intentar cargar imagen grande
        Bitmap bigImage = imageUrl != null ? downloadBitmap(imageUrl) : null;
        if (bigImage != null) {
            b.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(bigImage)
                .bigLargeIcon((Bitmap) null)
                .setSummaryText(body));
            // Ícono grande = thumbnail de la imagen
            Bitmap thumb = Bitmap.createScaledBitmap(bigImage, 128, 128, true);
            b.setLargeIcon(thumb);
        } else {
            b.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        }

        try {
            NotificationManagerCompat.from(ctx).notify(seq, b.build());
        } catch (SecurityException ignored) {}
    }

    private Bitmap downloadBitmap(String urlStr) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return bmp;
        } catch (Exception e) {
            Log.w(TAG, "No se pudo descargar imagen: " + urlStr);
            return null;
        }
    }

    private JSONArray tmdbFetch(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(TMDB_BASE + path).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + TMDB_BEARER);
        conn.setRequestProperty("accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        if (conn.getResponseCode() != 200) return new JSONArray();
        java.io.BufferedReader br = new java.io.BufferedReader(
            new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return new JSONObject(sb.toString()).optJSONArray("results");
    }

    /** Elige un elemento aleatorio de los resultados TMDB, omitiendo sin imagen si needsImage=true */
    private ContentSnippet pickRandom(JSONArray arr, boolean needsImage) {
        if (arr == null || arr.length() == 0) return null;
        List<JSONObject> candidates = new ArrayList<>();
        for (int i = 0; i < Math.min(arr.length(), 20); i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                String backdrop = o.optString("backdrop_path", "");
                String poster   = o.optString("poster_path", "");
                if (needsImage && backdrop.isEmpty() && poster.isEmpty()) continue;
                candidates.add(o);
            } catch (Exception ignored) {}
        }
        if (candidates.isEmpty()) return null;
        JSONObject pick = candidates.get(rng.nextInt(candidates.size()));
        boolean isMovie = pick.has("title");
        String title    = isMovie ? pick.optString("title", "") : pick.optString("name", "");
        String overview = pick.optString("overview", "");
        String backdrop = pick.optString("backdrop_path", "");
        String poster   = pick.optString("poster_path", "");
        String imgPath  = !backdrop.isEmpty() ? backdrop : poster;
        String imgUrl   = imgPath.isEmpty() ? null : TMDB_IMG + imgPath;
        double vote     = pick.optDouble("vote_average", 0.0);
        String rating   = String.format("%.1f", vote);
        return new ContentSnippet(title, overview, imgUrl, rating);
    }

    // ─────────────────── Canales ─────────────────────────────────────────────

    public static void createChannels(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm == null) return;

        nm.createNotificationChannel(makeChannel(CH_CONTENT, "Películas y Series",
            "Estrenos, tendencias y recomendaciones", NotificationManager.IMPORTANCE_HIGH));
        nm.createNotificationChannel(makeChannel(CH_SPORTS, "Deportes en Vivo",
            "Alertas de partidos y eventos deportivos", NotificationManager.IMPORTANCE_HIGH));
        nm.createNotificationChannel(makeChannel(CH_PROMO, "Estrenos y Promociones",
            "Novedades y contenido destacado", NotificationManager.IMPORTANCE_DEFAULT));
        nm.createNotificationChannel(makeChannel(CH_SERVER, "Avisos de Action Play",
            "Mensajes del equipo de Action Play", NotificationManager.IMPORTANCE_HIGH));
    }

    private static NotificationChannel makeChannel(String id, String name, String desc, int importance) {
        NotificationChannel ch = new NotificationChannel(id, name, importance);
        ch.setDescription(desc);
        ch.enableVibration(true);
        ch.enableLights(true);
        ch.setLightColor(0xFF1565C0);
        return ch;
    }

    // ─────────────────── Modelo interno ──────────────────────────────────────

    static class ContentSnippet {
        final String title, overview, imageUrl, rating;
        ContentSnippet(String t, String o, String i, String r) {
            title = t; overview = o; imageUrl = i; rating = r;
        }
    }
}
