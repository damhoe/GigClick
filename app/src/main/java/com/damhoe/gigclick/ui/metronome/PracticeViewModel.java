package com.damhoe.gigclick.ui.metronome;

import android.app.Application;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.damhoe.gigclick.PracticeOptions;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Track;

public class PracticeViewModel extends AndroidViewModel {

    private final Repository repository;

    private final MutableLiveData<PracticeOptions> optionsLD = new MutableLiveData<>();

    @SuppressWarnings("ConstantConditions")
    public PracticeViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        /* new instance */
        PracticeOptions mOptions = repository.getTrackLD().getValue().getpOptions().deepCopy();
        optionsLD.setValue(mOptions);
    }

    public LiveData<PracticeOptions> getOptionsLD() {
        return optionsLD;
    }

    public LiveData<Boolean> isMuteLD() {
        return Transformations.map(optionsLD, PracticeOptions::isMuted);
    }

    public LiveData<Boolean> isSpeedUpLD() {
        return Transformations.map(optionsLD, PracticeOptions::isSpeed);
    }

    @SuppressWarnings("ConstantConditions")
    public void applyOptions(PracticeOptions pOptions) {
        Track track = repository.getTrackLD().getValue();
        track.setpOptions(pOptions);
        repository.setTrack(track);
    }
}
