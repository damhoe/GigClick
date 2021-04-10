package com.damhoe.gigclick;

import android.annotation.SuppressLint;

import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Utility {

    public static boolean int2Boolean(int i) {
        return i == 1;
    }

    public static int boolean2Int(boolean b) {
        return b? 1: 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static String date2String(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
        return df.format(date);
    }

    public static String nTracks2String(int n) {
        return String.format(Locale.GERMANY, "%d Tracks" , n);
    }
}
