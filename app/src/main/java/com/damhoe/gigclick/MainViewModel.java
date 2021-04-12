package com.damhoe.gigclick;

import android.app.Application;
import android.content.Context;
import android.nfc.FormatException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        repository.initLibFromDb(new DbSource(application));
    }

    public LiveData<Boolean> getRunStateLD() {
        return repository.getRunStateLD();
    }

    @SuppressWarnings("ConstantConditions")
    public int getNumberOfBeats() {
        return repository.getTrackLD().getValue().getBeats().size();
    }

    @SuppressWarnings("ConstantConditions")
    public int getNumberSamplesPerSplittedBeat() {
        double bpm = repository.getTempo().getBpm();
        return (int) (AudioGenerator.SAMPLES_PER_MINUTE / bpm);
    }

    @SuppressWarnings("ConstantConditions")
    public Beat getBeat(int index) {
        return repository.getTrackLD().getValue().getBeats().get(index);
    }

    public Track getTrack() {
        return repository.getTrackLD().getValue();
    }

    @SuppressWarnings("ConstantConditions")
    public ArrayList<Beat> getBeats() {
        return repository.getTrackLD().getValue().getBeats();
    }

    @SuppressWarnings("ConstantConditions")
    public void setRunningState(boolean state) {
        if (repository.getRunStateLD().getValue() != state) {
            repository.setRunState(state);
        }
    }

    public void setFlashLD(int index) {
        repository.setFlashLD(index);
    }

    @SuppressWarnings("ConstantConditions")
    public void speedUp(int deltaSpeed) {
        double bpm = repository.getTempo().getBpm();
        repository.setBPM(bpm + deltaSpeed);
    }
}
