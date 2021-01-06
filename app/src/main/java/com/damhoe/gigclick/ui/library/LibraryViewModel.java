package com.damhoe.gigclick.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {

    private final Repository repository = Repository.getInstance();

    public LibraryViewModel() {
        // empty.
    }

    public void sortSets(int key) {
        ArrayList<Set> sets = getSetsLD().getValue();
        // TODO: sort
        repository.setSetsLD(sets);
    }

    public LiveData<Track> getCurrentTrackLD() {
        return repository.getTrackLD();
    }

    @SuppressWarnings("ConstantConditions")
    public void selectTrack(int position) {
        Track track = getCurrentSetLD().getValue().getTracks().get(position);
        repository.setTrack(track);
    }

    public LiveData<Set> getCurrentSetLD() {
        return repository.getSetLD();
    }

    @SuppressWarnings("ConstantConditions")
    public void selectSet(int position) {
        Set set = getSetsLD().getValue().get(position);
        repository.setSet(set);
    }

    public LiveData<ArrayList<Set>> getSetsLD() {
        return repository.getSetsLD();
    }

    /** Return the position of the track in the recycler view */
    @SuppressWarnings("ConstantConditions")
    public int getTrackPosition(long id) {
        ArrayList<Track> tracks = getCurrentSetLD().getValue().getTracks();
        for (Track track: tracks) {
            if (track.getId() == id) return tracks.indexOf(track);
        }
        return -1;
    }

    /** Return the position of the set in the recycler view */
    @SuppressWarnings("ConstantConditions")
    public int getSetPosition(long id) {
        ArrayList<Set> sets = getSetsLD().getValue();
        for (Set set: sets) {
            if (set.getId() == id) return sets.indexOf(set);
        }
        return -1;
    }
}