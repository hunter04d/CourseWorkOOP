package com.hunter04d.android.booleanminimizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceManager
{
    private static String sVarNames = "VarNames";

    @NonNull
    public static String[] getVarNames(Context context)
    {
        String s = PreferenceManager.getDefaultSharedPreferences(context).getString(sVarNames, "X1 X2 X3 X4 X5 X6 X7 X8");
        return s.split(" ");
    }

    public static HashMap<String, String> getVarNamesMap(Context context)
    {
        String s = PreferenceManager.getDefaultSharedPreferences(context).getString(sVarNames, "X1 X2 X3 X4 X5 X6 X7 X8");
        String[] sa = s.split(" ");
        HashMap<String, String> hm = new HashMap<>(8);
        for (int i = 0; i < 8; ++i)
        {
            hm.put(sa[i], "X" + (i+1));
        }
        return hm;
    }
    public static void setVarNames(Context context, String s)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(sVarNames, s).apply();
    }
}
