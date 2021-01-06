package com.damhoe.gigclick.ui.metronome;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.damhoe.gigclick.Beat;
import com.damhoe.gigclick.INotifyListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.databinding.FragmentMetronomeBinding;

import java.util.ArrayList;
import java.util.Locale;

import static com.damhoe.gigclick.Track.MAX_BEATS;

public class MetronomeFragment extends Fragment {

    public static final double FACTOR_ANGLE_TO_BEATS = 0.2;

    private MetronomeViewModel viewModel;
    private FragmentMetronomeBinding binding;
        private Handler handler;

        private double x, dx, phi0;
        private long t0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MetronomeViewModel.class);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_metronome, container,false);
        handler = new Handler();

        ((RotaryView) binding.rotaryForeground).setNotifyListener(new INotifyListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onNotifyAngle(double angle) {
                double bpm = viewModel.getTempoLD().getValue().getBpm();
                bpm +=  angle * FACTOR_ANGLE_TO_BEATS;
                viewModel.setBPM(bpm);
            }

            @Override
            public void onNotifyMillis(long millis) {
                double bpm = 60000. / millis;
                viewModel.setBPM(bpm);
            }
        });

        binding.buttonIncrease.setOnClickListener(v -> {
            double bpm = viewModel.getTempoLD().getValue().getBpm();
            bpm +=  1;
            viewModel.setBPM(bpm);
        });

        binding.buttonDecrease.setOnClickListener(v -> {
            double bpm = viewModel.getTempoLD().getValue().getBpm();
            bpm -= 1;
            viewModel.setBPM(bpm);
        });

        binding.buttonPlay.setOnClickListener(v -> {
            boolean isPlaying = viewModel.getRunStateLD().getValue();
            viewModel.setRunState(!isPlaying);
        });

        binding.buttonAddBeat.setOnClickListener(v -> viewModel.addBeat());
        binding.buttonDeleteBeat.setOnClickListener(v -> viewModel.removeBeat());

        binding.buttonDivision.setOnClickListener(view -> {
            findNavController().navigate(MetronomeFragmentDirections.actionNavigationMetronomeToDivisionDialog());
        });

        binding.buttonMode.setOnClickListener(view -> {
            findNavController().navigate(MetronomeFragmentDirections.actionNavigationMetronomeToPracticeDialog());
        });

        viewModel.getTempoLD().observe(getViewLifecycleOwner(), tempo -> {
            binding.textBpm.setText(String.format(Locale.GERMANY, "%3.0f", tempo.getBpm()));
            binding.textTempo.setText(tempo.getLabel());
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

        viewModel.getBeatLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getFlashLD().observe(getViewLifecycleOwner(), i -> {
            if (i != -1) { // not equals no flash
                flash(i);
            }
        });

        return binding.getRoot();
    }

    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressWarnings("ConstantConditions")
    private void flash(int i) {

        t0 = System.currentTimeMillis();
        if (dx > 0.) {
            phi0 = Math.PI * 0.0;
        } else {
            phi0 = 1.0 * Math.PI;
        }

        try {
            int drawableId;
            switch (viewModel.getBeats().get(i).getAccent()) {
                case Beat.ACCENT_HIGH:
                    drawableId = R.drawable.beat_accent_high_flash;
                    break;
                case Beat.ACCENT_MEDIUM:
                    drawableId = R.drawable.beat_accent_medium_flash;
                    break;
                case Beat.ACCENT_LOW:
                    drawableId = R.drawable.beat_accent_low_flash;
                    break;
                default:
                    drawableId = R.drawable.beat_accent_off_flash;
            }
            binding.beatPlaceholder.getChildAt(i).setBackground(
                    ContextCompat.getDrawable(getContext(), drawableId)
            );

            handler.postDelayed(new Runnable() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void run() {
                    int drawableId;
                    switch (viewModel.getBeats().get(i).getAccent()) {
                        case Beat.ACCENT_HIGH:
                            drawableId = R.drawable.beat_accent_high;
                            break;
                        case Beat.ACCENT_MEDIUM:
                            drawableId = R.drawable.beat_accent_medium;
                            break;
                        case Beat.ACCENT_LOW:
                            drawableId = R.drawable.beat_accent_low;
                            break;
                        default:
                            drawableId = R.drawable.beat_accent_off;
                    }
                    binding.beatPlaceholder.getChildAt(i).setBackground(
                            ContextCompat.getDrawable(getContext(), drawableId)
                    );
                }
            }, 100);
        } catch (Exception e) {
            viewModel.setFlashLD(-1);
        }

    }

    public static int getBeatWidth(int n) {
        final int MAX_WIDTH = 156;
        final int MIN_WIDTH = 64;

        return (int) (MIN_WIDTH + (MAX_WIDTH - MIN_WIDTH) * (double) (MAX_BEATS - n) / (MAX_BEATS - 1));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View createBeatView(Beat beat, int nBeats, final int idx) {
        View view = new View(getContext());
        view.setBackground(getResources().getDrawable(beat.getDrawableID(), null));
        int width = getBeatWidth(nBeats);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        view.setLayoutParams(params);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.nextAccent(idx);
            }
        });
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateUI(ArrayList<Beat> beats) {

        int size = beats.size();
        if (size < 1) return;

        ConstraintLayout parent = binding.beatPlaceholder;
        parent.removeAllViews();

        int[] children = new int[size];

        for (Beat b: beats) {

            final int idx = beats.indexOf(b);
            View v = createBeatView(b, size, idx);
            v.setId(View.generateViewId());
            children[idx] = v.getId();
            parent.addView(v);
        }

        // set constraints to the views
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.connect(children[0], ConstraintSet.LEFT, parent.getId(), ConstraintSet.LEFT);

        for (int j=1; j<size; j++) {
           set.connect(children[j-1], ConstraintSet.RIGHT, children[j], ConstraintSet.LEFT);
        }
        set.connect(children[size-1], ConstraintSet.RIGHT, parent.getId(), ConstraintSet.RIGHT);

        // with two or more elements create a chain
        if (size >= 2) {
            set.createHorizontalChain(
                    ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                    children, null, ConstraintSet.CHAIN_SPREAD
            );
        }
        set.applyTo(parent);
    }

    private void startPendulumThread(){

        new Thread(new Runnable() {

            final int REFRESH_RATE = 100; // per second
            final int DELAY = 1000 / 100;

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
}

