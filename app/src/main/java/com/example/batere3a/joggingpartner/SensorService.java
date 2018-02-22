package com.example.batere3a.joggingpartner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.example.batere3a.joggingpartner.database.FetchData;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    private static final String TAG = "SensorServiceTag";
    public int counter=0;

    private boolean loopingFetchData;
    private String userDataJson;
    private Thread thread;
    private TextView dummyTextView;

    private Context ctxSensorService;
    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //SharedPreferences prefs = MainActivity.ctx.getSharedPreferences
        //        ("serviceRunning", MainActivity.ctx.MODE_PRIVATE);
        //counter = prefs.getInt("counter", 0);
        getOrderDataService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(".RestartSensor");
        sendBroadcast(broadcastIntent);
        //stoptimertask();
        /*
        try {
            SharedPreferences prefs= getSharedPreferences("serviceRunning", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("counter", counter);
            editor.apply();
            //Long.i("MoveMore", "Saving readings to preferences");
        } catch (NullPointerException e) {
            Log.e(TAG, "error saving: are you testing?" +e.getMessage());
            e.printStackTrace();
        }
        */
    }

    public void getOrderDataService() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loopingFetchData = true;
                while (loopingFetchData) {
                    try {
                        FetchData users = new FetchData("Orders", "GET", dummyTextView);
                        users.execute();
                        try {
                            userDataJson = users.get();
                            Log.i("ASDFASDF", userDataJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void stopGetOrderDataService() {

    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}