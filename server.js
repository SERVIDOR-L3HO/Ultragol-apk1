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
