package com.ultragol.app;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * AirPlay device discovery (via Android NsdManager / mDNS) and control
 * using the AirPlay 1 HTTP protocol.
 *
 * Discovery: mDNS service type _airplay._tcp
 * Play:  POST http://<ip>:<port>/play   body: "Content-Location: <url>\nStart-Position: 0.0\n"
 * Pause: POST http://<ip>:<port>/rate?value=0.0
 * Resume:POST http://<ip>:<port>/rate?value=1.0
 * Stop:  POST http://<ip>:<port>/stop
 */
public class AirPlayManager {

    private static final String TAG          = "AirPlayManager";
    private static final String SERVICE_TYPE = "_airplay._tcp.";

    private static AirPlayManager instance;
    private final Handler main = new Handler(Looper.getMainLooper());
    private NsdManager.DiscoveryListener activeListener;
    private NsdManager activeNsd;

    public static synchronized AirPlayManager getInstance() {
        if (instance == null) instance = new AirPlayManager();
        return instance;
    }

    public interface DeviceListener {
        void onDeviceFound(CastDevice device);
        void onDiscoveryComplete();
    }

    // ── Discovery ─────────────────────────────────────────────────────────────

    public void discoverDevices(Context ctx, DeviceListener listener) {
        NsdManager nsd = (NsdManager) ctx.getSystemService(Context.NSD_SERVICE);
        if (nsd == null) { main.post(listener::onDiscoveryComplete); return; }

        Set<String> seen = Collections.synchronizedSet(new HashSet<>());

        NsdManager.DiscoveryListener dl = new NsdManager.DiscoveryListener() {
            @Override public void onStartDiscoveryFailed(String t, int e) {
                main.post(listener::onDiscoveryComplete);
            }
            @Override public void onDiscoveryStarted(String t)     {}
            @Override public void onDiscoveryStopped(String t)     {}
            @Override public void onStopDiscoveryFailed(String t, int e) {}
            @Override public void onServiceLost(NsdServiceInfo si) {}

            @Override
            public void onServiceFound(NsdServiceInfo si) {
                nsd.resolveService(si, new NsdManager.ResolveListener() {
                    @Override public void onResolveFailed(NsdServiceInfo s, int e) {}
                    @Override
                    public void onServiceResolved(NsdServiceInfo s) {
                        String ip = s.getHost() != null ? s.getHost().getHostAddress() : null;
                        if (ip == null) return;
                        String key = ip + ":" + s.getPort();
                        if (!seen.add(key)) return;
                        CastDevice dev = new CastDevice(key, s.getServiceName(), CastDevice.Type.AIRPLAY);
                        dev.setIpAddress(ip);
                        dev.setPort(s.getPort());
                        main.post(() -> listener.onDeviceFound(dev));
                    }
                });
            }
        };

        activeListener = dl;
        activeNsd      = nsd;

        try {
            nsd.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, dl);
        } catch (Exception e) {
            main.post(listener::onDiscoveryComplete);
            return;
        }

        main.postDelayed(() -> {
            stopDiscovery();
            listener.onDiscoveryComplete();
        }, 8_000);
    }

    public void stopDiscovery() {
        if (activeNsd != null && activeListener != null) {
            try { activeNsd.stopServiceDiscovery(activeListener); } catch (Exception ignored) {}
            activeNsd      = null;
            activeListener = null;
        }
    }

    // ── Control ──────────────────────────────────────────────────────────────

    public void playVideo(CastDevice device, String videoUrl, long startMs, Runnable onError) {
        new Thread(() -> {
            try {
                float sec  = startMs / 1000f;
                String body = "Content-Location: " + videoUrl + "\nStart-Position: " + sec + "\n";
                int code = airPlayPost(device, "/play", "text/parameters", body);
                if (code / 100 != 2 && onError != null) main.post(onError);
            } catch (Exception e) {
                if (onError != null) main.post(onError);
            }
        }).start();
    }

    public void stopVideo(CastDevice device) {
        if (device == null) return;
        new Thread(() -> {
            try { airPlayPost(device, "/stop", "", ""); }
            catch (Exception ignored) {}
        }).start();
    }

    public void togglePause(CastDevice device, boolean pause, Runnable onError) {
        if (device == null) return;
        new Thread(() -> {
            try {
                String path = pause ? "/rate?value=0.0" : "/rate?value=1.0";
                int code = airPlayPost(device, path, "", "");
                if (code / 100 != 2 && onError != null) main.post(onError);
            } catch (Exception e) {
                if (onError != null) main.post(onError);
            }
        }).start();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private int airPlayPost(CastDevice dev, String path, String contentType, String body)
            throws IOException {
        URL url = new URL("http://" + dev.getIpAddress() + ":" + dev.getPort() + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "MediaControl/1.0");
        if (!contentType.isEmpty()) conn.setRequestProperty("Content-Type", contentType);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        if (!body.isEmpty()) {
            byte[] b = body.getBytes("UTF-8");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", String.valueOf(b.length));
            conn.getOutputStream().write(b);
        }
        int code = conn.getResponseCode();
        conn.disconnect();
        return code;
    }
}
