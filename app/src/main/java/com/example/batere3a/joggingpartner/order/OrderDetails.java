package com.example.batere3a.joggingpartner.order;

import android.content.Intent;
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

public class OrderDetails extends AppCompatActivity {
    private String dataId;
    private String data;


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

            if (jsonData.getString("status").equals("Progress")) {
                View separator = (View) findViewById(R.id.phone_separator);
                separator.setVisibility(View.VISIBLE);

                LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.partner_contact);
                phoneLayout.setVisibility(LinearLayout.VISIBLE);

                TextView partnerPhone = findViewById(R.id.partner_phone);
                partnerPhone.setText(jsonData.getString("phone_partner"));
            }

            TextView location = findViewById(R.id.order_place);
            location.setText(jsonData.getString("address"));

            TextView dateTime = findViewById(R.id.order_date_time);
            String temp = jsonData.getString("date") + " " + jsonData.getString("time");
            dateTime.setText(temp);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
