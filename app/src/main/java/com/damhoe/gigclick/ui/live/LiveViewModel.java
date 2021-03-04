package com.damhoe.gigclick.ui.live;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;

public class LiveViewModel extends ViewModel {

    private final Repository repository;
    private final MutableLiveData<Integer> selectedLD;
    private final MutableLiveData<Set> setLD;
    private final MutableLiveData<Double> xLD;

    public LiveViewModel() {
        repository = Repository.getInstance();
        selectedLD = new MutableLiveData<>();
        selectedLD.setValue(0);

        // set Set LiveData
        setLD = new MutableLiveData<>();
        // TODO: load default set by set ID -> requires database
        long id = repository.getSetsLD().getValue().get(0).getId();
        Set set = repository.getSetById(id);
        setLD.setValue(set);

        // set playing Track LiveData
        // default with first Track in the current set
        repository.setTrack(getSetLD().getValue().getTracks().get(0));

        xLD = new MutableLiveData<>();
        xLD.setValue(0.);
    }

    public LiveData<Track> getTrackLD() {
        return repository.getTrackLD();
    }

    public LiveData<Integer> getSelectedLD() {
        return selectedLD;
    }

    @SuppressWarnings("ConstantConditions")
    public void setSelected(int position) {
        selectedLD.postValue(position);
        repository.setTrack(getSetLD().getValue().getTracks().get(position));
    }

    public LiveData<ArrayList<Track>> getBibLD() {
        return repository.getAllTracksLD();
    }

    public LiveData<Set> getSetLD() {
        // filter the current set
        return setLD;
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

    public LiveData<Double> getxLD() {
        return xLD;
    }

    public void setxLD(double x) {
        xLD.postValue(x);
    }
}