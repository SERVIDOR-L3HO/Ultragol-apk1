const express  = require('express');
const multer   = require('multer');
const path     = require('path');
const fs       = require('fs');
const webPush  = require('web-push');

const app  = express();
const PORT = process.env.PORT || 5000;
const ADMIN_KEY = process.env.ADMIN_KEY;

// ── FAIL-CLOSED: refuse to serve admin routes without a key ───────────────────
if (!ADMIN_KEY) {
    console.warn('⚠️  ADVERTENCIA: ADMIN_KEY no está configurada. Las rutas /admin/* estarán deshabilitadas.');
}

// ── AUTH MIDDLEWARE ───────────────────────────────────────────────────────────
function requireAdmin(req, res, next) {
    if (!ADMIN_KEY) return res.status(503).json({ error: 'Servidor no configurado: ADMIN_KEY ausente' });
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });
    next();
}

// ── BASE URL (dominio Replit actual) ──────────────────────────────────────────
function getBaseUrl() {
    const domains = process.env.REPLIT_DOMAINS;
    if (domains) return 'https://' + domains.split(',')[0].trim();
    const devDomain = process.env.REPLIT_DEV_DOMAIN;
    if (devDomain) return 'https://' + devDomain;
    return `http://localhost:${PORT}`;
}

app.use(express.json());
app.use(express.static('public'));

// ── MÚSICA — pancho-mix frontend & API ───────────────────────────────────────
app.use('/musica', express.static(path.join(__dirname, 'public', 'musica')));
app.get('/musica', (req, res) => res.sendFile(path.join(__dirname, 'public', 'musica', 'index.html')));

// Canal YouTube de ID Remake
const IDREMAKE_CHANNEL_ID = 'UC8mT-SdbG0MRk03K_UeyINg';
let _channelCache = null;
let _channelCacheTs = 0;
const CHANNEL_CACHE_TTL = 30 * 60 * 1000; // 30 min

app.get('/musica/api/channel-videos', async (req, res) => {
    try {
        if (_channelCache && Date.now() - _channelCacheTs < CHANNEL_CACHE_TTL) {
            return res.json(_channelCache);
        }
        const rssUrl = `https://www.youtube.com/feeds/videos.xml?channel_id=${IDREMAKE_CHANNEL_ID}`;
        const ctrl = new AbortController();
        const timer = setTimeout(() => ctrl.abort(), 12000);
        let xml;
        try {
            const r = await fetch(rssUrl, { signal: ctrl.signal });
            if (!r.ok) throw new Error(`RSS HTTP ${r.status}`);
            xml = await r.text();
        } finally { clearTimeout(timer); }

        const videos = [];
        const entryRe = /<entry>([\s\S]*?)<\/entry>/g;
        let m;
        while ((m = entryRe.exec(xml)) !== null) {
            const e = m[1];
            const get = re => (e.match(re) || [])[1] || '';
            const videoId   = get(/<yt:videoId>(.*?)<\/yt:videoId>/);
            const title     = get(/<title>([\s\S]*?)<\/title>/)
                .replace(/&amp;/g,'&').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&quot;/g,'"').replace(/&#39;/g,"'");
            const thumbnail = get(/media:thumbnail url="([^"]*)"/);
            const published = get(/<published>(.*?)<\/published>/);
            const views     = parseInt(get(/statistics views="([^"]*)"/)) || 0;
            const description = get(/<media:description>([\s\S]*?)<\/media:description>/).trim();
            if (videoId) videos.push({ videoId, title, thumbnail, published, views, description });
        }
        const result = { videos, channelName: 'ID Remake', channelId: IDREMAKE_CHANNEL_ID };
        _channelCache = result;
        _channelCacheTs = Date.now();
        res.json(result);
    } catch (err) {
        console.error('channel-videos error:', err.message);
        res.status(500).json({ error: 'No se pudieron cargar los videos' });
    }
});

