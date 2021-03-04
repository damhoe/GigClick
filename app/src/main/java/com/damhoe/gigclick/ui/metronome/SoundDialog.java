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

import com.damhoe.gigclick.MainActivity;
import com.damhoe.gigclick.R;
import com.damhoe.gigclick.databinding.FragmentDivisionDialogBinding;
import com.damhoe.gigclick.databinding.FragmentSoundDialogBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class SoundDialog extends Fragment {

    FragmentSoundDialogBinding binding;
    MainActivity mainActivity;
    MetronomeViewModel viewModel;

    public SoundDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MetronomeViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sound_dialog, container, false);
        mainActivity = (MainActivity) getActivity();
        //mainActivity.findViewById(R.id.nav_view).setVisibility(View.GONE);

        binding.buttonApply.setOnClickListener(view -> {
            findNavController().navigateUp();
        });

        viewModel.getTempoLD().observe(getViewLifecycleOwner(), tempo -> {
            binding.textBpm .setText(String.format(Locale.GERMANY, "%3.0f", tempo.getBpm()));
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
        //mainActivity.findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
    }
}