package com.ultragol.app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Native DLNA/UPnP manager — no external library needed.
 * Discovery: SSDP M-SEARCH multicast to 239.255.255.250:1900
 * Control: HTTP SOAP to device's AVTransport control URL
 */
public class DLNAManager {

    private static final String TAG       = "DLNAManager";
    private static final String MCAST_IP  = "239.255.255.250";
    private static final int    MCAST_PORT = 1900;
    private static final String MSEARCH   =
        "M-SEARCH * HTTP/1.1\r\n"
        + "HOST: 239.255.255.250:1900\r\n"
        + "MAN: \"ssdp:discover\"\r\n"
        + "MX: 3\r\n"
        + "ST: ssdp:all\r\n\r\n";

    private static DLNAManager instance;
    private final Handler main = new Handler(Looper.getMainLooper());

    public static synchronized DLNAManager getInstance() {
        if (instance == null) instance = new DLNAManager();
        return instance;
    }

    public interface DeviceListener {
        void onDeviceFound(CastDevice device);
        void onDiscoveryComplete();
    }

    // ── Discovery ─────────────────────────────────────────────────────────────

    public void discoverDevices(DeviceListener listener) {
        new Thread(() -> {
            Set<String> seen = new HashSet<>();
            try {
                DatagramSocket sock = new DatagramSocket();
                sock.setSoTimeout(1000);
                InetAddress grp = InetAddress.getByName(MCAST_IP);
                byte[] msg = MSEARCH.getBytes("UTF-8");

                for (int i = 0; i < 3; i++) {
                    sock.send(new DatagramPacket(msg, msg.length, grp, MCAST_PORT));
                    Thread.sleep(150);
                }

                long deadline = System.currentTimeMillis() + 8_000L;
                while (System.currentTimeMillis() < deadline) {
                    byte[] buf = new byte[4096];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    try {
                        sock.receive(pkt);
                        String resp = new String(pkt.getData(), 0, pkt.getLength(), "UTF-8");
                        String loc  = extractHeader(resp, "LOCATION");
                        if (loc != null && !seen.contains(loc)) {
                            seen.add(loc);
                            CastDevice dev = fetchDeviceDesc(loc);
                            if (dev != null) main.post(() -> listener.onDeviceFound(dev));
                        }
                    } catch (SocketTimeoutException ignored) {}
                }
                sock.close();
            } catch (Exception e) {
                Log.w(TAG, "SSDP error: " + e.getMessage());
            }
            main.post(listener::onDiscoveryComplete);
        }).start();
    }

    // ── Control ──────────────────────────────────────────────────────────────

    public void playVideo(CastDevice device, String videoUrl, Runnable onError) {
        new Thread(() -> {
            try {
                String setUri =
                    soap("SetAVTransportURI",
                         "<InstanceID>0</InstanceID>"
                         + "<CurrentURI>" + xmlEsc(videoUrl) + "</CurrentURI>"
                         + "<CurrentURIMetaData></CurrentURIMetaData>");
                int code = soapPost(device.getDlnaControlUrl(),
                    "urn:schemas-upnp-org:service:AVTransport:1#SetAVTransportURI", setUri);
                if (code / 100 != 2) { main.post(onError); return; }
                Thread.sleep(600);
                String play = soap("Play",
                    "<InstanceID>0</InstanceID><Speed>1</Speed>");
                soapPost(device.getDlnaControlUrl(),
                    "urn:schemas-upnp-org:service:AVTransport:1#Play", play);
            } catch (Exception e) { main.post(onError); }
        }).start();
    }

    public void stopVideo(CastDevice device) {
        if (device == null || device.getDlnaControlUrl() == null) return;
        new Thread(() -> {
            try {
                soapPost(device.getDlnaControlUrl(),
                    "urn:schemas-upnp-org:service:AVTransport:1#Stop",
                    soap("Stop", "<InstanceID>0</InstanceID>"));
            } catch (Exception ignored) {}
        }).start();
    }

    public void togglePause(CastDevice device, boolean pause) {
        if (device == null || device.getDlnaControlUrl() == null) return;
        new Thread(() -> {
            try {
                String action = pause ? "Pause" : "Play";
                String body   = pause
                    ? soap("Pause", "<InstanceID>0</InstanceID>")
                    : soap("Play",  "<InstanceID>0</InstanceID><Speed>1</Speed>");
                soapPost(device.getDlnaControlUrl(),
                    "urn:schemas-upnp-org:service:AVTransport:1#" + action, body);
            } catch (Exception ignored) {}
        }).start();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private CastDevice fetchDeviceDesc(String locationUrl) {
        try {
            URL url = new URL(locationUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.connect();
            if (conn.getResponseCode() != 200) return null;
            String xml = readStream(conn.getInputStream());
            conn.disconnect();

            String name = xmlTag(xml, "friendlyName");
            if (name == null || name.isEmpty()) return null;

            String ctrlUrl = avTransportControl(xml);
            if (ctrlUrl == null) return null;

            // Resolve relative URL
            String base = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
            String full = ctrlUrl.startsWith("http") ? ctrlUrl : base + ctrlUrl;

            CastDevice dev = new CastDevice(locationUrl, name, CastDevice.Type.DLNA);
            dev.setIpAddress(url.getHost());
            dev.setPort(url.getPort());
            dev.setDlnaControlUrl(full);
            return dev;
        } catch (Exception e) {
            return null;
        }
    }

    private String avTransportControl(String xml) {
        int idx = xml.indexOf("AVTransport");
        if (idx < 0) return null;
        int c = xml.indexOf("<controlURL>", idx);
        if (c < 0) return null;
        int e = xml.indexOf("</controlURL>", c);
        return e >= 0 ? xml.substring(c + "<controlURL>".length(), e).trim() : null;
    }

    private static String soap(String action, String params) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "<s:Body><u:" + action
            + " xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">"
            + params
            + "</u:" + action + "></s:Body></s:Envelope>";
    }

    private static int soapPost(String endpoint, String action, String body) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
        c.setRequestProperty("SOAPAction", "\"" + action + "\"");
        c.setDoOutput(true);
        c.setConnectTimeout(5000);
        c.setReadTimeout(5000);
        byte[] b = body.getBytes("UTF-8");
        c.setRequestProperty("Content-Length", String.valueOf(b.length));
        c.getOutputStream().write(b);
        int code = c.getResponseCode();
        c.disconnect();
        return code;
    }

    private static String extractHeader(String resp, String name) {
        for (String line : resp.split("\r\n")) {
            if (line.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase() + ":"))
                return line.substring(name.length() + 1).trim();
        }
        return null;
    }

    private static String xmlTag(String xml, String tag) {
        int s = xml.indexOf("<" + tag + ">");
        if (s < 0) s = xml.indexOf("<" + tag + " ");
        if (s < 0) return null;
        s = xml.indexOf(">", s) + 1;
        int e = xml.indexOf("</" + tag + ">", s);
        return e >= 0 ? xml.substring(s, e).trim() : null;
    }

    private static String readStream(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String l;
        while ((l = r.readLine()) != null) sb.append(l).append('\n');
        return sb.toString();
    }

    private static String xmlEsc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
