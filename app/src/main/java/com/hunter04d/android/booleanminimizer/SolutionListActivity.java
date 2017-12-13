package com.hunter04d.android.booleanminimizer;

import android.support.v4.app.Fragment;

/**
 * Created by hunter04d on 13.12.2017.
 */

public class SolutionListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return SolutionListFragment.newInstance();
    }
}
