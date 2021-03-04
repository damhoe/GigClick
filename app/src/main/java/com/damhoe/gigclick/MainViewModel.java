package com.damhoe.gigclick;

import android.content.Context;
import android.nfc.FormatException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private final Repository repository;

    public MainViewModel() {
        repository = Repository.getInstance();
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
        double bpm = repository.getTempoLD().getValue().getBpm();
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
        double bpm = repository.getTempoLD().getValue().getBpm();
        repository.setBPM(bpm + deltaSpeed);
    }
}
