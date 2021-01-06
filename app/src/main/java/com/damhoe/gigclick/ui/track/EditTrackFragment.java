package com.damhoe.gigclick.ui.track;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.Beat;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentEditTrackBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.damhoe.gigclick.ui.metronome.MetronomeFragment.getBeatWidth;


public class EditTrackFragment extends Fragment {

    private FragmentEditTrackBinding binding;
    private TrackViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TrackViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_track, container, false);

        viewModel.getTrackLD().observe(getViewLifecycleOwner(), this::updateUI);

        return binding.getRoot();
    }

    private void updateUI(Track track) {
        binding.editTitle.setText(track.getTitle());
        binding.editComment.setText(track.getComment());
        binding.textBpm.setText(track.getTempo().getBpmText());
        binding.textTempo.setText(track.getTempo().getLabel());

        updateBeats(track.getBeats());
        // TODO update subdivisions
    }

    private void updateBeats(ArrayList<Beat> beats) {

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
}