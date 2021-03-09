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

import org.jetbrains.annotations.NotNull;


public class TrackFragment extends Fragment {

    private FragmentTrackBinding binding;
    private LibraryViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        postponeEnterTransition();

        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementEnterTransition(transition);

        Transition transition1 = TransitionInflater.from(getContext()).inflateTransition(R.transition.source_exit_transition);
        setExitTransition(transition1);

        Transition returnTransition = TransitionInflater.from(getContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementReturnTransition(returnTransition);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        String titleId = TrackFragmentArgs.fromBundle(getArguments()).getTitleId();
        String bpmId = TrackFragmentArgs.fromBundle(getArguments()).getBpmId();

        // set transition names
        ViewCompat.setTransitionName(binding.titleTrack, titleId);
        ViewCompat.setTransitionName(binding.textBpm, bpmId);


        binding.buttonEdit.setOnClickListener(view -> {
            Track track = viewModel.getTrackLD().getValue();
            findNavController().navigate(TrackFragmentDirections.actionTrackFragmentToEditTrackFragment(
                    track.getId(), track.getTitle(), track.getComment(), (long) track.getTempo().getBpm()
            ));
        });

        viewModel.getTrackLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getLibraryLD().observe(getViewLifecycleOwner(), library -> {
            long id = viewModel.getTrackLD().getValue().getId();
            viewModel.setTrack(library.getTrackById(id));
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