package com.hunter04d.android.booleanminimizer;

/**
 * Created by Den on 04.11.2017.
 */

public class NativeLib
{
    // Used to load the 'native-lib' library on application startup.
    static
    {
        System.loadLibrary("native-lib");
    }
    public static native String stringFromJNI(String str);


    public static native String parseExpresion(String expr, int varNum);
}
