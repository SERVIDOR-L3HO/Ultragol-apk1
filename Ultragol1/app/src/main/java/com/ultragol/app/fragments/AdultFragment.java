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
import java.util.concurrent.*;

public class AdultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.gridTitle);
        if (title != null) title.setText("🔥 Adultos");

        RecyclerView grid = view.findViewById(R.id.contentGrid);
        ProgressBar pb    = view.findViewById(R.id.gridLoading);

        List<ContentItem> items = new ArrayList<>();
        ContentGridAdapter adapter = new ContentGridAdapter(requireContext(), items);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        grid.setAdapter(adapter);

        if (pb != null) pb.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Loads movies rated R / adult content from TMDB (certification MX:C)
                List<ContentItem> result = TmdbApi.fetchAdult();
                requireActivity().runOnUiThread(() -> {
                    items.addAll(result);
                    adapter.notifyDataSetChanged();
                    if (pb != null) pb.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (pb != null) pb.setVisibility(View.GONE);
                    });
                }
            }
        });
    }
}
