package com.ultragol.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Arrays;
import java.util.List;

public class NotificationBottomSheet extends BottomSheetDialogFragment {

    public static NotificationBottomSheet newInstance() { return new NotificationBottomSheet(); }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.notifList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new NotifAdapter(getSampleNotifs()));

        view.findViewById(R.id.btnMarkAll).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Todo marcado como leído", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    private List<Notif> getSampleNotifs() {
        return Arrays.asList(
            new Notif("⚽", "EN VIVO: Real Madrid vs Barcelona",
                "El partido acaba de comenzar. ¡No te lo pierdas!", "Ahora", true),
            new Notif("🆕", "Nuevo contenido disponible",
                "Squid Game T3 ya disponible en Ultragol", "5 min", true),
            new Notif("🏆", "Champions League Final",
                "Recuerda: La final es mañana a las 20:00", "1h", true),
            new Notif("🎬", "The Last of Us T2",
                "Episodio 3 ya disponible. No te lo pierdas", "3h", false),
            new Notif("⭐", "Recomendado para ti",
                "Basado en tus favoritos: Demon Slayer T5", "1 día", false),
            new Notif("📱", "Actualización disponible",
                "Nueva versión de Ultragol con mejoras", "2 días", false)
        );
    }

    static class Notif {
        String icon, title, msg, time;
        boolean unread;
        Notif(String icon, String title, String msg, String time, boolean unread) {
            this.icon = icon; this.title = title; this.msg = msg;
            this.time = time; this.unread = unread;
        }
    }

    static class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.VH> {
        private final List<Notif> items;
        NotifAdapter(List<Notif> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Notif n = items.get(pos);
            h.icon.setText(n.icon);
            h.title.setText(n.title);
            h.msg.setText(n.msg);
            h.time.setText(n.time);
            h.dot.setVisibility(n.unread ? View.VISIBLE : View.GONE);
            h.title.setAlpha(n.unread ? 1f : 0.65f);
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView icon, title, msg, time;
            View dot;
            VH(View v) {
                super(v);
                icon  = v.findViewById(R.id.notifIcon);
                title = v.findViewById(R.id.notifTitle);
                msg   = v.findViewById(R.id.notifMsg);
                time  = v.findViewById(R.id.notifTime);
                dot   = v.findViewById(R.id.notifDot);
            }
        }
    }
}
