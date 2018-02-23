package com.example.batere3a.joggingpartner.order;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.batere3a.joggingpartner.MainActivity;
import com.example.batere3a.joggingpartner.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class OrderDetails extends AppCompatActivity implements SensorEventListener {
    private String dataId;
    private String data;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private String orderStatus;
    private String GCMToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = getIntent();
        this.data = intent.getStringExtra("data");
        this.dataId = intent.getStringExtra("order_id");
        try {
            JSONObject jsonData = new JSONObject(data);

            TextView result = findViewById(R.id.data_intent);
            result.setText(data);

            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            String title = "ID - " + dataId.substring(1);
            toolbar.setTitle(title);

            // bind the data
            TextView status = findViewById(R.id.order_status);
            status.setText(jsonData.getString("status"));
            this.orderStatus = jsonData.getString("status");

            if (jsonData.getString("status").equals("Progress")) {
                View separator = (View) findViewById(R.id.phone_separator);
                separator.setVisibility(View.VISIBLE);

                LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.partner_contact);
                phoneLayout.setVisibility(LinearLayout.VISIBLE);

                TextView partnerPhone = findViewById(R.id.partner_phone);
                String phone = null;
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(OrderDetails.this);
                if(jsonData.getString("partner")
                        .equals(preferences.getString("userName", ""))){
                    phone = jsonData.getString("phone_runner"); // user as the partner
                } else {
                    phone = jsonData.getString("phone_partner"); // user as the runner
                }
                partnerPhone.setText(phone);

                View mainView = findViewById(R.id.order_detail);
                Snackbar snackbar = Snackbar
                        .make(mainView,
                                "Swipe the screen to call partner",
                                Snackbar.LENGTH_LONG);

                snackbar.show();

                Button acceptButton = findViewById(R.id.accept_button);
                acceptButton.setVisibility(Button.GONE);

                FloatingActionButton chatButton = findViewById(R.id.chat_button);
                chatButton.setVisibility(FloatingActionButton.VISIBLE);
            }

            TextView location = findViewById(R.id.order_place);
            location.setText(jsonData.getString("address"));

            TextView dateTime = findViewById(R.id.order_date_time);
            String temp = jsonData.getString("date") + " " + jsonData.getString("time");
            dateTime.setText(temp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        Button acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    saveOrderToDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(OrderDetails.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
//                    InstanceID iid = InstanceID.getInstance(OrderDetails.this);
//                    String authorizedEntity = "82905626474"; // Project id from Google Developer Console
//                    GCMToken = iid.getToken(getString(R.string.gcm_defaultSenderId),
//                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    GCMToken = FirebaseInstanceId.getInstance().getToken();

                    Log.d("GCM token id", GCMToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float distance = sensorEvent.values[0];
        if (distance <= 1.0 && orderStatus.equals("Progress")) {
            // get the number
            TextView phoneView = findViewById(R.id.partner_phone);
            String phone = "tel:" + phoneView.getText();

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(phone));
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void saveOrderToDatabase() throws IOException {
        // TODO: push the GCM token
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(OrderDetails.this);
                    String api = "https://android-544df.firebaseio.com/Orders/" + dataId + ".json";
                    URL url = new URL(api);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("PATCH");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    String json = "";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", "Progress");
                    jsonObject.put("partner",
                            preferences.getString("userName", ""));
                    jsonObject.put("phone_partner",
                            preferences.getString("userPhone", ""));
                    jsonObject.put("gcm_token_partner", GCMToken);
                    Log.i("order details", jsonObject.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonObject.toString());
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());

                    conn.disconnect();


                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + dataId);
                    /////////////////////////////
                    JSONObject message = new JSONObject();
                    message.put("to", "/topics/" + dataId);
                    message.put("data", new JSONObject()
                            .put("title", "Notification")
                            .put("message", "You found a partner!")
                    );

                    api = "https://fcm.googleapis.com/fcm/send";
                    url = new URL(api);
                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization",
                            "key=AAAAE02Pd2o:APA91bE5vf21y1ltyNMWlgHFoUKhVmTWb1lyPGE_MOjkLsOqyEOCfuccGdxO0S41_CIlWg0JGoqum4lYRVrfBSwIWDDUaHhwwZK2LeZSvkL1n7eXD0rj1A9Hkxq97GzFFVaMT1JEJgQe");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    json = message.toString();

                    os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(json);
                    os.flush();
                    os.close();
                    Log.i("FIRB STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("FIRB MSG", conn.getResponseMessage());
                    Log.d("message", json);

                    conn.disconnect();

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + dataId);

                } catch (Exception e) {
                    Log.e("Error", "ERROR JSON EXCEPTION");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
