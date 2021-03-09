package com.damhoe.gigclick.ui.track;

import android.app.Application;
import android.telephony.VisualVoicemailService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.Library;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

public class EditTrackViewModel extends AndroidViewModel {

    public MutableLiveData<String> titleLD = new MutableLiveData<>();
    public MutableLiveData<String> commentLD = new MutableLiveData<>();
    public MutableLiveData<Tempo> tempoLD = new MutableLiveData<>();

    private Track track;

    public EditTrackViewModel(@NonNull Application application, Track track) {
        super(application);
        this.track = track;
        titleLD.setValue(track.getTitle());
        commentLD.setValue(track.getComment());
        tempoLD.setValue(track.getTempo());
    }

    public MutableLiveData<String> getTitleLD() {
       return titleLD;
    }

    public LiveData<Tempo> getTempoLD() {
        return tempoLD;
    }

    public void setBPM(double bpm) {
        Tempo tempo = getTempoLD().getValue();
        tempo.setBpm(bpm);
        tempoLD.postValue(tempo);
    }

    public boolean isValidTitle(String title) {
        return !title.equals("");
    }

    public void saveTrack() {
        track.setTempo(tempoLD.getValue());
        track.setTitle(titleLD.getValue());
        track.setComment(commentLD.getValue());

        Repository repo = Repository.getInstance();
        Library library = repo.getLibraryLD().getValue();
        library.updateTrack(track);
        repo.setLibrary(library);
    }
}
