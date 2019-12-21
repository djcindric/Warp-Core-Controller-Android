package com.example.warpcorecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    String ssid = "";
    String password = "";

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ssid = sharedPref.getString(SettingsActivity.KEY_PREF_SSID, "Warp Core");
        password = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "jasmineislovins");

        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_button) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the button */
    public void connectToWarpCore(View view) throws IOException {
        Context context = getApplicationContext();

        //Switch to "main" activity
        Intent intent = new Intent(this, LightingConfigActivity.class);
        startActivity(intent);

        //new RetrieveFeedTask().execute();
    }

    private class RetrieveFeedTask extends AsyncTask {

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
