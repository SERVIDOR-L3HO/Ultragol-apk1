package com.streamnova.app.fragments;

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
import com.streamnova.app.R;
import com.streamnova.app.adapters.ContentGridAdapter;
import com.streamnova.app.models.ContentData;
import com.streamnova.app.models.ContentItem;
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

        String title;
        List<ContentItem> items;

        // Responsive span count based on screen width
        float screenWidthDp = requireContext().getResources().getDisplayMetrics().widthPixels
            / requireContext().getResources().getDisplayMetrics().density;
        int spanCount;
        if (screenWidthDp >= 1280) {
            spanCount = 6; // TV
        } else if (screenWidthDp >= 840) {
            spanCount = 5; // large tablet
        } else if (screenWidthDp >= 600) {
            spanCount = 4; // tablet
        } else {
            spanCount = 3; // phone landscape
        }

        switch (contentType) {
            case TYPE_SERIES:
                title = getString(R.string.series_title);
                items = ContentData.getSeries();
                break;
            case TYPE_ANIME:
                title = getString(R.string.anime_title);
                items = ContentData.getAnime();
                break;
            case TYPE_DORAMAS:
                title = getString(R.string.doramas_title);
                items = ContentData.getDoramas();
                break;
            case TYPE_SPORTS:
                title = getString(R.string.sports_title);
                items = ContentData.getSports();
                if (screenWidthDp >= 600) spanCount = Math.max(3, spanCount - 1);
                break;
            case TYPE_LIVE_TV:
                title = getString(R.string.live_tv_title);
                items = ContentData.getLiveTV();
                if (screenWidthDp >= 600) spanCount = Math.max(3, spanCount - 1);
                break;
            default:
                title = getString(R.string.movies_title);
                items = ContentData.getMovies();
                break;
        }

        sectionTitle.setText(title);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        grid.setLayoutManager(layoutManager);
        grid.setAdapter(new ContentGridAdapter(requireContext(), items));
        grid.setHasFixedSize(false);
    }
}
