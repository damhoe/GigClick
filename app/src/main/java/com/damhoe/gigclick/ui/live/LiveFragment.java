package com.damhoe.gigclick.ui.live;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentLiveBinding;
import com.damhoe.gigclick.ui.TrackItemDivider;

import java.util.Locale;

import kotlin.collections.AbstractIterator;

public class LiveFragment extends Fragment {

    private FragmentLiveBinding binding;
    private LiveViewModel viewModel;
    MainActivity mainActivity;
    ActionBar bar;

    private double x, dx, phi0;
    private long t0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LiveViewModel.class);
        viewModel.setRunState(false); // stop the player when starting
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live, container, false);

        mainActivity = (MainActivity) getActivity();

        setUpTrackRecyclerView();

        binding.buttonPlay.setOnClickListener(v -> {
            boolean isPlaying = viewModel.getRunStateLD().getValue();
            viewModel.setRunState(!isPlaying);
        });

        viewModel.getRunStateLD().observe(getViewLifecycleOwner(), isPlaying -> {
            Drawable drawable;
            if (isPlaying) {
                startPendulumThread();
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_stop_black_24dp);
            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_play_black_24dp);
            }
            binding.buttonPlay.setImageDrawable(drawable);
        });

        viewModel.getSelectedLD().observe(getViewLifecycleOwner(), this::updateUI);

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    private TrackAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    private void updateUI(int selected) {
        Track track = viewModel.getCurrentTracksLD().getValue().get(selected);
        binding.textBpm.setText(String.format(Locale.GERMANY, "%3.0f", track.getTempo().getBpm()));
        binding.textTempo.setText(track.getTempo().getLabel());
        binding.textTitle.setText(track.getTitle());
        binding.textComment.setText(track.getComment());
        binding.textArtist.setText(track.getArtist());
        binding.textTrackNumber.setText(String.format(Locale.GERMANY, "%d", selected + 1));
        adapter.notifyDataSetChanged();
    }

    private void setUpTrackRecyclerView() {
        adapter = new TrackAdapter(viewModel, viewModel.getBibLD().getValue(),
                new INotifyItemClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void notifyClick(int position) {
                        // ignore.
                    }

                    @Override
                    public void notifyClick(int position, RecyclerView.ViewHolder view) {
                        // ignore.
                    }

                    @Override
                    public void notifyLongClick(int position) {
                        // TODO: go to edit track menu
                    }
                });
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recycler.addItemDecoration(new TrackItemDivider(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void startPendulumThread() {

        new Thread(new Runnable() {

            final int REFRESH_RATE = 500; // per second
            final int DELAY = 1000 / REFRESH_RATE;

            long t;
            double f;

            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                try {
                    t0 = System.currentTimeMillis();
                    phi0 = 0.;
                    dx = 1.;
                    while (viewModel.getRunStateLD().getValue()) {
                        updatePendulum();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    // reset UI elements
                    binding.pendulumForeground.setPositionX(0.);
                }
            }

            @SuppressWarnings("ConstantConditions")
            private void updatePendulum() throws InterruptedException {
                f = viewModel.getTempoLD().getValue().getBpm() / 60000.; // beats per ms
                t = System.currentTimeMillis();
                x = Math.sin(Math.PI * f * (t - t0) + phi0);
                dx = Math.cos(Math.PI * f * (t - t0) + phi0);
                binding.pendulumForeground.setPositionX(x);
                Thread.sleep(DELAY);
            }
        }).start();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.setRunState(false); // stop the player when exiting
    }
}