package com.damhoe.gigclick.ui.library;

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

public class LibraryViewModel extends ViewModel {

    private final Repository repository = Repository.getInstance();
    private final MutableLiveData<Set> setLD = new MutableLiveData<>(); // set which can be viewed with details and edited
    private final MutableLiveData<Track> trackLD = new MutableLiveData<>();  // track which can be viewed with details and edited

    public LiveData<ArrayList<Set>> setsLD;

    private int idxTrack = 0;

    public LibraryViewModel() {
        setLD.setValue(new Set(""));
        trackLD.setValue(new Track());
    }

    public void sortSets(int key) {
        ArrayList<Set> sets = getSetsLD().getValue();
        // TODO: sort
        repository.setSetsLD(sets);
    }

    public LiveData<Library> getLibraryLD() {
        return repository.getLibraryLD();
    }

    public LiveData<Track> getTrackLD() {
        return trackLD;
    }
    public void setTrack(Track track) {
        trackLD.postValue(track);
    }

    @SuppressWarnings("ConstantConditions")
    public void selectTrack(int position) {
        Track track = getSetLD().getValue().getTracks().get(position);
        trackLD.setValue(track);
        idxTrack = position;
    }

    public void initTrack() {
        trackLD.postValue(setLD.getValue().getTracks().get(idxTrack));
    }

    public LiveData<Set> getSetLD() {
        return setLD;
    }

    @SuppressWarnings("ConstantConditions")
    public void selectSet(int position) {
        Set set = repository.getLibraryLD().getValue().getSets().get(position);
        setSetLD(set);
    }

    public void setSetLD(Set set) {
        setLD.setValue(set);
    }

    public LiveData<ArrayList<Set>> getSetsLD() {
        return Transformations.map(repository.getLibraryLD(), Library::getSets);
    }

    /** Return the position of the track in the recycler view */
    @SuppressWarnings("ConstantConditions")
    public int getTrackPosition(long id) {
        ArrayList<Track> tracks = getSetLD().getValue().getTracks();
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

    /** Functions for editing tracks */
    public Tempo getTempo() {
        return getTrackLD().getValue().getTempo();
    }

    public void setBPM(double bpm) {
        Track track = getTrackLD().getValue();
        track.setBPM(bpm);
        trackLD.postValue(track);
    }

    public void setTitle(String title) {
        Track track = getTrackLD().getValue();
        track.setTitle(title);
        trackLD.postValue(track);
    }

    public void setComment(String comment) {
        Track track = getTrackLD().getValue();
        track.setComment(comment);
        trackLD.postValue(track);
    }
}