// ── FILE STORAGE ───────────────────────────────────────────────────────────────
const ALLOWED_APPS = new Set(['ultragol1', 'ultra1', 'ultragol']);
const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, 'apks/'),
    filename:    (req, file, cb) => {
        const appName = ALLOWED_APPS.has(req.query.app) ? req.query.app : 'ultragol1';
        cb(null, `${appName}.apk`);
    }
});
const upload = multer({
    storage,
    limits: { fileSize: 100 * 1024 * 1024, files: 1 },  // 100 MB max
    fileFilter: (req, file, cb) => {
        if (file.mimetype === 'application/vnd.android.package-archive' ||
            file.originalname.toLowerCase().endsWith('.apk')) {
            cb(null, true);
        } else {
            cb(new Error('Solo se permiten archivos APK'));
        }
    }
});

// ── DATA FILES ────────────────────────────────────────────────────────────────
const VERSION_FILE        = path.join(__dirname, 'version.json');
const ULTRA1_VERSION_FILE = path.join(__dirname, 'version_ultra1.json');
const ULTRAGOL1_VERSION_FILE = path.join(__dirname, 'version.json');
const NOTIF_FILE          = path.join(__dirname, 'notifications.json');
const SUBS_FILE           = path.join(__dirname, 'subscriptions.json');
const VAPID_FILE          = path.join(__dirname, 'vapid.json');

function readJSON(file, fallback = {}) {
    try { return JSON.parse(fs.readFileSync(file, 'utf8')); } catch { return fallback; }
}
function writeJSON(file, data) {
    fs.writeFileSync(file, JSON.stringify(data, null, 2));
}

// ── VAPID KEYS (auto-generate once) ───────────────────────────────────────────
let vapidKeys;
if (fs.existsSync(VAPID_FILE)) {
    vapidKeys = readJSON(VAPID_FILE);
} else {
    vapidKeys = webPush.generateVAPIDKeys();
    writeJSON(VAPID_FILE, vapidKeys);
}
webPush.setVapidDetails(
    'mailto:admin@ultragol.app',
    vapidKeys.publicKey,
    vapidKeys.privateKey
);

// ── INIT NOTIF FILE ───────────────────────────────────────────────────────────
if (!fs.existsSync(NOTIF_FILE)) writeJSON(NOTIF_FILE, []);
if (!fs.existsSync(SUBS_FILE))  writeJSON(SUBS_FILE,  []);

// ── AUTO-FIX DOWNLOAD URLS ────────────────────────────────────────────────────
function autoFixDownloadUrls() {
    const base = getBaseUrl();
    const fixes = [
        { file: VERSION_FILE,        dlPath: '/ultragol1/download' },
        { file: ULTRA1_VERSION_FILE, dlPath: '/ultra1/download' },
    ];
    for (const { file, dlPath } of fixes) {
        if (!fs.existsSync(file)) continue;
        const data = readJSON(file);
        // Keep manifests that intentionally publish the APK from GitHub.
        // The Android client uses this stable URL for self-updates.
        if (typeof data.downloadUrl === 'string'
            && data.downloadUrl.includes('raw.githubusercontent.com/')) {
            continue;
        }
        const correctUrl = `${base}${dlPath}`;
        if (data.downloadUrl !== correctUrl) {
            data.downloadUrl = correctUrl;
            writeJSON(file, data);
            console.log(`URL actualizada: ${correctUrl}`);
        }
    }
}
autoFixDownloadUrls();

// ── VERSION HELPERS ───────────────────────────────────────────────────────────
function getVersionFile(appName) {
    if (appName === 'ultra1')    return ULTRA1_VERSION_FILE;
    if (appName === 'ultragol1') return ULTRAGOL1_VERSION_FILE;
    return VERSION_FILE;
}
function getApkName(appName) {
    if (appName === 'ultra1')    return 'ultra1.apk';
    if (appName === 'ultragol1') return 'ultra.apk';
    return 'ultragol.apk';
}
function getDownloadPath(appName) {
    if (appName === 'ultra1')    return '/ultra1/download';
    if (appName === 'ultragol1') return '/ultragol1/download';
    return '/download';
}

// ── VERSION ENDPOINTS ─────────────────────────────────────────────────────────
app.get('/version', (req, res) => {
    try { res.json(readJSON(VERSION_FILE)); }
    catch { res.status(500).json({ error: 'No se pudo leer la versión' }); }
});

