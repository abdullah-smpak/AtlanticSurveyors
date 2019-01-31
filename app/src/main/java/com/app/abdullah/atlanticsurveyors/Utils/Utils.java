package com.app.abdullah.atlanticsurveyors.Utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;

public class Utils {


    public static byte[] getBytes(Bitmap bitmap)
    {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,40,stream);


        return  stream.toByteArray();
    }


}