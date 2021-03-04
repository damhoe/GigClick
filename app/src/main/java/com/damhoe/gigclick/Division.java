package com.damhoe.gigclick;

import android.text.PrecomputedText;

public abstract class Division {

    /* Division class.
    *
    * Contains the definitions of divisions and picture ids.
    * All labels and definitions are done with respect to quarter bar signatures.
    *
    * */

    // codes are constructed by
    // "D" for division
    // "2", "3" binary, ternary
    // _numbers_ note length
    // "d" dotted
    // "b" break

    public static final int D2_4 = 0x01;
    public static final int D2_8_8 = 0x02;
    public static final int D2_8b_8 = 0x03;
    public static final int D2_8_16_16 = 0x10;
    public static final int D2_16_16_8 = 0x11;
    public static final int D2_16_8_16 = 0x12;
    public static final int D2_16_16_16_16 = 0x13;
    public static final int D3_8_8_8 = 0x20;
    public static final int D3_8_8b_8 = 0x21;
    public static final int D3_8b_8b_8 = 0x22;

    public static final String[] BPB = {"1", "2", "3", "4", "5", "6", "7", "8"};
    public static final String[] DIVS = {"2", "4", "8"};

    public String getString(int key) {
        return "D2_8_8b";
    }
}
