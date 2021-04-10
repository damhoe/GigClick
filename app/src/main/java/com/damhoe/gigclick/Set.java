package com.damhoe.gigclick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Set {

    private ArrayList<Track> tracks;
    private long id;
    private String title;
    private Date date;
    private boolean isFave;

    public Set(String title) {
        this.title = title;
        this.id = System.nanoTime();
        this.date = new Date();
        this.tracks = new ArrayList<>();
        this.isFave = false;
    }

    // for loading from database
    public Set(long id, String title, long date, boolean isFave) {
        this.title = title;
        this.tracks = new ArrayList<>();
        this.id = id;
        this.date = new Date(date);
        this.isFave = isFave;
    }

    public void setId(long id) {
        this.id = id;
        for (Track track: tracks) {
            track.setSetId(id);
        }
    }

    public long getId() {
        return id;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        track.setSetId(getId());
        tracks.add(track);
    }

    public int getNumberOfTracks() {
        return tracks.size();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getDateAsString() {
        return Utility.date2String(date);
    }

    public void setFave(boolean fave) {
        isFave = fave;
    }

    public boolean isFave() {
        return isFave;
    }

    public int editTrack(Track track) {
        int pos = getTrackPosById(track.getId());
        if (pos != -1) {
            tracks.set(pos, track);
            return 0;
        }
        return 1;
    }

    public void deleteTrack(long id) {
        tracks.remove(getTrackPosById(id));
    }

    public static ArrayList<Set> getExampleSets() {
        int size = 5;

        ArrayList<Set> sets = new ArrayList<>();
        for (int j=0; j<size; j++) {
            Set set = new Set(String.format(Locale.GERMANY, "Set no. %d", j));
            set.setTracks(Track.getExampleTracks());
            sets.add(set);
        }

        return sets;
    }

    private int getTrackPosById(long id) {
        for (Track mTrack: tracks) {
            if (mTrack.getId() == id) return tracks.indexOf(mTrack);
        }
        return -1;
    }

    public void updateTrackAt(int position, Track track) {
        tracks.set(position, track);
    }


}
