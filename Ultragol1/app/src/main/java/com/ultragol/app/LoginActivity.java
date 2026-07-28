package com.ultragol.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_GOOGLE = 9001;
    private static final String WEB_CLIENT_ID =
        "62425304873-uk10oeag3sf4e0q980o5850ei5ge0eha.apps.googleusercontent.com";

    private FirebaseAuth        mAuth;
    private GoogleSignInClient  googleClient;

    private EditText    etEmail, etPassword;
    private TextView    btnAction, btnToggle, tvForgot, tvTitle, tvSubtitle, tvQuestion;
    private ProgressBar progressBar;
    private boolean     isLoginMode = true;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) { syncAndGoToMain(); return; }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build();
        googleClient = GoogleSignIn.getClient(this, gso);

        buildUI();
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                setLoading(true);
                AuthCredential cred = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(cred)
                    .addOnCompleteListener(this, t -> {
                        if (t.isSuccessful()) syncAndGoToMain();
                        else { setLoading(false); toast("Error con Google Sign-In"); }
                    });
            } catch (ApiException e) {
                int code = e.getStatusCode();
                if (code == 12501 || code == 12500) {
                    // 12501 = cancelado por el usuario, 12500 = flujo cancelado
                } else if (code == 10) {
                    // DEVELOPER_ERROR — SHA-1 no registrada en Firebase Console
                    toast("Error de configuración Google (código 10): registra el SHA-1 en Firebase Console");
                } else {
                    toast("Error Google Sign-In (código " + code + ")");
                }
            }
        }
    }

    @Override public void onBackPressed() { finishAffinity(); }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ 0xFF0A000F, 0xFF070707 });
        root.setBackground(bg);
        setContentView(root);

        // ── Top spacer + brand ────────────────────────────────────────────────
        root.addView(spacer(dp(90)));

        View bar = new View(this);
        GradientDrawable barBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{ 0xFFCC1111, 0xFFFF4422 });
        barBg.setCornerRadius(dp(2));
        bar.setBackground(barBg);
        LinearLayout.LayoutParams barLp =
            new LinearLayout.LayoutParams(dp(48), dp(4));
        barLp.gravity = Gravity.CENTER_HORIZONTAL;
        barLp.bottomMargin = dp(18);
        bar.setLayoutParams(barLp);
        root.addView(bar);

        tvTitle = label("ULTRAGOL", 30f, Color.WHITE, true);
        tvTitle.setLetterSpacing(0.15f);
        tvTitle.setGravity(Gravity.CENTER);
        marginBottom(tvTitle, dp(6));
        root.addView(tvTitle);

        tvSubtitle = label("Inicia sesión para continuar", 13f, 0x88FFFFFF, false);
        tvSubtitle.setGravity(Gravity.CENTER);
        marginBottom(tvSubtitle, dp(36));
        root.addView(tvSubtitle);

        // ── Form card ─────────────────────────────────────────────────────────
        LinearLayout card = card(dp(24));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.leftMargin = cardLp.rightMargin = dp(22);
        card.setLayoutParams(cardLp);
        root.addView(card);

        etEmail    = field("Correo electrónico", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etPassword = field("Contraseña",
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        card.addView(etEmail);
        card.addView(spacer(dp(10)));
        card.addView(etPassword);

        tvForgot = label("¿Olvidaste tu contraseña?", 12f, 0xFFCC1111, false);
        tvForgot.setGravity(Gravity.END);
        LinearLayout.LayoutParams fLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fLp.topMargin = dp(10); fLp.bottomMargin = dp(18);
        tvForgot.setLayoutParams(fLp);
        tvForgot.setClickable(true); tvForgot.setFocusable(true);
        tvForgot.setOnClickListener(v -> showForgotPassword());
        card.addView(tvForgot);

        // Email login button
        btnAction = actionBtn("INICIAR SESIÓN", 0xFFCC1111, 0xFFFF3311);
        btnAction.setOnClickListener(v -> onEmailAction());
        card.addView(btnAction);

        // Divider "o"
        LinearLayout divRow = new LinearLayout(this);
        divRow.setOrientation(LinearLayout.HORIZONTAL);
        divRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams drLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        drLp.topMargin = dp(14); drLp.bottomMargin = dp(14);
        divRow.setLayoutParams(drLp);
        View divL = dividerLine(); View divR = dividerLine();
        TextView tvOr = label("  ó  ", 12f, 0x44FFFFFF, false);
        tvOr.setGravity(Gravity.CENTER);
        divRow.addView(divL); divRow.addView(tvOr); divRow.addView(divR);
        card.addView(divRow);

        // Google Sign-In button
        TextView btnGoogle = googleBtn();
        btnGoogle.setOnClickListener(v ->
            startActivityForResult(googleClient.getSignInIntent(), RC_GOOGLE));
        card.addView(btnGoogle);

        // Progress
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminateTintList(
            android.content.res.ColorStateList.valueOf(0xFFCC1111));
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pbLp.gravity = Gravity.CENTER_HORIZONTAL;
        pbLp.topMargin = dp(8);
        progressBar.setLayoutParams(pbLp);
        progressBar.setVisibility(View.GONE);
        card.addView(progressBar);

        // ── Toggle login/register ─────────────────────────────────────────────
        LinearLayout toggleRow = new LinearLayout(this);
        toggleRow.setOrientation(LinearLayout.HORIZONTAL);
        toggleRow.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tLp.topMargin = dp(22); tLp.bottomMargin = dp(40);
        tLp.leftMargin = tLp.rightMargin = dp(22);
        toggleRow.setLayoutParams(tLp);
        root.addView(toggleRow);

        tvQuestion = label("¿No tienes cuenta?  ", 13f, 0x77FFFFFF, false);
        toggleRow.addView(tvQuestion);

        btnToggle = label("Regístrate", 13f, 0xFFCC1111, true);
        btnToggle.setClickable(true); btnToggle.setFocusable(true);
        btnToggle.setOnClickListener(v -> toggleMode());
        toggleRow.addView(btnToggle);
    }

    // ── Widget factories ──────────────────────────────────────────────────────

    private TextView label(String text, float size, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    private EditText field(String hint, int inputType) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setHintTextColor(0x55FFFFFF);
        et.setTextColor(Color.WHITE);
        et.setTextSize(14f);
        et.setInputType(inputType);
        et.setSingleLine(true);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF1A1A26); bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), 0x33FFFFFF);
        et.setBackground(bg);
        et.setPadding(dp(16), dp(14), dp(16), dp(14));
        et.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return et;
    }

    private TextView actionBtn(String text, int colorL, int colorR) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btn.setLetterSpacing(0.08f);
        btn.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, new int[]{ colorL, colorR });
        bg.setCornerRadius(dp(12));
        btn.setBackground(bg);
        btn.setPadding(0, dp(15), 0, dp(15));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(lp);
        btn.setClickable(true); btn.setFocusable(true);
        return btn;
    }

    private TextView googleBtn() {
        TextView btn = new TextView(this);
        btn.setText("G   Continuar con Google");
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btn.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF1E1E2A);
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(1), 0x44FFFFFF);
        btn.setBackground(bg);
        btn.setPadding(0, dp(15), 0, dp(15));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(lp);
        btn.setClickable(true); btn.setFocusable(true);
        return btn;
    }

    private LinearLayout card(int padding) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF111118); bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), 0x22FFFFFF);
        l.setBackground(bg);
        l.setPadding(padding, padding, padding, padding);
        return l;
    }

    private View dividerLine() {
        View v = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(1), 1f);
        v.setLayoutParams(lp);
        v.setBackgroundColor(0x22FFFFFF);
        return v;
    }

    private View spacer(int h) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, h));
        return v;
    }

    private void marginBottom(View v, int margin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = margin;
        v.setLayoutParams(lp);
    }

    // ── Mode toggle ───────────────────────────────────────────────────────────

    private void toggleMode() {
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

    // ── Auth ──────────────────────────────────────────────────────────────────

    private void onEmailAction() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (email.isEmpty())    { etEmail.setError("Ingresa tu correo"); return; }
        if (password.isEmpty()) { etPassword.setError("Ingresa tu contraseña"); return; }
        if (password.length() < 6) { etPassword.setError("Mínimo 6 caracteres"); return; }

        setLoading(true);
        if (isLoginMode) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, t -> {
                    if (t.isSuccessful()) syncAndGoToMain();
                    else { setLoading(false); showAuthError(t.getException()); }
                });
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, t -> {
                    if (t.isSuccessful()) syncAndGoToMain();
                    else { setLoading(false); showAuthError(t.getException()); }
                });
        }
    }

    private void showForgotPassword() {
        EditText et = new EditText(this);
        et.setHint("Tu correo electrónico");
        et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et.setPadding(dp(16), dp(12), dp(16), dp(12));
        String pre = etEmail.getText().toString().trim();
        if (!pre.isEmpty()) et.setText(pre);
        new AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setView(et)
            .setPositiveButton("Enviar", (d, w) -> {
                String email = et.getText().toString().trim();
                if (email.isEmpty()) return;
                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(t ->
                        toast(t.isSuccessful() ? "Correo enviado a " + email
                                               : "No se pudo enviar el correo"));
            })
            .setNegativeButton("Cancelar", null).show();
    }

    private void showAuthError(Exception e) {
        String msg = "Error de autenticación";
        if (e instanceof FirebaseAuthInvalidUserException)
            msg = "No existe cuenta con ese correo";
        else if (e instanceof FirebaseAuthInvalidCredentialsException)
            msg = "Correo o contraseña incorrectos";
        else if (e instanceof FirebaseAuthWeakPasswordException)
            msg = "Contraseña muy débil (mínimo 6 caracteres)";
        else if (e instanceof FirebaseAuthUserCollisionException)
            msg = "Ya existe una cuenta con ese correo";
        else if (e != null && e.getMessage() != null) msg = e.getMessage();
        toast(msg);
    }

    // ── Sync & navigate ───────────────────────────────────────────────────────

    /** Pull cloud data, then navigate to ProfileSelector. */
    private void syncAndGoToMain() {
        String uid = mAuth.getCurrentUser().getUid();
        // Pull with loading spinner; go to main when done (or on failure)
        UserSyncManager.pullFromCloud(getApplicationContext(), uid, () ->
            runOnUiThread(() -> {
                setLoading(false);
                startActivity(new Intent(this, ProfileSelectorActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setLoading(boolean on) {
        progressBar.setVisibility(on ? View.VISIBLE : View.GONE);
        btnAction.setEnabled(!on);
        btnAction.setAlpha(on ? 0.5f : 1f);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