app.get('/ultra1/version', (req, res) => {
    try { res.json(readJSON(ULTRA1_VERSION_FILE)); }
    catch { res.status(500).json({ error: 'No se pudo leer la versión' }); }
});

app.get('/ultragol1/version', (req, res) => {
    try { res.json(readJSON(ULTRAGOL1_VERSION_FILE)); }
    catch { res.status(500).json({ error: 'No se pudo leer la versión' }); }
});

// ── DOWNLOAD ENDPOINTS ────────────────────────────────────────────────────────
app.get('/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultragol.apk');
    if (!fs.existsSync(apkPath)) return res.status(404).json({ error: 'APK no encontrado' });
    res.download(apkPath, 'ultragol.apk');
});

app.get('/ultra1/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultra1.apk');
    if (!fs.existsSync(apkPath)) return res.status(404).json({ error: 'APK no encontrado' });
    res.download(apkPath, 'ultra1.apk');
});

app.get('/ultragol1/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultra.apk');
    if (!fs.existsSync(apkPath)) return res.status(404).json({ error: 'APK no encontrado' });
    res.download(apkPath, 'ultra.apk');
});

// ── ADMIN UPDATE ──────────────────────────────────────────────────────────────
app.post('/admin/update', requireAdmin, upload.single('apk'), (req, res) => {
    const { versionCode, versionName, changelog, forceUpdate } = req.body;
    if (!versionCode || !versionName)
        return res.status(400).json({ error: 'Faltan versionCode o versionName' });

    const appName = ALLOWED_APPS.has(req.query.app) ? req.query.app : 'ultragol1';
    const vFile   = getVersionFile(appName);
    const dlPath  = getDownloadPath(appName);

    const data = {
        versionCode:  parseInt(versionCode),
        versionName,
        changelog:    changelog || '',
        downloadUrl:  `${getBaseUrl()}${dlPath}`,
        forceUpdate:  forceUpdate === 'true' || forceUpdate === true,
        updatedAt:    new Date().toISOString()
    };

    writeJSON(vFile, data);
    res.json({ ok: true, version: data });
});

// ── ADMIN INFO ────────────────────────────────────────────────────────────────
app.get('/admin/info', requireAdmin, (req, res) => {
    const appName  = ALLOWED_APPS.has(req.query.app) ? req.query.app : 'ultragol1';
    const vFile    = getVersionFile(appName);
    const apkFile  = getApkName(appName);
    const apkPath  = path.join(__dirname, 'apks', apkFile);
    const apkExists = fs.existsSync(apkPath);
    const apkSize   = apkExists ? (fs.statSync(apkPath).size / 1024 / 1024).toFixed(2) + ' MB' : null;
    res.json({ version: readJSON(vFile), apkExists, apkSize });
});

// ── NOTIFICATIONS — PUBLIC ────────────────────────────────────────────────────
app.get('/notifications', (req, res) => {
    const notifs = readJSON(NOTIF_FILE, []);
    const now    = Date.now();
    const active = notifs.filter(n => !n.expiresAt || new Date(n.expiresAt).getTime() > now);
    res.json({ notifications: active });
});

// ── NOTIFICATIONS — ADMIN: CREATE ─────────────────────────────────────────────
app.post('/admin/notify', requireAdmin, async (req, res) => {
    const { title, message, type, expiresIn } = req.body;
    if (!title || !message)
        return res.status(400).json({ error: 'Faltan title y message' });

    const validTypes = new Set(['info', 'update', 'alert']);
    const parsedExpiry = expiresIn ? parseInt(expiresIn, 10) : NaN;
    const expiresAt = (!isNaN(parsedExpiry) && parsedExpiry > 0)
        ? new Date(Date.now() + parsedExpiry * 60 * 60 * 1000).toISOString()
        : null;

    const notif = {
        id:        Date.now().toString(),
        title:     title.trim(),
        message:   message.trim(),
        type:      validTypes.has(type) ? type : 'info',
        createdAt: new Date().toISOString(),
        expiresAt,
    };

    const notifs = readJSON(NOTIF_FILE, []);
    notifs.unshift(notif);
    writeJSON(NOTIF_FILE, notifs);

    // Send web push to all subscribers
    const subs = readJSON(SUBS_FILE, []);
    const payload = JSON.stringify({ title: notif.title, body: notif.message, type: notif.type });
    const deadSubs = [];

    await Promise.allSettled(subs.map(async (sub, i) => {
        try {
            await webPush.sendNotification(sub, payload);
        } catch (e) {
            if (e.statusCode === 404 || e.statusCode === 410) deadSubs.push(i);
        }
    }));

    if (deadSubs.length) {
        const alive = subs.filter((_, i) => !deadSubs.includes(i));
        writeJSON(SUBS_FILE, alive);
    }

    res.json({ ok: true, notif, sent: subs.length - deadSubs.length });
});

