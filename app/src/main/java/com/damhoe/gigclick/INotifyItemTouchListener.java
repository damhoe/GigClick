package com.damhoe.gigclick;

public interface INotifyItemTouchListener {
    void onItemMoved(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
