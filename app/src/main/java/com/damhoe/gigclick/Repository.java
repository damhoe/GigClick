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
    private final MutableLiveData<Library> libraryLD = new MutableLiveData<>();

    // metronome data
    private final MutableLiveData<Boolean> isRunningLD;
    private final MutableLiveData<Track> trackLD; // information about beats
    private final MutableLiveData<Set> setLD;
    private final MutableLiveData<Integer> flashLD;

    @SuppressWarnings("ConstantConditions")
    public Repository() {
        libraryLD.setValue(Library.createEmptyLibrary());

        isRunningLD = new MutableLiveData<>();
        isRunningLD.setValue(false);
        flashLD = new MutableLiveData<>();
        flashLD.setValue(-1);
        trackLD = new MutableLiveData<>();
        trackLD.postValue(new Track());
        setLD = new MutableLiveData<>();
        setLD.setValue(new Set("Example Set"));
    }

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void setLibrary(Library library) {
        libraryLD.postValue(library);
    }

    public LiveData<Library> getLibraryLD() {
        return libraryLD;
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

    @SuppressWarnings("ConstantConditions")
    public void setBPB(int bpb) {
        Track track = getTrackLD().getValue();
        track.setBPB(bpb);
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

    @SuppressWarnings("ConstantConditions")
    public Tempo getTempo() {
        return trackLD.getValue().getTempo();
    }

    public LiveData<Set> getSetLD() {
        return setLD;
    }

    public void setSet(Set set) {
        setLD.postValue(set);
    }

    public void sortLibBy(int key) {
        Library lib = getLibraryLD().getValue();
        lib.sortBy(key);
        setLibrary(lib);
    }


    /** Interactions with database.
     *
     */

    public void initLibFromDb(DbSource source) {
        setLibrary(source.loadLibFromDb());
    }

    public void set2Db(Set set, DbSource source) {
        source.set2Db(set);
        // update data
        setLibrary(source.loadLibFromDb());
    }

    public void updateSet(Set set, DbSource source) {
        source.updateSet(set);
        // update data
        setLibrary(source.loadLibFromDb());
    }

    public void delSetFromDb(long id, DbSource source) {
        source.deleteSet(id);
        // update data
        setLibrary(source.loadLibFromDb());
    }

    public void updateTrack(Track track, DbSource source) {
        source.updateTrack(track);
        setLibrary(source.loadLibFromDb());
    }

    public void updateSetMeta(Set set, DbSource source) {
        source.updateSetMeta(set);
        setLibrary(source.loadLibFromDb());
    }
}
