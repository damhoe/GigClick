package com.damhoe.gigclick.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.databinding.FragmentSetBinding;
import com.damhoe.gigclick.ui.TrackItemDivider;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SetFragment extends Fragment implements INotifyItemClickListener {

    private FragmentSetBinding binding;
    private LibraryViewModel viewModel;
    private TrackAdapter adapter;

    Transition transition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementEnterTransition(transition);
        postponeEnterTransition();

        Transition returnTransition = TransitionInflater.from(getContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementReturnTransition(returnTransition);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false);

            String transitionIdTitle = SetFragmentArgs.fromBundle(getArguments()).getSetTitle();
            String transitionIdDate = SetFragmentArgs.fromBundle(getArguments()).getSetDate();
            String transitionIdNTracks = SetFragmentArgs.fromBundle(getArguments()).getSetNtracks();
            ViewCompat.setTransitionName(binding.textTitle, transitionIdTitle);
            ViewCompat.setTransitionName(binding.textDate, transitionIdDate);
            ViewCompat.setTransitionName(binding.textNTracks, transitionIdNTracks);
        }

        viewModel.getCurrentSetLD().observe(getViewLifecycleOwner(), this::updateUI);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter == null) {
            adapter = new TrackAdapter(viewModel, this);
            binding.recycler.setAdapter(adapter);
            binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            binding.recycler.addItemDecoration(new TrackItemDivider(getContext(), DividerItemDecoration.VERTICAL));
        }
        adapter.notifyDataSetChanged();
        startPostponedEnterTransition();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    private void updateUI(Set set) {
        // set general info
        binding.textTitle.setText(set.getTitle());
        binding.textNTracks.setText(getString(R.string.n_tracks, set.getNumberOfTracks()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyClick(int position) {
        // ignore.
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void notifyClick(int position, RecyclerView.ViewHolder view) {
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.source_exit_transition));

        TrackAdapter.TrackViewHolder holder = (TrackAdapter.TrackViewHolder) view;

        // go to set fragment
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(holder.title, holder.title.getTransitionName())
                .addSharedElement(holder.bpm, holder.bpm.getTransitionName())
                .build();

        SetFragmentDirections.ActionSetFragmentToTrackFragment action = SetFragmentDirections.actionSetFragmentToTrackFragment(
                holder.title.getTransitionName(), holder.bpm.getTransitionName());

        //((TransitionSet) SetFragment.this.getExitTransition()).excludeTarget((View) holder.itemView, true);

        findNavController().navigate(action, extras);
    }

    @Override
    public void notifyLongClick(int position) {

    }
}