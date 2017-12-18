package com.hunter04d.android.booleanminimizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Den on 18.12.2017.
 */

public class WebViewActivity extends SingleFragmentActivity
{
    public static final String EXTRA_VECTOR = "vector";
    public static final String EXTRA_FUNCTION = "function";
    public static final String EXTRA_IS_EXPR_MODE = "expr_mode";

    @Override
    protected Fragment createFragment()
    {
        return WebViewFragment.newInstance();
    }
}
