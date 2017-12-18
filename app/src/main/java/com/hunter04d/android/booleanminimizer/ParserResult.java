package com.hunter04d.android.booleanminimizer;

/**
 * Created by Den on 09.11.2017.
 */

public class ParserResult
{
    private String mResult;
    private boolean mHasSucceeded;

    public ParserResult(String str, boolean b)
    {
        mResult = str;
        mHasSucceeded = b;
    }
    public String getResult()
    {
        return mResult;
    }

    public boolean hasSucceeded()
    {
        return mHasSucceeded;
    }




}
