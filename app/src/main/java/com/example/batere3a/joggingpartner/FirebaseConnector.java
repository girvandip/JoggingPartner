package com.example.batere3a.joggingpartner;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.spec.ECField;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Aldrich on 2/20/2018.
 */

public class FirebaseConnector {
    // URI
    private static final String BASE_URL = "https://android-544df.firebaseio.com/";


    public FirebaseConnector() {
    }

    static String getResource(String resource, String requestMethod) {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONResult = null;

        try {
            Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(resource + ".json")
                    .build();

            Log.d("url : ", builtURI.toString()); // test

            URL url = new URL(builtURI.toString());
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.connect();

            // Parse
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JSONResult = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return JSONResult;
    }
}
