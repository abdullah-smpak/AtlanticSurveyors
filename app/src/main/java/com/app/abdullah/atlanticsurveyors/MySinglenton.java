package com.app.abdullah.atlanticsurveyors;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import  com.android.volley.toolbox.Volley;


public class MySinglenton {


    private static  MySinglenton mInstance;
    private RequestQueue requestQueue;
    private  static Context mctx;





    private MySinglenton(Context context)
    {


        mctx = context;
        requestQueue = getRequestQueue();

    }

    private RequestQueue getRequestQueue()
    {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        return requestQueue;
    }

    public static synchronized  MySinglenton getmInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance = new MySinglenton(context);
        }
        return mInstance;

    }

    public <T> void addTpRequestQue(Request<T> request)
    {
        getRequestQueue().add(request);
    }

}
