package com.example.warpcorecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.warpcorecontroller.ui.lightingconfig.LightingConfigFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LightingConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_config_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LightingConfigFragment.newInstance())
                    .commitNow();
        }
    }

    /** Called when the user touches the button */
    public void updateBrightness(View view) throws IOException {
        new SendHTTPTask().execute();
    }

    private class SendHTTPTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            return send();
        }
    }

    public boolean send(){
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            //String apiUrl = "Http:/192.168.4.1/changeSpeed?delay=50";
            String apiUrl = "Http:/192.168.4.1/changeColor?red=0&blue=255&green=255";

            URL requestUrl = new URL(apiUrl);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.i("res", result.toString());
            if(result.toString().contains("Changed speed")){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return false;
    }
}
