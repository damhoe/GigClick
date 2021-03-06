package com.damhoe.gigclick;

import android.util.Log;

import java.util.Locale;

public class Tempo {

    public final static double DEFAULT_BPM = 120.; // max > default > min
    public static final int MAX_BPM = 280;
    public static final int MIN_BPM = 40;

    double bpm;
    String label;

    public Tempo() {
        setBpm(DEFAULT_BPM);
    }

    public Tempo(double bpm) {
        boolean b = setBpm(bpm);
        if (!b) {
            setBpm(DEFAULT_BPM);
        }
    }

    public boolean setBpm(double bpm) {
        boolean b = true;
        this.bpm = bpm;

        if ((int) this.bpm > MAX_BPM) {
            b = false;
            this.bpm = MAX_BPM;
        } else if ((int) this.bpm < MIN_BPM) {
            b = false;
            this.bpm = MIN_BPM;
        }

        updateLabel();
        return b;
    }

    public double getBpm() {
        return bpm;
    }

    public String getBpmText() {
        return String.format(Locale.GERMANY, "%3.0f", bpm);
    }

    public String getLabel() {
        return label;
    }

    /** the label depends on the bpm */
    private void updateLabel() {
        // see: https://de.wikipedia.org/wiki/Tempo_(Musik)
        if (this.bpm >= MIN_BPM && bpm < 60) label = "Largo";
        else if (bpm >= 60 && bpm < 66) label = "Larghetto";
        else if (bpm >= 66 && bpm < 76) label = "Adagio";
        else if (bpm >= 76 && bpm < 108) label = "Andante";
        else if (bpm >= 108 && bpm < 120) label = "Moderato";
        else if (bpm >= 120 && bpm < 168) label = "Allegro";
        else if (bpm >= 168 && bpm < 200) label = "Presto";
        else if (bpm >= 200 && bpm <= MAX_BPM) label = "Prestissimo";
    }

    @Override
    public String toString() {
        return String.format(Locale.GERMANY,"%3.0f", bpm);
    }

}
