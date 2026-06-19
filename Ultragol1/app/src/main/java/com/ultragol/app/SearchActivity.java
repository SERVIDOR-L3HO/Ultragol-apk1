package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {
    private EditText searchInput;
    private RecyclerView resultsGrid;
    private TextView emptyState;
    private View loadingView;
    private ContentGridAdapter adapter;
    private final List<ContentItem> results = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        resultsGrid = findViewById(R.id.resultsGrid);
        emptyState  = findViewById(R.id.emptyState);
        loadingView = findViewById(R.id.loadingSearch);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        adapter = new ContentGridAdapter(this, results);
        resultsGrid.setLayoutManager(new GridLayoutManager(this, 3));
        resultsGrid.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                String q = s.toString().trim();
                if (q.length() < 2) { showEmpty(); return; }
                searchRunnable = () -> doSearch(q);
                handler.postDelayed(searchRunnable, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        searchInput.requestFocus();
    }

    private void doSearch(String query) {
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (emptyState  != null) emptyState.setVisibility(View.GONE);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            try {
                List<ContentItem> r = TmdbApi.searchMulti(query);
                runOnUiThread(() -> {
                    results.clear(); results.addAll(r);
                    adapter.notifyDataSetChanged();
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    if (emptyState  != null) emptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    if (emptyState  != null) emptyState.setVisibility(View.VISIBLE);
                });
            }
        });
        exec.shutdown();
    }

    private void showEmpty() {
        results.clear(); adapter.notifyDataSetChanged();
        if (loadingView != null) loadingView.setVisibility(View.GONE);
        if (emptyState  != null) emptyState.setVisibility(View.VISIBLE);
    }
}