// ── NOTIFICATIONS — ADMIN: DELETE ─────────────────────────────────────────────
app.delete('/admin/notify/:id', requireAdmin, (req, res) => {

    const notifs  = readJSON(NOTIF_FILE, []);
    const filtered = notifs.filter(n => n.id !== req.params.id);
    writeJSON(NOTIF_FILE, filtered);
    res.json({ ok: true, removed: notifs.length - filtered.length });
});

// ── NOTIFICATIONS — ADMIN: LIST ALL ───────────────────────────────────────────
app.get('/admin/notifications', requireAdmin, (req, res) => {
    res.json({ notifications: readJSON(NOTIF_FILE, []) });
});

// ── PUSH SUBSCRIBE ─────────────────────────────────────────────────────────────
const MAX_SUBSCRIPTIONS = 10000;
app.post('/push/subscribe', (req, res) => {
    const sub = req.body;
    if (!sub || !sub.endpoint || typeof sub.endpoint !== 'string')
        return res.status(400).json({ error: 'Suscripción inválida' });
    const subs = readJSON(SUBS_FILE, []);
    const exists = subs.some(s => s.endpoint === sub.endpoint);
    if (!exists) {
        if (subs.length >= MAX_SUBSCRIPTIONS)
            return res.status(429).json({ error: 'Límite de suscripciones alcanzado' });
        subs.push(sub);
        writeJSON(SUBS_FILE, subs);
    }
    res.json({ ok: true });
});

app.delete('/push/subscribe', (req, res) => {
    const { endpoint } = req.body;
    if (!endpoint) return res.status(400).json({ error: 'Falta endpoint' });
    const subs = readJSON(SUBS_FILE, []).filter(s => s.endpoint !== endpoint);
    writeJSON(SUBS_FILE, subs);
    res.json({ ok: true });
});

// ── VAPID PUBLIC KEY ──────────────────────────────────────────────────────────
app.get('/push/vapid-public-key', (req, res) => {
    res.json({ key: vapidKeys.publicKey });
});

// ── GOL API PROXY ─────────────────────────────────────────────────────────────
const GOL_API = 'https://ultrago-xi.vercel.app/gol-3';

app.get('/api/gol', async (req, res) => {
    try {
        const response = await fetch(GOL_API);
        if (!response.ok) throw new Error(`API error ${response.status}`);
        const data = await response.json();
        const lista = data.transmisiones || data || [];
        const map = new Map();
        for (const item of lista) {
            const key = item.titulo;
            if (!map.has(key)) {
                map.set(key, { titulo: item.titulo, hora: item.hora, fecha: item.fecha, liga: item.liga, logoUrl: item.logoUrl, servidores: [] });
            }
            map.get(key).servidores.push({ canal: item.canal, url: item.url });
        }
        res.json({ partidos: Array.from(map.values()) });
    } catch (err) {
        res.status(500).json({ error: 'No se pudo obtener la cartelera', detail: err.message });
    }
});

// ── DRAMA-SHORTS API (proxy Dailymotion) ──────────────────────────────────────
const DM_API    = 'https://api.dailymotion.com';
const DM_FIELDS = 'id,title,thumbnail_url,duration,views_total,owner.screenname';

async function dmFetch(path) {
    const r = await fetch(`${DM_API}${path}`);
    if (!r.ok) throw new Error(`DM ${r.status}`);
    return r.json();
}

function mapDM(v) {
    return {
        id:          v.id,
        titulo:      v.title || '',
        thumbnailUrl:v.thumbnail_url || '',
        duracion:    v.duration || 0,
        vistas:      v.views_total || 0,
        canal:       v['owner.screenname'] || '',
        url:         `https://www.dailymotion.com/embed/video/${v.id}`
    };
}

