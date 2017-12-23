package com.hunter04d.android.booleanminimizer;

/**
 * Created by Den on 04.11.2017.
 */

public class NativeLib
{
    static
    {
        System.loadLibrary("native-lib");
    }
    public static native String[] calculateMinimisation(String str, boolean isAllCases);
    public static native DetailedSolutionResult getDetailedResult(String str, String str1);

    public static native String stringOfVarTable(int n, int num_of_vars);
    public static native ParserResult parseExpression(String expr, double varNum);
}
