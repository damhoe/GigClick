package com.damhoe.gigclick.ui.track;

import android.app.Activity;
import android.app.Application;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.damhoe.gigclick.R;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Track;

import java.lang.reflect.InvocationTargetException;

public class EditTrackVMFactory extends ViewModelProvider.AndroidViewModelFactory {

    Application application;
    Track track;
    boolean isNew;

    public EditTrackVMFactory(@NonNull Application application, Track track, boolean isNew) {
        super(application);
        this.application = application;
        this.track = track;
        this.isNew = isNew;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            AndroidViewModel viewModel = null;
            if (modelClass.isAssignableFrom(EditTrackViewModel.class)) {
                viewModel = new EditTrackViewModel(application, track, isNew);
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

        // if new track set ID is passed and track ID equals -1
        // else both IDs are passed
        EditTrackFragmentArgs args = EditTrackFragmentArgs.fromBundle(bundle);
        long trackId = args.getTrackId();
        long setId = args.getSetId();
        boolean isNew = args.getIsNew();

        Track track;
        if (isNew) {
            track = new Track();
            track.setSetId(setId);
        } else {
            track = Repository.getInstance().getLibraryLD().getValue().getTrackById(trackId).deepCopy();
        }

        EditTrackVMFactory factory = new EditTrackVMFactory(application, track, isNew);
        return new ViewModelProvider(context, factory).get(EditTrackViewModel.class);
    }
}
