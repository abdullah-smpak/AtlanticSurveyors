package com.app.abdullah.atlanticsurveyors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Splash extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    //    startService(new Intent(this, IService.class));

        start = findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
            }

            private void checkConnection() {
                boolean isConnected = ConnectivityReceiver.isConnected();
                test(isConnected);
            }
        });



        
            }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        test(isConnected);
    }

    private void test(boolean isConnected) {
        if (isConnected) {
           Intent intent = new Intent(Splash.this,MainActivity.class);
           startActivity(intent);
           finish();
        } else {
            Toast.makeText(this, "Turn On Wifi or 3G & Login Again", Toast.LENGTH_SHORT).show();
        }
    }
}