// 1. Recientes
app.get('/drama-shorts', async (req, res) => {
    try {
        const page  = parseInt(req.query.page)  || 1;
        const limit = Math.min(parseInt(req.query.limit) || 20, 100);
        const lang  = req.query.lang || 'es';
        const q     = req.query.q   || 'drama corto';
        const data  = await dmFetch(
            `/videos?search=${encodeURIComponent(q)}&language=${lang}&fields=${DM_FIELDS}&limit=${limit}&page=${page}&sort=recent`
        );
        res.json({ transmisiones: (data.list || []).map(mapDM), total: data.total || 0, page });
    } catch (e) { res.status(500).json({ error: 'Error drama-shorts', detail: e.message }); }
});

// 2. Buscar por título
app.get('/drama-shorts/buscar', async (req, res) => {
    try {
        const titulo = req.query.titulo;
        if (!titulo) return res.status(400).json({ error: 'Falta titulo' });
        const lang  = req.query.lang  || 'es';
        const page  = parseInt(req.query.page)  || 1;
        const limit = Math.min(parseInt(req.query.limit) || 20, 100);
        const data  = await dmFetch(
            `/videos?search=${encodeURIComponent(titulo)}&language=${lang}&fields=${DM_FIELDS}&limit=${limit}&page=${page}`
        );
        res.json({ resultados: (data.list || []).map(mapDM), total: data.total || 0 });
    } catch (e) { res.status(500).json({ error: 'Error buscar', detail: e.message }); }
});

// 3. Video por ID
app.get('/drama-shorts/video/:id', async (req, res) => {
    try {
        const data = await dmFetch(`/video/${req.params.id}?fields=${DM_FIELDS},description`);
        res.json(mapDM(data));
    } catch (e) { res.status(500).json({ error: 'Error video', detail: e.message }); }
});

// 4. Videos de un canal
app.get('/drama-shorts/canal/:username', async (req, res) => {
    try {
        const page  = parseInt(req.query.page)  || 1;
        const limit = Math.min(parseInt(req.query.limit) || 20, 100);
        const data  = await dmFetch(
            `/user/${req.params.username}/videos?fields=${DM_FIELDS}&limit=${limit}&page=${page}`
        );
        res.json({ canal: req.params.username, videos: (data.list || []).map(mapDM), total: data.total || 0 });
    } catch (e) { res.status(500).json({ error: 'Error canal', detail: e.message }); }
});

// 5. Populares
app.get('/drama-shorts/populares', async (req, res) => {
    try {
        const lang  = req.query.lang  || 'es';
        const page  = parseInt(req.query.page)  || 1;
        const limit = Math.min(parseInt(req.query.limit) || 20, 100);
        const data  = await dmFetch(
            `/videos?search=drama&language=${lang}&fields=${DM_FIELDS}&limit=${limit}&page=${page}&sort=visited`
        );
        res.json({ populares: (data.list || []).map(mapDM), total: data.total || 0 });
    } catch (e) { res.status(500).json({ error: 'Error populares', detail: e.message }); }
});

// 6. Tendencias
app.get('/drama-shorts/tendencias', async (req, res) => {
    try {
        const lang  = req.query.lang  || 'es';
        const page  = parseInt(req.query.page)  || 1;
        const limit = Math.min(parseInt(req.query.limit) || 20, 100);
        const data  = await dmFetch(
            `/videos?search=drama&language=${lang}&fields=${DM_FIELDS}&limit=${limit}&page=${page}&sort=trending`
        );
        res.json({ tendencias: (data.list || []).map(mapDM), total: data.total || 0 });
    } catch (e) { res.status(500).json({ error: 'Error tendencias', detail: e.message }); }
});

// ── HLS PROXY ─────────────────────────────────────────────────────────────────
// Proxies any m3u8/ts/segment URL through the server to bypass CORS/Referer
// restrictions. Rewrites m3u8 playlists so all sub-URLs also go through proxy.

const http  = require('http');
const https = require('https');

