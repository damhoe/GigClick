package com.damhoe.gigclick.ui.live;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;

public class LiveViewModel extends ViewModel {

    private final Repository repository;
    private final MutableLiveData<Integer> selectedLD;

    public LiveViewModel() {
        repository = Repository.getInstance();
        selectedLD = new MutableLiveData<>();
        selectedLD.setValue(0);
    }

    public LiveData<Integer> getSelectedLD() {
        return selectedLD;
    }

    @SuppressWarnings("ConstantConditions")
    public void setSelected(int position) {
        selectedLD.postValue(position);
        setTempo(getCurrentTracksLD().getValue().get(position).getTempo());
    }

    public LiveData<ArrayList<Track>> getBibLD() {
        return repository.getAllTracksLD();
    }

    public LiveData<ArrayList<Track>> getCurrentTracksLD() {
        // filter the current set
        return repository.getAllTracksLD();
    }

    public LiveData<Boolean> getRunStateLD() {
        return repository.getRunStateLD();
    }

    public void setRunState(boolean isRunning) {
        repository.setRunState(isRunning);
    }

    public LiveData<Tempo> getTempoLD() {
        return repository.getTempoLD();
    }

    public void setTempo(Tempo tempo) {
        repository.setBPM(tempo.getBpm());
    }
}