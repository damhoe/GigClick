package com.damhoe.gigclick.ui.track;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.damhoe.gigclick.Track;

import java.lang.reflect.InvocationTargetException;

public class EditTrackVMFactory extends ViewModelProvider.AndroidViewModelFactory {

    Application application;
    Track track;

    public EditTrackVMFactory(@NonNull Application application, Track track) {
        super(application);
        this.application = application;
        this.track = track;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            AndroidViewModel viewModel = null;
            if (modelClass.isAssignableFrom(EditTrackViewModel.class)) {
                viewModel = new EditTrackViewModel(application, track);
            } else {
                super.create(modelClass);
            }
            return (T) viewModel;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static EditTrackViewModel createEditTrackVM(@NonNull Application application,
                                @NonNull ViewModelStoreOwner context, Bundle bundle) {

        EditTrackFragmentArgs args = EditTrackFragmentArgs.fromBundle(bundle);

        Track track = new Track();
        track.setId(args.getId());
        track.setTitle(args.getTitle());
        track.setComment(args.getComment());
        track.setBPM(args.getBpm());

        EditTrackVMFactory factory = new EditTrackVMFactory(application, track);
        return new ViewModelProvider(context, factory).get(EditTrackViewModel.class);
    }
}
