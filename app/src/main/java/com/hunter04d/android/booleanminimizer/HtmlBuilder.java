package com.hunter04d.android.booleanminimizer;

import j2html.tags.ContainerTag;

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
    public static String result(String res, String[] varNames)
    {
        if (res.isEmpty())
        {
            return p("`0`").render();
        }
        String[] parts = res.split("v");
        StringBuilder out = new StringBuilder("`");
        for (String part : parts)
        {
            int count = 0;
            if (parts.length != 1)
            {
                out.append('(');
            }
            for (char ch : part.toCharArray())
            {

                if (ch == '1')
                {
                    out.append(varNames[count]).append("*");
                }
                else if(ch == '0')
                {
                    out.append("bar(").append(varNames[count]).append(')').append("*");
                }
                count++;
               // out.append("᠎"); TODO: complain at mathjax
            }
            out.deleteCharAt(out.length()-1);
            if (parts.length != 1)
            {
                out.append(")` ∨ `");
            }
        }
        if (parts.length != 1)
        {
            out.delete(out.length()-4, out.length());
        }
        else
        {
            out.append('`');
        }
        return p(out.toString()).render();
    }
    public static String vector(String vector)
    {
        return h6("Function vector:").render() + p(vector).render();
    }
}
