package com.damhoe.gigclick.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
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

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.databinding.FragmentLibraryBinding;
import com.damhoe.gigclick.ui.TrackItemDivider;

import java.util.List;
import java.util.Map;

public class LibraryFragment extends Fragment implements INotifyItemClickListener {

    private FragmentLibraryBinding binding;
    private LibraryViewModel viewModel;
    private MainActivity mainActivity;
    private SetAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.source_exit_transition);
        setExitTransition(transition);
        postponeEnterTransition();
        Transition returnTransition = TransitionInflater.from(getContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementReturnTransition(returnTransition);
    }

    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false);
        }
        // enable title
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        // postponeEnterTransition();

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter == null) {
            adapter = new SetAdapter(viewModel, this);
            binding.recycler.setAdapter(adapter);
            binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            binding.recycler.addItemDecoration(new TrackItemDivider(getContext(), DividerItemDecoration.VERTICAL));
        }
        startPostponedEnterTransition();
//        adapter = new LibraryPagerAdapter(this, this);
//        binding.pager.setAdapter(adapter);
//        new TabLayoutMediator(binding.tabLayout, binding.pager, ((tab, position) -> {
//            tab.setText(LibraryPagerAdapter.titles[position]);
//        })).attach();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.library_menu, menu);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void notifyClick(int position) {
        // ignore.
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void notifyClick(int position, RecyclerView.ViewHolder view) {
        SetAdapter.SetViewHolder holder = (SetAdapter.SetViewHolder) view;

        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.source_exit_transition));

        // go to set fragment
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(holder.title, holder.title.getTransitionName())
                .addSharedElement(holder.date, holder.date.getTransitionName())
                .addSharedElement(holder.nTracks, holder.nTracks.getTransitionName())
                .build();

        LibraryFragmentDirections.DetailsSetAction action = LibraryFragmentDirections.detailsSetAction(
                        holder.title.getTransitionName(), holder.date.getTransitionName(), holder.nTracks.getTransitionName());

        ((TransitionSet) LibraryFragment.this.getExitTransition()).excludeTarget((View) holder.itemView, true);

        findNavController().navigate(action, extras);
    }

    @Override
    public void notifyLongClick(int position) {
        // ignore
    }
}

//class LibraryPagerAdapter extends FragmentStateAdapter {
//
//    static final String[] titles = new String[]{"Sets", "All Tracks"};
//    private INotifyItemClickListener listener;
//
//    public LibraryPagerAdapter(@NonNull Fragment fragment, INotifyItemClickListener listener) {
//        super(fragment);
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        switch (position) {
//            case 0:
//                return new PagerSetFragment(listener);
//            case 1:
//                return new PagerTrackFragment();
//        }
//        return null;
//    }
//
//    @Override
//    public int getItemCount() {
//        return 2;
//    }
//}


