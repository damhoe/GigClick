package com.damhoe.gigclick;

import java.util.ArrayList;
import java.util.ListResourceBundle;

public class Library {

    /**
     * The Library class contains all sets and tracks.
     *
     */

    private ArrayList<Set> sets = new ArrayList<>();

    public Library() {
        // empty.
    }

    public void updateTrack(Track track) {
        // find the set which contains the track
        long id = track.getId();
        for (Set set: sets) {
            for (Track track1: set.getTracks()) {
                if (track1.getId() == id) {
                    track1.update(track);
                }
            }
        }
    }

    public void updateSet(Set set) {
        // TODO
    }

    public Track getTrackById(long id) {
        for (Set set: sets) {
            for (Track track: set.getTracks()) {
                if (id == track.getId()) {
                    return track;
                }
            }
        }
        return null;
    }

    public void getSetById(long id) {
        // TODO
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }

    public ArrayList<Set> getSets() {
        return sets;
    }

    public void addSet(Set set) {
        sets.add(set);
    }

    public ArrayList<Track> getAllTracks() {
        ArrayList<Track> tracks = new ArrayList<>();
        for (Set set: sets) {
            tracks.addAll(set.getTracks());
        }
        return tracks;
    }

    /** Create example library. */
    public static Library createExampleLibrary() {
        Library lib = new Library();
        lib.setSets(Set.getExampleSets());
        return lib;
    }
}
