package com.hunter04d.android.booleanminimizer;

import static j2html.TagCreator.*;
/**
 * Created by hunter04d on 10.11.2017.
 */

public class HtmlBuilder
{
    public String truthTable(String vector)
    {
        return "";
    }
    public static String result(String res)
    {
        String[] parts = res.split("v");
        StringBuilder out = new StringBuilder("`");
        for (String part : parts)
        {
            int count = 0;
            for (char ch : part.toCharArray())
            {

                if (ch == '1')
                {
                    out.append("X").append(count);
                }
                else if(ch == '0')
                {
                    out.append("bar (X").append(count).append(')');
                }
                count++;
            }
            out.append("vv");
        }
        out.append("`");
        return p(out.toString()).render();
    }
}
