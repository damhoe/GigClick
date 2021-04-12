package com.damhoe.gigclick.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.gigclick.R;
import com.google.android.material.transition.MaterialFadeThrough;


public class AboutFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialFadeThrough tEnter = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setEnterTransition(tEnter);

        MaterialFadeThrough tExit = new MaterialFadeThrough();
        tEnter.setDuration((long) getResources().getInteger(R.integer.material_motion_duration_long_1));
        setExitTransition(tExit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}