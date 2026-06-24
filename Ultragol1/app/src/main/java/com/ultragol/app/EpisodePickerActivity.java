package com.ultragol.app;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class EpisodePickerActivity extends AppCompatActivity {

    private ContentItem item;
    private int currentSeason = 1;
    private int totalSeasons  = 1;
    private RecyclerView rvEpisodes;
    private EpisodeAdapter adapter;
    private ProgressBar loading;
    private TextView tvSeasonLabel;
    private View btnPrevSeason, btnNextSeason;
    private ExecutorService exec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_picker);

        item = (ContentItem) getIntent().getSerializableExtra("item");
        if (item == null) { finish(); return; }

        rvEpisodes    = findViewById(R.id.rvEpisodes);
        loading       = findViewById(R.id.epLoading);
        tvSeasonLabel = findViewById(R.id.tvSeasonLabel);
        btnPrevSeason = findViewById(R.id.btnPrevSeason);
        btnNextSeason = findViewById(R.id.btnNextSeason);
        View btnBack  = findViewById(R.id.btnEpBack);

        TextView tvSeriesTitle = findViewById(R.id.tvEpSeriesTitle);
        if (tvSeriesTitle != null) tvSeriesTitle.setText(item.getTitle());

        rvEpisodes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EpisodeAdapter(this, item);
        rvEpisodes.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnPrevSeason != null) btnPrevSeason.setOnClickListener(v -> {
            if (currentSeason > 1) { currentSeason--; updateSeasonUI(); loadEpisodes(); }
        });
        if (btnNextSeason != null) btnNextSeason.setOnClickListener(v -> {
            if (currentSeason < totalSeasons) { currentSeason++; updateSeasonUI(); loadEpisodes(); }
        });

        // Tap season label → number picker
        View seasonCard = findViewById(R.id.seasonCard);
        if (seasonCard != null) seasonCard.setOnClickListener(v -> showSeasonPicker());

        exec = Executors.newSingleThreadExecutor();
        loadSeasonCount();
    }

    private void loadSeasonCount() {
        if (loading != null) loading.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).post(() -> {
            exec.execute(() -> {
                try {
                    totalSeasons = TmdbApi.fetchSeriesSeasonCount(item.getTmdbId());
                } catch (Exception ignored) { totalSeasons = 5; }
                runOnUiThread(() -> {
                    updateSeasonUI();
                    loadEpisodes();
                });
            });
        });
    }

    private void loadEpisodes() {
        if (loading != null) loading.setVisibility(View.VISIBLE);
        adapter.clear();
        exec.execute(() -> {
            try {
                List<TmdbApi.EpisodeInfo> eps = TmdbApi.fetchSeasonEpisodes(item.getTmdbId(), currentSeason);
                runOnUiThread(() -> {
                    if (loading != null) loading.setVisibility(View.GONE);
                    adapter.setEpisodes(eps);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (loading != null) loading.setVisibility(View.GONE);
                });
            }
        });
    }

    private void updateSeasonUI() {
        if (tvSeasonLabel != null)
            tvSeasonLabel.setText("Temporada " + currentSeason);
        if (btnPrevSeason != null)
            btnPrevSeason.setAlpha(currentSeason > 1 ? 1f : 0.3f);
        if (btnNextSeason != null)
            btnNextSeason.setAlpha(currentSeason < totalSeasons ? 1f : 0.3f);
    }

    private void showSeasonPicker() {
        if (totalSeasons <= 1) return;
        String[] options = new String[totalSeasons];
        for (int i = 0; i < totalSeasons; i++) options[i] = "Temporada " + (i + 1);
        new android.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar temporada")
            .setItems(options, (d, which) -> {
                currentSeason = which + 1;
                updateSeasonUI();
                loadEpisodes();
            })
            .show();
    }

    public void onEpisodeSelected(int season, int episode) {
        ServerSelectDialog.show(this, item, season, episode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exec != null) exec.shutdownNow();
    }
}
