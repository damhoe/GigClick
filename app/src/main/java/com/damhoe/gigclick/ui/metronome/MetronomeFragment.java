package com.damhoe.gigclick.ui.metronome;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.damhoe.gigclick.Beat;
import com.damhoe.gigclick.INotifyListener;
import com.damhoe.gigclick.PracticeOptions;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.databinding.FragmentMetronomeBinding;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.damhoe.gigclick.Track.MAX_BEATS;
import static com.damhoe.gigclick.ui.metronome.MetronomeFragmentDirections.actionNavigationMetronomeToDivisionDialog;
import static com.damhoe.gigclick.ui.metronome.MetronomeFragmentDirections.actionNavigationMetronomeToPracticeDialog;
import static com.damhoe.gigclick.ui.metronome.MetronomeFragmentDirections.actionNavigationMetronomeToSoundDialog;

public class MetronomeFragment extends Fragment {

    public static final double FACTOR_ANGLE_TO_BEATS = 0.2;

    private MetronomeViewModel viewModel;
    private FragmentMetronomeBinding binding;

    private double x, dx, phi0, bpm;
    private long t0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MetronomeViewModel.class);

        MaterialFadeThrough tEnter = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setEnterTransition(tEnter);

        MaterialFadeThrough tExit = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setExitTransition(tExit);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_metronome, container,false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        binding.rotaryForeground.setNotifyListener(new INotifyListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onNotifyAngle(double angle) {
                viewModel.setBPM(bpm + angle * FACTOR_ANGLE_TO_BEATS);
            }

            @Override
            public void onNotifyMillis(long millis) {
                double bpm = 60000. / millis;
                viewModel.setBPM(bpm);
            }

            @Override
            public void onNotifyTouch(int action) {
                int cPressed = ContextCompat.getColor(getContext(), R.color.colorAccentTransparent);
                int cUnpressed = ContextCompat.getColor(getContext(), R.color.colorAccent);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        binding.buttonTap.setTextColor(cPressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        binding.buttonTap.setTextColor(cUnpressed);
                        break;
                }
            }
        });
        binding.buttonPlay.setOnClickListener(v -> {viewModel.switchRunState();});
        binding.buttonIncrease.setOnClickListener(view -> {viewModel.setBPM(++bpm);});
        binding.buttonDecrease.setOnClickListener(v -> {viewModel.setBPM(--bpm);});
        binding.buttonSounds.setOnClickListener(view -> {navToSoundDialog();});
        binding.buttonPractice.setOnClickListener(view -> {navToPracticeDialog();});
        binding.buttonEditBeats.setOnClickListener(view -> {navToDivisionDialog();});

        viewModel.getFlashLD().observe(getViewLifecycleOwner(), i -> {
            if (i != -1) { // not equals no flash
                flash(i);
            }
        });

        viewModel.getBPB_LD().observe(getViewLifecycleOwner(), i -> {
            binding.timeSignatureView.setText(getString(R.string.time_signature, i,
                    Integer.parseInt(getString(R.string.default_metric))));
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel.getTempoLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getBeatLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getPOptionsLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getRunStateLD().observe(getViewLifecycleOwner(), this::updateUI);

        super.onViewCreated(view, savedInstanceState);
    }

    private void updateUI(Boolean isPlaying) {
        Drawable drawable;
        if (isPlaying) {
            startPendulumThread();
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_black_24dp, null);
        } else {
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_black_24dp, null);
            resetBeats();
        }
        binding.buttonPlay.setImageDrawable(drawable);
    }

    private void updateUI(Tempo tempo) {
        bpm = tempo.getBpm();
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

    private void updateUI(PracticeOptions pOptions) {
        if (pOptions.isMuted()) {
            binding.icMute.setVisibility(View.VISIBLE);
        } else {
            binding.icMute.setVisibility(View.GONE);
        }

        if (pOptions.isSpeed()) {
            binding.icSpeed.setVisibility(View.VISIBLE);
        } else {
            binding.icSpeed.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressWarnings("ConstantConditions")
    private void resetBeats() {
        if (viewModel.getTrack() != null) {
            for (int i=0; i<viewModel.getBeats().size(); i++) {
                binding.beatPlaceholder.getChildAt(i).setBackground(
                        ContextCompat.getDrawable(getContext(), viewModel.getBeats().get(i).getDrawableID())
                );
            }
        }
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
                    drawableId = R.drawable.beat_accent_flash_view;
                    break;
                case Beat.ACCENT_MEDIUM:
                    drawableId = R.drawable.beat_flash_view;
                    break;
                case Beat.ACCENT_LOW:
                    drawableId = R.drawable.beat_accent_low_flash;
                    break;
                default:
                    drawableId = R.drawable.beat_off_flash_view;
            }

            int old = (i + viewModel.getBeats().size() - 1) % viewModel.getBeats().size();
            binding.beatPlaceholder.getChildAt(old).setBackground(
                    ContextCompat.getDrawable(getContext(), viewModel.getBeats().get(old).getDrawableID())
            );

            binding.beatPlaceholder.getChildAt(i).setBackground(
                    ContextCompat.getDrawable(getContext(), drawableId)
            );

        } catch (Exception e) {
            viewModel.setFlashLD(-1);
        }

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View createBeatView(Beat beat, int nBeats, final int idx) {
        View view = new View(getContext());
        view.setBackground(getResources().getDrawable(beat.getDrawableID(), null));
        int width = dpToPx(22);//getBeatWidth(nBeats);
        int height = dpToPx(22); //ViewGroup.LayoutParams.MATCH_PARENT;
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
                    //binding.pendulumForeground.setPositionX(0.);
                }
            }

            @SuppressWarnings("ConstantConditions")
            private void updatePendulum() throws InterruptedException {
                f = viewModel.getBpm() / 60000.; // beats per ms
                t = System.currentTimeMillis();
                x = Math.sin(Math.PI * f * (t - t0) + phi0);
                dx = Math.cos(Math.PI * f * (t - t0) + phi0);
                // binding.pendulumForeground.setPositionX(x);
                Thread.sleep(DELAY);
            }
        }).start();
    }

    private void navToPracticeDialog() {
        MaterialSharedAxis tReenter = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        tReenter.setDuration(800);
        setReenterTransition(tReenter);
        MaterialSharedAxis tExit = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        tExit.setDuration(800);
        setExitTransition(tExit);
        findNavController().navigate(actionNavigationMetronomeToPracticeDialog());
    }

    private void navToDivisionDialog() {
        MaterialSharedAxis tReenter = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        tReenter.setDuration(800);
        setReenterTransition(tReenter);
        MaterialSharedAxis tExit = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        tExit.setDuration(800);
        setExitTransition(tExit);
        findNavController().navigate(actionNavigationMetronomeToDivisionDialog());
    }

    private void navToSoundDialog() {
        MaterialSharedAxis tReenter = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        tReenter.setDuration(800);
        setReenterTransition(tReenter);
        MaterialSharedAxis tExit = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        tExit.setDuration(800);
        setExitTransition(tExit);
        findNavController().navigate(actionNavigationMetronomeToSoundDialog());
    }
}

