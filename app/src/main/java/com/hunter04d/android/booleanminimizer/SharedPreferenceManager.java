package com.hunter04d.android.booleanminimizer;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreferenceManager
{
    private static String sVarNames = "VarNames";

    public static String[] getVarNames(Context context)
    {
        String s = PreferenceManager.getDefaultSharedPreferences(context).getString(sVarNames, "X1 X2 X3 X4 X5 X6 X7 X8");
        return s.split(" ");
    }
}