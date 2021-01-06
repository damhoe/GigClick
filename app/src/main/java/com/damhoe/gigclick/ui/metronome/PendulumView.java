package com.damhoe.gigclick.ui.metronome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.damhoe.gigclick.INotifyListener;
import com.damhoe.gigclick.R;

import org.jetbrains.annotations.Nullable;

public class PendulumView extends androidx.appcompat.widget.AppCompatImageView {

    final static float PENDULUM_WIDTH = 48; // dp

    double x = 0., y = 0.; // relative coordinates -1 < x < 1 and 0 < y < 1
    float u, v; // absolute coordinates

    public PendulumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        transformCoordinates();

        float left = u;
        float right = left + PENDULUM_WIDTH;
        float top = v;
        float bottom = top + getHeight();

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorAccentLight));
        paint.setStrokeWidth(0);
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void transformCoordinates() {
        float width = getWidth() - PENDULUM_WIDTH;
        float height = getHeight();
        u = (float) ((x + 1) * 0.5 * width);
        v = (float) (y * height);
    }

    public void setPositionX(double newX) {
        this.x = newX;
        invalidate();
    }

    public double getPositionX() {
        return x;
    }
}

