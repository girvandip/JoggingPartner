package com.example.batere3a.joggingpartner.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.batere3a.joggingpartner.MainActivity;
import com.example.batere3a.joggingpartner.R;
import com.example.batere3a.joggingpartner.models.ChangeTheme;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    //Used for Pedometer
    private TextView TvSteps;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChangeTheme theme = new ChangeTheme(this);
        theme.change();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        Button BtnStart = (Button) findViewById(R.id.btn_start);
        Button BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                numSteps = 0;
                sensorManager.registerListener(PedometerActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(PedometerActivity.this);
            }
        });
        // fetch data and print it to screen

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }
}
