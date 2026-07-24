package com.ultragol.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.*;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Chromecast discovery
import androidx.mediarouter.media.*;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.*;

/**
 * Bottom sheet that discovers and lists available cast devices
 * (Chromecast, DLNA Smart TVs, AirPlay) and calls back when one is selected.
 * Also shows Web Video Caster as a recommended app.
 */
public class CastBottomSheet extends BottomSheetDialogFragment {

    public interface OnDeviceSelectedCallback {
        void onDeviceSelected(CastDevice device);
    }

    private static final String WVC_PACKAGE = "com.instantbits.cast.webvideo";

    private final List<CastDevice> devices = new ArrayList<>();
    private CastDeviceAdapter adapter;
    private TextView tvStatus;
    private ProgressBar pbSearching;

    private String videoUrl, videoTitle;
    private boolean isM3u8;
    private OnDeviceSelectedCallback callback;
    private final AtomicInteger pendingSources = new AtomicInteger(0);

    private MediaRouter mediaRouter;
    private MediaRouter.Callback castRouterCallback;

    public static CastBottomSheet newInstance() { return new CastBottomSheet(); }

    public void setVideoInfo(String url, String title, boolean isM3u8) {
        this.videoUrl   = url;
        this.videoTitle = title;
        this.isM3u8     = isM3u8;
    }

    public void setCallback(OnDeviceSelectedCallback cb) { this.callback = cb; }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_cast_bottom_sheet, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        tvStatus    = view.findViewById(R.id.castStatus);
        pbSearching = view.findViewById(R.id.castSearching);

        RecyclerView rv = view.findViewById(R.id.castDeviceList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CastDeviceAdapter(requireContext(), devices, dev -> {
            dismiss();
            if (callback != null) callback.onDeviceSelected(dev);
        });
        rv.setAdapter(adapter);
        view.findViewById(R.id.castBtnCancel).setOnClickListener(v -> dismiss());

        setupWebVideoCasterCard(view);
        setupLocalStreamCard(view);
        startAllDiscovery();
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface d) {
        super.onDismiss(d);
        stopCastRouterCallback();
        AirPlayManager.getInstance().stopDiscovery();
    }

    // ── Transmisión Local ─────────────────────────────────────────────────────

