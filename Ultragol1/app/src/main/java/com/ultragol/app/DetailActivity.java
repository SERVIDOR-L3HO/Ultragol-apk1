package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.StreamingApi;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private ContentItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        item = (ContentItem) getIntent().getSerializableExtra("item");
        if (item == null) { finish(); return; }

        ImageView backdrop = findViewById(R.id.detailBackdrop);
        ImageView poster   = findViewById(R.id.detailPoster);
        TextView  title    = findViewById(R.id.detailTitle);
        TextView  meta     = findViewById(R.id.detailMeta);
        TextView  rating   = findViewById(R.id.detailRating);
        TextView  overview = findViewById(R.id.detailOverview);
        TextView  badge    = findViewById(R.id.detailBadge);
        View      btnPlay  = findViewById(R.id.btnPlay);
        View      btnBack  = findViewById(R.id.btnDetailBack);

        if (title != null)    title.setText(item.getTitle());
        if (meta != null)     meta.setText(item.getGenreYear());
        if (rating != null)   rating.setText(item.getRatingDisplay());
        if (overview != null) overview.setText(item.getOverview());
        if (badge != null)    badge.setText(item.getBadge());

        if (backdrop != null && !item.getBackdropUrl().isEmpty()) {
            Glide.with(this).load(item.getBackdropUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backdrop);
        }
        if (poster != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(this).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(poster);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        });

        if (btnPlay != null) btnPlay.setOnClickListener(v ->
            ServerSelectDialog.show(this, item));
    }
}
