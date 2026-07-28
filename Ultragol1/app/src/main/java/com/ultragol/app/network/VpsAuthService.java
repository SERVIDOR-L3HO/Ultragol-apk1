package com.ultragol.app.network;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Maneja el ciclo de vida del token VPS de A7X TV.
 * Almacena el token en SharedPreferences (equivalente a AsyncStorage en React Native).
 */
public final class VpsAuthService {

    private VpsAuthService() {}

    // ── Token storage ─────────────────────────────────────────────────────────

    public static void storeVpsAccessToken(Context ctx, String accessToken, long expiresAt) {
        SharedPreferences prefs = ctx.getSharedPreferences(
                A7xConstants.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(A7xConstants.KEY_ACCESS_TOKEN, accessToken)
                .putLong(A7xConstants.KEY_EXPIRES_AT, expiresAt)
                .apply();
    }

    public static String getAccessToken(Context ctx) {
        return ctx.getSharedPreferences(A7xConstants.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(A7xConstants.KEY_ACCESS_TOKEN, null);
    }

    public static long getExpiresAt(Context ctx) {
        return ctx.getSharedPreferences(A7xConstants.PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(A7xConstants.KEY_EXPIRES_AT, 0L);
    }

    public static void clearVpsAccessToken(Context ctx) {
        ctx.getSharedPreferences(A7xConstants.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(A7xConstants.KEY_ACCESS_TOKEN)
                .remove(A7xConstants.KEY_EXPIRES_AT)
                .remove(A7xConstants.KEY_GOOGLE_AUTH)
                .apply();
    }

    // ── Validación del token ──────────────────────────────────────────────────

    /**
     * Retorna true si el token existe y no expira en los próximos 45 segundos.
     */
    public static boolean isTokenValid(Context ctx) {
        String token = getAccessToken(ctx);
        if (token == null || token.isEmpty()) return false;
        long expiresAt = getExpiresAt(ctx);
        return expiresAt > (System.currentTimeMillis() + A7xConstants.TOKEN_EXPIRY_MARGIN_MS);
    }

    /**
     * Retorna el accessToken si es válido, null si expiró o no existe.
     */
    public static String getValidAccessToken(Context ctx) {
        return isTokenValid(ctx) ? getAccessToken(ctx) : null;
    }

    // ── Login con Google → A7X Backend ────────────────────────────────────────

    /**
     * Llama al endpoint /api/auth/session con el token de Google.
     * Debe ejecutarse en un hilo de fondo.
     * @return accessToken JWT si el login fue exitoso, null si falló.
     */
    public static String authenticateWithGoogle(Context ctx,
                                                 String idToken,
                                                 String accessToken) {
        try {
            JSONObject body = new JSONObject();
            body.put("provider", "google");
            body.put("id_token", idToken != null ? idToken : "");
            body.put("access_token", accessToken != null ? accessToken : "");

            byte[] bodyBytes = body.toString().getBytes("UTF-8");

            HttpURLConnection conn =
                    (HttpURLConnection) new URL(A7xConstants.AUTH_SESSION).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(15_000);
            conn.setRequestProperty("Content-Type",  "application/json");
            conn.setRequestProperty("User-Agent",    A7xConstants.SECURE_USER_AGENT);
            conn.setRequestProperty("X-A7X-Client",  A7xConstants.SECURE_CLIENT_ID);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }

            int code = conn.getResponseCode();
            if (code != 200) return null;

            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            JSONObject resp = new JSONObject(sb.toString());
            String jwt = resp.optString("accessToken", "");
            long expiresAt = resp.optLong("expiresAt", 0L);

            if (!jwt.isEmpty()) {
                storeVpsAccessToken(ctx, jwt, expiresAt);
                // Guardar datos de sesión Google
                if (resp.has("user")) {
                    ctx.getSharedPreferences(A7xConstants.PREFS_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putString(A7xConstants.KEY_GOOGLE_AUTH, resp.optJSONObject("user").toString())
                            .apply();
                }
                return jwt;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    /**
     * Cierra la sesión en el backend A7X y limpia el token local.
     * Debe ejecutarse en un hilo de fondo.
     */
    public static void logout(Context ctx) {
        String token = getAccessToken(ctx);
        if (token != null && !token.isEmpty()) {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) new URL(A7xConstants.AUTH_LOGOUT).openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(8_000);
                conn.setReadTimeout(8_000);
                conn.setRequestProperty("User-Agent",    A7xConstants.SECURE_USER_AGENT);
                conn.setRequestProperty("X-A7X-Client",  A7xConstants.SECURE_CLIENT_ID);
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.getResponseCode(); // fire and forget
                conn.disconnect();
            } catch (Exception ignored) {}
        }
        clearVpsAccessToken(ctx);
    }
}
