package com.damhoe.gigclick.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class RotatableImageView extends AppCompatImageView {

    private float angle = -135f;
    Matrix matrix = new Matrix();

    public RotatableImageView(@NonNull Context context) {
        super(context);
    }

    public RotatableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    private void onCreate() {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onCreate();
        rotateKnob();
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setImage(int resourceId) {
        setImageResource(resourceId);
    }

    private void rotateKnob() {
        int w = this.getDrawable().getIntrinsicWidth();
        int h = this.getDrawable().getIntrinsicHeight();
        matrix.reset();
        matrix.setRotate(angle, w / 2, h / 2);
        this.setScaleType(ImageView.ScaleType.MATRIX);
        this.setImageMatrix(matrix);
    }
}
