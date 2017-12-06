package com.hunter04d.android.booleanminimizer.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

/**
 * Created by hunter04d on 06.12.2017.
 */

public class SolutionsManager
{
    private static SolutionsManager sSolutionsManager;
    private AppDatabase mDb;
    private int mCount;

    private SolutionsManager(Context context)
    {
        mDb = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "solutiondatabase").build();
        mCount = mDb.mSolutionDao().getCount();

    }

    public SolutionsManager instance(Context context)
    {
        if (sSolutionsManager == null)
        {
            sSolutionsManager = new SolutionsManager(context);
    }
        return sSolutionsManager;
    }
    public List<Solution> getAllSolutions()
    {
       return mDb.mSolutionDao().getAll();
    }
    public void insertSolution(Solution solution)
    {
        if (getSolutionCount() == 100)
        {
            List<Solution> all = mDb.mSolutionDao().getAll();
            all.remove(0);
            for (int i = 0 ; i < all.size();++i)
            {
                all.get(i).setUid(i+1);
            }
            mDb.mSolutionDao().setAll();
        }
        mDb.mSolutionDao().insertSolution(solution);
    }
    public int getSolutionCount()
    {
        return mDb.mSolutionDao().getCount();
    }
}
