package com.damhoe.gigclick.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.INotifyItemTouchListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.ui.GigClickTouchCallback;

import java.util.ArrayList;
import java.util.Locale;

public class TrackAdapter extends RecyclerView.Adapter<com.damhoe.gigclick.ui.library.TrackAdapter.TrackViewHolder> implements INotifyItemTouchListener {

    private ArrayList<Track> tracks = new ArrayList<>();
    private INotifyItemClickListener listener;

    public TrackAdapter(INotifyItemClickListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return tracks.get(position).getId();
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {

        Track track = tracks.get(position);
        holder.title.setText(track.getTitle());
        holder.comment.setText(track.getComment());
        holder.number.setText(String.format(Locale.GERMANY, "%d", position + 1)); // start with 1
        holder.bpm.setText(String.format(Locale.GERMANY, "%3.0f", track.getTempo().getBpm()));
        holder.itemView.setOnClickListener(view -> {
            listener.notifyClick(position, holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            listener.notifyLongClick(position);
            return true;
        });

        // set transition names
        holder.title.setTransitionName("track_title_" + position);
        holder.bpm.setTransitionName("track_bpm_" + position);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {

    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {

        TextView title, comment, bpm, number;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            comment = itemView.findViewById(R.id.text_comment);
            bpm = itemView.findViewById(R.id.text_bpm);
            number = itemView.findViewById(R.id.text_number);
        }
    }

    public Track getTrackAt(int position) {
        return tracks.get(position);
    }
}
