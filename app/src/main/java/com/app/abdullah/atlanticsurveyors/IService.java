package com.app.abdullah.atlanticsurveyors;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import javax.xml.transform.OutputKeys;

public class IService extends Service {

    private static final String CHANNEL_ID = "Channel_Id";


    public IService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        onTaskRemoved(intent);
        //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();


        startInForeground();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }






    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restart = new Intent(getApplicationContext(), this.getClass());
        restart.setPackage(getPackageName());
        startService(restart);

        super.onTaskRemoved(rootIntent);
    }


    private void startInForeground() {
        int icon = R.mipmap.ic_launcher;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            icon = R.mipmap.ic_launcher;
        }

        Intent notificationIntent = new Intent(this, IService.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("Service")
                .setContentText("Running...");
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Sync Service", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Service Name");

            channel.setSound(null,null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle("Service")
                    .setContentText("Running...")
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(icon)
                    .setSound(null,null)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(121, notification);
    }
}
