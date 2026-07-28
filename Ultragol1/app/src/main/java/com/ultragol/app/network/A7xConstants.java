package com.ultragol.app.network;

/**
 * Constantes globales de la API A7X TV.
 * Todos los valores provienen del bundle compilado de la app original.
 */
public final class A7xConstants {

    private A7xConstants() {}

    // ── Base URL ──────────────────────────────────────────────────────────────
    public static final String BASE_URL       = "https://api.a7xtv.com/api/iptv";
    public static final String AUTH_SESSION   = "https://api.a7xtv.com/api/auth/session";
    public static final String AUTH_LOGOUT    = "https://api.a7xtv.com/api/auth/logout";
    public static final String UPDATE_URL     = "https://a7xtv.com/app-update.json";
    public static final String ADS_URL        = "https://a7xtv.com/app-ad.html";
    public static final String OAUTH_REDIRECT = "https://a7xtv.com/oauth/google/";
    public static final String AVATAR_BASE    = "https://api.dicebear.com/9.x/adventurer/png?seed=";

    // ── Identificadores de seguridad ──────────────────────────────────────────
    public static final String SECURE_CLIENT_ID  = "a7x-android-secure-v1";
    public static final String SECURE_USER_AGENT = "A7X-TV-Android-Secure-Player/1.0";

    // ── SharedPreferences ─────────────────────────────────────────────────────
    public static final String PREFS_NAME       = "a7x_vps_prefs";
    public static final String KEY_ACCESS_TOKEN = "a7x.vps.access-token.v1";
    public static final String KEY_EXPIRES_AT   = "a7x.vps.expires-at.v1";
    public static final String KEY_GOOGLE_AUTH  = "auth.google";

    // ── Caché ─────────────────────────────────────────────────────────────────
    public static final String CACHE_PREFS         = "a7x_iptv_cache";
    public static final String CACHE_KEY_LIVE       = "a7x.iptv.cache.v2.live";
    public static final String CACHE_KEY_VOD        = "a7x.iptv.cache.v2.vod";
    public static final String CACHE_KEY_SERIES     = "a7x.iptv.cache.v2.series";
    public static final String CACHE_KEY_LAST_REFRESH = "a7x.iptv.lastRefresh";
    public static final long   CACHE_TTL_MS         = 3_600_000L; // 1 hora

    // ── Margen de expiración de token (45 segundos antes) ─────────────────────
    public static final long TOKEN_EXPIRY_MARGIN_MS = 45_000L;
}
