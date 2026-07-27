package com.ultragol.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.FavoritesManager;
import com.ultragol.app.R;
import com.ultragol.app.TvHelper;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class FavoritesFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_grid, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);

        TextView title = view.findViewById(R.id.gridTitle);
        if (title != null) title.setText("♥  Favoritos");

        View btnBack = view.findViewById(R.id.gridBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        loadContent(view);
    }

    private void loadContent(View view) {
        RecyclerView grid = view.findViewById(R.id.contentGrid);
        if (grid == null) return;
        int cols = getResources().getInteger(R.integer.content_grid_columns);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), cols));
        TvHelper.makeFocusable(grid);
        List<ContentItem> items = FavoritesManager.getAll(requireContext());
        if (items.isEmpty()) {
            grid.setVisibility(View.GONE);
            TextView empty = new TextView(requireContext());
            empty.setText("Aún no tienes favoritos.\nAgrega contenido desde su detalle.");
            empty.setTextColor(0xAAFFFFFF);
            empty.setTextSize(15f);
            empty.setGravity(android.view.Gravity.CENTER);
            android.widget.LinearLayout.LayoutParams lp =
                new android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp.weight = 1f;
            empty.setLayoutParams(lp);
            ((ViewGroup) view).addView(empty);
        } else {
            grid.setVisibility(View.VISIBLE);
            grid.setAdapter(new ContentGridAdapter(requireContext(), items));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view == null) return;
        RecyclerView grid = view.findViewById(R.id.contentGrid);
        if (grid != null) {
            List<ContentItem> items = FavoritesManager.getAll(requireContext());
            grid.setAdapter(new ContentGridAdapter(requireContext(), items));
        }
    }
}
