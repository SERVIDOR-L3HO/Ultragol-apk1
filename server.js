const express  = require('express');
const multer   = require('multer');
const path     = require('path');
const fs       = require('fs');
const webPush  = require('web-push');

const app  = express();
const PORT = process.env.PORT || 5000;
const ADMIN_KEY = process.env.ADMIN_KEY;

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
const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, 'apks/'),
    filename:    (req, file, cb) => {
        const app = req.query.app || 'ultragol1';
        cb(null, `${app}.apk`);
    }
});
const upload = multer({ storage });

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
    if (appName === 'ultragol1') return 'ultragol1.apk';
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
    const apkPath = path.join(__dirname, 'apks', 'ultragol1.apk');
    if (!fs.existsSync(apkPath)) return res.status(404).json({ error: 'APK no encontrado' });
    res.download(apkPath, 'ultragol1.apk');
});

// ── ADMIN UPDATE ──────────────────────────────────────────────────────────────
app.post('/admin/update', upload.single('apk'), (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });

    const { versionCode, versionName, changelog, forceUpdate } = req.body;
    if (!versionCode || !versionName)
        return res.status(400).json({ error: 'Faltan versionCode o versionName' });

    const appName = req.query.app || 'ultragol1';
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
app.get('/admin/info', (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });

    const appName  = req.query.app || 'ultragol1';
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
app.post('/admin/notify', async (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });

    const { title, message, type, expiresIn } = req.body;
    if (!title || !message)
        return res.status(400).json({ error: 'Faltan title y message' });

    const notif = {
        id:        Date.now().toString(),
        title:     title.trim(),
        message:   message.trim(),
        type:      type || 'info',           // info | update | alert
        createdAt: new Date().toISOString(),
        expiresAt: expiresIn
            ? new Date(Date.now() + parseInt(expiresIn) * 60 * 60 * 1000).toISOString()
            : null,
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
app.delete('/admin/notify/:id', (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });

    const notifs  = readJSON(NOTIF_FILE, []);
    const filtered = notifs.filter(n => n.id !== req.params.id);
    writeJSON(NOTIF_FILE, filtered);
    res.json({ ok: true, removed: notifs.length - filtered.length });
});

// ── NOTIFICATIONS — ADMIN: LIST ALL ───────────────────────────────────────────
app.get('/admin/notifications', (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY)
        return res.status(401).json({ error: 'No autorizado' });
    res.json({ notifications: readJSON(NOTIF_FILE, []) });
});

// ── PUSH SUBSCRIBE ─────────────────────────────────────────────────────────────
app.post('/push/subscribe', (req, res) => {
    const sub = req.body;
    if (!sub || !sub.endpoint) return res.status(400).json({ error: 'Suscripción inválida' });
    const subs = readJSON(SUBS_FILE, []);
    const exists = subs.some(s => s.endpoint === sub.endpoint);
    if (!exists) { subs.push(sub); writeJSON(SUBS_FILE, subs); }
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
const GOL_API = 'https://ultragol-api-3--maricarmen43549.replit.app/gol-3';

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

// ─────────────────────────────────────────────────────────────────────────────
app.listen(PORT, '0.0.0.0', () => console.log(`Servidor activo en puerto ${PORT}`));
