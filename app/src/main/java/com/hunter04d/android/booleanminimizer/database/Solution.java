package com.hunter04d.android.booleanminimizer.database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hunter04d on 06.12.2017.
 */

@Entity
public class Solution
{

    Solution(int uid,String expression, String linearResult, String varNames)
    {
        this.uid = uid;
        this.expression = expression;
        this.linearResult = linearResult;
        this.varNames = varNames;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression(String expression)
    {
        this.expression = expression;
    }

    public String getLinearResult()
    {
        return linearResult;
    }

    public void setLinearResult(String linearResult)
    {
        this.linearResult = linearResult;
    }

    public String getVarNames()
    {
        return varNames;
    }

    public void setVarNames(String varNames)
    {
        this.varNames = varNames;
    }

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }


    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "expression")
    private String expression;


    @ColumnInfo(name = "linear_result")
    private String linearResult;

    @ColumnInfo(name = "var_names")
    private String varNames;
}
