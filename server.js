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
    filename:    (req, file, cb) => cb(null, 'ultragol.apk')
});
const upload = multer({ storage });

const VERSION_FILE = path.join(__dirname, 'version.json');

function readVersion() {
    return JSON.parse(fs.readFileSync(VERSION_FILE, 'utf8'));
}

function writeVersion(data) {
    fs.writeFileSync(VERSION_FILE, JSON.stringify(data, null, 2));
}

app.get('/version', (req, res) => {
    try {
        res.json(readVersion());
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

app.post('/admin/update', upload.single('apk'), (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY) {
        return res.status(401).json({ error: 'No autorizado' });
    }

    const { versionCode, versionName, changelog, forceUpdate } = req.body;
    if (!versionCode || !versionName) {
        return res.status(400).json({ error: 'Faltan versionCode o versionName' });
    }

    const host = `${req.protocol}://${req.get('host')}`;
    const data = {
        versionCode:  parseInt(versionCode),
        versionName,
        changelog:    changelog || '',
        downloadUrl:  `${host}/download`,
        forceUpdate:  forceUpdate === 'true' || forceUpdate === true,
        updatedAt:    new Date().toISOString()
    };

    writeVersion(data);
    res.json({ ok: true, version: data });
});

app.get('/admin/info', (req, res) => {
    if (req.headers['x-admin-key'] !== ADMIN_KEY) {
        return res.status(401).json({ error: 'No autorizado' });
    }
    const apkPath = path.join(__dirname, 'apks', 'ultragol.apk');
    const apkExists = fs.existsSync(apkPath);
    const apkSize = apkExists ? (fs.statSync(apkPath).size / 1024 / 1024).toFixed(2) + ' MB' : null;
    res.json({ version: readVersion(), apkExists, apkSize });
});

app.listen(PORT, () => console.log(`Servidor activo en puerto ${PORT}`));
