package com.hunter04d.android.booleanminimizer;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return BooleanMinimizerFragment.newInstance();
    }
}
