package com.app.abdullah.atlanticsurveyors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

public class LocationReciver extends BroadcastReceiver {
    LocationData ld = new LocationData();
    public String name1 = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getExtras().getString("state");

        if (null != intent && intent.getAction().equals("my.action")) {
            Location locationData = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
            LocationData ld = new LocationData();
            name1= state;
            Toast.makeText(context, name1, Toast.LENGTH_SHORT).show();
            String state1 = intent.getExtras().getString("state");
            state = state1;
            //send your call to api or do any things with the of location data
        }
    }
}
