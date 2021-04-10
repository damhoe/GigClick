package com.damhoe.gigclick.ui.track;

import android.app.Application;
import android.telephony.VisualVoicemailService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.gigclick.DbSource;
import com.damhoe.gigclick.Library;
import com.damhoe.gigclick.Repository;
import com.damhoe.gigclick.Set;
import com.damhoe.gigclick.Tempo;
import com.damhoe.gigclick.Track;

public class EditTrackViewModel extends AndroidViewModel {

    public MutableLiveData<String> titleLD = new MutableLiveData<>();
    public MutableLiveData<String> commentLD = new MutableLiveData<>();
    public MutableLiveData<Tempo> tempoLD = new MutableLiveData<>();

    private Track track;
    private boolean isNew;
    private final Repository repo;

    public EditTrackViewModel(@NonNull Application application, Track track, boolean isNew) {
        super(application);
        repo = Repository.getInstance();

        this.isNew = isNew;
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

        DbSource source = new DbSource(getApplication());

        if (isNew) {
            Set set = repo.getLibraryLD().getValue().getSetById(track.getSetId());
            set.addTrack(track);
            repo.updateSet(set, source);
        } else {
            repo.updateTrack(track, source);
        }
    }
}
