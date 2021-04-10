package com.damhoe.gigclick.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemTouchListener;
import com.damhoe.gigclick.IRecyclerViewHolder;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.ui.library.SetAdapter;

public class GigClickTouchCallback extends ItemTouchHelper.Callback {

    private INotifyItemTouchListener listener;
    private float x;

    public GigClickTouchCallback(INotifyItemTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        listener.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragDirs = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeDirs =ItemTouchHelper.START;
        return makeMovementFlags(dragDirs, swipeDirs);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ((IRecyclerViewHolder) viewHolder).onDragSelected();
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            x = 0;
            getDefaultUIUtil().onSelected(((SetAdapter.SetViewHolder) viewHolder).foreground);
            ((IRecyclerViewHolder) viewHolder).onSwipeSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        getDefaultUIUtil().clearView(((SetAdapter.SetViewHolder) viewHolder).foreground);
        super.clearView(recyclerView, viewHolder);

        ((IRecyclerViewHolder) viewHolder).onSwipeCleared();
        ((IRecyclerViewHolder) viewHolder).onDragCleared();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            x += dX;
            if (dX < -viewHolder.itemView.getHeight()) {
                ((SetAdapter.SetViewHolder) viewHolder).showRed();
            } else {
                ((SetAdapter.SetViewHolder) viewHolder).hideRed();
            }
            getDefaultUIUtil().onDraw(c, recyclerView, ((SetAdapter.SetViewHolder) viewHolder).foreground, dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            getDefaultUIUtil().onDrawOver(c, recyclerView, ((SetAdapter.SetViewHolder) viewHolder).foreground, dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