    private void setupLocalStreamCard(View view) {
        TextView tvIpLabel  = view.findViewById(R.id.localStreamIpLabel);
        TextView tvDot      = view.findViewById(R.id.localStreamDot);
        TextView tvUrl      = view.findViewById(R.id.localStreamUrl);
        TextView btnCopy    = view.findViewById(R.id.btnCopyLocalUrl);
        android.widget.Switch toggleSwitch = view.findViewById(R.id.localStreamToggle);

        String localIp = getLocalIpAddress();
        boolean hasIp  = localIp != null && !localIp.isEmpty();

        LocalStreamServer server = LocalStreamServer.getInstance();

        // Arrange shown URL: local server URL if available, else raw stream URL
        final String[] shownUrl = new String[1];

        if (hasIp && videoUrl != null && !videoUrl.isEmpty()) {
            server.start(videoUrl);
            shownUrl[0] = server.getLocalUrl(localIp);
            tvDot.setTextColor(0xFF00E5A0);
            tvIpLabel.setText("Servidor local activo · " + localIp + ":" + LocalStreamServer.PORT);
        } else {
            shownUrl[0] = videoUrl != null ? videoUrl : "URL no disponible";
            tvDot.setTextColor(hasIp ? 0xFFFF9800 : 0xFFFF5252);
            tvIpLabel.setText(hasIp ? "Sin URL de stream" : "Sin red Wi-Fi activa");
        }
        tvUrl.setText(shownUrl[0]);

        if (toggleSwitch != null) {
            toggleSwitch.setChecked(hasIp && server.isRunning());
            toggleSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked && videoUrl != null && !videoUrl.isEmpty() && localIp != null) {
                    server.start(videoUrl);
                    shownUrl[0] = server.getLocalUrl(localIp);
                    tvDot.setTextColor(0xFF00E5A0);
                    tvIpLabel.setText("Servidor local activo · " + localIp + ":" + LocalStreamServer.PORT);
                } else {
                    server.stop();
                    shownUrl[0] = videoUrl != null ? videoUrl : "";
                    tvDot.setTextColor(0xFFFF5252);
                    tvIpLabel.setText("Servidor local detenido");
                }
                tvUrl.setText(shownUrl[0]);
            });
        }

        btnCopy.setOnClickListener(v -> {
            if (shownUrl[0] == null || shownUrl[0].isEmpty()) return;
            ClipboardManager cm = (ClipboardManager) requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("Stream URL", shownUrl[0]));
                Toast.makeText(requireContext(), "URL copiada al portapapeles",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private String getLocalIpAddress() {
        try {
            WifiManager wm = (WifiManager) requireContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
            if (wm == null) return null;
            WifiInfo info = wm.getConnectionInfo();
            int ip = info.getIpAddress();
            if (ip == 0) return null;
            return Formatter.formatIpAddress(ip);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Web Video Caster ──────────────────────────────────────────────────────

    private void setupWebVideoCasterCard(View view) {
        View card         = view.findViewById(R.id.cardWebVideoCaster);
        ImageView icon    = view.findViewById(R.id.iconWebVideoCaster);
        TextView  btnOpen = view.findViewById(R.id.btnWebVideoCaster);

        boolean installed = isAppInstalled(WVC_PACKAGE);

        // Load real app icon if installed, else use a cast-style placeholder tint
        if (installed) {
            try {
                Drawable appIcon = requireContext().getPackageManager()
                    .getApplicationIcon(WVC_PACKAGE);
                icon.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException ignored) {
                icon.setImageResource(R.drawable.ic_cast);
                icon.setColorFilter(0xFF4FC3F7);
            }
            btnOpen.setText("ABRIR");
        } else {
            icon.setImageResource(R.drawable.ic_cast);
            icon.setColorFilter(0xFF4FC3F7);
            btnOpen.setText("INSTALAR");
        }

        View.OnClickListener openAction = v -> {
            openOrInstallWebVideoCaster();
            dismiss();
        };
        card.setOnClickListener(openAction);
        btnOpen.setOnClickListener(openAction);
    }

    private void openOrInstallWebVideoCaster() {
        PackageManager pm = requireContext().getPackageManager();

        if (isAppInstalled(WVC_PACKAGE)) {
            // Try to send the video URL directly to the app
            if (videoUrl != null && !videoUrl.isEmpty()) {
                try {
                    Intent send = new Intent(Intent.ACTION_VIEW);
                    send.setDataAndType(Uri.parse(videoUrl),
                        isM3u8 ? "application/x-mpegURL" : "video/*");
                    send.setPackage(WVC_PACKAGE);
                    send.putExtra("title", videoTitle != null ? videoTitle : "");
                    startActivity(send);
                    return;
                } catch (Exception ignored) {}
            }
            // Fallback: just launch the app
            Intent launch = pm.getLaunchIntentForPackage(WVC_PACKAGE);
            if (launch != null) { startActivity(launch); return; }
        }

        // Not installed → Play Store
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + WVC_PACKAGE)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + WVC_PACKAGE)));
        }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            requireContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // ── Discovery ─────────────────────────────────────────────────────────────

    private void startAllDiscovery() {
        pendingSources.set(3); // Chromecast + DLNA + AirPlay
        updateStatus();

        discoverChromecast();
        discoverDLNA();
        discoverAirPlay();
    }

    private void discoverChromecast() {
        try {
            mediaRouter = MediaRouter.getInstance(requireContext());
            String appId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

            MediaRouteSelector selector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(appId))
                .build();

            for (MediaRouter.RouteInfo r : mediaRouter.getRoutes()) {
                if (r.matchesSelector(selector) && !r.isDefault()) {
                    addDevice(routeToCastDevice(r));
                }
            }

            castRouterCallback = new MediaRouter.Callback() {
                @Override
                public void onRouteAdded(MediaRouter r, MediaRouter.RouteInfo route) {
                    if (!isAdded()) return;
                    addDevice(routeToCastDevice(route));
                }
                @Override public void onRouteRemoved(MediaRouter r, MediaRouter.RouteInfo route) {}
                @Override public void onRouteChanged(MediaRouter r, MediaRouter.RouteInfo route) {}
            };
            mediaRouter.addCallback(selector, castRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
                | MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);

        } catch (Exception ignored) {}
        sourceDone();
    }

    private void discoverDLNA() {
        DLNAManager.getInstance().discoverDevices(new DLNAManager.DeviceListener() {
            @Override public void onDeviceFound(CastDevice dev) { addDevice(dev); }
            @Override public void onDiscoveryComplete()         { sourceDone(); }
        });
    }

    private void discoverAirPlay() {
        AirPlayManager.getInstance().discoverDevices(requireContext(), new AirPlayManager.DeviceListener() {
            @Override public void onDeviceFound(CastDevice dev) { addDevice(dev); }
            @Override public void onDiscoveryComplete()         { sourceDone(); }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private synchronized void addDevice(CastDevice dev) {
        if (!isAdded() || dev == null) return;
        for (CastDevice existing : devices) {
            if (existing.getId().equals(dev.getId())) return;
        }
        devices.add(dev);
        adapter.notifyItemInserted(devices.size() - 1);
        updateStatus();
    }

    private synchronized void sourceDone() {
        if (!isAdded()) return;
        pendingSources.decrementAndGet();
        updateStatus();
    }

    private void updateStatus() {
        if (tvStatus == null || pbSearching == null) return;
        boolean searching = pendingSources.get() > 0;
        pbSearching.setVisibility(searching ? View.VISIBLE : View.GONE);
        if (searching) {
            tvStatus.setText(devices.isEmpty() ? "Buscando dispositivos..." : "Buscando más...");
        } else {
            tvStatus.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            if (devices.isEmpty())
                tvStatus.setText("No se encontraron dispositivos.\nAsegúrate de estar en la misma red WiFi que tu TV.");
        }
    }

    private void stopCastRouterCallback() {
        if (mediaRouter != null && castRouterCallback != null) {
            try { mediaRouter.removeCallback(castRouterCallback); }
            catch (Exception ignored) {}
            castRouterCallback = null;
        }
    }

    private static CastDevice routeToCastDevice(MediaRouter.RouteInfo r) {
        CastDevice dev = new CastDevice(r.getId(), r.getName(), CastDevice.Type.CHROMECAST);
        dev.setRouteInfo(r);
        return dev;
    }
}
