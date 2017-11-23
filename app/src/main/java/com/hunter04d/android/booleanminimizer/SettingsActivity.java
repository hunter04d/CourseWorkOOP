package com.hunter04d.android.booleanminimizer;

import android.support.v4.app.Fragment;


public class SettingsActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return SettingsFragment.newInstance();
    }


    @Override
    public void onBackPressed()
    {
        for (Fragment f : getSupportFragmentManager().getFragments())
        {
            if (f instanceof  OnBackPressedListener)
            {
                ((OnBackPressedListener)f).onBackPressed();
            }
        }
        super.onBackPressed();
    }

    public interface OnBackPressedListener
    {
        void onBackPressed();
    }
}
