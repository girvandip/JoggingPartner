package com.example.batere3a.joggingpartner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MakeOrderActivity extends AppCompatActivity {

    private static final String TAG = "MakeOrderActivityTag";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private TextView mLatlngTextView;
    private EditText mDateText;
    private EditText mTimeText;
    private EditText mLocationNameEditText;
    private EditText mAddressNameEditText;

    private FirebaseUser user;
    private FirebaseDatabase database;

    private LatLng mLatLng;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("dateText", mDateText.getText().toString().trim());
        savedInstanceState.putString("timeText", mTimeText.getText().toString().trim());
        savedInstanceState.putString("locationText",
                mLocationNameEditText.getText().toString().trim());
        savedInstanceState.putString("addressText",
                mAddressNameEditText.getText().toString().trim());
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        mDateText.setText(savedInstanceState.getString("dateText"));
        mTimeText.setText(savedInstanceState.getString("timeText"));
        mLocationNameEditText.setText(savedInstanceState.getString("locationText"));
        mAddressNameEditText.setText(savedInstanceState.getString("addressText"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mDateText = (EditText) findViewById(R.id.dateText);
        mTimeText = (EditText) findViewById(R.id.timeText);
        mLocationNameEditText = (EditText) findViewById(R.id.mapText);
        mAddressNameEditText = (EditText) findViewById(R.id.addressText);

        mLatlngTextView = (TextView) findViewById(R.id.latlngText);

        database = FirebaseDatabase.getInstance();

        if(isServicesOK()){
            init();
        }

        Bundle bundle = getIntent().getParcelableExtra("bundleFromMaps");
        if (bundle != null) {
            mLatLng = bundle.getParcelable("latLngLocation");
            String mLocationName = bundle.getString("locationName");
            String mAddressName = bundle.getString("addressName");
            mDateText.setText(bundle.getString("dateText"));
            mTimeText.setText(bundle.getString("timeText"));
            if (mLatLng != null) {
                Log.d(TAG,  mLatLng.latitude + " " + mLatLng.longitude);
                //mLatlngTextView.setText("Location: " + mLatLng.latitude + " " + mLatLng.longitude);
            }

            if (mLocationName != null) {
                mLocationNameEditText.setText(mLocationName);
            }

            if (mAddressName != null) {
                mAddressNameEditText.setText(mAddressName);
            }
        }
    }

    /*
    @Override
    public void onResume(){
        super.onResume();
        loadPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("STOPP", "ASDFASDFSADF");
        clearPref();
    }
    */

    public void saveOrderToDatabase(View view) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(MakeOrderActivity.this);
                    URL url = new URL("https://android-544df.firebaseio.com/Orders.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Log.i("ASDF", "MASUK4");
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    //Log.i("ASDF", "MASUK5");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    //Log.i("ASDF", "MASUK6");
                    conn.connect();
                    //Log.i("ASDF", "MASUK3");

                    String json = "";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("runner", user.getDisplayName());
                    jsonObject.put("partner", "");
                    jsonObject.put("date", mDateText.getText().toString().trim());
                    jsonObject.put("time", mTimeText.getText().toString().trim());
                    jsonObject.put("latitude", mLatLng.latitude);
                    jsonObject.put("longitude", mLatLng.longitude);
                    jsonObject.put("location", mLocationNameEditText.getText().toString().trim());
                    jsonObject.put("address", mAddressNameEditText.getText().toString().trim());
                    jsonObject.put("phone_runner", preferences.getString("userPhone", ""));
                    //Log.i("PHONERUNNER", preferences.getString("userPhone", ""));
                    jsonObject.put("phone_partner", "");
                    jsonObject.put("status", "Open");
                    //Log.i("ASDF", "MASUK");
                    Log.i("JSON", jsonObject.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonObject.toString());
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("Error", "ERROR JSON EXCEPTION");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void init(){
        ImageView imageToMap = (ImageView) findViewById(R.id.ic_map);
        imageToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager =
                        (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    displayPromptForEnablingGPS(MakeOrderActivity.this);
                } else {
                    Bundle args = new Bundle();
                    args.putString("dateText", mDateText.getText().toString().trim());
                    args.putString("timeText", mTimeText.getText().toString().trim());
                    Intent intentGoToMap = new Intent
                            (MakeOrderActivity.this, MapsActivity.class);
                    intentGoToMap.putExtra("bundleFromMakeOrder", args);
                    startActivity(intentGoToMap);
                }
            }
        });

        mLocationNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager =
                        (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    displayPromptForEnablingGPS(MakeOrderActivity.this);
                } else {
                    Bundle args = new Bundle();
                    args.putString("dateText", mDateText.getText().toString().trim());
                    args.putString("timeText", mTimeText.getText().toString().trim());
                    Intent intentGoToMap = new Intent
                            (MakeOrderActivity.this, MapsActivity.class);
                    intentGoToMap.putExtra("bundleFromMakeOrder", args);
                    startActivity(intentGoToMap);
                }
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MakeOrderActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog
                            (MakeOrderActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText
                    (this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), getString(R.string.date_picker));
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), getString(R.string.time_picker));
    }

    public void processDatePickerResult(int year, int month, int day) {
        // The month integer returned by the date picker starts counting at 0
        // for January, so you need to add 1 to show months starting at 1.
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        // Assign the concatenated strings to dateMessage.
        String dateMessage = (month_string + "/" + day_string + "/" + year_string);
        mDateText.setText(dateMessage);
        //Toast.makeText(this, getString(R.string.date) + dateMessage, Toast.LENGTH_SHORT).show();
    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        // Convert time elements into strings.
        String hour_string = Integer.toString(hourOfDay);
        String minute_string = Integer.toString(minute);
        // Assign the concatenated strings to timeMessage.
        String timeMessage = (hour_string + ":" + minute_string);
        mTimeText.setText(timeMessage);
        //Toast.makeText(this, getString(R.string.time) + timeMessage, Toast.LENGTH_SHORT).show();
    }

    public void displayPromptForEnablingGPS(final Activity activity)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please turn on your gps to proceed";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}
