const express = require('express');
const multer  = require('multer');
const path    = require('path');
const fs      = require('fs');

const app  = express();
const PORT = process.env.PORT || 5000;
const ADMIN_KEY = process.env.ADMIN_KEY || 'ultragol2024';

app.use(express.json());
app.use(express.static('public'));

const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, 'apks/'),
    filename:    (req, file, cb) => {
        const app = req.query.app || 'ultragol';
        cb(null, `${app}.apk`);
    }
});
const upload = multer({ storage });

const VERSION_FILE = path.join(__dirname, 'version.json');
const ULTRA1_VERSION_FILE = path.join(__dirname, 'version_ultra1.json');
const ULTRAGOL1_VERSION_FILE = path.join(__dirname, 'version.json');

function readVersion(file) {
    return JSON.parse(fs.readFileSync(file, 'utf8'));
}

function writeVersion(file, data) {
    fs.writeFileSync(file, JSON.stringify(data, null, 2));
}

// ── Ultragol endpoints ────────────────────────────────────────────────────────

app.get('/version', (req, res) => {
    try {
        res.json(readVersion(VERSION_FILE));
    } catch {
        res.status(500).json({ error: 'No se pudo leer la versión' });
    }
});

app.get('/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultragol.apk');
    if (!fs.existsSync(apkPath)) {
        return res.status(404).json({ error: 'APK no encontrado' });
    }
    res.download(apkPath, 'ultragol.apk');
});

// ── Ultra1 endpoints ──────────────────────────────────────────────────────────

app.get('/ultra1/version', (req, res) => {
    try {
        res.json(readVersion(ULTRA1_VERSION_FILE));
    } catch {
        res.status(500).json({ error: 'No se pudo leer la versión' });
    }
});

app.get('/ultra1/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultra1.apk');
    if (!fs.existsSync(apkPath)) {
        return res.status(404).json({ error: 'APK no encontrado' });
    }
    res.download(apkPath, 'ultra1.apk');
});

// ── Ultragol1 endpoints ───────────────────────────────────────────────────────

app.get('/ultragol1/version', (req, res) => {
    try {
        res.json(readVersion(ULTRAGOL1_VERSION_FILE));
    } catch {
        res.status(500).json({ error: 'No se pudo leer la versión' });
    }
});

app.get('/ultragol1/download', (req, res) => {
    const apkPath = path.join(__dirname, 'apks', 'ultragol1.apk');
    if (!fs.existsSync(apkPath)) {
        return res.status(404).json({ error: 'APK no encontrado' });
    }
    res.download(apkPath, 'ultragol1.apk');
});

// ── Admin endpoints ───────────────────────────────────────────────────────────

app.post('/admin/update', upload.single('apk'), (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY) {
        return res.status(401).json({ error: 'No autorizado' });
    }

    const { versionCode, versionName, changelog, forceUpdate } = req.body;
    if (!versionCode || !versionName) {
        return res.status(400).json({ error: 'Faltan versionCode o versionName' });
    }

    const appName  = req.query.app || 'ultragol';
    const vFile    = appName === 'ultra1' ? ULTRA1_VERSION_FILE : VERSION_FILE;
    const host     = `${req.protocol}://${req.get('host')}`;
    const dlPath   = appName === 'ultra1' ? '/ultra1/download'
                   : appName === 'ultragol1' ? '/ultragol1/download'
                   : '/download';

    const data = {
        versionCode:  parseInt(versionCode),
        versionName,
        changelog:    changelog || '',
        downloadUrl:  `${host}${dlPath}`,
        forceUpdate:  forceUpdate === 'true' || forceUpdate === true,
        updatedAt:    new Date().toISOString()
    };

    writeVersion(vFile, data);
    res.json({ ok: true, version: data });
});

// ── Gol API proxy ─────────────────────────────────────────────────────────────

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
                map.set(key, {
                    titulo: item.titulo,
                    hora:   item.hora,
                    fecha:  item.fecha,
                    liga:   item.liga,
                    logoUrl: item.logoUrl,
                    servidores: []
                });
            }
            map.get(key).servidores.push({ canal: item.canal, url: item.url });
        }

        res.json({ partidos: Array.from(map.values()) });
    } catch (err) {
        res.status(500).json({ error: 'No se pudo obtener la cartelera', detail: err.message });
    }
});

// ─────────────────────────────────────────────────────────────────────────────

app.get('/admin/info', (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY) {
        return res.status(401).json({ error: 'No autorizado' });
    }
    const appName  = req.query.app || 'ultragol';
    const vFile    = appName === 'ultra1'    ? ULTRA1_VERSION_FILE
                   : appName === 'ultragol1' ? ULTRAGOL1_VERSION_FILE
                   : VERSION_FILE;
    const apkFile  = appName === 'ultra1'    ? 'ultra1.apk'
                   : appName === 'ultragol1' ? 'ultragol1.apk'
                   : 'ultragol.apk';
    const apkPath  = path.join(__dirname, 'apks', apkFile);
    const apkExists = fs.existsSync(apkPath);
    const apkSize   = apkExists ? (fs.statSync(apkPath).size / 1024 / 1024).toFixed(2) + ' MB' : null;
    res.json({ version: readVersion(vFile), apkExists, apkSize });
});

app.listen(PORT, '0.0.0.0', () => console.log(`Servidor activo en puerto ${PORT}`));
