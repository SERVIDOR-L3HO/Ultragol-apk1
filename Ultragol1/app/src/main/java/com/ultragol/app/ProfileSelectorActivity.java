package com.ultragol.app;

import android.animation.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ultragol.app.adapters.ProfileAdapter;
import java.util.List;
import android.widget.ImageView;

public class ProfileSelectorActivity extends AppCompatActivity
        implements ProfileAdapter.Listener {

    private RecyclerView rvProfiles;
    private ProfileAdapter adapter;
    private List<ProfileManager.Profile> profiles;
    private TextView btnManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selector);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        btnManage = findViewById(R.id.btnManage);
        rvProfiles = findViewById(R.id.rvProfiles);

        rvProfiles.setLayoutManager(new GridLayoutManager(this, 2));

        btnManage.setOnClickListener(v -> toggleManageMode());
        loadProfiles();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Guardar perfiles y datos en Firestore al salir de la pantalla de selección
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserSyncManager.pushToCloud(getApplicationContext(), user.getUid());
        }
    }

    private void loadProfiles() {
        profiles = ProfileManager.getAll(this);

        // If no profiles, show create dialog immediately
        if (profiles.isEmpty()) {
            showCreateProfileDialog(null);
        }

        adapter = new ProfileAdapter(this, profiles, this);
        rvProfiles.setAdapter(adapter);
        animateCards();
    }

    private void animateCards() {
        rvProfiles.post(() -> {
            for (int i = 0; i < rvProfiles.getChildCount(); i++) {
                View child = rvProfiles.getChildAt(i);
                if (child == null) continue;
                child.setTranslationY(80f);
                child.setAlpha(0f);
                child.animate()
                    .translationY(0f).alpha(1f)
                    .setStartDelay(i * 60L).setDuration(350)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
            }
        });
    }

    private void toggleManageMode() {
        boolean entering = !adapter.isManageMode();
        adapter.setManageMode(entering);
        btnManage.setText(entering ? "Listo" : "Gestionar");
        btnManage.setTextColor(entering
            ? Color.parseColor("#FFFFFF")
            : Color.parseColor("#FF6B00"));
    }

    // ── ProfileAdapter.Listener ───────────────────────────────────────────────

    @Override
    public void onProfileClick(ProfileManager.Profile profile) {
        if (profile.hasPin()) {
            showPinDialog(profile);
        } else {
            selectProfileAndContinue(profile);
        }
    }

    @Override
    public void onProfileLongClick(ProfileManager.Profile profile, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 0, 0, "✏️  Editar perfil");
        popup.getMenu().add(0, 1, 1, "🔒  " + (profile.hasPin() ? "Cambiar PIN" : "Añadir PIN"));
        popup.getMenu().add(0, 2, 2, "🗑️  Eliminar perfil");
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0: showCreateProfileDialog(profile); break;
                case 1: showSetPinDialog(profile); break;
                case 2: confirmDeleteProfile(profile); break;
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onAddClick() {
        showCreateProfileDialog(null);
    }

    @Override
    public void onDeleteClick(ProfileManager.Profile profile) {
        confirmDeleteProfile(profile);
    }

    // ── Profile selection ─────────────────────────────────────────────────────

    private void selectProfileAndContinue(ProfileManager.Profile profile) {
        ProfileManager.setCurrentId(this, profile.id);
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK forces MainActivity to fully
        // recreate so HomeFragment.loadAll() runs fresh with the newly selected profile.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    // ── PIN dialog ────────────────────────────────────────────────────────────

    private void showPinDialog(ProfileManager.Profile profile) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pin_entry, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setView(v).setCancelable(true).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvProfileName = v.findViewById(R.id.pinProfileName);
        tvProfileName.setText("Perfil: " + profile.name);

        View[] dots = {
            v.findViewById(R.id.dot1), v.findViewById(R.id.dot2),
            v.findViewById(R.id.dot3), v.findViewById(R.id.dot4)
        };
        TextView tvError = v.findViewById(R.id.tvPinError);
        StringBuilder entered = new StringBuilder();

        Runnable updateDots = () -> {
            for (int i = 0; i < 4; i++) {
                dots[i].setBackgroundResource(
                    i < entered.length() ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
            }
        };

        int[] keyIds = {R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                        R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9};

        for (int digit = 0; digit <= 9; digit++) {
            final int d = digit;
            v.findViewById(keyIds[d]).setOnClickListener(btn -> {
                if (entered.length() >= 4) return;
                entered.append(d);
                updateDots.run();
                tvError.setVisibility(View.INVISIBLE);
                if (entered.length() == 4) {
                    if (entered.toString().equals(profile.pin)) {
                        dialog.dismiss();
                        selectProfileAndContinue(profile);
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                        animateShake(v.findViewById(R.id.pinDots));
                        entered.setLength(0);
                        updateDots.run();
                    }
                }
            });
        }

        v.findViewById(R.id.keyDel).setOnClickListener(btn -> {
            if (entered.length() > 0) {
                entered.deleteCharAt(entered.length() - 1);
                updateDots.run();
                tvError.setVisibility(View.INVISIBLE);
            }
        });

        v.findViewById(R.id.btnPinCancel).setOnClickListener(btn -> dialog.dismiss());
        dialog.show();
    }

    // ── Set / Change PIN dialog ───────────────────────────────────────────────

    private void showSetPinDialog(ProfileManager.Profile profile) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pin_entry, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setView(v).setCancelable(true).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = v.findViewById(R.id.pinTitle);
        TextView tvProfileName = v.findViewById(R.id.pinProfileName);
        title.setText(profile.hasPin() ? "Nuevo PIN" : "Crear PIN");
        tvProfileName.setText("Ingresa 4 dígitos para " + profile.name);
        v.findViewById(R.id.tvPinError).setVisibility(View.INVISIBLE);

        View[] dots = {
            v.findViewById(R.id.dot1), v.findViewById(R.id.dot2),
            v.findViewById(R.id.dot3), v.findViewById(R.id.dot4)
        };
        StringBuilder entered = new StringBuilder();

        Runnable updateDots = () -> {
            for (int i = 0; i < 4; i++)
                dots[i].setBackgroundResource(
                    i < entered.length() ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
        };

        int[] keyIds = {R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                        R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9};

        for (int digit = 0; digit <= 9; digit++) {
            final int d = digit;
            v.findViewById(keyIds[d]).setOnClickListener(btn -> {
                if (entered.length() >= 4) return;
                entered.append(d);
                updateDots.run();
                if (entered.length() == 4) {
                    profile.pin = entered.toString();
                    ProfileManager.save(this, profile);
                    dialog.dismiss();
                    reloadAdapter();
                    Toast.makeText(this, "PIN configurado", Toast.LENGTH_SHORT).show();
                }
            });
        }

        v.findViewById(R.id.keyDel).setOnClickListener(btn -> {
            if (entered.length() > 0) { entered.deleteCharAt(entered.length() - 1); updateDots.run(); }
        });
        v.findViewById(R.id.btnPinCancel).setOnClickListener(btn -> dialog.dismiss());
        dialog.show();
    }

    // ── Create / Edit profile dialog ──────────────────────────────────────────

    private void showCreateProfileDialog(ProfileManager.Profile existing) {
        View v = getLayoutInflater().inflate(R.layout.dialog_create_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setView(v).setCancelable(existing != null).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView dialogTitle      = v.findViewById(R.id.dialogTitle);
        EditText etName           = v.findViewById(R.id.etName);
        FrameLayout avatarPrev    = v.findViewById(R.id.avatarPreview);
        ImageView ivAvatarPreview = v.findViewById(R.id.ivAvatarIconPreview);
        LinearLayout colorPicker  = v.findViewById(R.id.colorPicker);
        LinearLayout avatarPickerRow = v.findViewById(R.id.avatarPickerRow);
        androidx.appcompat.widget.SwitchCompat switchKids = v.findViewById(R.id.switchKids);
        TextView tvPinStatus = v.findViewById(R.id.tvPinStatus);
        TextView btnSetPin   = v.findViewById(R.id.btnSetPin);
        TextView btnSave     = v.findViewById(R.id.btnSave);

        boolean isEdit = existing != null;
        dialogTitle.setText(isEdit ? "Editar perfil" : "Nuevo perfil");
        if (isEdit) {
            etName.setText(existing.name);
            switchKids.setChecked(existing.isKids);
        }

        final ProfileManager.Profile[] pending = {isEdit ? existing : ProfileManager.newProfile("", ProfileManager.AVATAR_COLORS[0])};
        final String[] selectedColor  = {isEdit ? existing.avatarColor : ProfileManager.AVATAR_COLORS[0]};
        final int[]    selectedAvatar = {isEdit ? existing.avatarId    : 0};
        float dp = getResources().getDisplayMetrics().density;

        // ── Update big preview ──────────────────────────────────────────────
        Runnable updatePreview = () -> {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            try { gd.setColor(Color.parseColor(selectedColor[0])); }
            catch (Exception e) { gd.setColor(0xFFFF6B00); }
            if (avatarPrev != null) avatarPrev.setBackground(gd);
            if (ivAvatarPreview != null) {
                boolean kids = switchKids != null && switchKids.isChecked();
                int[] arr = kids ? ProfileManager.KIDS_AVATARS : ProfileManager.ADULT_AVATARS;
                int id = selectedAvatar[0] >= 100 ? selectedAvatar[0] - 100 : selectedAvatar[0];
                if (id < 0 || id >= arr.length) id = 0;
                ivAvatarPreview.setImageResource(arr[id]);
            }
        };
        updatePreview.run();

        // ── Avatar picker ───────────────────────────────────────────────────
        final Runnable[] buildPicker = {null};
        buildPicker[0] = () -> {
            if (avatarPickerRow == null) return;
            avatarPickerRow.removeAllViews();
            boolean kids = switchKids != null && switchKids.isChecked();
            int[] avatarArr = kids ? ProfileManager.KIDS_AVATARS : ProfileManager.ADULT_AVATARS;
            int sel = selectedAvatar[0];
            for (int i = 0; i < avatarArr.length; i++) {
                final int finalIdx   = i;
                final int finalAvatarId = kids ? (100 + i) : i;
                FrameLayout item = new FrameLayout(this);
                int sz = (int)(58 * dp);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz, sz);
                lp.setMargins((int)(5*dp), (int)(4*dp), (int)(5*dp), (int)(4*dp));
                item.setLayoutParams(lp);

                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.OVAL);
                try { bg.setColor(Color.parseColor(selectedColor[0])); } catch (Exception e) { bg.setColor(0xFFFF6B00); }
                boolean isSel = (sel == finalAvatarId);
                bg.setStroke(isSel ? (int)(3*dp) : (int)(1*dp), isSel ? 0xFFFFFFFF : 0x44FFFFFF);
                item.setBackground(bg);

                ImageView iv = new ImageView(this);
                FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams((int)(34*dp), (int)(34*dp));
                ivlp.gravity = android.view.Gravity.CENTER;
                iv.setLayoutParams(ivlp);
                iv.setImageResource(avatarArr[finalIdx]);
                iv.setColorFilter(isSel ? 0xFFFFFFFF : 0xAAFFFFFF);
                item.addView(iv);

                item.setOnClickListener(sv -> {
                    selectedAvatar[0] = finalAvatarId;
                    buildPicker[0].run();
                    updatePreview.run();
                });
                avatarPickerRow.addView(item);
            }
        };
        buildPicker[0].run();

        // ── Kids toggle ─────────────────────────────────────────────────────
        switchKids.setOnCheckedChangeListener((btn, isChecked) -> {
            selectedAvatar[0] = isChecked ? 100 : 0;
            buildPicker[0].run();
            updatePreview.run();
        });

        etName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { updatePreview.run(); }
            public void afterTextChanged(Editable s) {}
        });

        // ── Color swatches ──────────────────────────────────────────────────
        for (String color : ProfileManager.AVATAR_COLORS) {
            FrameLayout swatch = new FrameLayout(this);
            int sz = (int)(38 * dp);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz, sz);
            lp.setMargins((int)(5*dp), 0, (int)(5*dp), 0);
            swatch.setLayoutParams(lp);
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            try { gd.setColor(Color.parseColor(color)); } catch (Exception ignored) {}
            if (color.equals(selectedColor[0])) gd.setStroke((int)(3*dp), Color.WHITE);
            swatch.setBackground(gd);
            swatch.setOnClickListener(sv -> {
                selectedColor[0] = color;
                for (int i = 0; i < colorPicker.getChildCount(); i++) {
                    View sw = colorPicker.getChildAt(i);
                    GradientDrawable gd2 = new GradientDrawable();
                    gd2.setShape(GradientDrawable.OVAL);
                    String c = ProfileManager.AVATAR_COLORS[i];
                    try { gd2.setColor(Color.parseColor(c)); } catch (Exception ignored) {}
                    if (c.equals(selectedColor[0])) gd2.setStroke((int)(3*dp), Color.WHITE);
                    sw.setBackground(gd2);
                }
                buildPicker[0].run();
                updatePreview.run();
            });
            colorPicker.addView(swatch);
        }

        // ── PIN status ──────────────────────────────────────────────────────
        Runnable refreshPinStatus = () -> {
            tvPinStatus.setText(pending[0].hasPin() ? "PIN configurado ✓" : "Sin PIN configurado");
            tvPinStatus.setTextColor(pending[0].hasPin()
                ? Color.parseColor("#2ECC71") : Color.parseColor("#66FFFFFF"));
            btnSetPin.setText(pending[0].hasPin() ? "Cambiar" : "Configurar");
        };
        refreshPinStatus.run();

        btnSetPin.setOnClickListener(btn -> {
            dialog.dismiss();
            pending[0].name        = etName.getText().toString().trim();
            if (pending[0].name.isEmpty()) pending[0].name = "Perfil";
            pending[0].avatarColor = selectedColor[0];
            pending[0].avatarId    = selectedAvatar[0];
            pending[0].isKids      = switchKids.isChecked();
            showInlineSetPin(pending[0], refreshPinStatus, dialog);
        });

        btnSave.setOnClickListener(btn -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Ingresa un nombre");
                return;
            }
            pending[0].name        = name;
            pending[0].avatarColor = selectedColor[0];
            pending[0].avatarId    = selectedAvatar[0];
            pending[0].isKids      = switchKids.isChecked();
            ProfileManager.save(this, pending[0]);
            dialog.dismiss();
            reloadAdapter();
            if (!isEdit && ProfileManager.getAll(this).size() == 1) {
                selectProfileAndContinue(pending[0]);
            }
        });

        dialog.setCanceledOnTouchOutside(isEdit);
        dialog.show();

        if (dialog.getWindow() != null) {
            int screenW = getResources().getDisplayMetrics().widthPixels;
            int margin  = (int)(20 * getResources().getDisplayMetrics().density);
            dialog.getWindow().setLayout(screenW - margin * 2,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setDimAmount(0.75f);
        }
    }

    private void showInlineSetPin(ProfileManager.Profile profile, Runnable onDone, AlertDialog parent) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pin_entry, null);
        AlertDialog pinDialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setView(v).setCancelable(true).create();
        if (pinDialog.getWindow() != null)
            pinDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = v.findViewById(R.id.pinTitle);
        title.setText("Crear PIN");
        v.findViewById(R.id.pinProfileName);
        v.findViewById(R.id.tvPinError).setVisibility(View.INVISIBLE);

        View[] dots = {v.findViewById(R.id.dot1), v.findViewById(R.id.dot2),
                       v.findViewById(R.id.dot3), v.findViewById(R.id.dot4)};
        StringBuilder entered = new StringBuilder();

        Runnable upd = () -> {
            for (int i = 0; i < 4; i++)
                dots[i].setBackgroundResource(i < entered.length() ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
        };

        int[] keyIds = {R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                        R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9};
        for (int digit = 0; digit <= 9; digit++) {
            final int d = digit;
            v.findViewById(keyIds[d]).setOnClickListener(btn -> {
                if (entered.length() >= 4) return;
                entered.append(d); upd.run();
                if (entered.length() == 4) {
                    profile.pin = entered.toString();
                    pinDialog.dismiss();
                    showCreateProfileDialog(profile);
                }
            });
        }
        v.findViewById(R.id.keyDel).setOnClickListener(btn -> {
            if (entered.length() > 0) { entered.deleteCharAt(entered.length()-1); upd.run(); }
        });
        v.findViewById(R.id.btnPinCancel).setOnClickListener(btn -> {
            pinDialog.dismiss();
            showCreateProfileDialog(profile);
        });
        pinDialog.show();
    }

    // ── Delete confirmation ───────────────────────────────────────────────────

    private void confirmDeleteProfile(ProfileManager.Profile profile) {
        new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setTitle("Eliminar perfil")
            .setMessage("¿Eliminar el perfil \"" + profile.name + "\"?\nSe borrará todo su historial.")
            .setPositiveButton("Eliminar", (d, w) -> {
                ProfileManager.delete(this, profile.id);
                reloadAdapter();
                if (ProfileManager.getAll(this).isEmpty()) {
                    showCreateProfileDialog(null);
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void reloadAdapter() {
        profiles.clear();
        profiles.addAll(ProfileManager.getAll(this));
        adapter.notifyDataSetChanged();
    }

    private void animateShake(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(target, "translationX",
            0f, -18f, 18f, -12f, 12f, -6f, 6f, 0f);
        anim.setDuration(500);
        anim.start();
    }
}
