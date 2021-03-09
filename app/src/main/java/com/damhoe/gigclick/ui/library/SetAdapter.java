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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {

    private INotifyItemClickListener listener;
    private ArrayList<Set> sets = new ArrayList<>();

    public SetAdapter(INotifyItemClickListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return sets.get(position).getId();
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {

        Set set = sets.get(position);
        holder.title.setText(set.getTitle());

        SimpleDateFormat df = new SimpleDateFormat("MMM, d ''yy");
        holder.date.setText(df.format(set.getDate()));

        holder.nTracks.setText(String.format(Locale.GERMANY, "%d", set.getNumberOfTracks()));
        holder.itemView.setOnClickListener(view -> {
            listener.notifyClick(position, holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            listener.notifyLongClick(position);
            return true;
        });

        // set the transition names
        holder.title.setTransitionName("set_title_" + set.getId());
        holder.date.setTransitionName("set_data_" + set.getId());
        holder.nTracks.setTransitionName("set_ntracks_" + set.getId());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int getItemCount() {
        return sets.size();
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
