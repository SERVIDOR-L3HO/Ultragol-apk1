package com.ultragol.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.ultragol.app.DetailActivity;
import com.ultragol.app.DownloadsManager;
import com.ultragol.app.R;
import com.ultragol.app.models.ContentItem;
import java.io.File;
import java.util.List;

public class DownloadsFragment extends Fragment {

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
            grid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
            grid.setAdapter(new DownloadCardAdapter(requireContext(), items, () -> loadContent(view)));
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

            // Tap → open detail
            h.itemView.setOnClickListener(v -> {
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
                            items.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, items.size());
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
            TextView title, year;
            VH(View v) {
                super(v);
                poster = v.findViewById(R.id.downloadPoster);
                title  = v.findViewById(R.id.downloadTitle);
                year   = v.findViewById(R.id.downloadYear);
            }
        }
    }
}
