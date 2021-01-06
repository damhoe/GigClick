package com.damhoe.gigclick.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.R;

public class TrackItemDivider extends DividerItemDecoration {

    private final Drawable mDivider;

    public TrackItemDivider(Context context, int orientation) {
        super(context, orientation);
        mDivider = ContextCompat.getDrawable(context, R.drawable.track_item_divider);
        this.setDrawable(mDivider);
    }
}