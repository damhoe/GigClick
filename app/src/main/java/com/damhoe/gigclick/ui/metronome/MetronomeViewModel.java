package com.damhoe.gigclick.ui.metronome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Beat;
import com.damhoe.gigclick.PracticeOptions;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

import java.util.ArrayList;

public class MetronomeViewModel extends ViewModel {

    private final Repository repository;

    public MetronomeViewModel() {
        repository = Repository.getInstance();
    }

    public Track getTrack() {
        return repository.getTrackLD().getValue();
    }

    @SuppressWarnings("ConstantConditions")
    public ArrayList<Beat> getBeats() {
        return repository.getTrackLD().getValue().getBeats();
    }

    public LiveData<ArrayList<Beat>> getBeatLD() {
        return Transformations.map(repository.getTrackLD(), Track::getBeats);
    }

    public LiveData<Integer> getBPB_LD() {
        return Transformations.map(repository.getTrackLD(), Track::getBPB);
    }

    public LiveData<PracticeOptions> getPOptionsLD() {
        return Transformations.map(repository.getTrackLD(), Track::getpOptions);
    }

    public LiveData<Tempo> getTempoLD() {
        return Transformations.map(repository.getTrackLD(), Track::getTempo);
    }

    public LiveData<Track> getTrackLD() {
        return repository.getTrackLD();
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

    @SuppressWarnings("ConstantConditions")
    public void switchRunState() {repository.setRunState(!getRunStateLD().getValue());}

    public void setBPB(int bpb) {
        repository.setBPB(bpb);
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

    public double getBpm() {
        return getTrack().getTempo().getBpm();
    }

    @SuppressWarnings("ConstantConditions")
    public void setPracticeOptions(boolean bs, boolean bm, int n, int su, int ds, int m, int nm) {
        Track track = repository.getTrackLD().getValue();
        PracticeOptions options = track.getpOptions();
        options.update(n, su, ds, m, nm);
        options.setMuted(bm);
        options.setSpeed(bs);
        track.setpOptions(options);
        repository.setTrack(track);
    }
}