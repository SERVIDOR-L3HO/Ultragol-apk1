package com.ultragol.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileBottomSheet extends BottomSheetDialogFragment {

    private Runnable onMyListClick;

    public static ProfileBottomSheet newInstance() { return new ProfileBottomSheet(); }

    public ProfileBottomSheet setOnMyListClick(Runnable r) { onMyListClick = r; return this; }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int favCount = FavoritesManager.get(requireContext()).count();
        ((TextView) view.findViewById(R.id.tvFavCount)).setText(String.valueOf(favCount));

        view.findViewById(R.id.menuMyList).setOnClickListener(v -> {
            dismiss();
            if (onMyListClick != null) onMyListClick.run();
        });

        view.findViewById(R.id.menuHistory).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "⏱ Historial próximamente", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.menuSettings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "⚙️ Configuración próximamente", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnSignOut).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
