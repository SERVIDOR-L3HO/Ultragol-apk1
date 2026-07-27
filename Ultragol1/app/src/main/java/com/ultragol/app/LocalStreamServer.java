package com.ultragol.app;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lightweight HTTP proxy server that runs on the device.
 *
 * Main entry:   http://[local-ip]:4445/v.mkv  → proxies the primary streamUrl
 * Segments:     http://[local-ip]:4445/seg?u=<encoded_url> → proxies that URL
 *
 * For HLS (m3u8) streams, the playlist is rewritten so every segment/variant
 * URL is replaced with a /seg?u=... link pointing back to this server.
 * This allows a TV or VLC on the same Wi-Fi to play token-protected streams
 * without directly reaching the origin CDN.
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
    private volatile String        localIp;          // needed to rewrite m3u8 URLs
    private volatile ServerSocket  serverSocket;
    private final AtomicBoolean    running   = new AtomicBoolean(false);
    private final ExecutorService  executor  = Executors.newCachedThreadPool();

    // ── Public API ────────────────────────────────────────────────────────────

    /** Start without knowing local IP (URL rewriting disabled for HLS). */
    public synchronized void start(String url) { start(url, null); }

    /** Start with local IP so HLS playlists can be rewritten for TV playback. */
    public synchronized void start(String url, String ip) {
        stop();
        if (url == null || url.isEmpty()) return;
        this.streamUrl = url;
        this.localIp   = ip;
        running.set(true);
        executor.execute(this::acceptLoop);
        Log.i(TAG, "Started port=" + PORT + " ip=" + ip + " → " + url);
    }

    public synchronized void stop() {
        running.set(false);
        if (serverSocket != null) {
            try { serverSocket.close(); } catch (IOException ignored) {}
            serverSocket = null;
        }
        Log.i(TAG, "Stopped");
    }

    public boolean isRunning() { return running.get(); }

    /** e.g. "http://192.168.1.5:4445/v.mkv" */
    public String getLocalUrl(String wifiIp) {
        return "http://" + wifiIp + ":" + PORT + PATH;
    }

    // ── Accept loop ───────────────────────────────────────────────────────────

    private void acceptLoop() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new java.net.InetSocketAddress(PORT));
            synchronized (this) { serverSocket = ss; }
            while (running.get()) {
                try {
                    Socket client = ss.accept();
                    executor.execute(() -> handleClient(client));
                } catch (IOException e) {
                    if (running.get()) Log.w(TAG, "accept: " + e.getMessage());
                    // break if socket was closed by stop()
                    if (ss.isClosed()) break;
                }
            }
        } catch (IOException e) {
            if (running.get()) Log.e(TAG, "server bind/accept: " + e.getMessage());
        } finally {
            if (ss != null && !ss.isClosed()) {
                try { ss.close(); } catch (IOException ignored) {}
            }
            running.set(false);
        }
    }

    // ── Handle one HTTP request ───────────────────────────────────────────────

    private void handleClient(Socket client) {
        try {
            BufferedReader rdr = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            String requestLine = rdr.readLine();
            if (requestLine == null) { client.close(); return; }

            String[] parts  = requestLine.split(" ");
            String   method = parts.length > 0 ? parts[0] : "GET";
            String   rawPath = parts.length > 1 ? parts[1] : "/";

            // Drain headers, pick up Range
            long   rangeStart = 0;
            String line;
            while ((line = rdr.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("range:")) {
                    try {
                        String b = line.substring(6).trim()
                                       .replace("bytes=", "").split("-")[0].trim();
                        rangeStart = Long.parseLong(b);
                    } catch (Exception ignored) {}
                }
            }

            if (!"GET".equalsIgnoreCase(method)) {
                sendError(client, 405, "Method Not Allowed");
                return;
            }

            // Decide which URL to proxy
            String targetUrl;
            if (rawPath.startsWith("/seg")) {
                // Segment: /seg?u=<encoded>
                String query = rawPath.contains("?")
                        ? rawPath.substring(rawPath.indexOf('?') + 1) : "";
                targetUrl = null;
                for (String param : query.split("&")) {
                    if (param.startsWith("u=")) {
                        try { targetUrl = URLDecoder.decode(param.substring(2), "UTF-8"); }
                        catch (Exception e) { targetUrl = param.substring(2); }
                        break;
                    }
                }
                if (targetUrl == null || targetUrl.isEmpty()) {
                    sendError(client, 400, "Bad Request: missing u= param");
                    return;
                }
            } else {
                // Main path → primary stream
                targetUrl = streamUrl;
                if (targetUrl == null || targetUrl.isEmpty()) {
                    sendError(client, 503, "No stream URL configured");
                    return;
                }
            }

            proxyUrl(client, targetUrl, rangeStart);

        } catch (Exception e) {
            Log.w(TAG, "handleClient: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    // ── Proxy one URL to the client ───────────────────────────────────────────

    private void proxyUrl(Socket client, String url, long rangeStart) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(12_000);
            conn.setReadTimeout(30_000);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
            conn.setRequestProperty("Accept", "*/*");
            // Pass origin domain as Referer so protected streams keep tokens
            try {
                URL parsed = new URL(url);
                conn.setRequestProperty("Referer",
                        parsed.getProtocol() + "://" + parsed.getHost() + "/");
                conn.setRequestProperty("Origin",
                        parsed.getProtocol() + "://" + parsed.getHost());
            } catch (Exception ignored) {}
            if (rangeStart > 0)
                conn.setRequestProperty("Range", "bytes=" + rangeStart + "-");
            conn.connect();

            int    status = conn.getResponseCode();
            String ct     = conn.getContentType();
            if (ct == null) ct = guessMime(url);
            long   cl     = conn.getContentLengthLong();

            boolean isPlaylist = isM3u(ct, url);
            OutputStream out   = client.getOutputStream();
            StringBuilder hdr  = new StringBuilder();

            if (isPlaylist) {
                // Read full playlist, rewrite URLs, send modified copy
                InputStream in   = conn.getInputStream();
                String raw       = readAll(in);
                String rewritten = rewritePlaylist(raw, url);
                byte[] body      = rewritten.getBytes("UTF-8");

                hdr.append("HTTP/1.1 200 OK\r\n");
                hdr.append("Content-Type: application/vnd.apple.mpegurl\r\n");
                hdr.append("Content-Length: ").append(body.length).append("\r\n");
                hdr.append("Access-Control-Allow-Origin: *\r\n");
                hdr.append("Cache-Control: no-cache\r\n");
                hdr.append("Connection: close\r\n\r\n");
                out.write(hdr.toString().getBytes("UTF-8"));
                out.write(body);
                out.flush();

            } else {
                // Raw byte stream
                if (rangeStart > 0 && status == 206) {
                    hdr.append("HTTP/1.1 206 Partial Content\r\n");
                    String cr = conn.getHeaderField("Content-Range");
                    if (cr != null) hdr.append("Content-Range: ").append(cr).append("\r\n");
                } else {
                    hdr.append("HTTP/1.1 200 OK\r\n");
                }
                hdr.append("Content-Type: ").append(ct).append("\r\n");
                if (cl > 0) hdr.append("Content-Length: ").append(cl).append("\r\n");
                hdr.append("Accept-Ranges: bytes\r\n");
                hdr.append("Access-Control-Allow-Origin: *\r\n");
                hdr.append("Connection: close\r\n\r\n");
                out.write(hdr.toString().getBytes("UTF-8"));

                InputStream in = conn.getInputStream();
                byte[] buf = new byte[65536];
                int read;
                while ((read = in.read(buf)) != -1) out.write(buf, 0, read);
                out.flush();
            }

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ── HLS playlist rewriting ────────────────────────────────────────────────

    /**
     * Rewrites every non-comment URL line and every URI="..." attribute in the
     * playlist so they all route through http://[localIp]:PORT/seg?u=<encoded>.
     */
    private String rewritePlaylist(String playlist, String baseUrl) {
        String proxyBase = buildProxyBase();
        if (proxyBase == null) {
            Log.w(TAG, "localIp not set — returning playlist unchanged");
            return playlist;
        }

        String baseDir = extractBaseDir(baseUrl);
        StringBuilder out = new StringBuilder();

        for (String line : playlist.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) { out.append(line).append("\n"); continue; }

            if (trimmed.startsWith("#")) {
                // Rewrite URI="..." attributes (e.g. #EXT-X-KEY, #EXT-X-MEDIA)
                if (trimmed.contains("URI=\"")) {
                    line = rewriteTagUri(line, baseDir, proxyBase);
                }
                out.append(line).append("\n");
            } else {
                // Segment / variant URL line
                String resolved = resolveUrl(trimmed, baseDir);
                out.append(proxyBase).append("/seg?u=")
                   .append(urlEncode(resolved)).append("\n");
            }
        }
        return out.toString();
    }

    private String rewriteTagUri(String tagLine, String baseDir, String proxyBase) {
        int start = tagLine.indexOf("URI=\"");
        if (start < 0) return tagLine;
        int uriStart = start + 5;
        int uriEnd   = tagLine.indexOf('"', uriStart);
        if (uriEnd < 0) return tagLine;
        String orig     = tagLine.substring(uriStart, uriEnd);
        String resolved = resolveUrl(orig, baseDir);
        return tagLine.substring(0, uriStart)
               + proxyBase + "/seg?u=" + urlEncode(resolved)
               + tagLine.substring(uriEnd);
    }

    /** Turns a possibly-relative URL into an absolute one given the playlist's base dir. */
    private String resolveUrl(String url, String baseDir) {
        if (url.startsWith("http://") || url.startsWith("https://")) return url;
        if (url.startsWith("//")) return "https:" + url;
        if (url.startsWith("/")) {
            try {
                URL base = new URL(baseDir);
                return base.getProtocol() + "://" + base.getHost()
                       + (base.getPort() != -1 ? ":" + base.getPort() : "") + url;
            } catch (Exception e) { return url; }
        }
        return baseDir + url;
    }

    /** Extracts the directory portion of a URL, e.g. "https://cdn.x.com/hls/" */
    private String extractBaseDir(String url) {
        try {
            URL u    = new URL(url);
            String p = u.getPath();
            int    s = p.lastIndexOf('/');
            String dir = s >= 0 ? p.substring(0, s + 1) : "/";
            return u.getProtocol() + "://" + u.getHost()
                   + (u.getPort() != -1 ? ":" + u.getPort() : "") + dir;
        } catch (Exception e) { return url; }
    }

    private String buildProxyBase() {
        if (localIp != null && !localIp.isEmpty())
            return "http://" + localIp + ":" + PORT;
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String readAll(InputStream in) throws IOException {
        BufferedReader r  = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder  sb = new StringBuilder();
        char[] buf = new char[8192];
        int n;
        while ((n = r.read(buf)) != -1) sb.append(buf, 0, n);
        return sb.toString();
    }

    private boolean isM3u(String ct, String url) {
        if (ct != null) {
            String c = ct.toLowerCase();
            if (c.contains("mpegurl") || c.contains("m3u")) return true;
        }
        String u = url.toLowerCase().split("\\?")[0];
        return u.endsWith(".m3u8") || u.endsWith(".m3u");
    }

    private String urlEncode(String s) {
        try { return URLEncoder.encode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }

    private void sendError(Socket client, int code, String msg) {
        try {
            String body = "<h1>" + code + " " + msg + "</h1>";
            String resp = "HTTP/1.1 " + code + " " + msg + "\r\n"
                    + "Content-Type: text/html\r\nContent-Length: "
                    + body.length() + "\r\n\r\n" + body;
            client.getOutputStream().write(resp.getBytes("UTF-8"));
        } catch (IOException ignored) {}
    }

    private String guessMime(String url) {
        String u = url.toLowerCase().split("\\?")[0];
        if (u.endsWith(".m3u8") || u.endsWith(".m3u")) return "application/vnd.apple.mpegurl";
        if (u.endsWith(".ts"))   return "video/mp2t";
        if (u.endsWith(".mp4"))  return "video/mp4";
        if (u.endsWith(".mkv"))  return "video/x-matroska";
        if (u.endsWith(".webm")) return "video/webm";
        if (u.endsWith(".avi"))  return "video/x-msvideo";
        return "video/mp4";
    }
}
