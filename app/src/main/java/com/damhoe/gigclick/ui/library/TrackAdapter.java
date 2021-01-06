package com.damhoe.gigclick.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;

import java.util.Locale;

public class TrackAdapter extends RecyclerView.Adapter<com.damhoe.gigclick.ui.library.TrackAdapter.TrackViewHolder> {

    private LibraryViewModel viewModel;
    private INotifyItemClickListener listener;

    public TrackAdapter(LibraryViewModel viewModel, INotifyItemClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public com.damhoe.gigclick.ui.library.TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new com.damhoe.gigclick.ui.library.TrackAdapter.TrackViewHolder(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull com.damhoe.gigclick.ui.library.TrackAdapter.TrackViewHolder holder, int position) {

        Track track = viewModel.getCurrentSetLD().getValue().getTracks().get(position);
        holder.title.setText(track.getTitle());
        holder.comment.setText(track.getComment());
        holder.number.setText(String.format(Locale.GERMANY, "%d", position + 1)); // start with 1
        holder.bpm.setText(String.format(Locale.GERMANY, "%3.0f", track.getTempo().getBpm()));
        holder.itemView.setOnClickListener(view -> {
            viewModel.selectTrack(position);
            listener.notifyClick(position, holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            //listener.notifyLongClick(position);
            return true;
        });

        // set transition names
        holder.title.setTransitionName("track_title_" + position);
        holder.bpm.setTransitionName("track_bpm_" + position);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int getItemCount() {
        return viewModel.getCurrentSetLD().getValue().getTracks().size();
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
