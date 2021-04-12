package com.damhoe.gigclick.ui.library;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.INotifyItemTouchListener;
import com.damhoe.gigclick.IRecyclerViewHolder;
import com.damhoe.gigclick.OnStartDragListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.ui.ILibraryChangeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder>
        implements INotifyItemTouchListener {

    private final INotifyItemClickListener listener;
    private final OnStartDragListener dragListener;
    private final ILibraryChangeListener libListener;

    private ArrayList<Set> sets = new ArrayList<>();
    private boolean isActionMode = false;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public SetAdapter(INotifyItemClickListener listener, OnStartDragListener dragListener,
                      ILibraryChangeListener libListener) {
        this.listener = listener;
        this.dragListener = dragListener;
        this.libListener = libListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return sets.get(position).getId();
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
        initSelectedItems();
        notifyDataSetChanged();
    }

    public void setActionMode(boolean state) {
        isActionMode = state;
        notifyDataSetChanged();
    }

    private void initSelectedItems() {
        selectedItems.clear();
        for (int i=0; i<getItemCount(); i++) {
            selectedItems.put(i, false);
        }
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {

        Set set = sets.get(position);
        holder.title.setText(set.getTitle());

        holder.date.setText(set.getDateAsString());

        holder.star.setVisibility(set.isFave()? View.VISIBLE: View.GONE);

        holder.nTracks.setText(String.format(Locale.GERMANY, "%d Tracks", set.getNumberOfTracks()));
        holder.foreground.setOnClickListener(view -> {
            if (isActionMode) {
                listener.notifyClick(position);
            } else {
                listener.notifyClick(position, holder);
            }
        });
        holder.foreground.setOnLongClickListener(view -> {
            listener.notifyLongClick(position);
            return true;
        });

        holder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(holder);
                }
                return false;
            }
        });

        // set the transition names
        holder.itemView.setTransitionName("holder_trans" + set.getId());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int getItemCount() {
        return sets.size();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(sets, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(sets, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Set set = sets.get(position);
        sets.remove(position);
        notifyItemRemoved(position);
        libListener.notifySetDeleted(position, set);
    }

    public void insertItem(int position, Set set) {
        sets.add(position, set);
        notifyItemInserted(position);
    }

    public static class SetViewHolder extends RecyclerView.ViewHolder implements
            IRecyclerViewHolder {

        TextView title, date, nTracks;
        ImageView star, handle;
        public View foreground, background;
        public View red;

        ImageView blackBin, whiteBin;

        float aFloat = 0f;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            date = itemView.findViewById(R.id.text_date);
            nTracks = itemView.findViewById(R.id.text_n_tracks);
            star = itemView.findViewById(R.id.star);
            handle = itemView.findViewById(R.id.ic_drag);
            foreground = itemView.findViewById(R.id.foreground);
            background = itemView.findViewById(R.id.background);
            red = itemView.findViewById(R.id.color);
            blackBin = itemView.findViewById(R.id.black_bin);
            whiteBin = itemView.findViewById(R.id.white_bin);
        }

        @Override
        public void onDragSelected() {
            itemView.setTranslationZ(8f);
            itemView.setSelected(true);
        }

        @Override
        public void onDragCleared() {
            itemView.setTranslationZ(aFloat);
            itemView.setSelected(false);
        }

        @Override
        public void onSwipeSelected() {
            itemView.setTranslationZ(8f);
            itemView.setSelected(true);
        }

        @Override
        public void onSwipeCleared() {
            background.setVisibility(View.INVISIBLE);
            red.setVisibility(View.INVISIBLE);
            whiteBin.setAlpha(0f);
            blackBin.setAlpha(1f);

            itemView.setTranslationZ(0f);
            itemView.setSelected(false);
        }

        public void hideRed() {
            if (red.getVisibility() == View.VISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.item_bg_hide);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        background.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                Animation fadeIn = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in);
                Animation fadeOut = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_out);
                red.startAnimation(animation);
                whiteBin.startAnimation(fadeOut);
                whiteBin.setAlpha(0f);
                blackBin.startAnimation(fadeIn);
                blackBin.setAlpha(1f);
                red.setVisibility(View.INVISIBLE);
            }
        }

        public void showRed() {
            if (red.getVisibility() == View.INVISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.item_bg_appear);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //ignore.
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        background.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // ignore.
                    }
                });
                Animation fadeIn = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in);
                Animation fadeOut = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_out);
                red.startAnimation(animation);
                blackBin.startAnimation(fadeOut);
                blackBin.setAlpha(0f);
                whiteBin.startAnimation(fadeIn);
                whiteBin.setAlpha(1f);
                red.setVisibility(View.VISIBLE);
            }
        }
    }

    public void selectAll() {
        for (int k=0; k<getItemCount(); k++) {
            selectedItems.put(k, true);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        initSelectedItems();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        boolean val = selectedItems.get(position);
        selectedItems.put(position, !val);
        notifyDataSetChanged();
    }

    public ArrayList<Long> getSelectedItemIds() {
        ArrayList<Long> list = new ArrayList<>();
        for (int i=0; i<selectedItems.size(); i++) {
            if (selectedItems.get(i)) {
                list.add(sets.get(i).getId());
            }
        }
        return list;
    }

    public ArrayList<Set> getSelectedItems() {
        ArrayList<Set> list = new ArrayList<>();
        for (int i=0; i<selectedItems.size(); i++) {
            if (selectedItems.get(i)) {
                list.add(sets.get(i));
            }
        }
        return list;
    }

    public Set getSetAt(int position) {
        return sets.get(position);
    }
}
