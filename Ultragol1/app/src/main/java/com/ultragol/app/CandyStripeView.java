package com.ultragol.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Barra de progreso "estilo caramelo": franjas diagonales rojas en movimiento
 * continuo, como las clásicas barras de descarga tipo "barber pole".
 */
public class CandyStripeView extends View {

    private Paint paint;
    private BitmapShader shader;
    private final Matrix shaderMatrix = new Matrix();
    private float offset = 0f;
    private int tileSize = 1;

    public CandyStripeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        tileSize = Math.max(1, (int) (26 * density));

        Bitmap bmp = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        // Base roja intensa
        c.drawColor(Color.parseColor("#D50000"));

        // Franjas diagonales rojo más claro ("caramelo")
        Paint stripePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stripePaint.setColor(Color.parseColor("#FF5252"));
        c.save();
        c.rotate(45f, tileSize / 2f, tileSize / 2f);
        float half = tileSize / 2f;
        c.drawRect(-tileSize, -tileSize, half - tileSize * 0.5f, tileSize * 2, stripePaint);
        c.drawRect(tileSize - half + tileSize * 0.5f, -tileSize, tileSize, tileSize * 2, stripePaint);
        c.restore();

        shader = new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);
    }

    private final Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            offset -= 2.4f;
            if (offset <= -tileSize * 200) offset = 0f;
            invalidate();
            postDelayed(this, 16);
        }
    };

    public void startAnimating() {
        removeCallbacks(animRunnable);
        post(animRunnable);
    }

    public void stopAnimating() {
        removeCallbacks(animRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shader == null || getWidth() == 0 || getHeight() == 0) return;
        shaderMatrix.setTranslate(offset, 0f);
        shader.setLocalMatrix(shaderMatrix);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimating();
    }
}
