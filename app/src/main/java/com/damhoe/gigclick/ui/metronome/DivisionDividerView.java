package com.damhoe.gigclick.ui.metronome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.damhoe.gigclick.R;
import com.damhoe.gigclick.databinding.FragmentSetBinding;

import org.jetbrains.annotations.Nullable;

public class DivisionDividerView extends AppCompatImageView {

    private final static int IMG_SIZE = 36;

    public DivisionDividerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int img_size = dpToPx(IMG_SIZE);

        float w = getWidth();
        float h = getHeight();
        float w2 = w / 2;
        float h2 = h / 2 * .7f;

        // width of bezier curve and gap of anchor points
        float l = img_size + 40;
        float g = 20;
        float g2 = 10;

        h -= 10;

        // help variables
        float c1 = w2 - l * .5f;
        float c2 = w2 + l * .5f;

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorBackgroundDark));
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);

        Path path = new Path();
        path.moveTo(0f, h2);
        path.lineTo(w2 - l, h2);

        path.cubicTo(c1 + g, h2, c1 - g2, h, w2, h);

        path.cubicTo(c2 + g2, h, c2 - g, h2, w2 + l, h2);

        // path.moveTo(w2 + img_size * .5f, h2);
        path.lineTo(w, h2);

        canvas.drawPath(path, paint);
    }
}
