package com.damhoe.gigclick.ui.track;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.INotifyListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentEditTrackBinding;
import com.damhoe.gigclick.ui.metronome.RotaryView;
import com.damhoe.gigclick.ui.track.EditTrackViewModel;

import org.jetbrains.annotations.NotNull;

import static com.damhoe.gigclick.ui.metronome.MetronomeFragment.FACTOR_ANGLE_TO_BEATS;


public class EditTrackFragment extends Fragment {

    public static final int TRACK_ID_KEY = 0;
    public static final int SET_ID_KEY = 1;

    private FragmentEditTrackBinding binding;
    private EditTrackViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = EditTrackVMFactory.createEditTrackVM(getActivity().getApplication(),
                this, getArguments());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_track, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.rotaryForeground.setOnTouchListener((view, motionEvent) -> {
            binding.scrollview.requestDisallowInterceptTouchEvent(true);
            binding.rotaryForeground.onTouchEvent(motionEvent);
            return false;
        });

        ((RotaryView) binding.rotaryForeground).setNotifyListener(new INotifyListener() {
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

            @Override
            public void onNotifyTouch(int action) {

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

        binding.buttonCancel.setOnClickListener(view -> {
            findNavController().navigateUp();
        });

        binding.buttonApply.setOnClickListener(view -> {
            viewModel.saveTrack();
            findNavController().navigateUp();
        });

        viewModel.getTitleLD().observe(getViewLifecycleOwner(), s -> {
            binding.buttonApply.setEnabled(viewModel.isValidTitle(s));
        });

        return binding.getRoot();
    }

    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }
}