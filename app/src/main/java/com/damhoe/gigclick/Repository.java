package com.damhoe.gigclick;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;

public class Repository {

    /**
     * The repo. stores the Sets containing the different tracks.
     *
     * It provides functions used to filter and sort the sets and tracks.
     *
     */

    private static Repository repository = null;

    // stored data
    private final MutableLiveData<ArrayList<Track>> allTracksLD;
    private final MutableLiveData<ArrayList<Set>> setsLD;

    // metronome data
    private final MutableLiveData<Boolean> isRunningLD;
    private final MutableLiveData<Track> trackLD; // information about beats
    private final MutableLiveData<Set> setLD;
    private final MutableLiveData<Integer> flashLD;

    private final LiveData<Tempo> tempoLD;

    @SuppressWarnings("ConstantConditions")
    public Repository() {
        setsLD = new MutableLiveData<>();
        setsLD.setValue(Set.getExampleSets());
        allTracksLD = new MutableLiveData<>();
        ArrayList<Track> tracks = new ArrayList<>();
        for (Set set: getSetsLD().getValue()) {
            tracks.addAll(set.getTracks());
        }
        allTracksLD.setValue(tracks);

        isRunningLD = new MutableLiveData<>();
        isRunningLD.setValue(false);
        flashLD = new MutableLiveData<>();
        flashLD.setValue(-1);
        trackLD = new MutableLiveData<>();
        trackLD.postValue(new Track());
        tempoLD = Transformations.map(getTrackLD(), Track::getTempo);
        setLD = new MutableLiveData<>();
        setLD.setValue(new Set("Example Set"));
    }

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void setAllTracksLD(ArrayList<Track> tracks) {
        allTracksLD.postValue(tracks);
    }

    public LiveData<ArrayList<Track>> getAllTracksLD() {
        return allTracksLD;
    }

    public LiveData<ArrayList<Set>> getSetsLD() {
        return setsLD;
    }

    public void setSetsLD(ArrayList<Set> sets) {
        setsLD.postValue(sets);
    }

    @SuppressWarnings("ConstantConditions")
    public Track getTrackById(long id) {
        for (Track mTrack: getAllTracksLD().getValue()) {
            if (mTrack.getId() == id) return mTrack;
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public Set getSetById(long id) {
        for (Set mSet: getSetsLD().getValue()) {
            if (mSet.getId() == id) return mSet;
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public void nextAccent(int index) {
        Track track = trackLD.getValue();
        track.getBeats().get(index).nextAccent();
        trackLD.postValue(track);
    }

    @SuppressWarnings("ConstantConditions")
    public void addBeat() {
        Track track = trackLD.getValue();
        track.addBeat();
        trackLD.postValue(track);
    }

    @SuppressWarnings("ConstantConditions")
    public void removeBeat() {
        Track track = trackLD.getValue();
        track.removeBeat();
        trackLD.postValue(track);
    }

    @SuppressWarnings("ConstantConditions")
    public void setBPM(double bpm) {
        Track track = getTrackLD().getValue();
        track.setBPM(bpm);
        trackLD.postValue(track);
    }

    public void setFlashLD(int index) {
        flashLD.postValue(index);
    }

    public LiveData<Integer> getFlashLD() {
        return flashLD;
    }

    @SuppressWarnings("ConstantConditions")
    public void setRunState(boolean state) {
        if (!(getRunStateLD().getValue() == state)) isRunningLD.postValue(state);
    }

    public LiveData<Boolean> getRunStateLD() {
        return isRunningLD;
    }

    public LiveData<Track> getTrackLD() {
        return trackLD;
    }

    public void setTrack(Track track) {
        trackLD.postValue(track);
    }

    public LiveData<Tempo> getTempoLD() {
        return tempoLD;
    }

    public LiveData<Set> getSetLD() {
        return setLD;
    }

    public void setSet(Set set) {
        setLD.postValue(set);
    }
}
