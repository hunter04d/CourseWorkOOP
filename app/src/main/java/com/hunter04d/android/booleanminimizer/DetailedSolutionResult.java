package com.hunter04d.android.booleanminimizer;

/**
 * Created by Den on 24.12.2017.
 */

public class DetailedSolutionResult
{
    private String mCore;
    private String[] mRest;
    private String[] mTables;
    private String[] mResults;
    private boolean mHasSucceeded;
    public DetailedSolutionResult(String core, String[] rest, String[] tables, String[] results, boolean hasSucceeded)
    {
        mCore = core;
        mRest = rest;
        mTables = tables;
        mResults = results;
        mHasSucceeded = hasSucceeded;
    }
    public DetailedSolutionResult()
    {
        mHasSucceeded = false;
    }
    public String getCore()
    {
        return mCore;
    }

    public String[] getRest()
    {
        return mRest;
    }

    public String[] getTables()
    {
        return mTables;
    }

    public String[] getResults()
    {
        return mResults;
    }
    public boolean hasSucceeded()
    {
        return mHasSucceeded;
    }
}
