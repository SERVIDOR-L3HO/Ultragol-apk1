package com.ultragol.app;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.*;
import java.util.List;

public class CastDeviceAdapter extends RecyclerView.Adapter<CastDeviceAdapter.VH> {

    public interface OnDeviceClickListener {
        void onDeviceClick(CastDevice device);
    }

    private final Context ctx;
    private final List<CastDevice> items;
    private final OnDeviceClickListener listener;

    public CastDeviceAdapter(Context ctx, List<CastDevice> items, OnDeviceClickListener listener) {
        this.ctx = ctx; this.items = items; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_cast_device, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CastDevice dev = items.get(pos);
        h.name.setText(dev.getName());
        h.type.setText("[" + dev.getTypeLabel() + "]");
        h.inUse.setVisibility(dev.isInUse() ? View.VISIBLE : View.GONE);

        switch (dev.getType()) {
            case CHROMECAST:
                h.icon.setImageResource(R.drawable.ic_cast);
                h.icon.setColorFilter(0xFF4FC3F7);
                break;
            case DLNA:
                h.icon.setImageResource(R.drawable.ic_cast_tv);
                h.icon.setColorFilter(0xFF81C784);
                break;
            case AIRPLAY:
                h.icon.setImageResource(R.drawable.ic_cast_phone);
                h.icon.setColorFilter(0xFFFFA726);
                break;
        }

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onDeviceClick(dev); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView  name, type, inUse;
        VH(View v) {
            super(v);
            icon  = v.findViewById(R.id.castDeviceIcon);
            name  = v.findViewById(R.id.castDeviceName);
            type  = v.findViewById(R.id.castDeviceType);
            inUse = v.findViewById(R.id.castDeviceInUse);
        }
    }
}
