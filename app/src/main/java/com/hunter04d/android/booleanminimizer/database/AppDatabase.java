package com.hunter04d.android.booleanminimizer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by hunter04d on 06.12.2017.
 */
@Database(entities = {Solution.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract SolutionDao mSolutionDao();
}
