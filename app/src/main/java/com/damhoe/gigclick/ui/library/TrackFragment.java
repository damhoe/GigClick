package com.damhoe.gigclick.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentTrackBinding;
import com.google.android.material.transition.MaterialContainerTransform;

import org.jetbrains.annotations.NotNull;


public class TrackFragment extends Fragment {

    private FragmentTrackBinding binding;
    private LibraryViewModel viewModel;

    MaterialContainerTransform transition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        postponeEnterTransition();

        transition = new MaterialContainerTransform();
        transition.setScrimColor(getResources().getColor(R.color.colorBackgroundDark, null));
        transition.setAllContainerColors(getResources().getColor(R.color.colorBackground));
        transition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));
        setSharedElementEnterTransition(transition);

        setReturnTransition(transition);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());


        binding.buttonEdit.setOnClickListener(view -> {
            Track track = viewModel.getTrackLD().getValue();
            findNavController().navigate(TrackFragmentDirections.actionTrackFragmentToEditTrackFragment(
                    track.getId(), track.getSetId(), false
            ));
        });

        viewModel.getTrackLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getLibraryLD().observe(getViewLifecycleOwner(), library -> {
            long id = viewModel.getTrackLD().getValue().getId();
            viewModel.setTrack(library.getTrackById(id));
        });

        binding.iconClose.setOnClickListener(view -> {
            findNavController().navigateUp();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.initTrack();
        startPostponedEnterTransition();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    private void updateUI(Track track) {
        //binding.textTitle.setText(track.getTitle());
        //binding.textBpm.setText(track.getTempo().getBpmText());
        //binding.textTempo.setText(track.getTempo().getLabel());
        //binding.textComment.setText(track.getComment());
    }

}