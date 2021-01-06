package com.damhoe.gigclick;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Set {

    private ArrayList<Track> tracks;
    private long id;
    private String title;
    private Date date;

    public Set(String title) {
        this.title = title;
        this.id = System.currentTimeMillis();
        this.date = new Date();
        this.tracks = new ArrayList<>();
    }

    public void setId(long id) {
        this.id = id;
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
}
