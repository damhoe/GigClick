package com.damhoe.gigclick.ui.live;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;
import java.util.Locale;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private ArrayList<Track> tracks = new ArrayList<>();
    INotifyItemClickListener listener;
    private int selected;

    public TrackAdapter(INotifyItemClickListener listener) {
        this.listener = listener;
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
        holder.title.setText(tracks.get(position).getTitle());
        holder.comment.setText(tracks.get(position).getComment());
        holder.number.setText(String.format(Locale.GERMANY, "%d", position + 1)); // start with 1
        holder.bpm.setText(String.format(Locale.GERMANY, "%3.0f", tracks.get(position).getTempo().getBpm()));
        holder.itemView.setOnClickListener(view -> {
            listener.notifyClick(position);
            selected = position;
        });
        holder.itemView.setOnLongClickListener(view -> {
            listener.notifyLongClick(position);
            return true;
        });
        if (position == selected) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
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
}
