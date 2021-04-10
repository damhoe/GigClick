package com.damhoe.gigclick.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.damhoe.gigclick.R;

public class TrackItemDivider extends DividerItemDecoration {

    private final Drawable mDivider;

    public TrackItemDivider(Context context, int orientation) {
        super(context, orientation);
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider_track_item);
        this.setDrawable(mDivider);
    }
}