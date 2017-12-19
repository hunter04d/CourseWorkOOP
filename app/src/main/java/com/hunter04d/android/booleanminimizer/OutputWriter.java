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
    private Context mContext;
    private File mFile;
    private FileOutputStream mStream;

    OutputWriter(Context context, String fileName)
    {
        mContext = context;
        InputStream open;
        try
        {
            open =  context.getAssets().open("base.html");
            mFile = new File(context.getFilesDir(), fileName);
            if (mFile.exists())
            {
                if (! mFile.delete())
                {
                    throw new IOException();
                }
            }
            mFile.createNewFile();
            mStream = new FileOutputStream(mFile);
            byte[] buffer = new byte[1024];
            int read;
            while((read = open.read(buffer)) != -1)
            {
                mStream.write(buffer, 0, read);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public OutputWriter append(String s)
    {
        try
        {
            byte[] write = s.getBytes();
            mStream.write(write, 0, write.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }
    public String save()
    {
        try
        {
            byte[] write = ("</body>" + "</html>").getBytes();
            mStream.write(write, 0, write.length);
            mStream.flush();
            mStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "file:///" + mFile.toString();

    }



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
