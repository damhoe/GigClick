package com.damhoe.gigclick.ui.metronome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.PracticeOptions;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.databinding.FragmentPracticeDialogBinding;
import com.google.android.material.transition.MaterialSharedAxis;

import org.jetbrains.annotations.NotNull;

import static com.damhoe.gigclick.PracticeOptions.getMuteBarsDisplayValues;
import static com.damhoe.gigclick.PracticeOptions.getPlayBarsDisplayValues;
import static com.damhoe.gigclick.PracticeOptions.getSpeedBarsDisplayValues;
import static com.damhoe.gigclick.PracticeOptions.getSpeedDeltaDisplayValues;
import static com.damhoe.gigclick.PracticeOptions.muteBars;
import static com.damhoe.gigclick.PracticeOptions.playBars;
import static com.damhoe.gigclick.PracticeOptions.speedUpBars;
import static com.damhoe.gigclick.PracticeOptions.speedUpDeltas;

public class PracticeDialog extends Fragment {

    private FragmentPracticeDialogBinding binding;
    private PracticeViewModel viewModel;

    private boolean animateMuteContent, animateSpeedContent;

    public PracticeDialog() {
        // Required empty public constructor
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PracticeViewModel.class);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        MaterialSharedAxis tEnter = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        tEnter.setDuration(800);
        setEnterTransition(tEnter);

        MaterialSharedAxis tReturn = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        tReturn.setDuration(800);
        setReturnTransition(tReturn);

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_practice_dialog, container, false);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        initNumberPickers();

        binding.muteSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                show(binding.muteContent);
            } else {
                hide(binding.muteContent);
            }
        });

        binding.speedUpSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                show(binding.speedUpContent);
            } else {
                hide(binding.speedUpContent);
            }
        });

        binding.buttonApply.setOnClickListener(view -> {
            PracticeOptions mOptions = viewModel.getOptionsLD().getValue();
            int n = mOptions.getnBars();
            int su, ds, ns, nm;

            int pSU = binding.speedUpBarPicker.getValue();
            int pDS = binding.speedUpDeltaPicker.getValue();
            int pNS = binding.playBarsPicker.getValue();
            int pNM = binding.muteBarsPicker.getValue();
            su = speedUpBars[pSU];
            ds = speedUpDeltas[pDS];
            ns = playBars[pNS];
            nm = muteBars[pNM];

            mOptions.update(n, su, ds, ns, nm);
            mOptions.setMuted(binding.muteSwitch.isChecked());
            mOptions.setSpeed(binding.speedUpSwitch.isChecked());

            viewModel.applyOptions(mOptions);
            findNavController().navigateUp();
        });
        binding.buttonCancel.setOnClickListener(view -> {findNavController().navigateUp();});
        binding.iconClose.setOnClickListener(view -> {findNavController().navigateUp();});

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.getOptionsLD().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.isMuteLD().observe(getViewLifecycleOwner(), this::updateMute);
        viewModel.isSpeedUpLD().observe(getViewLifecycleOwner(), this::updateSpeedUp);
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateMute(boolean state) {
        binding.muteSwitch.setChecked(state);
    }

    private void updateSpeedUp(boolean state) {
        binding.speedUpSwitch.setChecked(state);
    }

    private void initNumberPickers() {
        // init number pickers
        binding.playBarsPicker.setMinValue(0);
        binding.playBarsPicker.setMaxValue(playBars.length - 1);
        binding.playBarsPicker.setDisplayedValues(getPlayBarsDisplayValues());

        binding.muteBarsPicker.setMinValue(0);
        binding.muteBarsPicker.setMaxValue(muteBars.length - 1);
        binding.muteBarsPicker.setDisplayedValues(getMuteBarsDisplayValues());

        binding.speedUpBarPicker.setMinValue(0);
        binding.speedUpBarPicker.setMaxValue(speedUpBars.length - 1);
        binding.speedUpBarPicker.setDisplayedValues(getSpeedBarsDisplayValues());

        binding.speedUpDeltaPicker.setMinValue(0);
        binding.speedUpDeltaPicker.setMaxValue(speedUpDeltas.length - 1);
        binding.speedUpDeltaPicker.setDisplayedValues(getSpeedDeltaDisplayValues());
    }

    private void hide(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.content_hide_vertical);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //view.setTranslationZ(4);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //view.setTranslationZ(0);
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void show(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.content_show_vertical);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                //view.setTranslationZ(4);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //view.setTranslationZ(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void updateUI(PracticeOptions pOptions) {
        binding.muteContent.setVisibility(pOptions.isMuted()? View.VISIBLE: View.INVISIBLE);
        binding.speedUpContent.setVisibility(pOptions.isSpeed()? View.VISIBLE: View.INVISIBLE);

        binding.playBarsPicker.setValue(findIndex(playBars, pOptions.getnBarsSound()));
        binding.muteBarsPicker.setValue(findIndex(muteBars, pOptions.getnBarsMuted()));
        binding.speedUpBarPicker.setValue(findIndex(speedUpBars, pOptions.getSpeedUpEach()));
        binding.speedUpDeltaPicker.setValue(findIndex(speedUpDeltas, pOptions.getDeltaSpeed()));
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onDestroy();
    }

    private static int findIndex(int[] arr, int t) {
        // if array is Null
        if (arr == null) {
            return 0;
        }

        // find length of array
        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t
            // then return the index
            if (arr[i] == t) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return 0;
    }
}