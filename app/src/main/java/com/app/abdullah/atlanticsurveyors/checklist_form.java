package com.app.abdullah.atlanticsurveyors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraActivity;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.app.abdullah.atlanticsurveyors.DBHelper.DBHelper;
import com.app.abdullah.atlanticsurveyors.Utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.app.abdullah.atlanticsurveyors.MainActivity.MyPREFERENCES;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Office;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Name;
import static com.app.abdullah.atlanticsurveyors.MainActivity.ID;
import static com.app.abdullah.atlanticsurveyors.MainActivity.Office_site;

public class checklist_form extends HiddenCameraActivity {

    public static final String MyPREFERENCES1 = "MyPrefs";

    HttpParse httpParse = new HttpParse();
    HashMap<String, String> hashMap = new HashMap<>();
    Bitmap rephoto1, rephoto2, rephoto3;
    RadioButton radio1button, radio7button;
    RadioGroup radio1group, radio7group;
    EditText edit1text, edit7text;
    Button submitbtn;
    SharedPreferences sharedpreferences;
    public String idholder, nameholder, officeholder, siteholder;
    public String aholder1, aholder7;
    public String rholder1, rholder7;
    // JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    GPSTracker gps;
    // private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath1 = "";
    private String imageFilePath2 = "";
    private String imageFilePath3 = "",finalResult;


    private static final int REQ_CODE_CAMERA_PERMISSION = 1253;
    private CameraConfig mCameraConfig;
    Bitmap photo1, photo2, photo3, spy;
    // private static final int SELECT_PHOTO = 7777;
    DBHelper dbHelper;

    String lat_holder,lon_holder,date,time;
    ImageView btimgg2, btimgg3, btimgg1;

    private String HttpURLsubmit = "http://www.mucaddam.pk/mobile_app/checklist_mob.php";
    String HttpURL = "http://www.mucaddam.pk/mobile_app/tracking.php";
    //private String HttpURLsubmit = "http://192.168.1.103/Upload/checklist_mob.php";
    //   private static final String TAG_SUCCESS = "success";
    //  private static final String TAG_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_form);




        dbHelper = new DBHelper(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        radio1group = (RadioGroup) findViewById(R.id.first);
        radio7group = (RadioGroup) findViewById(R.id.seventh);

        edit1text = (EditText) findViewById(R.id.remarks1);
        edit7text = (EditText) findViewById(R.id.remarks7);

        submitbtn = (Button) findViewById(R.id.submit);

        btimgg1 = findViewById(R.id.btimgg1);
        btimgg2 = findViewById(R.id.btimgg2);
        btimgg3 = findViewById(R.id.btimgg3);


        mCameraConfig = new CameraConfig()
                .getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();


        btimgg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {

                        Random gen = new Random();
                        int n = 10000;
                        n = gen.nextInt(n);
                        //  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String imageFileName = "IMG_" + n + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                        imageFilePath1 = image.getAbsolutePath();
                        photoFile = image;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(pictureIntent, 1);
                }

            }


        });

        btimgg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {

                        Random gen = new Random();
                        int n = 10000;
                        n = gen.nextInt(n);
                        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String imageFileName = "IMG_" + n + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                        imageFilePath2 = image.getAbsolutePath();
                        photoFile = image;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(pictureIntent, 2);
                }

            }
        });

        btimgg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {

                        Random gen = new Random();
                        int n = 10000;
                        n = gen.nextInt(n);
                        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String imageFileName = "IMG_" + n + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                        imageFilePath3 = image.getAbsolutePath();
                        photoFile = image;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(pictureIntent, 3);
                }

            }
        });

//spy cam permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQ_CODE_CAMERA_PERMISSION);
        }


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            siteholder = null;
        } else {
            siteholder = extras.getString("siteid");
        }


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        officeholder = sharedpreferences.getString(Office_site, "");
        nameholder = sharedpreferences.getString(Name, "");
        idholder = sharedpreferences.getString(ID, "");

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId1 = radio1group.getCheckedRadioButtonId();
                int selectedId7 = radio7group.getCheckedRadioButtonId();

                radio1button = (RadioButton) findViewById(selectedId1);
                radio7button = (RadioButton) findViewById(selectedId7);

                aholder1 = radio1button.getText().toString();
                aholder7 = radio7button.getText().toString();

                rholder1 = edit1text.getText().toString();
                rholder7 = edit7text.getText().toString();

                //new savedata().execute();


                if (rephoto1 == null || rephoto2 == null || rephoto2 == null) {
                    Toast.makeText(checklist_form.this, "Kindly Capture Images", Toast.LENGTH_SHORT).show();
                } else {
                    takePicture();

                }


            }
        });


    }


    //permission
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                startCamera(mCameraConfig);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //spy reult
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onImageCapture(@NonNull File imageFile) {


        // Convert file to bitmap.
        // Do something.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        spy = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);


        dbHelper.addData(idholder, officeholder, siteholder, aholder1, rholder1, aholder7, rholder7, Utils.getBytes(rephoto1), Utils.getBytes(rephoto2), Utils.getBytes(rephoto3), Utils.getBytes(spy));
        uploadImage();
        //Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();

    }

    //show in button
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {


                btimgg1.setImageURI(Uri.parse(imageFilePath1));
                photo1 = ((BitmapDrawable) btimgg1.getDrawable()).getBitmap();
                rephoto1 = Bitmap.createScaledBitmap(photo1, 924, 676, true);
                savegallry(photo1);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                btimgg2.setImageURI(Uri.parse(imageFilePath2));
                photo2 = ((BitmapDrawable) btimgg2.getDrawable()).getBitmap();
                rephoto2 = Bitmap.createScaledBitmap(photo2, 924, 676, true);
                savegallry(photo2);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                btimgg3.setImageURI(Uri.parse(imageFilePath3));
                photo3 = ((BitmapDrawable) btimgg3.getDrawable()).getBitmap();
                rephoto3 = Bitmap.createScaledBitmap(photo3, 924, 676, true);
                savegallry(photo3);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }


    }


    //spy error
    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot Open", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, "Cannot Write", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "No Camera", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //gallery save
    private void savegallry(Bitmap finalbitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/SurveyorsImages");
        myDir.mkdir();
        Random gen = new Random();
        int n = 10000;
        n = gen.nextInt(n);

        String imgnam = "Image-" + n + ".jpg";
        File file = new File(myDir, imgnam);

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            String ImagePath = file.getAbsolutePath();
            out.flush();
            out.close();

            //  Toast.makeText(this, "Your Photo is Saved", Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImage() {
        pDialog = new ProgressDialog(checklist_form.this);
        pDialog.setMessage("Uploading Data...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURLsubmit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");


                            Toast.makeText(checklist_form.this, Response, Toast.LENGTH_SHORT).show();

                            if (Response.equals("Image Uploaded Successfully")) {
                                pDialog.dismiss();
                                Intent intent = new Intent(checklist_form.this, site_visit.class);
                                startActivity(intent);
                                finish();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", idholder);

                params.put("site_id", siteholder);
                params.put("office_id", officeholder);
                params.put("gk_available", aholder1);
                params.put("quantity_stock_tally", aholder7);
                params.put("gk_available_remarks", rholder1);
                params.put("quantity_stock_tally_remarks", rholder7);
                params.put("stk_img", imagetostr(rephoto1));
                params.put("bin_img", imagetostr(rephoto2));
                params.put("reg_img", imagetostr(rephoto3));
                params.put("spy_img", imagetostr(spy));

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySinglenton.getmInstance(checklist_form.this).addTpRequestQue(stringRequest);
    }


    private String imagetostr(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

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


}

