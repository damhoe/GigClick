package com.damhoe.gigclick.ui.library;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.DbSource;
import com.damhoe.gigclick.Library;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class LibraryViewModel extends AndroidViewModel {

    private final Repository repository = Repository.getInstance();
    private final MutableLiveData<Set> setLD = new MutableLiveData<>(); // set which can be viewed with details and edited
    private final MutableLiveData<Track> trackLD = new MutableLiveData<>();  // track which can be viewed with details and edited
    private final MutableLiveData<Integer> scrollYLib = new MutableLiveData<>();
    private final MutableLiveData<Integer> scrollYSet = new MutableLiveData<>();

    public LiveData<ArrayList<Set>> setsLD;

    private DbSource source;

    private int idxTrack = 0;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        setLD.setValue(new Set(""));
        trackLD.setValue(new Track());
        source = new DbSource(application);
        repository.initLibFromDb(source);
        scrollYLib.setValue(0);
        scrollYSet.setValue(0);
    }

    public void sortSets(int key) {
        ArrayList<Set> sets = getSetsLD().getValue();
        // TODO: sort
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
    public LiveData<Integer> getScrollYLib() {
        return scrollYLib;
    }

    public LiveData<Integer> getScrollYSet() {
        return scrollYSet;
    }

    @SuppressWarnings("ConstantConditions")
    public void updateScrollYLib(int dy) {
        int val = scrollYLib.getValue();
        val += dy;
        if (val < 0) val = 0;
        scrollYLib.postValue(val);
    }

    @SuppressWarnings("ConstantConditions")
    public void updateScrollYSet(int dy) {
        int val = scrollYSet.getValue();
        val += dy;
        if (val < 0) val = 0;
        scrollYSet.postValue(val);
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

    public void saveSet(Set set) {
        repository.set2Db(set, source);
    }

    public void deleteSet(long id) {
        repository.delSetFromDb(id, source);
    }

    public void updateSet(Set set) {
        repository.updateSet(set, source);
        setSetLD(set);
    }

    public void updateSetMeta(Set set) {
        repository.updateSetMeta(set, source);
        setSetLD(set);
    }

    public void updateTrack(Track track) {
        repository.updateTrack(track, source);
        Set set = getSetLD().getValue();
        setSetLD(repository.getLibraryLD().getValue().getSetById(set.getId()));
    }

    public void sortLibBy(int key) {
        repository.sortLibBy(key);
    }
}