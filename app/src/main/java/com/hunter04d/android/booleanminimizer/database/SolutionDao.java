package com.hunter04d.android.booleanminimizer.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hunter04d on 06.12.2017.
 */


@Dao
public interface SolutionDao
{

    @Query("SELECT * FROM solution")
    List<Solution> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSolution(Solution s);

    @Query("DELETE FROM solution")
    void deleteAll();

    @Query("SELECT count(*) FROM solution")
    int getCount();

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void setAll(Solution... solutions);

    @Query("DELETE FROM solution WHERE uid = 1")
    void deleteLast();
}
