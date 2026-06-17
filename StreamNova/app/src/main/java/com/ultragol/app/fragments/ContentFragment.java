package com.ultragol.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.R;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentData;
import com.ultragol.app.models.ContentItem;
import java.util.ArrayList;
import java.util.List;

public class ContentFragment extends Fragment {

    public static final String ARG_TYPE = "content_type";
    public static final int TYPE_MOVIES = 0;
    public static final int TYPE_SERIES = 1;
    public static final int TYPE_ANIME = 2;
    public static final int TYPE_DORAMAS = 3;
    public static final int TYPE_SPORTS = 4;
    public static final int TYPE_LIVE_TV = 5;

    private int contentType;
    private ContentGridAdapter adapter;
    private final List<ContentItem> items = new ArrayList<>();

    public static ContentFragment newInstance(int type) {
        ContentFragment f = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentType = getArguments().getInt(ARG_TYPE, TYPE_MOVIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView sectionTitle = view.findViewById(R.id.sectionTitle);
        RecyclerView grid = view.findViewById(R.id.contentGrid);

        float screenWidthDp = requireContext().getResources().getDisplayMetrics().widthPixels
            / requireContext().getResources().getDisplayMetrics().density;
        int spanCount;
        if (screenWidthDp >= 1280) spanCount = 6;
        else if (screenWidthDp >= 840) spanCount = 5;
        else if (screenWidthDp >= 600) spanCount = 4;
        else if (screenWidthDp >= 400) spanCount = 3;
        else spanCount = 2;

        String title;
        switch (contentType) {
            case TYPE_SERIES:   title = getString(R.string.series_title); break;
            case TYPE_ANIME:    title = getString(R.string.anime_title); break;
            case TYPE_DORAMAS:  title = getString(R.string.doramas_title); break;
            case TYPE_SPORTS:
                title = getString(R.string.sports_title);
                if (screenWidthDp >= 600) spanCount = Math.max(3, spanCount - 1);
                break;
            case TYPE_LIVE_TV:
                title = getString(R.string.live_tv_title);
                if (screenWidthDp >= 600) spanCount = Math.max(3, spanCount - 1);
                break;
            default:            title = getString(R.string.movies_title); break;
        }

        sectionTitle.setText(title);
        adapter = new ContentGridAdapter(requireContext(), items);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        grid.setAdapter(adapter);
        grid.setHasFixedSize(false);

        loadContent();
    }

    private void loadContent() {
        ContentData.ContentCallback cb = result -> {
            if (!isAdded()) return;
            items.clear();
            items.addAll(result);
            if (adapter != null) adapter.notifyDataSetChanged();
        };

        switch (contentType) {
            case TYPE_MOVIES:   ContentData.fetchMovies(cb); break;
            case TYPE_SERIES:   ContentData.fetchSeries(cb); break;
            case TYPE_ANIME:    ContentData.fetchAnime(cb); break;
            case TYPE_DORAMAS:  ContentData.fetchDoramas(cb); break;
            case TYPE_SPORTS:
                items.addAll(ContentData.getSports());
                if (adapter != null) adapter.notifyDataSetChanged();
                break;
            case TYPE_LIVE_TV:
                items.addAll(ContentData.getLiveTV());
                if (adapter != null) adapter.notifyDataSetChanged();
                break;
        }
    }
}
