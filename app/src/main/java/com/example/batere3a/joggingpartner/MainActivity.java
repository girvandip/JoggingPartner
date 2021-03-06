package com.example.batere3a.joggingpartner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.database.FetchData;
import com.example.batere3a.joggingpartner.models.ChangeTheme;
import com.example.batere3a.joggingpartner.notif.MyFirebaseMessageService;
import com.example.batere3a.joggingpartner.order.OrderDetails;
import com.example.batere3a.joggingpartner.pedometer.PedometerActivity;
import com.example.batere3a.joggingpartner.pedometer.StepDetector;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.github.clans.fab.FloatingActionButton;

import com.example.batere3a.joggingpartner.order.PagerAdapter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.example.batere3a.joggingpartner.SensorService;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final String BASE_URL = "https://android-544df.firebaseio.com/";
    private String userData = null;

    //Used for Pedometer
    private TextView TvSteps;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    // service
    private MyFirebaseMessageService firebaseMessageService;
    private Intent firebaseIntent;
    Intent mServiceIntent;
    private SensorService mSensorService;

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private Handler handler;
    private TabLayout tabLayout;
    private TextView result;

    static Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public void clearPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.remove("userName");
        editor.remove("userEmail");
        editor.remove("userPhone");
        editor.commit();
    }

    public TextView getTextViewDummy() {
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChangeTheme theme = new ChangeTheme(this);
        theme.change();
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);

        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }



        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "You got an error !"
                                , Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

     

        com.github.clans.fab.FloatingActionButton fabChat = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.chatButton);
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent
                        (MainActivity.this, ChatActivity.class));
            }
        });

        com.github.clans.fab.FloatingActionButton fabPedometer = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pedometerButton);
        fabPedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent
                        (MainActivity.this, PedometerActivity.class));
            }
        });

        com.github.clans.fab.FloatingActionButton fabOrder = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.makeOrderButton);
        fabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent
                        (MainActivity.this, MakeOrderActivity.class));
                Toast.makeText
                        (MainActivity.this, R.string.introductionMakeOrder,
                                Toast.LENGTH_LONG).show();
            }
        });

        result = findViewById(R.id.result);
        FetchData users = new FetchData("Orders", "GET", result);
        users.execute();
        try {
            userData = users.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create an instance of the tab layout from the view.
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        
        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab()
                .setText("Your Appointments").setIcon(R.drawable.my_orders));
        tabLayout.addTab(tabLayout.newTab()
                .setText("Appointments").setIcon(R.drawable.openorder));
        tabLayout.addTab(tabLayout.newTab()
                .setText(R.string.history).setIcon(R.drawable.history));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Using PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        // get the name
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        String username = preferences.getString("userName", "");
        String id = preferences.getString("userId", "");
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), userData, username, id);
        viewPager.setAdapter(adapter);

        // Setting a listener for clicks.
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.SelectedTab));
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                getSupportActionBar().setTitle(tab.getText());
                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.SelectedTab));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSensorService.userDataString != null) {
            // Create an instance of the tab layout from the view.
            final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

            // Using PagerAdapter to manage page views in fragments.
            // Each page is represented by its own fragment.
            // get the name
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
            String username = preferences.getString("userName", "");
            String id = preferences.getString("userId", "");
            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new PagerAdapter
                    (getSupportFragmentManager(),
                            tabLayout.getTabCount(), mSensorService.userDataString, username, id);
            viewPager.setAdapter(adapter);

            // Setting a listener for clicks.
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.SelectedTab));
            viewPager.addOnPageChangeListener(new
                    TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    getSupportActionBar().setTitle(tab.getText());
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.SelectedTab));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        firebaseMessageService = new MyFirebaseMessageService();
        firebaseIntent = new Intent(this, firebaseMessageService.getClass());
        startService(firebaseIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            mAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            clearPreferences();
            finish();
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
