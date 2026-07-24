package com.ultragol.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lightweight HTTP server that runs on the device and proxies any video/stream
 * URL at http://[local-ip]:PORT/v.mkv — identical to how apps like dvex/movp
 * expose a "Transmisión local" URL for VLC, PCs, or TVs on the same Wi-Fi.
 *
 * Usage:
 *   LocalStreamServer.getInstance().start(streamUrl);
 *   String localUrl = LocalStreamServer.getInstance().getLocalUrl(wifiIp);
 *   LocalStreamServer.getInstance().stop();
 */
public class LocalStreamServer {

    private static final String TAG  = "LocalStreamServer";
    public  static final int    PORT = 4445;
    public  static final String PATH = "/v.mkv";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile LocalStreamServer INSTANCE;
    public static LocalStreamServer getInstance() {
        if (INSTANCE == null) synchronized (LocalStreamServer.class) {
            if (INSTANCE == null) INSTANCE = new LocalStreamServer();
        }
        return INSTANCE;
    }
    private LocalStreamServer() {}

    // ── State ─────────────────────────────────────────────────────────────────
    private volatile String        streamUrl;
    private volatile ServerSocket  serverSocket;
    private final AtomicBoolean    running   = new AtomicBoolean(false);
    private final ExecutorService  executor  = Executors.newCachedThreadPool();

    // ── Public API ────────────────────────────────────────────────────────────

    /** Start (or restart) serving the given URL. Thread-safe. */
    public synchronized void start(String url) {
        stop();
        if (url == null || url.isEmpty()) return;
        this.streamUrl = url;
        running.set(true);
        executor.execute(this::acceptLoop);
        Log.i(TAG, "Server started on port " + PORT + " → " + url);
    }

    /** Stop the server. */
    public synchronized void stop() {
        running.set(false);
        if (serverSocket != null) {
            try { serverSocket.close(); } catch (IOException ignored) {}
            serverSocket = null;
        }
        Log.i(TAG, "Server stopped");
    }

    public boolean isRunning() { return running.get(); }

    /** Returns the local URL to display, e.g. http://192.168.1.5:4445/v.mkv */
    public String getLocalUrl(String wifiIp) {
        return "http://" + wifiIp + ":" + PORT + PATH;
    }

    // ── Accept loop ───────────────────────────────────────────────────────────

    private void acceptLoop() {
        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
            while (running.get()) {
                try {
                    Socket client = serverSocket.accept();
                    executor.execute(() -> handleClient(client));
                } catch (IOException e) {
                    if (running.get()) Log.w(TAG, "accept error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            if (running.get()) Log.e(TAG, "Server error: " + e.getMessage());
        }
    }

    // ── Handle one client connection ──────────────────────────────────────────

    private void handleClient(Socket client) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String requestLine = reader.readLine();
            if (requestLine == null) { client.close(); return; }

            // Drain headers
            String line;
            long rangeStart = 0;
            String rangeHeader = null;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("range:")) {
                    rangeHeader = line.substring(6).trim(); // e.g. "bytes=0-"
                    try {
                        String bytesPart = rangeHeader.replace("bytes=", "").split("-")[0].trim();
                        rangeStart = Long.parseLong(bytesPart);
                    } catch (Exception ignored) {}
                }
            }

            // Only serve GET /v.mkv (or any path — be permissive)
            boolean isGet = requestLine.startsWith("GET ");
            if (!isGet) {
                sendError(client, 405, "Method Not Allowed");
                return;
            }

            proxyStream(client, rangeStart);
        } catch (Exception e) {
            Log.w(TAG, "Client error: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    // ── Proxy the upstream URL to the client ──────────────────────────────────

    private void proxyStream(Socket client, long rangeStart) throws IOException {
        String url = streamUrl;
        if (url == null || url.isEmpty()) { sendError(client, 503, "No stream URL"); return; }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(30_000);
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36");
            conn.setRequestProperty("Accept", "*/*");
            if (rangeStart > 0)
                conn.setRequestProperty("Range", "bytes=" + rangeStart + "-");
            conn.connect();

            int upStatus = conn.getResponseCode();
            String ct    = conn.getContentType();
            if (ct == null) ct = guessMime(url);
            long   cl    = conn.getContentLengthLong();

            OutputStream out = client.getOutputStream();

            // Write HTTP response header
            StringBuilder header = new StringBuilder();
            if (rangeStart > 0 && upStatus == 206) {
                header.append("HTTP/1.1 206 Partial Content\r\n");
                String cr = conn.getHeaderField("Content-Range");
                if (cr != null) header.append("Content-Range: ").append(cr).append("\r\n");
            } else {
                header.append("HTTP/1.1 200 OK\r\n");
            }
            header.append("Content-Type: ").append(ct).append("\r\n");
            if (cl > 0) header.append("Content-Length: ").append(cl).append("\r\n");
            header.append("Accept-Ranges: bytes\r\n");
            header.append("Connection: close\r\n");
            header.append("Access-Control-Allow-Origin: *\r\n");
            header.append("\r\n");
            out.write(header.toString().getBytes("UTF-8"));

            // Pipe body
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[65536];
            int read;
            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
            }
            out.flush();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private void sendError(Socket client, int code, String msg) {
        try {
            String body = "<h1>" + code + " " + msg + "</h1>";
            String resp = "HTTP/1.1 " + code + " " + msg + "\r\n" +
                "Content-Type: text/html\r\nContent-Length: " + body.length() + "\r\n\r\n" + body;
            client.getOutputStream().write(resp.getBytes("UTF-8"));
        } catch (IOException ignored) {}
    }

    private String guessMime(String url) {
        url = url.toLowerCase();
        if (url.contains(".m3u8"))  return "application/vnd.apple.mpegurl";
        if (url.contains(".ts"))    return "video/mp2t";
        if (url.contains(".mp4"))   return "video/mp4";
        if (url.contains(".mkv"))   return "video/x-matroska";
        if (url.contains(".webm"))  return "video/webm";
        if (url.contains(".avi"))   return "video/x-msvideo";
        return "video/mp4";
    }
}
