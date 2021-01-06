package com.damhoe.gigclick;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public interface INotifyItemClickListener {
    void notifyClick(int position);
    void notifyClick(int position, RecyclerView.ViewHolder view);
    void notifyLongClick(int position);
}
