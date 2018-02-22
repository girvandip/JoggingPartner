package com.example.batere3a.joggingpartner.order;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.batere3a.joggingpartner.R;

import org.json.JSONObject;

public class OrderDetails extends AppCompatActivity implements SensorEventListener {
    private String dataId;
    private String data;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private String orderStatus;

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
                partnerPhone.setText(jsonData.getString("phone_partner"));

                View mainView = findViewById(R.id.order_detail);
                Snackbar snackbar = Snackbar
                        .make(mainView,
                                "Swipe the screen to call partner",
                                Snackbar.LENGTH_LONG);

                snackbar.show();
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
}