function proxyFetch(targetUrl, reqHeaders) {
    return new Promise((resolve, reject) => {
        const parsed  = new URL(targetUrl);
        const lib     = parsed.protocol === 'https:' ? https : http;
        const options = {
            hostname: parsed.hostname,
            port:     parsed.port || (parsed.protocol === 'https:' ? 443 : 80),
            path:     parsed.pathname + parsed.search,
            method:   'GET',
            headers:  {
                'User-Agent':      'Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36',
                'Accept':          '*/*',
                'Accept-Language': 'es-ES,es;q=0.9',
                'Origin':          parsed.origin,
                'Referer':         parsed.origin + '/',
            }
        };
        const req = lib.request(options, resolve);
        req.on('error', reject);
        req.end();
    });
}

function resolveUrl(base, relative) {
    try { return new URL(relative).href; } catch { /* relative */ }
    try { return new URL(relative, base).href; } catch { return relative; }
}

// Rewrite m3u8 playlists so every segment/sub-playlist goes through /proxy
function rewriteM3u8(content, baseUrl, proxyBase) {
    return content.split('\n').map(line => {
        const trimmed = line.trim();
        if (!trimmed || trimmed.startsWith('#')) return line;
        const abs = resolveUrl(baseUrl, trimmed);
        return `${proxyBase}?url=${encodeURIComponent(abs)}`;
    }).join('\n');
}

app.get('/proxy', async (req, res) => {
    const targetUrl = req.query.url;
    if (!targetUrl) return res.status(400).json({ error: 'Falta ?url=' });

    let parsed;
    try { parsed = new URL(targetUrl); }
    catch { return res.status(400).json({ error: 'URL inválida' }); }

    try {
        const upstream = await proxyFetch(targetUrl, req.headers);
        const ct = (upstream.headers['content-type'] || '').toLowerCase();
        const isM3u8 = ct.includes('mpegurl') || ct.includes('x-mpegurl') ||
                       targetUrl.includes('.m3u8');

        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Access-Control-Allow-Headers', '*');

        if (isM3u8) {
            res.setHeader('Content-Type', 'application/vnd.apple.mpegurl');
            let body = '';
            upstream.on('data', chunk => body += chunk.toString());
            upstream.on('end', () => {
                const proxyBase = `${getBaseUrl()}/proxy`;
                res.send(rewriteM3u8(body, targetUrl, proxyBase));
            });
        } else {
            res.setHeader('Content-Type', upstream.headers['content-type'] || 'application/octet-stream');
            if (upstream.headers['content-length'])
                res.setHeader('Content-Length', upstream.headers['content-length']);
            upstream.pipe(res);
        }
    } catch (e) {
        res.status(502).json({ error: 'No se pudo conectar al origen', detail: e.message });
    }
});

