package com.hunter04d.android.booleanminimizer;

import android.support.v4.app.Fragment;


public class SettingsActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return SettingsFragment.newInstance();
    }
}
