package com.damhoe.gigclick;

public interface INotifyListener {
    void onNotifyAngle(double angle);
    void onNotifyMillis(long millis);
    void onNotifyTouch(int action);
}
