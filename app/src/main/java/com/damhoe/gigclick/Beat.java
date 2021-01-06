package com.damhoe.gigclick;

public class Beat {

    public static final int ACCENT_HIGH = 0x00;
    public static final int ACCENT_MEDIUM = 0x01;
    public static final int ACCENT_LOW = 0x02;
    public static final int ACCENT_OFF = 0x03;

    private int accent;

    public Beat() {
        accent = ACCENT_MEDIUM;
    }

    public void setAccent(int accent) {
        this.accent = accent;
    }

    public int getAccent() {
        return accent;
    }

    public void nextAccent() {
        switch (accent) {
            case ACCENT_HIGH:
                accent = ACCENT_OFF;
                break;
            case ACCENT_MEDIUM:
                accent = ACCENT_HIGH;
                break;
            case ACCENT_LOW:
                accent = ACCENT_MEDIUM;
                break;
            case ACCENT_OFF:
                accent = ACCENT_LOW;
                break;
        }
    }

    public int getDrawableID() {
        switch (accent) {
            case ACCENT_HIGH:
                return R.drawable.beat_accent_high;
            case ACCENT_MEDIUM:
                return R.drawable.beat_accent_medium;
            case ACCENT_LOW:
                return R.drawable.beat_accent_low;
            case ACCENT_OFF:
                return R.drawable.beat_accent_off;
            default:
                return -1;
        }
    }
}
