package com.damhoe.gigclick.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;

import java.util.Locale;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {

    private LibraryViewModel viewModel;
    private INotifyItemClickListener listener;

    public SetAdapter(LibraryViewModel viewModel, INotifyItemClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull SetAdapter.SetViewHolder holder, int position) {

        Set set = viewModel.getSetsLD().getValue().get(position);
        holder.title.setText(set.getTitle());

        if (set.getDate() != null) {
            holder.date.setText("TODO...");
        }
        holder.nTracks.setText(String.format(Locale.GERMANY, "%d", set.getNumberOfTracks()));
        holder.itemView.setOnClickListener(view -> {
            viewModel.selectSet(position);
            listener.notifyClick(position, holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            listener.notifyLongClick(position);
            return true;
        });

        // set the transition names
        holder.title.setTransitionName("set_title_" + position);
        holder.date.setTransitionName("set_data_" + position);
        holder.nTracks.setTransitionName("set_ntracks_" + position);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int getItemCount() {
        return viewModel.getSetsLD().getValue().size();
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, nTracks;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            date = itemView.findViewById(R.id.text_date);
            nTracks = itemView.findViewById(R.id.text_n_tracks);
        }
    }
}
