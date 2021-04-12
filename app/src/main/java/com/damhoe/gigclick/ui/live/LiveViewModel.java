package com.damhoe.gigclick.ui.live;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Library;
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
        //long id = repository.getLibraryLD().getValue().getSets().get(0).getId();
        Library lib = repository.getLibraryLD().getValue();
        //Set set = repository.getSetById(id);
        setLD.setValue(lib.getSets().get(0));

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

    public LiveData<Set> getSetLD() {
        // filter the current set
        return setLD;
    }

    public void setSet(Set set) {
        setLD.postValue(set);
    }

    public ArrayList<Set> getSets() {
        return repository.getLibraryLD().getValue().getSets();
    }

    public int getCurrentSetIndex() {
        Library lib = repository.getLibraryLD().getValue();
        long id = getSetLD().getValue().getId();
        return lib.getSetIndex(id);
    }

    public String[] getSetTitles() {
        ArrayList<Set> sets = repository.getLibraryLD().getValue().getSets();
        int N = sets.size();
        String[] titles = new String[N];
        for (int k=0; k<N; k++) {
            titles[k] = sets.get(k).getTitle();
        }
        return titles;
    }

    public LiveData<Boolean> getRunStateLD() {
        return repository.getRunStateLD();
    }

    public void setRunState(boolean isRunning) {
        repository.setRunState(isRunning);
    }

    public LiveData<Double> getxLD() {
        return xLD;
    }

    public void setxLD(double x) {
        xLD.postValue(x);
    }

    public int getTrackNumber(long id) {
        ArrayList<Track> tracks = getSetLD().getValue().getTracks();
        for (int i=0; i<tracks.size(); i++) {
            if (tracks.get(i).getId() == id) {
                return i + 1;
            }
        }
        return 0;
    }
}