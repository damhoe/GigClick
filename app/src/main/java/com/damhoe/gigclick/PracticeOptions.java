package com.damhoe.gigclick;

import android.annotation.SuppressLint;

import java.util.ArrayList;

public class PracticeOptions {

    /** Class for handling practice mode options.
     *
     * Practice mode options are e.g. speed ups every ith bar or muting every jth bar.
     *
     */

    public static final int[] speedUpBars = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int[] speedUpDeltas = {1, 2, 5, 10};
    public static final int[] playBars = {1, 2, 3, 4};
    public static final int[] muteBars = {1, 2, 3, 4};

    private int nBars = 1000;
    private int speedUpEach = 0;
    private int deltaSpeed = 0;
    private int nBarsSound = 1;
    private int nBarsMuted = 0;
    private boolean mute = false;
    private boolean speed = false;

    private ArrayList<Integer> mTimeline;
    private ArrayList<Integer> sTimeline;

    public PracticeOptions() {
        // empty.
        //mute = 3;
        //nBarsMuted = 2;
        updateTimeline();
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setSpeed(boolean speed) {
        this.speed = speed;
    }

    public boolean isMuted() {
        return mute;
    }

    public boolean isSpeed() {
        return speed;
    }

    public void update(int n, int su, int ds, int ns, int nm) {
        nBars = n;
        speedUpEach = su;
        deltaSpeed = ds;
        nBarsSound = ns;
        nBarsMuted = nm;
        updateTimeline();
    }

    private void updateTimeline() {
        mTimeline = new ArrayList<>(nBars);
        sTimeline = new ArrayList<>(nBars);

        int c, c1=0;
        c = nBarsSound;

        int bar = 0;
        while (bar < nBars) {

            for (int k=0; k<nBarsSound; k++) {
                if (bar + k >= nBars) break;
                mTimeline.add(0);
            }

            for (int k=0; k<nBarsMuted; k++) {
                if (bar + k + nBarsSound >= nBars) break;
                mTimeline.add(1);
            }

            bar = mTimeline.size();
        }

        for (int k=1; k<=nBars; k++) {

            if (speedUpEach == 0) {
                sTimeline.add(0);
            } else {
                if (k % speedUpEach == 0) {
                    sTimeline.add(1);
                } else {
                    sTimeline.add(0);
                }
            }

        }
    }

    public boolean isIdxMuted(int idx) {
        return mTimeline.get(idx) == 1;
    }

    public boolean isSpeedUp(int idx) {
        return sTimeline.get(idx) == 1;
    }

    public int getnBars() {
        return nBars;
    }

    public int getDeltaSpeed() {
        return deltaSpeed;
    }

    @SuppressLint("DefaultLocale")
    public static String[] getSpeedDeltaDisplayValues() {
        int l = speedUpDeltas.length;
        String[] array = new String[l];

        for (int i=0; i<l; i++) {
            array[i] = String.format("%d", speedUpDeltas[i]);
        }

        return array;
    }

    @SuppressLint("DefaultLocale")
    public static String[] getSpeedBarsDisplayValues() {
        int l = speedUpBars.length;
        String[] array = new String[l];

        array[0] = String.format("%d st", speedUpBars[0]);
        array[1] = String.format("%d nd", speedUpBars[1]);
        array[2] = String.format("%d rd", speedUpBars[2]);

        for (int i=3; i<l; i++) {
            array[i] = String.format("%d th", speedUpBars[i]);
        }

        return array;
    }

    @SuppressLint("DefaultLocale")
    public static String[] getMuteBarsDisplayValues() {
        int l = muteBars.length;
        String[] array = new String[l];

        for (int i=0; i<l; i++) {
            array[i] = String.format("%d", muteBars[i]);
        }

        return array;
    }

    @SuppressLint("DefaultLocale")
    public static String[] getPlayBarsDisplayValues() {
        int l = playBars.length;
        String[] array = new String[l];

        for (int i=0; i<l; i++) {
            array[i] = String.format("%d", playBars[i]);
        }

        return array;
    }

    public int getSpeedUpEach() {
        return speedUpEach;
    }

    public int getnBarsMuted() {
        return nBarsMuted;
    }

    public int getnBarsSound() {
        return nBarsSound;
    }
}
