package com.damhoe.gigclick.ui.metronome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.damhoe.gigclick.INotifyListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.ui.RotatableImageView;

import org.jetbrains.annotations.Nullable;

public class RotaryView extends FrameLayout {

    // for rotation
    public float angle = -135;
    public float angleRing = -135;

    Paint paint;
    INotifyListener listener;

    // for tapping
    private long first = 0;
    private long second = 0;
    private static final long TOLERANCE = 100;

    private RotatableImageView knob;
    private RotatableImageView ring;
    private int ringRes = R.drawable.rotary_ring_unpressed;
    private boolean isPressed = false;

    public RotaryView(Context context) {
        super(context);
    }

    public RotaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNotifyListener(INotifyListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onCreate();
    }

    private void onCreate() {
        knob = findViewById(R.id.knob);
        ring = findViewById(R.id.ring);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        ring.setImage(ringRes);

        knob.setAngle(angle);
        knob.invalidate();
        ring.setRotation(angleRing);
        //ring.setAngle(angleRing);
        ring.invalidate();
    }


    private double previousAlpha;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float cx = (float) getWidth() / 2;
        float cy = (float) getHeight() / 2;
        float x = event.getX();
        float y = event.getY();
        double alpha = Math.atan2(y - cy, x - cx) * 180 / Math.PI;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                listener.onNotifyTouch(MotionEvent.ACTION_DOWN);

                ringRes = R.drawable.rotary_ring_full_pressed;

                angleRing = (float) (alpha + 90);
                updateViews();

                final long third = System.currentTimeMillis();
                long a = third - second;
                long b = second - first;

                if (TOLERANCE < a && Math.abs(a - b) < TOLERANCE) {
                    listener.onNotifyMillis((a + b) / 2);
                }

                first = second;
                second = third;
                break;

            case MotionEvent.ACTION_MOVE:

                ringRes = R.drawable.rotary_ring_side_pressed;

                double delta = (alpha - previousAlpha);

                if (delta > 180)
                    delta -= 360;

                if (delta < -180)
                    delta += 360;

                angle += delta;
                angleRing += delta;

                if (listener != null)
                    listener.onNotifyAngle(delta);

                updateViews();
                break;

            case MotionEvent.ACTION_SCROLL:
                return false;

            case MotionEvent.ACTION_UP:
                ringRes = R.drawable.rotary_ring_unpressed;
                updateViews();
                listener.onNotifyTouch(MotionEvent.ACTION_UP);
        }

        previousAlpha = alpha;
        return true;
    }

    private void updateViews() {
        onCreate();
    }
}

