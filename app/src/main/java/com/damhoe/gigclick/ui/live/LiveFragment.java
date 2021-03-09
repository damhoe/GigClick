package com.damhoe.gigclick.ui.live;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentLiveBinding;
import com.damhoe.gigclick.ui.TrackItemDivider;
import com.damhoe.gigclick.ui.library.SetAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Locale;

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

        binding.buttonSelectSet.setOnClickListener(v -> {

            String[] setTitles = {"Set 1", "set 2", "set 3"};
            int selected = 2;

            new MaterialAlertDialogBuilder(getContext(), R.style.GigClickTheme_AlertDialog)
                    .setTitle(getResources().getString(R.string.dialog_select_set_title))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                    .setPositiveButton("Apply", ((dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }))
                    .setSingleChoiceItems(setTitles, selected, (dialogInterface, i) -> {
                        // ignore.
                    })
                    .show();
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


        viewModel.getxLD().observe(getViewLifecycleOwner(), x -> {
            binding.pendulumForeground.setPositionX(x);
        });

        viewModel.getSetLD().observe(getViewLifecycleOwner(), set -> {
            adapter.setTracks(set.getTracks());
        });

        viewModel.getTrackLD().observe(getViewLifecycleOwner(), this::updateUI);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    private TrackAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    private void updateUI(Track track) {
        // show current playing Track in preview
        binding.textBpm.setText(String.format(Locale.GERMANY, "%3.0f", track.getTempo().getBpm()));
        //binding.textTempo.setText(track.getTempo().getLabel());
        binding.textTitle.setText(track.getTitle());
        //binding.textComment.setText(track.getComment());
        //binding.textArtist.setText(track.getArtist());
        binding.textTrackNumber.setText(String.format(Locale.GERMANY, "%d", viewModel.getTrackNumber(track.getId())));
        adapter.notifyDataSetChanged();
    }

    private void setUpTrackRecyclerView() {
        adapter = new TrackAdapter(
                new INotifyItemClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void notifyClick(int position) {
                        if (position == viewModel.getSelectedLD().getValue()) {
                            viewModel.setRunState(!viewModel.getRunStateLD().getValue());
                        } else {
                            viewModel.setSelected(position);
                            if (!viewModel.getRunStateLD().getValue()) {
                                viewModel.setRunState(true);
                            }
                        }
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

            final int REFRESH_RATE = 700; // per second
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
                    //binding.pendulumForeground.setPositionX(0.);
                    viewModel.setxLD(0.);
                }
            }

            @SuppressWarnings("ConstantConditions")
            private void updatePendulum() throws InterruptedException {
                f = viewModel.getTempoLD().getValue().getBpm() / 60000.; // beats per ms
                t = System.currentTimeMillis();
                x = Math.sin(Math.PI * f * (t - t0) + phi0);
                //dx = Math.cos(Math.PI * f * (t - t0) + phi0);
                viewModel.setxLD(x);
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