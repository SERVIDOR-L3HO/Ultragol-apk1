package com.ultragol.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.ProfileManager;
import com.ultragol.app.R;
import com.ultragol.app.TvHelper;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.A7xIptvApi;
import com.ultragol.app.network.TmdbApi;
import java.util.*;
import java.util.concurrent.*;

/**
 * Pantalla de películas — usa A7X TV IPTV como fuente principal.
 * Si A7X falla (sin conexión, sin token), cae a TMDB como respaldo.
 * En perfil infantil carga sólo contenido familiar.
 */
public class MoviesFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_grid, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);

        // Back button
        View backBtn = view.findViewById(R.id.gridBack);
        if (backBtn != null) backBtn.setOnClickListener(v -> requireActivity().onBackPressed());

        // Detect kids profile
        boolean isKids = false;
        try {
            ProfileManager.Profile prof = ProfileManager.getCurrentProfile(requireContext());
            if (prof != null) isKids = prof.isKids;
        } catch (Exception ignored) {}
        final boolean kidsMode = isKids;

        TextView title = view.findViewById(R.id.gridTitle);
        if (title != null) title.setText(kidsMode ? "🎬 Películas Infantiles" : "🎬 Películas");

        RecyclerView grid = view.findViewById(R.id.contentGrid);
        ProgressBar pb   = view.findViewById(R.id.gridLoading);

        List<ContentItem> items = new ArrayList<>();
        ContentGridAdapter adapter = new ContentGridAdapter(requireContext(), items);
        int cols = getResources().getInteger(R.integer.content_grid_columns);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), cols));
        grid.setAdapter(adapter);
        TvHelper.makeFocusable(grid);

        if (pb != null) pb.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<ContentItem> result = new ArrayList<>();
            try {
                if (kidsMode) {
                    // Kids profile: only family/animation movies from TMDB
                    result = TmdbApi.fetchKidsMovies();
                    List<ContentItem> anim = TmdbApi.fetchKidsAnimation();
                    for (ContentItem c : anim) {
                        if (!containsId(result, c)) result.add(c);
                    }
                } else {
                    // Normal: A7X IPTV primary, TMDB fallback
                    result = A7xIptvApi.loadMovies(requireContext());
                    if (result.isEmpty()) result = TmdbApi.fetchMovies();
                }
            } catch (Exception ignored) {}

            final List<ContentItem> finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isAdded()) return;
                items.addAll(finalResult);
                adapter.notifyDataSetChanged();
                if (pb != null) pb.setVisibility(View.GONE);
            });
        });
    }

    private boolean containsId(List<ContentItem> list, ContentItem item) {
        for (ContentItem c : list) {
            if (c.getTmdbId() == item.getTmdbId()) return true;
        }
        return false;
    }
}
