package com.android.samll.aidl;

interface ISecondary {
    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
}
