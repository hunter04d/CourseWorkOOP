package com.hunter04d.android.booleanminimizer;

import android.content.Context;

/**
 * Created by hunter04d on 19.12.2017.
 */

public class ParserErrorHandler
{
    private static final int UNKNOWN = 0,
    INVALID_VARIABLE = 1,
    EXPECTED_OPERATOR = 2,
    MISMATCHED_PARENTHESIS = 3,
    OPERATOR_IN_FRONT = 4,
    OPERATOR_BEFORE_RIGHT_BRACE = 5,
    EVALUATES_TO_CONSTANT = 6,
    OPERATOR_AT_THE_END = 7;

    private static ParserErrorHandler instance;
    private String[] arr;

    private ParserErrorHandler(Context context)
    {
        this.arr = context.getResources().getStringArray(R.array.parser_errors);
    }

    public static ParserErrorHandler getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new ParserErrorHandler(context);
        }
        return instance;
    }

    public String handleError(String e)
    {
        String[] s = e.split(" ");
        int errorCode = Integer.parseInt(s[0]);
        int pos = Integer.parseInt(s[1]);

        switch (errorCode)
        {
            case UNKNOWN:
                return arr[UNKNOWN];
            case INVALID_VARIABLE:
                return arr[UNKNOWN];
            case EXPECTED_OPERATOR:
                return arr[EXPECTED_OPERATOR];
            case MISMATCHED_PARENTHESIS:
                return arr[MISMATCHED_PARENTHESIS];
            case OPERATOR_IN_FRONT:
                return arr[OPERATOR_IN_FRONT];
            case OPERATOR_BEFORE_RIGHT_BRACE:
                return arr[OPERATOR_BEFORE_RIGHT_BRACE];
            case EVALUATES_TO_CONSTANT:
                return arr[EVALUATES_TO_CONSTANT];
            case OPERATOR_AT_THE_END:
                return arr[OPERATOR_AT_THE_END];
            default:
                return arr[UNKNOWN];
        }
    }
}
