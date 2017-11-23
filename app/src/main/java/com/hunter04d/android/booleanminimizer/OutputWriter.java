package com.hunter04d.android.booleanminimizer;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Den on 23.11.2017.
 */

public class OutputWriter
{
    public static String writeToBaseHTML(String s, Context context)
    {
        InputStream open;
        try
        {
            open =  context.getAssets().open("base.html");
            File file = new File(context.getFilesDir(), "res.html");
            if (file.exists())
            {
                if (file.delete() == false)
                {
                    throw new IOException();
                }
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while((read = open.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }
            byte[] write = (s +"</body>" + "</html>").getBytes();
            out.write(write, 0, write.length);
            open.close();
            out.flush();
            out.close();
            return "file:///" + file.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
