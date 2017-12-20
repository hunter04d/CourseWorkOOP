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
    public static native String[] calculateMinification(String str, boolean isAllCases);
    public static native String[] getTablesHtml(String str, String str1);

    public static native String stringOfVarTable(int n, int num_of_vars);
    public static native ParserResult parseExpresion(String expr, double varNum);
}
