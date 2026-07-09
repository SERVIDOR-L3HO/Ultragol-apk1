package com.ultragol.app;

import android.content.Context;
import android.os.Bundle;
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
 */
public class CastBottomSheet extends BottomSheetDialogFragment {

    public interface OnDeviceSelectedCallback {
        void onDeviceSelected(CastDevice device);
    }

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

        startAllDiscovery();
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface d) {
        super.onDismiss(d);
        stopCastRouterCallback();
        AirPlayManager.getInstance().stopDiscovery();
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
            Context ctx = requireContext();
            mediaRouter = MediaRouter.getInstance(ctx);
            String appId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

            // Add existing routes
            MediaRouteSelector selector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(appId))
                .build();

            for (MediaRouter.RouteInfo r : mediaRouter.getRoutes()) {
                if (r.matchesSelector(selector) && !r.isDefault()) {
                    addDevice(routeToCastDevice(r));
                }
            }

            // Listen for newly discovered routes
            castRouterCallback = new MediaRouter.Callback() {
                @Override
                public void onRouteAdded(MediaRouter r, MediaRouter.RouteInfo route) {
                    if (!isAdded()) return;
                    addDevice(routeToCastDevice(route));
                }
                @Override
                public void onRouteRemoved(MediaRouter r, MediaRouter.RouteInfo route) {}
                @Override
                public void onRouteChanged(MediaRouter r, MediaRouter.RouteInfo route) {}
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

    // ── Helpers ──────────────────────────────────────────────────────────────

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
