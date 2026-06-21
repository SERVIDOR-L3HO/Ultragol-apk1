package com.ultragol.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.MyListManager;
import com.ultragol.app.R;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class MyListFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_grid, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);

        TextView title = view.findViewById(R.id.gridTitle);
        if (title != null) title.setText("⊟  Mi Lista");

        View btnBack = view.findViewById(R.id.gridBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        loadContent(view);
    }

    private void loadContent(View view) {
        RecyclerView grid = view.findViewById(R.id.contentGrid);
        if (grid == null) return;
        grid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        List<ContentItem> items = MyListManager.getAll(requireContext());
        if (items.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("Tu lista está vacía.\nAgrega contenido desde su detalle.");
            empty.setTextColor(0x88FFFFFF);
            empty.setTextSize(14f);
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(32, 80, 32, 80);
            ((ViewGroup) view).addView(empty);
        } else {
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
            List<ContentItem> items = MyListManager.getAll(requireContext());
            grid.setAdapter(new ContentGridAdapter(requireContext(), items));
        }
    }
}
