package com.damhoe.gigclick.ui.metronome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.AudioPlayer;
import com.damhoe.gigclick.Beat;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;

import javax.net.ssl.SSLSession;

public class MetronomeViewModel extends ViewModel {

    private final Repository repository;

    public MetronomeViewModel() {
        repository = Repository.getInstance();
    }

    public LiveData<Tempo> getTempoLD() {
        return repository.getTempoLD();
    }

    @SuppressWarnings("ConstantConditions")
    public ArrayList<Beat> getBeats() {
        return repository.getTrackLD().getValue().getBeats();
    }

    public LiveData<ArrayList<Beat>> getBeatLD() {
        return Transformations.map(repository.getTrackLD(), Track::getBeats);
    }

    public void setBPM(double bpm) {
        repository.setBPM(bpm);
    }

    public LiveData<Boolean> getRunStateLD() {
        return repository.getRunStateLD();
    }

    public void setRunState(boolean state) {
        repository.setRunState(state);
    }

    public void addBeat() {
        repository.addBeat();
    }

    public void removeBeat() {
        repository.removeBeat();
    }

    public LiveData<Integer> getFlashLD() {
        return repository.getFlashLD();
    }

    public void setFlashLD(int index) {
        repository.setFlashLD(index);
    }

    public void nextAccent(int index) {
        repository.nextAccent(index);
    }
}