package com.ultragol.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.*;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_ADD     = 1;

    public interface Listener {
        void onProfileClick(ProfileManager.Profile profile);
        void onProfileLongClick(ProfileManager.Profile profile, View anchor);
        void onAddClick();
        void onDeleteClick(ProfileManager.Profile profile);
    }

    private final Context ctx;
    private final List<ProfileManager.Profile> profiles;
    private final Listener listener;
    private boolean manageMode = false;
    private final boolean canAdd;

    public ProfileAdapter(Context ctx, List<ProfileManager.Profile> profiles, Listener listener) {
        this.ctx = ctx;
        this.profiles = profiles;
        this.listener = listener;
        this.canAdd = profiles.size() < ProfileManager.MAX_PROFILES;
    }

    public void setManageMode(boolean on) {
        this.manageMode = on;
        notifyDataSetChanged();
    }

    public boolean isManageMode() { return manageMode; }

    @Override public int getItemViewType(int pos) {
        return pos < profiles.size() ? TYPE_PROFILE : TYPE_ADD;
    }

    @Override public int getItemCount() {
        int count = profiles.size();
        if (count < ProfileManager.MAX_PROFILES) count++; // add card
        return count;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(ctx);
        if (type == TYPE_PROFILE) {
            View v = inf.inflate(R.layout.item_profile_card, parent, false);
            return new ProfileVH(v);
        } else {
            View v = inf.inflate(R.layout.item_profile_add, parent, false);
            return new AddVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder instanceof ProfileVH) {
            bindProfile((ProfileVH) holder, profiles.get(pos));
        }
        // AddVH: click handled in constructor
    }

    private void bindProfile(ProfileVH h, ProfileManager.Profile p) {
        // Avatar color
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        try { circle.setColor(Color.parseColor(p.avatarColor)); }
        catch (Exception e) { circle.setColor(Color.parseColor("#FF6B00")); }
        h.avatarCircle.setBackground(circle);

        h.tvInitial.setText(p.getInitial());
        h.tvName.setText(p.name);

        // PIN badge
        h.tvPinBadge.setVisibility(p.hasPin() ? View.VISIBLE : View.INVISIBLE);

        // Kids badge
        h.badgeKids.setVisibility(p.isKids ? View.VISIBLE : View.GONE);

        // Delete button in manage mode
        h.btnDelete.setVisibility(manageMode ? View.VISIBLE : View.GONE);
        h.btnDelete.setOnClickListener(v -> listener.onDeleteClick(p));

        // Card glass — highlight current profile
        String currentId = ProfileManager.getCurrentId(ctx);
        h.cardContainer.setBackgroundResource(
            p.id.equals(currentId) ? R.drawable.profile_card_selected : R.drawable.profile_card_glass);

        // Clicks
        h.itemView.setOnClickListener(v -> { if (!manageMode) listener.onProfileClick(p); });
        h.itemView.setOnLongClickListener(v -> {
            listener.onProfileLongClick(p, v);
            return true;
        });
    }

    // ─── ViewHolders ─────────────────────────────────────────────────────────

    class ProfileVH extends RecyclerView.ViewHolder {
        FrameLayout cardContainer, avatarCircle;
        TextView tvInitial, tvName, tvPinBadge, badgeKids, btnDelete;
        ProfileVH(View v) {
            super(v);
            cardContainer = v.findViewById(R.id.cardContainer);
            avatarCircle  = v.findViewById(R.id.avatarCircle);
            tvInitial     = v.findViewById(R.id.tvInitial);
            tvName        = v.findViewById(R.id.tvName);
            tvPinBadge    = v.findViewById(R.id.tvPinBadge);
            badgeKids     = v.findViewById(R.id.badgeKids);
            btnDelete     = v.findViewById(R.id.btnDelete);
        }
    }

    class AddVH extends RecyclerView.ViewHolder {
        AddVH(View v) {
            super(v);
            v.setOnClickListener(view -> listener.onAddClick());
        }
    }
}
