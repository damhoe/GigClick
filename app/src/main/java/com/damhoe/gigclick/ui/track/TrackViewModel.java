package com.damhoe.gigclick.ui.track;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Track;

public class TrackViewModel extends ViewModel {

    private Repository repository = Repository.getInstance();

    public TrackViewModel() {

    }

    public LiveData<Track> getTrackLD() {
        return repository.getTrackLD();
    }

    @SuppressWarnings("ConstantConditions")
    public void selectTrack(int position) {
        Track track = repository.getSetLD().getValue().getTracks().get(position);
        repository.setTrack(track);
    }

    public void setBPM(double bpm) {
        repository.setBPM(bpm);
    }

    public void nextAccent(int i) {
        repository.nextAccent(i);
    }

    public void addBeat() {
        repository.addBeat();
    }

    public void removeBeat() {
        repository.removeBeat();
    }
}
