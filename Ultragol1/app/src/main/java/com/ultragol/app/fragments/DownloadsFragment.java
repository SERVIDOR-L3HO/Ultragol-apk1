package com.ultragol.app.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.ultragol.app.DetailActivity;
import com.ultragol.app.DownloadCompleteReceiver;
import com.ultragol.app.DownloadUtil;
import com.ultragol.app.TvHelper;
import com.ultragol.app.DownloadsManager;
import com.ultragol.app.MediaActivity;
import com.ultragol.app.R;
import com.ultragol.app.models.ContentItem;
import java.io.File;
import java.util.List;

public class DownloadsFragment extends Fragment {

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable            refreshRunnable;
    private DownloadCardAdapter adapter;

    // Refreshes the list immediately when any download finishes
    private final BroadcastReceiver downloadDoneReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            View v = getView();
            if (v != null) loadContent(v);
        }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_downloads, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        View btnBack = view.findViewById(R.id.downloadsBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        loadContent(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) loadContent(view);
        startProgressPolling();
        // Listen for download completion → instant list refresh
        IntentFilter filter = new IntentFilter(DownloadCompleteReceiver.ACTION_REFRESH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                downloadDoneReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireContext().registerReceiver(downloadDoneReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopProgressPolling();
        try { requireContext().unregisterReceiver(downloadDoneReceiver); }
        catch (Exception ignored) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopProgressPolling();
    }

    private void startProgressPolling() {
        stopProgressPolling();
        refreshRunnable = new Runnable() {
            @Override public void run() {
                if (adapter != null) adapter.notifyDataSetChanged();
                refreshHandler.postDelayed(this, 2000);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, 2000);
    }

    private void stopProgressPolling() {
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshRunnable = null;
        }
    }

    private void loadContent(View view) {
        RecyclerView grid = view.findViewById(R.id.downloadsGrid);
        TextView empty   = view.findViewById(R.id.downloadsEmpty);
        if (grid == null) return;

        List<ContentItem> items = DownloadsManager.getAll(requireContext());

        if (items.isEmpty()) {
            grid.setVisibility(View.GONE);
            if (empty != null) empty.setVisibility(View.VISIBLE);
        } else {
            grid.setVisibility(View.VISIBLE);
            if (empty != null) empty.setVisibility(View.GONE);
            int cols = getResources().getInteger(R.integer.content_grid_columns);
            grid.setLayoutManager(new GridLayoutManager(requireContext(), cols));
            TvHelper.makeFocusable(grid);
            adapter = new DownloadCardAdapter(requireContext(), items, () -> loadContent(view));
            grid.setAdapter(adapter);
        }
    }

    // ── Inner adapter ─────────────────────────────────────────────────────────

    private static class DownloadCardAdapter
            extends RecyclerView.Adapter<DownloadCardAdapter.VH> {

        private final Context ctx;
        private final List<ContentItem> items;
        private final Runnable onDeleted;

        DownloadCardAdapter(Context ctx, List<ContentItem> items, Runnable onDeleted) {
            this.ctx = ctx; this.items = items; this.onDeleted = onDeleted;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.item_download_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            ContentItem item = items.get(pos);

            // Load poster — prefer local file, fall back to remote URL
            String local = item.getLocalPosterPath();
            if (local != null && !local.isEmpty() && new File(local).exists()) {
                Glide.with(ctx).load(BitmapFactory.decodeFile(local)).into(h.poster);
            } else {
                Glide.with(ctx).load(item.getPosterUrl()).into(h.poster);
            }

            h.title.setText(item.getTitle());
            h.year.setText(item.getYear());

            // Refresh state from DownloadManager
            String state = DownloadsManager.getVideoState(ctx, item);
            item.setVideoState(state);

            switch (state) {
                case "COMPLETE": {
                    h.progressBar.setVisibility(View.GONE);
                    h.statusBadge.setText("▶ Listo");
                    h.statusBadge.setTextColor(0xFF4CAF50);
                    h.statusBadge.setVisibility(View.VISIBLE);
                    break;
                }
                case "DOWNLOADING": {
                    int pct = DownloadsManager.getDownloadProgress(ctx, item.getTmdbId());
                    h.progressBar.setVisibility(View.VISIBLE);
                    h.progressBar.setIndeterminate(pct < 0);
                    if (pct >= 0) h.progressBar.setProgress(pct);
                    h.statusBadge.setText(pct >= 0 ? pct + "%" : "⏳");
                    h.statusBadge.setTextColor(0xFF4FC3F7);
                    h.statusBadge.setVisibility(View.VISIBLE);
                    break;
                }
                case "FAILED": {
                    h.progressBar.setVisibility(View.GONE);
                    h.statusBadge.setText("✗ Error");
                    h.statusBadge.setTextColor(0xFFFF5252);
                    h.statusBadge.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    h.progressBar.setVisibility(View.GONE);
                    h.statusBadge.setText("⬇");
                    h.statusBadge.setTextColor(0xFF4FC3F7);
                    h.statusBadge.setVisibility(View.VISIBLE);
                    break;
                }
            }

            // Tap → play offline if COMPLETE, else open detail
            h.itemView.setOnClickListener(v -> {
                if ("COMPLETE".equals(state)) {
                    String videoPath = item.getLocalVideoPath();
                    if (videoPath != null && !videoPath.isEmpty()) {
                        // Real .mp4 file on disk → play as file URI
                        String playUrl;
                        if (videoPath.startsWith("/")) {
                            File f = new File(videoPath);
                            if (!f.exists()) {
                                Toast.makeText(ctx, "Archivo no encontrado", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            playUrl = "file://" + videoPath;
                        } else {
                            playUrl = videoPath; // streaming URL or m3u8
                        }
                        boolean isM3u8 = playUrl.contains(".m3u8");
                        Intent intent = new Intent(ctx, MediaActivity.class);
                        intent.putExtra("url",         playUrl);
                        intent.putExtra("title",       item.getTitle());
                        intent.putExtra("referer",     playUrl);
                        intent.putExtra("is_m3u8",     isM3u8);
                        intent.putExtra("use_offline", !isM3u8);
                        ctx.startActivity(intent);
                        return;
                    }
                }
                // Fallback: open detail page
                Intent intent = new Intent(ctx, DetailActivity.class);
                intent.putExtra("item", item);
                ctx.startActivity(intent);
            });

            // Long-press → delete dialog
            h.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(ctx)
                        .setTitle("Eliminar descarga")
                        .setMessage("¿Eliminar \"" + item.getTitle() + "\" de tus descargas?")
                        .setPositiveButton("Eliminar", (d, w) -> {
                            DownloadsManager.remove(ctx, item);
                            int adapterPos = h.getAdapterPosition();
                            if (adapterPos != RecyclerView.NO_ID) {
                                items.remove(adapterPos);
                                notifyItemRemoved(adapterPos);
                                notifyItemRangeChanged(adapterPos, items.size());
                            }
                            if (items.isEmpty() && onDeleted != null) onDeleted.run();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            });
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            ImageView poster;
            TextView  title, year, statusBadge;
            ProgressBar progressBar;
            VH(View v) {
                super(v);
                poster      = v.findViewById(R.id.downloadPoster);
                title       = v.findViewById(R.id.downloadTitle);
                year        = v.findViewById(R.id.downloadYear);
                statusBadge = v.findViewById(R.id.downloadStatusBadge);
                progressBar = v.findViewById(R.id.downloadProgressBar);
            }
        }
    }
}
