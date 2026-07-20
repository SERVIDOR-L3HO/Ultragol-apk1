package com.ultragol.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword;
    private TextView btnAction, btnToggle, tvForgot, tvTitle, tvSubtitle;
    private ProgressBar progressBar;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full dark immersive
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        // ── If already logged in, go straight to main ─────────────────────────
        if (mAuth.getCurrentUser() != null) {
            goToMain();
            return;
        }

        buildUI();
    }

    // ── Build the entire UI programmatically ──────────────────────────────────

    private void buildUI() {
        // Root
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF070707);
        root.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

        // Background gradient overlay
        GradientDrawable grad = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ 0xFF0D0010, 0xFF070707, 0xFF070707 });
        root.setBackground(grad);

        setContentView(root);

        int p24 = dp(24);
        int p16 = dp(16);

        // ── Logo area ─────────────────────────────────────────────────────────
        View spacerTop = new View(this);
        spacerTop.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(90)));
        root.addView(spacerTop);

        // App name accent bar
        View accentBar = new View(this);
        GradientDrawable bar = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{ 0xFFCC1111, 0xFFFF4422 });
        bar.setCornerRadius(dp(2));
        accentBar.setBackground(bar);
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(dp(48), dp(4));
        barLp.setMargins(0, 0, 0, dp(20));
        barLp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        accentBar.setLayoutParams(barLp);
        root.addView(accentBar);

        tvTitle = new TextView(this);
        tvTitle.setText("ULTRAGOL");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(30f);
        tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        tvTitle.setLetterSpacing(0.15f);
        tvTitle.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(6));
        tvTitle.setLayoutParams(titleLp);
        root.addView(tvTitle);

        tvSubtitle = new TextView(this);
        tvSubtitle.setText("Inicia sesión para continuar");
        tvSubtitle.setTextColor(0x88FFFFFF);
        tvSubtitle.setTextSize(13f);
        tvSubtitle.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, 0, 0, dp(40));
        tvSubtitle.setLayoutParams(subLp);
        root.addView(tvSubtitle);

        // ── Form card ─────────────────────────────────────────────────────────
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(0xFF111118);
        cardBg.setCornerRadius(dp(20));
        cardBg.setStroke(dp(1), 0x22FFFFFF);
        card.setBackground(cardBg);
        card.setPadding(p24, p24, p24, p24);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(p24, 0, p24, 0);
        card.setLayoutParams(cardLp);
        root.addView(card);

        // Email field
        etEmail = makeField("Correo electrónico", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        card.addView(etEmail);

        // Spacer
        card.addView(spacer(dp(12)));

        // Password field
        etPassword = makeField("Contraseña",
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        card.addView(etPassword);

        // Forgot password
        tvForgot = new TextView(this);
        tvForgot.setText("¿Olvidaste tu contraseña?");
        tvForgot.setTextColor(0xFFCC1111);
        tvForgot.setTextSize(12f);
        tvForgot.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams forgotLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        forgotLp.setMargins(0, dp(10), 0, dp(20));
        tvForgot.setLayoutParams(forgotLp);
        tvForgot.setClickable(true);
        tvForgot.setFocusable(true);
        tvForgot.setOnClickListener(v -> showForgotPassword());
        card.addView(tvForgot);

        // Action button
        btnAction = new TextView(this);
        btnAction.setText("INICIAR SESIÓN");
        btnAction.setTextColor(Color.WHITE);
        btnAction.setTextSize(14f);
        btnAction.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btnAction.setLetterSpacing(0.08f);
        btnAction.setGravity(android.view.Gravity.CENTER);
        GradientDrawable btnBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{ 0xFFCC1111, 0xFFFF3311 });
        btnBg.setCornerRadius(dp(12));
        btnAction.setBackground(btnBg);
        btnAction.setPadding(0, dp(16), 0, dp(16));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(0, 0, 0, dp(16));
        btnAction.setLayoutParams(btnLp);
        btnAction.setClickable(true);
        btnAction.setFocusable(true);
        btnAction.setOnClickListener(v -> onActionClick());
        card.addView(btnAction);

        // Progress bar
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminateTintList(
            android.content.res.ColorStateList.valueOf(0xFFCC1111));
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pbLp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        pbLp.setMargins(0, 0, 0, dp(8));
        progressBar.setLayoutParams(pbLp);
        progressBar.setVisibility(View.GONE);
        card.addView(progressBar);

        // ── Toggle login/register ─────────────────────────────────────────────
        LinearLayout toggleRow = new LinearLayout(this);
        toggleRow.setOrientation(LinearLayout.HORIZONTAL);
        toggleRow.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams toggleLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        toggleLp.setMargins(p24, dp(24), p24, dp(40));
        toggleRow.setLayoutParams(toggleLp);
        root.addView(toggleRow);

        TextView tvQuestion = new TextView(this);
        tvQuestion.setText("¿No tienes cuenta?  ");
        tvQuestion.setTextColor(0x77FFFFFF);
        tvQuestion.setTextSize(13f);
        toggleRow.addView(tvQuestion);

        btnToggle = new TextView(this);
        btnToggle.setText("Regístrate");
        btnToggle.setTextColor(0xFFCC1111);
        btnToggle.setTextSize(13f);
        btnToggle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btnToggle.setClickable(true);
        btnToggle.setFocusable(true);
        btnToggle.setOnClickListener(v -> toggleMode(tvQuestion));
        toggleRow.addView(btnToggle);
    }

    // ── Input field factory ───────────────────────────────────────────────────

    private EditText makeField(String hint, int inputType) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setHintTextColor(0x55FFFFFF);
        et.setTextColor(Color.WHITE);
        et.setTextSize(14f);
        et.setInputType(inputType);
        et.setBackground(null);
        et.setSingleLine(true);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF1A1A26);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), 0x33FFFFFF);
        et.setBackground(bg);
        et.setPadding(dp(16), dp(14), dp(16), dp(14));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(lp);
        return et;
    }

    private View spacer(int h) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h));
        return v;
    }

    // ── Mode toggle ───────────────────────────────────────────────────────────

    private void toggleMode(TextView tvQuestion) {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvTitle.setText("ULTRAGOL");
            tvSubtitle.setText("Inicia sesión para continuar");
            btnAction.setText("INICIAR SESIÓN");
            tvForgot.setVisibility(View.VISIBLE);
            tvQuestion.setText("¿No tienes cuenta?  ");
            btnToggle.setText("Regístrate");
        } else {
            tvTitle.setText("CREAR CUENTA");
            tvSubtitle.setText("Regístrate gratis");
            btnAction.setText("CREAR CUENTA");
            tvForgot.setVisibility(View.GONE);
            tvQuestion.setText("¿Ya tienes cuenta?  ");
            btnToggle.setText("Inicia sesión");
        }
    }

    // ── Auth actions ──────────────────────────────────────────────────────────

    private void onActionClick() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        if (isLoginMode) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        goToMain();
                    } else {
                        showAuthError(task.getException());
                    }
                });
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        goToMain();
                    } else {
                        showAuthError(task.getException());
                    }
                });
        }
    }

    private void showForgotPassword() {
        EditText etReset = new EditText(this);
        etReset.setHint("Tu correo electrónico");
        etReset.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etReset.setPadding(dp(16), dp(12), dp(16), dp(12));
        String pre = etEmail.getText().toString().trim();
        if (!pre.isEmpty()) etReset.setText(pre);

        new AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setView(etReset)
            .setPositiveButton("Enviar", (d, w) -> {
                String email = etReset.getText().toString().trim();
                if (email.isEmpty()) return;
                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            toast("Correo enviado a " + email);
                        } else {
                            toast("No se pudo enviar el correo");
                        }
                    });
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void showAuthError(Exception e) {
        String msg = "Error de autenticación";
        if (e instanceof FirebaseAuthInvalidUserException)
            msg = "No existe una cuenta con ese correo";
        else if (e instanceof FirebaseAuthInvalidCredentialsException)
            msg = "Correo o contraseña incorrectos";
        else if (e instanceof FirebaseAuthWeakPasswordException)
            msg = "La contraseña es muy débil (mínimo 6 caracteres)";
        else if (e instanceof FirebaseAuthUserCollisionException)
            msg = "Ya existe una cuenta con ese correo";
        else if (e != null && e.getMessage() != null)
            msg = e.getMessage();
        toast(msg);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAction.setEnabled(!loading);
        btnAction.setAlpha(loading ? 0.5f : 1f);
    }

    private void goToMain() {
        startActivity(new Intent(this, ProfileSelectorActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    @Override public void onBackPressed() {
        // Prevent going back to splash without logging in
        finishAffinity();
    }
}
