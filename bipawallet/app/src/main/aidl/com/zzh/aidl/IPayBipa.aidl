// IPayBipa.aidl
package com.zzh.aidl;

// Declare any non-default types here with import statements

interface IPayBipa {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    String getValue();
    void setValue(String value);
}
