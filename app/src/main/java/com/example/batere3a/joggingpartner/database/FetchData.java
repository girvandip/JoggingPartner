package com.example.batere3a.joggingpartner.database;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Aldrich on 2/20/2018.
 */

public class FetchData extends AsyncTask<String, Void, String> {
    public TextView view = null;
    private String resource, requestMethod;

    public FetchData(String resource, String requestMethod, TextView view) {
        this.resource = resource;
        this.requestMethod = requestMethod;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... strings) {
        return FirebaseConnector.getResource(resource, requestMethod);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject result = new JSONObject(s);
            view.setText(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
