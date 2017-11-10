package com.hunter04d.android.booleanminimizer;

/**
 * Created by Den on 09.11.2017.
 */

public class ParserResult
{
    private String mResult;
    private boolean mHasSucceded;

    public ParserResult(String str, boolean b)
    {
        mResult = str;
        mHasSucceded = b;
    }
    public String getResult()
    {
        return mResult;
    }

    public boolean HasSucceded()
    {
        return mHasSucceded;
    }




}
