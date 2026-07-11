package com.ultragol.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.R;

/**
 * Single-item adapter that wraps the whole "above the fold" Home content
 * (topbar, hero banner, continue watching, rows, live carousel) as ONE
 * RecyclerView item, so it can be concatenated (via ConcatAdapter) with the
 * infinite discover grid's adapter in the SAME RecyclerView.
 *
 * This is what makes the discover grid's items get genuinely recycled: a
 * RecyclerView nested inside a NestedScrollView with wrap_content height
 * cannot recycle off-screen rows (it must lay out everything to know its own
 * height), which is what caused scroll jank that got worse the more pages
 * loaded. Putting everything in one top-level RecyclerView fixes that.
 *
 * onBindViewHolder is intentionally a no-op after the first bind: the header
 * view (and all the state inside it — ViewPager2 position, nested
 * RecyclerView scroll offsets, live data) is set up once via the creation
 * callback and must be preserved verbatim if the same ViewHolder is recycled
 * back into view after being scrolled off-screen.
 */
public class HomeHeaderAdapter extends RecyclerView.Adapter<HomeHeaderAdapter.HeaderVH> {

    public interface OnHeaderCreatedListener {
        void onHeaderCreated(View headerView);
    }

    private final OnHeaderCreatedListener listener;

    public HomeHeaderAdapter(OnHeaderCreatedListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) { return 0L; }

    @NonNull @Override
    public HeaderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_home_header, parent, false);
        HeaderVH vh = new HeaderVH(v);
        if (listener != null) listener.onHeaderCreated(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderVH holder, int position) {
        // No-op on purpose — see class doc.
    }

    @Override
    public int getItemCount() { return 1; }

    static class HeaderVH extends RecyclerView.ViewHolder {
        HeaderVH(View v) { super(v); }
    }
}
