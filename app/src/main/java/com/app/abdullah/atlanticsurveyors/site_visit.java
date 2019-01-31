package com.app.abdullah.atlanticsurveyors;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.app.abdullah.atlanticsurveyors.MainActivity.Level;
import static com.app.abdullah.atlanticsurveyors.MainActivity.MyPREFERENCES;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Designation;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Office;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Name;
import static com.app.abdullah.atlanticsurveyors.MainActivity.ID;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Zone;

public class site_visit extends AppCompatActivity implements OnItemSelectedListener, LocationListener{

    private Spinner spinnersite;
    private static TextView username;
    private Button sitein, logout;
    String degholder, idholder, offholder, latholder, longholder, siteholder, levelholder, zoneholder;
    private ArrayList<Site> siteslist;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    double latitude, longitude;
    GPSTracker gps;

    List<String> site_id = new ArrayList<String>();
    List<String> site_name = new ArrayList<String>();
    List<String> sites = new ArrayList<String>();

    String HttpURLshowsites = "http://www.mucaddam.pk/mobile_app/view_site.php";

    String HttpURLsitein = "http://www.mucaddam.pk/mobile_app/site_in_edit.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_visit);

        username = (TextView) findViewById(R.id.Username);
        spinnersite = (Spinner) findViewById(R.id.sitelist);
        sitein = (Button) findViewById(R.id.sitein);
        logout = (Button) findViewById(R.id.logoutbtn);

        gps = new GPSTracker(site_visit.this);
        if(gps.canGetLocation()){

        }else{
            gps.showSettingsAlert();
        }

        if (checkPlayServices()) {
            startFusedLocation();
            registerRequestUpdate(this);
        }

        siteslist =new ArrayList<Site>();

        spinnersite.setOnItemSelectedListener(this);

        //sharedprefrences
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username.setText("Hello! " + sharedpreferences.getString(Name, ""));
        degholder = sharedpreferences.getString(Designation, "");
        levelholder = sharedpreferences.getString(Level, "");
        zoneholder = sharedpreferences.getString(Zone, "");
        idholder = sharedpreferences.getString(ID, "");
        offholder = sharedpreferences.getString(Office, "");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(site_visit.this);
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

        sitein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gps = new GPSTracker(site_visit.this);
                if(gps.canGetLocation()) {


                    if (latholder == "0.0" || longholder == "0.0") {
                        Toast.makeText(site_visit.this, "Please Check location is 'ON' or 'OFF'.. ", Toast.LENGTH_LONG).show();
                    } else {

                        new invisit().execute();

                    }
                }else{
                    gps.showSettingsAlert();
                }
            }
        });

        new showsites().execute();

    }

    class showsites extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(site_visit.this);
            pDialog.setMessage("Fetching Sites..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UserId", idholder));
            params.add(new BasicNameValuePair("useroffice", offholder));
            params.add(new BasicNameValuePair("userdesignation", degholder));
            params.add(new BasicNameValuePair("userlevel", levelholder));
            params.add(new BasicNameValuePair("userzone", zoneholder));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(HttpURLshowsites, "POST", params);

            // check log cat fro responseLog.d("Create Response", json.toString());

            if (json.toString() != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json.toString());
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray("Sites");

                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Site cat = new Site(catObj.getInt("site_id"),
                                    catObj.getString("site_name"));
                            siteslist.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }

    private void populateSpinner() {

        for (int i = 0; i < siteslist.size(); i++) {
            site_name.add((siteslist.get(i).getName()) + " ["+ (String.valueOf(siteslist.get(i).getId()))+"]");
        }


        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, site_name);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnersite.setAdapter(spinnerAdapter);


    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        siteholder = spinnersite.getSelectedItem().toString();




    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    //************* FOR SITE IN **************

    class invisit extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(site_visit.this);
            pDialog.setMessage("Marking Please Wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UserId", idholder));
            params.add(new BasicNameValuePair("useroffice", offholder));
            params.add(new BasicNameValuePair("longitude", longholder));
            params.add(new BasicNameValuePair("latitude", latholder));
            params.add(new BasicNameValuePair("site", siteholder));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(HttpURLsitein, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    Intent i = new Intent(site_visit.this, checklist_form.class);
                    i.putExtra("siteid", siteholder);
                    startActivity(i);

                    return json.getString(TAG_MESSAGE);


                } else {

                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Toast.makeText(site_visit.this, "exception"+e, Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onPostExecute(String message1) {
            pDialog.dismiss();
            if (message1 != null){
                Toast.makeText(site_visit.this, message1, Toast.LENGTH_LONG).show();

            }
        }

    }

    protected void onStop() {
        stopFusedLocation();
        super.onStop();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(),
                        "This device is supported. Please download google play services", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }
                        @Override
                        public void onConnected(Bundle connectionHint) {
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }
    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // every second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 10);
    }
    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }
    @Override
    public void onLocationChanged(Location location) {
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());

        latholder = ""+getFusedLatitude();
        longholder = ""+getFusedLongitude();

    }
    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }
    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }
    public double getFusedLatitude() {
        return fusedLatitude;
    }


    public double getFusedLongitude() {

        return fusedLongitude;

    }



}
