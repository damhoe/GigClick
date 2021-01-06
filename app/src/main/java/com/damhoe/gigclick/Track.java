package com.damhoe.gigclick;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Random;

public class Track {

    public static final int MAX_BEATS = 8;
    public static final int MIN_BEATS = 1;

    private ArrayList<Beat> beats;
    private long id;
    private Tempo tempo;
    private String title;
    private String comment;
    private String artist;

    // default constructor
    public Track() {
        this.id = System.currentTimeMillis();
        this.tempo = new Tempo();
        int n = 4; // add 4 beats
        beats = new ArrayList<>();
        for (int i=0; i<n; i++) {
            this.beats.add(new Beat());
        }
    }

    public ArrayList<Beat> getBeats() {
        return beats;
    }

    public Tempo getTempo() {
        return tempo;
    }

    public void setTempo(Tempo tempo) {
        this.tempo = tempo;
    }

    // always remove the last beat
    public boolean removeBeat() {
        int size = beats.size();
        if (size > MIN_BEATS) {
            beats.remove(size - 1);
            return true;
        }
        return false;
    }

    // always append
    public boolean addBeat() {
        if (beats.size() < MAX_BEATS) {
            beats.add(new Beat());
            return true;
        }
        return false;
    }

    public boolean setBPM(double bpm) {
        return tempo.setBpm(bpm);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ArrayList<Track> getExampleTracks() {
        ArrayList<Track> tracks = new ArrayList<>();

        String[] titles = new String[]{"Blowin' in the wind", "Bloody Sunday", "Fly me to the moon", "Hot Stuff", "On the top", "Cockroach King"};
        String[] comments = new String[]{"only with guitar", "", "Foxtrott", "", "One of my favorites", "by Haken"};
        String[] artits = new String[]{"Bob Dylan", "U2", "Frank Sinatra", "Donna Summer", "Jinjer", "Haken"};

        for (int i=0; i<Math.min(titles.length, comments.length);  i++) {
            Track track = new Track();
            track.setComment(comments[i]);
            track.setTitle(titles[i]);
            track.setArtist(artits[i]);
            tracks.add(track);
        }

        return tracks;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
