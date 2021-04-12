package com.damhoe.gigclick.ui.library;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.damhoe.gigclick.INotifyItemClickListener;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Track;
import com.damhoe.gigclick.databinding.FragmentSetBinding;
import com.damhoe.gigclick.ui.GigClickTouchCallback;
import com.damhoe.gigclick.ui.TrackItemDivider;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialElevationScale;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class SetFragment extends Fragment implements INotifyItemClickListener {

    private FragmentSetBinding binding;
    private LibraryViewModel viewModel;
    private TrackAdapter adapter;
    private ItemTouchHelper helper;

    private boolean fabIsExpanded = false   ;

    MaterialContainerTransform transition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        //
        postponeEnterTransition();

        transition = new MaterialContainerTransform();
        transition.setScrimColor(getResources().getColor(R.color.colorBackgroundDark, null));
        transition.setAllContainerColors(getResources().getColor(R.color.colorBackground));
        transition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));
        setSharedElementEnterTransition(transition);

        setReturnTransition(transition);
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_set, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_24dp, null));

        adapter = new TrackAdapter(this);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //binding.recycler.addItemDecoration(new TrackItemDivider(getContext(), DividerItemDecoration.VERTICAL));
        GigClickTouchCallback callback = new GigClickTouchCallback(adapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(binding.recycler);

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
                viewModel.updateScrollYSet(dy);
            }
        });

        Animation maskEnterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation maskExitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation rotateClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        Animation rotateAntiClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);

        binding.fabMore.setOnClickListener(view -> {
            Set set = viewModel.getSetLD().getValue();

            if (fabIsExpanded) {
                binding.mask.startAnimation(maskExitAnim);
                binding.mask.setVisibility(View.INVISIBLE);
                if (set.isFave()) {
                    binding.fabFaveUndo.hide();
                } else {
                    binding.fabFave.hide();
                }
                binding.fabCalendar.hide();
                binding.fabEdit.hide();
                binding.fabReorder.hide();
                binding.fabAdd.hide();
                binding.fabMore.startAnimation(rotateAntiClock);
            } else {
                binding.mask.startAnimation(maskEnterAnim);
                binding.mask.setVisibility(View.VISIBLE);
                binding.fabMore.startAnimation(rotateClock);
                binding.fabAdd.show();
                binding.fabReorder.show();
                binding.fabEdit.show();
                binding.fabCalendar.show();
                if (set.isFave()) {
                    binding.fabFaveUndo.show();
                } else {
                    binding.fabFave.show();
                }
            }
            fabIsExpanded = !fabIsExpanded;
        });

        binding.mask.setOnClickListener(view -> {
            if (fabIsExpanded) {
                //binding.fabMore.callOnClick();
            }
        });

        binding.mask.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                binding.fabMore.callOnClick();
            }
            return true;
        });

        binding.fabEdit.setOnClickListener(view -> {
            showEditSetTitleDialog();
            binding.fabMore.callOnClick();
        });

        binding.fabFave.setOnClickListener(view -> {
            binding.fabMore.callOnClick();
            Set set = viewModel.getSetLD().getValue();
            set.setFave(true);
            viewModel.updateSet(set);
        });

        binding.fabFaveUndo.setOnClickListener(view -> {
            binding.fabMore.callOnClick();
            Set set = viewModel.getSetLD().getValue();
            set.setFave(false);
            viewModel.updateSet(set);
        });


        binding.fabCalendar.setOnClickListener(view -> {
            binding.fabMore.callOnClick();
        });

        binding.fabAdd.setOnClickListener(view -> {
            addTrack();
            binding.fabMore.callOnClick();
        });

        binding.fabReorder.setOnClickListener(view -> {
            binding.fabMore.callOnClick();
        });

        binding.iconClose.setOnClickListener(view -> {
            findNavController().navigateUp();
        });

        return binding.getRoot();
    }

    @SuppressLint("InflateParams")
    private void showEditSetTitleDialog() {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_text_field_dialog, null, false);
        EditText text = view.findViewById(R.id.edit);
        Set set = viewModel.getSetLD().getValue();
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

    private void addTrack() {
        Set set = viewModel.getSetLD().getValue();

        findNavController().navigate(
                SetFragmentDirections.actionSetFragmentToEditTrackFragment(
                       -1, set.getId(), true
                )
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //adapter.notifyDataSetChanged();
        startPostponedEnterTransition();

        viewModel.getSetLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getScrollYSet().observe(getViewLifecycleOwner(), scrollY -> {
            binding.titlePlaceholder.setVisibility(scrollY > 0 ? View.VISIBLE: View.INVISIBLE);
            binding.notScroll.setElevation(scrollY > 0 ? 10f: 0f);
        });
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void updateUI(Set set) {
        // set general info
        adapter.setTracks(set.getTracks());

        if (!fabIsExpanded) {
            binding.mask.setVisibility(View.INVISIBLE);
            binding.fabCalendar.setVisibility(View.INVISIBLE);
            binding.fabEdit.setVisibility(View.INVISIBLE);
            binding.fabFave.setVisibility(View.INVISIBLE);
            binding.fabFaveUndo.setVisibility(View.INVISIBLE);
            binding.fabAdd.setVisibility(View.INVISIBLE);
            binding.fabReorder.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void notifyClick(int position) {
        // ignore.
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void notifyClick(int position, RecyclerView.ViewHolder view) {
        viewModel.selectTrack(position);

        Transition exitTransition = new MaterialElevationScale(false);
        Transition reenterTransition = new MaterialElevationScale(true);
        exitTransition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));
        reenterTransition.setDuration(getResources().getInteger(R.integer.material_motion_duration_long_1));

        setExitTransition(exitTransition);
        setReenterTransition(reenterTransition);

        String transitionName = getString(R.string.track_detail_transition_name);

        // go to set fragment
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(view.itemView, transitionName)
                .build();

        SetFragmentDirections.DetailsTrackAction action = SetFragmentDirections.detailsTrackAction(adapter.getItemId(position));

        findNavController().navigate(action, extras);
    }

    @Override
    public void notifyLongClick(int position) {
        startActionDialog(position);
    }

    private void startActionDialog(int position) {
        Set set = viewModel.getSetLD().getValue();
        Track track = adapter.getTrackAt(position);

        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.GigClickTheme_BottomSheet);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.fragment_set_bottom_dialog);

        LinearLayout del = dialog.findViewById(R.id.item_delete);
        LinearLayout title = dialog.findViewById(R.id.item_edit);
        LinearLayout comment = dialog.findViewById(R.id.item_comment);
        LinearLayout reorder = dialog.findViewById(R.id.item_reorder);

        del.setOnClickListener(view -> {
            set.deleteTrack(track.getId());
            viewModel.updateSet(set);
            dialog.dismiss();
        });

        title.setOnClickListener(view -> {
            showEditTrackTitleDialog(position);
            dialog.dismiss();
        });

        comment.setOnClickListener(view -> {
            showEditTrackCommentDialog(position);
            dialog.dismiss();
        });

        reorder.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Edit Set", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showEditTrackTitleDialog(int position) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_text_field_dialog, null, false);
        EditText text = view.findViewById(R.id.edit);
        Track track = adapter.getTrackAt(position);
        text.setText(track.getTitle());
        text.setSelection(text.length());

        new MaterialAlertDialogBuilder(getContext(), R.style.GigClickTheme_AlertDialog)
                .setView(view)
                .setTitle(R.string.title_edit_track_title_dialog)
                .setMessage(R.string.msg_edit_track_title_dialog)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Apply", ((dialogInterface, i) -> {
                    track.setTitle(text.getText().toString());
                    viewModel.updateTrack(track);
                    dialogInterface.cancel();
                }))
                .create()
                .show();

    }

    @SuppressLint("InflateParams")
    private void showEditTrackCommentDialog(int position) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_text_field_dialog, null, false);
        EditText text = view.findViewById(R.id.edit);
        Track track = adapter.getTrackAt(position);
        text.setText(track.getComment());
        text.setSelection(text.length());

        new MaterialAlertDialogBuilder(getContext(), R.style.GigClickTheme_AlertDialog)
                .setView(view)
                .setTitle(R.string.title_edit_track_comment_dialog)
                .setMessage(R.string.msg_edit_track_comment_dialog)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Apply", ((dialogInterface, i) -> {
                    track.setComment(text.getText().toString());
                    viewModel.updateTrack(track);
                    dialogInterface.cancel();
                }))
                .create()
                .show();

    }

}