// Player page — serves a web HLS player for any proxied stream
app.get('/player', (req, res) => {
    const rawUrl   = req.query.url || '';
    const title    = req.query.title || 'Player';
    const proxyUrl = rawUrl
        ? `${getBaseUrl()}/proxy?url=${encodeURIComponent(rawUrl)}`
        : '';

    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.send(`<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>${title}</title>
<script src="https://cdn.jsdelivr.net/npm/hls.js@latest/dist/hls.min.js"></script>
<style>
*{box-sizing:border-box;margin:0;padding:0}
body{background:#0a0a0f;color:#fff;font-family:system-ui,sans-serif;min-height:100vh;display:flex;flex-direction:column;align-items:center;justify-content:flex-start;padding:16px}
h1{font-size:15px;font-weight:600;color:#aaa;margin-bottom:12px;text-align:center;max-width:600px;word-break:break-all}
.player-wrap{width:100%;max-width:720px;background:#111;border-radius:14px;overflow:hidden;box-shadow:0 0 0 1px #ffffff18}
video{width:100%;display:block;max-height:55vh;background:#000}
.controls{padding:14px 16px;display:flex;flex-direction:column;gap:10px}
.url-row{display:flex;gap:8px;align-items:center}
input{flex:1;background:#1a1a2e;border:1px solid #ffffff22;color:#00e5a0;border-radius:8px;padding:10px 12px;font-size:12px;font-family:monospace;outline:none}
input::placeholder{color:#555}
button{background:#00e5a0;color:#000;border:none;border-radius:8px;padding:10px 18px;font-size:13px;font-weight:700;cursor:pointer;white-space:nowrap}
button:active{opacity:.8}
.status{font-size:11px;color:#555;text-align:center}
.status.ok{color:#00e5a0}
.status.err{color:#ff5252}
.copy-row{display:flex;gap:8px}
.copy-btn{background:#ffffff10;color:#aaa;border:1px solid #ffffff18;border-radius:8px;padding:8px 14px;font-size:12px;cursor:pointer;flex:1;text-align:center}
.copy-btn:active{background:#ffffff20}
</style>
</head>
<body>
<h1 id="ttl">${title || 'Ultragol · HLS Player'}</h1>
<div class="player-wrap">
  <video id="v" controls playsinline></video>
  <div class="controls">
    <div class="url-row">
      <input id="inp" placeholder="Pega aquí la URL m3u8 o directo..." value="${rawUrl}"/>
      <button onclick="load()">▶ Play</button>
    </div>
    <div id="status" class="status">${proxyUrl ? 'Cargando stream…' : 'Ingresa una URL y pulsa Play'}</div>
    <div class="copy-row">
      <div class="copy-btn" onclick="copyProxy()">📋 Copiar URL proxy</div>
      <div class="copy-btn" onclick="copyDirect()">🔗 Copiar URL directa</div>
    </div>
  </div>
</div>
<script>
const base = '${getBaseUrl()}';
let hls = null;

function proxyFor(url){ return base+'/proxy?url='+encodeURIComponent(url); }

function load(){
  const raw = document.getElementById('inp').value.trim();
  if(!raw){ setStatus('Ingresa una URL primero','err'); return; }
  history.replaceState(null,'','/player?url='+encodeURIComponent(raw));
  playUrl(raw);
}

function playUrl(raw){
  const src = proxyFor(raw);
  const vid = document.getElementById('v');
  if(hls){ hls.destroy(); hls=null; }
  setStatus('Conectando…','');
  if(Hls.isSupported()){
    hls = new Hls({xhrSetup:x=>x.withCredentials=false});
    hls.loadSource(src);
    hls.attachMedia(vid);
    hls.on(Hls.Events.MANIFEST_PARSED, ()=>{ vid.play(); setStatus('✓ Stream activo — reproduciendo','ok'); });
    hls.on(Hls.Events.ERROR, (_,d)=>{ if(d.fatal) setStatus('Error: '+d.details,'err'); });
  } else if(vid.canPlayType('application/vnd.apple.mpegurl')){
    vid.src = src; vid.play();
    setStatus('✓ Reproduciendo (nativo)','ok');
  } else {
    setStatus('Este navegador no soporta HLS','err');
  }
}

function setStatus(msg, cls){
  const el = document.getElementById('status');
  el.textContent = msg;
  el.className = 'status '+(cls||'');
}

function copyProxy(){
  const raw = document.getElementById('inp').value.trim();
  if(!raw) return;
  navigator.clipboard.writeText(proxyFor(raw)).then(()=>setStatus('URL proxy copiada','ok'));
}

function copyDirect(){
  const raw = document.getElementById('inp').value.trim();
  if(!raw) return;
  navigator.clipboard.writeText(raw).then(()=>setStatus('URL directa copiada','ok'));
}

${proxyUrl ? `window.addEventListener('load',()=>playUrl(${JSON.stringify(rawUrl)}));` : ''}
</script>
</body>
</html>`);
});

// ── GLOBAL ERROR HANDLER ──────────────────────────────────────────────────────
app.use((err, req, res, _next) => {
    if (err.code === 'LIMIT_FILE_SIZE')
        return res.status(413).json({ error: 'Archivo demasiado grande (máx 100 MB)' });
    if (err.message && err.message.includes('APK'))
        return res.status(400).json({ error: err.message });
    console.error('Error inesperado:', err.message);
    res.status(500).json({ error: 'Error interno del servidor' });
});

// ─────────────────────────────────────────────────────────────────────────────
app.listen(PORT, '0.0.0.0', () => console.log(`Servidor activo en puerto ${PORT}`));
