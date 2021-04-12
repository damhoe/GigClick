package com.damhoe.gigclick.ui.library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.Library;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.OnStartDragListener;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.databinding.FragmentLibraryBinding;
import com.damhoe.gigclick.ui.GigClickTouchCallback;
import com.damhoe.gigclick.ui.ILibraryChangeListener;
import com.damhoe.gigclick.ui.library.LibraryFragmentDirections.DetailsSetAction;
import com.damhoe.gigclick.ui.track.SetItemDivider;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.material.transition.MaterialFadeThrough;

public class LibraryFragment extends Fragment implements
        INotifyItemClickListener, OnStartDragListener, ILibraryChangeListener {

    private FragmentLibraryBinding binding;
    private LibraryViewModel viewModel;
    private MainActivity mainActivity;
    private SetAdapter adapter;
    private ActionMode mode;
    private ItemTouchHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        postponeEnterTransition();

        MaterialFadeThrough tEnter = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setEnterTransition(tEnter);

        MaterialFadeThrough tExit = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setExitTransition(tExit);
    }

    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false);

        // enable title
        mainActivity = (MainActivity) requireActivity();
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        adapter = new SetAdapter(this, this, this);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recycler.addItemDecoration(new SetItemDivider(getContext(), DividerItemDecoration.VERTICAL));
        GigClickTouchCallback callback = new GigClickTouchCallback(adapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(binding.recycler);

        binding.fabMore.setOnClickListener(button -> {
            viewModel.saveSet(Set.getExampleSets().get(0));
        });

        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollY = 0;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        binding.fabMore.show();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        binding.fabMore.hide();
                        break;

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.updateScrollYLib(dy);
            }
        });


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
        binding.recycler.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                startPostponedEnterTransition();
                binding.recycler.getChildAt(0);
                return true;
            }
        });
        viewModel.getSetsLD().observe(getViewLifecycleOwner(), sets -> {
            adapter.setSets(sets);
        });

        viewModel.getScrollYLib().observe(getViewLifecycleOwner(), scrollY -> {
            binding.header.setVisibility(scrollY > 0 ? View.VISIBLE: View.INVISIBLE);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.library_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_by_popularity:
                viewModel.sortLibBy(Library.POPULARITY);
                break;
            case R.id.menu_search:
                Toast.makeText(getContext(), "Search function is still to be implemented.", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void notifyClick(int position) {

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void notifyClick(int position, RecyclerView.ViewHolder view) {
        viewModel.selectSet(position);
        long setId = adapter.getItemId(position);

        SetAdapter.SetViewHolder holder = (SetAdapter.SetViewHolder) view;

        Transition exitTransition = new MaterialElevationScale(false);
        Transition reenterTransition = new MaterialElevationScale(true);
        exitTransition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));
        reenterTransition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));

        setExitTransition(exitTransition);
        setReenterTransition(reenterTransition);

        String transitionName = getString(R.string.set_detail_transition_name);

        // go to set fragment
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(view.itemView, transitionName)
                .build();

        DetailsSetAction action = LibraryFragmentDirections.detailsSetAction(setId);

        //((TransitionSet) LibraryFragment.this.getExitTransition()).excludeTarget((View) holder.itemView, true);

        findNavController().navigate(action, extras);
    }

    @Override
    public void notifyLongClick(int position) {
        //enableActionMode();
        //adapter.toggleSelection(position);
        //startActionDialog(position); // Bottom sheet dialog
    }

    private void startActionDialog(int position) {
        Set set = adapter.getSetAt(position);

        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.GigClickTheme_BottomSheet);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.fragment_library_bottom_dialog);

        LinearLayout del = dialog.findViewById(R.id.item_delete);
        LinearLayout fave = dialog.findViewById(R.id.item_fave);
        LinearLayout faveUndo = dialog.findViewById(R.id.item_fave_undo);
        LinearLayout edit = dialog.findViewById(R.id.item_edit);

        faveUndo.setVisibility(set.isFave()? View.VISIBLE: View.INVISIBLE);
        fave.setVisibility(set.isFave()? View.INVISIBLE: View.VISIBLE);

        del.setOnClickListener(view -> {
            viewModel.deleteSet(set.getId());
            dialog.dismiss();
        });

        fave.setOnClickListener(view -> {
            set.setFave(true);
            viewModel.updateSet(set);
            dialog.dismiss();
        });

        faveUndo.setOnClickListener(view -> {
            set.setFave(false);
            viewModel.updateSet(set);
            dialog.dismiss();
        });

        edit.setOnClickListener(view -> {
            showEditSetTitleDialog(position);
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showEditSetTitleDialog(int position) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_text_field_dialog, null, false);
        EditText text = view.findViewById(R.id.edit);
        Set set = adapter.getSetAt(position);
        text.setText(set.getTitle());
        text.setSelection(text.length());

        new MaterialAlertDialogBuilder(getContext(), R.style.GigClickTheme_AlertDialog)
                .setView(view)
                .setTitle(R.string.title_edit_set_title_dialog)
                .setMessage(R.string.msg_edit_set_title_dialog)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Apply", ((dialogInterface, i) -> {
                    set.setTitle(text.getText().toString());
                    viewModel.updateSetMeta(set);
                    dialogInterface.cancel();
                }))
                .create()
                .show();

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        helper.startDrag(holder);
    }

    @Override
    public void notifySetDeleted(int position, Set set) {
        Snackbar.make(getView(), set.getTitle() + "was deleted.", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    adapter.insertItem(position, set);
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (!(event == Snackbar.Callback.DISMISS_EVENT_ACTION)) {
                            viewModel.deleteSet(set.getId());
                        }
                    }
                })
                .show();
    }
}

