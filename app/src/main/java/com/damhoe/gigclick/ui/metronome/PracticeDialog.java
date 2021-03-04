package com.damhoe.gigclick.ui.metronome;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.Division;
import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.PracticeOptions;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.databinding.FragmentPracticeDialogBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PracticeDialog extends Fragment {

    FragmentPracticeDialogBinding binding;
    MainActivity mainActivity;
    MetronomeViewModel viewModel;

    public PracticeDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MetronomeViewModel.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_practice_dialog, container, false);
        mainActivity = (MainActivity) getActivity();

        // init number pickers
        PracticeOptions options = viewModel.getTrack().getpOptions();
        int pBars = options.getnBarsSound();
        int idxPBars = Math.max(0, findIndex(PracticeOptions.playBars, pBars));
        binding.playBarsPicker.setMinValue(0);
        binding.playBarsPicker.setMaxValue(PracticeOptions.playBars.length - 1);
        binding.playBarsPicker.setDisplayedValues(PracticeOptions.getPlayBarsDisplayValues());
        binding.playBarsPicker.setValue(idxPBars);

        int mBars = options.getnBarsMuted();
        int idxMBars = Math.max(0, findIndex(PracticeOptions.muteBars, mBars));
        binding.muteBarsPicker.setMinValue(0);
        binding.muteBarsPicker.setMaxValue(PracticeOptions.muteBars.length - 1);
        binding.muteBarsPicker.setDisplayedValues(PracticeOptions.getMuteBarsDisplayValues());
        binding.muteBarsPicker.setValue(idxMBars);

        int sU = options.getSpeedUpEach();
        int idxSU = Math.max(0, findIndex(PracticeOptions.speedUpBars, sU));
        binding.speedUpBarPicker.setMinValue(0);
        binding.speedUpBarPicker.setMaxValue(PracticeOptions.speedUpBars.length - 1);
        binding.speedUpBarPicker.setDisplayedValues(PracticeOptions.getSpeedBarsDisplayValues());
        binding.speedUpBarPicker.setValue(idxSU);

        int suDelta = options.getDeltaSpeed();
        int idxSUDelta = Math.max(0, findIndex(PracticeOptions.speedUpDeltas, suDelta));
        binding.speedUpDeltaPicker.setMinValue(0);
        binding.speedUpDeltaPicker.setMaxValue(PracticeOptions.speedUpDeltas.length - 1);
        binding.speedUpDeltaPicker.setDisplayedValues(PracticeOptions.getSpeedDeltaDisplayValues());
        binding.speedUpDeltaPicker.setValue(idxSUDelta);

        binding.speedUpSwitch.setChecked(options.isSpeed());
        binding.muteSwitch.setChecked(options.isMuted());

        binding.buttonApply.setOnClickListener(view -> {
            PracticeOptions po = viewModel.getTrack().getpOptions();
            int n = po.getnBars();
            int su = po.getSpeedUpEach();
            int ds = po.getDeltaSpeed();
            int ns = po.getnBarsSound();
            int nm = po.getnBarsMuted();

            boolean bs = binding.speedUpSwitch.isChecked();
            boolean bm = binding.muteSwitch.isChecked();

            int pSU = binding.speedUpBarPicker.getValue();
            int pDS = binding.speedUpDeltaPicker.getValue();
            int pNS = binding.playBarsPicker.getValue();
            int pNM = binding.muteBarsPicker.getValue();
            su = PracticeOptions.speedUpBars[pSU];
            ds = PracticeOptions.speedUpDeltas[pDS];
            ns = PracticeOptions.playBars[pNS];
            nm = PracticeOptions.muteBars[pNM];


            viewModel.setPracticeOptions(bs, bm, n, su, ds, ns, nm);
            findNavController().navigateUp();
        });

        binding.buttonCancel.setOnClickListener(view -> {
            findNavController().navigateUp();
        });

        viewModel.getTempoLD().observe(getViewLifecycleOwner(), tempo -> {
            //binding.textBpm .setText(String.format(Locale.GERMANY, "%3.0f", tempo.getBpm()));
        });

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static int findIndex(int[] arr, int t) {
        // if array is Null
        if (arr == null) {
            return -1;
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
        return -1;
    }
}