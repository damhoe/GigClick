package com.damhoe.gigclick;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.TransformationsKt;

import java.util.ArrayList;
import java.util.HashMap;

public class DbSource {

    private DbHelper helper;
    private SQLiteDatabase database;

    private final String[] column_set = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_TITLE,
            DbHelper.COLUMN_DATE,
            DbHelper.COLUMN_FAVE
    };

    private final String[] column_track = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_TITLE,
            DbHelper.COLUMN_BPM,
            DbHelper.COLUMN_COMMENT,
            DbHelper.COLUMN_SET_ID,
            DbHelper.COLUMN_POSITION
    };


    public DbSource(@NonNull Application application) {
        helper = new DbHelper(application.getApplicationContext());
    }

    private void open() {
        database = helper.getWritableDatabase();
    }

    private void close() {
        database.close();
    }

    /** Set functions
     *
     */

    public Set cursor2Set(Cursor cursor) {
        // get indices
        int idxId = cursor.getColumnIndex(DbHelper.COLUMN_ID);
        int idxTitle = cursor.getColumnIndex(DbHelper.COLUMN_TITLE);
        int idxDate = cursor.getColumnIndex(DbHelper.COLUMN_DATE);
        int idxFave = cursor.getColumnIndex(DbHelper.COLUMN_FAVE);
        // get data
        long id = cursor.getLong(idxId);
        String title = cursor.getString(idxTitle);
        long date = cursor.getLong(idxDate);
        boolean isFave = Utility.int2Boolean(cursor.getInt(idxFave));
        // set data
        return new Set(id, title, date, isFave);
    }

    public void set2Db(Set set) {

        open();

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TITLE, set.getTitle());
        values.put(DbHelper.COLUMN_DATE, set.getDate().getTime());
        values.put(DbHelper.COLUMN_FAVE, Utility.boolean2Int(set.isFave()));
        long id = database.insert(DbHelper.TABLE_SET, null, values);
        set.setId(id);

        close();

        for (int k=0; k<set.getNumberOfTracks(); k++) {
            track2Db(set.getTracks().get(k), k);
        }
    }

    public void updateSet(Set set) {
        open();

        database.delete(DbHelper.TABLE_TRACK, DbHelper.COLUMN_SET_ID + "=" + set.getId(), null);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TITLE, set.getTitle());
        values.put(DbHelper.COLUMN_DATE, set.getDate().getTime());
        values.put(DbHelper.COLUMN_FAVE, Utility.boolean2Int(set.isFave()));
        database.update(DbHelper.TABLE_SET, values, DbHelper.COLUMN_ID + "=" + set.getId(), null);

        close();

        for (int k=0; k<set.getNumberOfTracks(); k++) {
            track2Db(set.getTracks().get(k), k);
        }
    }

    public void updateSetMeta(Set set) {
        open();

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TITLE, set.getTitle());
        values.put(DbHelper.COLUMN_DATE, set.getDate().getTime());
        values.put(DbHelper.COLUMN_FAVE, Utility.boolean2Int(set.isFave()));
        database.update(DbHelper.TABLE_SET, values, DbHelper.COLUMN_ID + "=" + set.getId(), null);

        close();
    }

    public void deleteSet(long id) {
        open();
        // delete all Tracks
        database.delete(DbHelper.TABLE_TRACK, DbHelper.COLUMN_SET_ID + "=" + id, null);
        // delete Set info
        database.delete(DbHelper.TABLE_SET, DbHelper.COLUMN_ID + "=" + id, null);
        close();
    }

    public Set loadSet(long id) {

        return null;
    }

    /** Track functions
     *
     * Modifying a track should be initiated by modifying the corresponding set.
     */

    public void track2Db(Track track, int position) {

        open();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TITLE, track.getTitle());
        values.put(DbHelper.COLUMN_BPM, track.getTempo().getBpm());
        values.put(DbHelper.COLUMN_COMMENT, track.getComment());
        values.put(DbHelper.COLUMN_SET_ID, track.getSetId());
        values.put(DbHelper.COLUMN_POSITION, position);
        long id = database.insert(DbHelper.TABLE_TRACK, null, values);
        track.setId(id);
        close();
    }

    // ignores track position
    private Track cursor2Track(Cursor cursor) {
        // get indices
        int idxId = cursor.getColumnIndex(DbHelper.COLUMN_ID);
        int idxTitle = cursor.getColumnIndex(DbHelper.COLUMN_TITLE);
        int idxBpm = cursor.getColumnIndex(DbHelper.COLUMN_BPM);
        int idxComment = cursor.getColumnIndex(DbHelper.COLUMN_COMMENT);
        int idxSetId = cursor.getColumnIndex(DbHelper.COLUMN_SET_ID);
        // get data
        long id = cursor.getLong(idxId);
        String title = cursor.getString(idxTitle);
        int bpm = cursor.getInt(idxBpm);
        String comment = cursor.getString(idxComment);
        long setId = cursor.getLong(idxSetId);
        // set data
        return new Track(id, title, bpm, comment, setId);
    }

    private ArrayList<Track> cursor2TrackList(Cursor cursor) {
        HashMap<Integer, Track> mTrackMap = new HashMap<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            // get indices
            int idxId = cursor.getColumnIndex(DbHelper.COLUMN_ID);
            int idxTitle = cursor.getColumnIndex(DbHelper.COLUMN_TITLE);
            int idxBpm = cursor.getColumnIndex(DbHelper.COLUMN_BPM);
            int idxComment = cursor.getColumnIndex(DbHelper.COLUMN_COMMENT);
            int idxSetId = cursor.getColumnIndex(DbHelper.COLUMN_SET_ID);
            int idxPos = cursor.getColumnIndex(DbHelper.COLUMN_POSITION);
            // get data
            long id = cursor.getLong(idxId);
            String title = cursor.getString(idxTitle);
            int bpm = cursor.getInt(idxBpm);
            String comment = cursor.getString(idxComment);
            long setId = cursor.getLong(idxSetId);
            int pos = cursor.getInt(idxPos);
            // set data
            mTrackMap.put(pos, new Track(id, title, bpm, comment, setId));

            cursor.moveToNext();
        }

        ArrayList<Track> mTracks = new ArrayList<>();
        for (int j=0; j<mTrackMap.size(); j++) {
            mTracks.add(mTrackMap.get(j));
        }
        return mTracks;
    }

    public void updateTrack(Track track) {
        open();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_TITLE, track.getTitle());
        values.put(DbHelper.COLUMN_BPM, track.getTempo().getBpm());
        values.put(DbHelper.COLUMN_COMMENT, track.getComment());
        values.put(DbHelper.COLUMN_SET_ID, track.getSetId());

        database.update(DbHelper.TABLE_TRACK, values, DbHelper.COLUMN_ID + "=" + track.getId(), null);
        close();
    }

    public void deleteTrack(long id) {

    }

    private Track loadTrack(long id) {

        return null;
    }

    public Library loadLibFromDb() {
        Library lib = new Library();
        ArrayList<Set> sets = new ArrayList<>();

        open();

        // load sets
        Cursor cursor = database.query(DbHelper.TABLE_SET, column_set,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Set set = cursor2Set(cursor);
            loadTracksFromDb(set);
            sets.add(set);
            cursor.moveToNext();
        }
        cursor.close();

        close();

        lib.setSets(sets);
        return lib;
    }

    private void loadTracksFromDb(Set set) {
        // database is already opened
        Cursor cursor = database.query(DbHelper.TABLE_TRACK, column_track,
                DbHelper.COLUMN_SET_ID + "=" + set.getId(),
                null, null, null, null);

        ArrayList<Track> tracks = cursor2TrackList(cursor);
        cursor.close();
        set.setTracks(tracks);
    }
}
