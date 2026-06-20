package com.ultragol.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.*;
import java.util.concurrent.Executors;

public class PlatformFragment extends Fragment {

    private static final String ARG_NAME       = "platform_name";
    private static final String ARG_PROVIDER   = "provider_id";
    private static final String ARG_TYPE       = "content_type";

    public static PlatformFragment newInstance(String platformName, int providerId, String type) {
        PlatformFragment f = new PlatformFragment();
        Bundle b = new Bundle();
        b.putString(ARG_NAME, platformName);
        b.putInt(ARG_PROVIDER, providerId);
        b.putString(ARG_TYPE, type);
        f.setArguments(b);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_grid, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);

        String platformName = getArguments() != null ? getArguments().getString(ARG_NAME, "Plataforma") : "Plataforma";
        int providerId      = getArguments() != null ? getArguments().getInt(ARG_PROVIDER, 8) : 8;
        String type         = getArguments() != null ? getArguments().getString(ARG_TYPE, "all") : "all";

        TextView title = view.findViewById(R.id.gridTitle);
        if (title != null) title.setText(platformName);

        RecyclerView grid = view.findViewById(R.id.contentGrid);
        ProgressBar pb    = view.findViewById(R.id.gridLoading);
        List<ContentItem> items = new ArrayList<>();
        ContentGridAdapter adapter = new ContentGridAdapter(requireContext(), items);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        grid.setAdapter(adapter);
        if (pb != null) pb.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ContentItem> r = new ArrayList<>();
                if ("anime".equals(type)) {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "tv"));
                } else if ("movie".equals(type)) {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "movie"));
                } else {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "movie"));
                    r.addAll(TmdbApi.fetchByProvider(providerId, "tv"));
                }
                requireActivity().runOnUiThread(() -> {
                    items.addAll(r);
                    adapter.notifyDataSetChanged();
                    if (pb != null) pb.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    if (pb != null) pb.setVisibility(View.GONE);
                });
            }
        });
    }
}
