package com.app.abdullah.atlanticsurveyors;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundService extends Service {
    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // do your jobs here



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //write your code here to be executed after 1 second


                Toast.makeText(BackgroundService.this, "Please Check location is 'ON' or 'OFF'.. ", Toast.LENGTH_LONG).show();
            }
        }, 10000); // after 10 seconds delay










        return super.onStartCommand(intent, flags, startId);
    }
}
