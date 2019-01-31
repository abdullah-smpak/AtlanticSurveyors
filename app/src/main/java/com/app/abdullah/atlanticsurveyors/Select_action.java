package com.app.abdullah.atlanticsurveyors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import br.com.safety.locationlistenerhelper.core.LocationTracker;
import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

import static com.app.abdullah.atlanticsurveyors.MainActivity.MyPREFERENCES;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Name;
import static com.app.abdullah.atlanticsurveyors.MainActivity.ID;

import static com.app.abdullah.atlanticsurveyors.MainActivity.Office;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Office_site;


public class Select_action extends AppCompatActivity implements LocationListener {

    private static Button attendence;
    HttpParse httpParse = new HttpParse();

    private static Button site;
    private static Button logout;
    private static TextView username;
    SharedPreferences sharedpreferences;
    GPSTracker gps;
    private DatabaseReference mDatabase;
    String lat_holder,lon_holder,date,time;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    String HttpURL = "http://www.mucaddam.pk/mobile_app/tracking.php";
    HashMap<String, String> hashMap = new HashMap<>();
    String finalResult;
    String latholder, longholder, idholder, officeholder;

    //String HttpURLin = "http://www.mucaddam.pk/mobile_app/save_location.php";
    String HttpURLin = "http://192.168.1.129/Upload/att_in.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    JSONParser jsonParser = new JSONParser();
    private LocationTracker locationTracker;
    public String newlat,newlon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);





        attendence = (Button) findViewById(R.id.att);
        site = (Button) findViewById(R.id.site);
        logout = (Button) findViewById(R.id.logoutbtn);
        username = (TextView) findViewById(R.id.Username);




        startGettingLocations();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        username.setText("Hello! " + sharedpreferences.getString(Name, ""));
        idholder = sharedpreferences.getString(ID, "");
        officeholder = sharedpreferences.getString(Office, "");

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gps = new GPSTracker(Select_action.this);
                                if (gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    String lat = String.valueOf(latitude);
                                    String lon = String.valueOf(longitude);

                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                                    date = mdformat.format(calendar.getTime());

                                    SimpleDateFormat mdformat1 = new SimpleDateFormat("HH:mm:ss");
                                    time = mdformat1.format(calendar.getTime());



                                    SendLoc(idholder,lat, lon,date,time);
                                    LocationData locationData = new LocationData(latitude, longitude,date,time,sharedpreferences.getString(Name, ""));
                                    mDatabase.child("location").child(idholder).setValue(locationData);




                                } else {
                                    gps.showSettingsAlert();
                                }



                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();




/*        Intent in = new Intent("my.action");
        in.putExtra("state", sharedpreferences.getString(Name, ""));
        sendBroadcast(in);

        locationTracker = new LocationTracker("my.action")
                .setInterval(1000)
                .setGps(true)
                .setNetWork(false)
                .start(getBaseContext(),this);*/










        attendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Select_action.this, user_attandence.class);

                startActivity(intent);

            }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Select_action.this, site_visit.class);

                startActivity(intent);

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Select_action.this);
                builder.setCancelable(false);
                builder.setMessage("Do you want to Exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
       String date = mdformat.format(calendar.getTime());

        SimpleDateFormat mdformat1 = new SimpleDateFormat("HH:mm:ss");
       String time = mdformat1.format(calendar.getTime());


        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(),date,time,sharedpreferences.getString(Name, ""));
        mDatabase.child("location").child(idholder).setValue(locationData);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;// Distance in meters
        long MIN_TIME_BW_UPDATES = 100;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {


                    lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);



            } else if (isNetwork) {
                // from Network Provider



                    lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);



            }
        } else {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
        }
    }
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS disabled!");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Enable GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    private void SendLoc(String user_id ,String lat_holder, String lon_holder, String date,String time) {

        class SendLocClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                // progressDialog = ProgressDialog.show(Driver_Reg.this, "Registering Driver", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                // Toast.makeText(checklist_form.this, "Send", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("user_id", params[0]);
                hashMap.put("lat", params[1]);
                hashMap.put("lon", params[2]);
                hashMap.put("date", params[3]);
                hashMap.put("time", params[4]);


                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }


        }
        SendLocClass sendLocClass = new SendLocClass();

        sendLocClass.execute(user_id,lat_holder, lon_holder,date,time);

    }

  /*  public class LocationReceiver extends BroadcastReceiver  {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && intent.getAction().equals("my.action")) {
                Location locationData = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
               // Log.d("Location: ", );

                Toast.makeText(context, "Latitude: " + locationData.getLatitude() + "Longitude:" + locationData.getLongitude(), Toast.LENGTH_SHORT).show();
                //send your call to api or do any things with the of location data
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTracker.stopLocationService(this);
    }*/



}
