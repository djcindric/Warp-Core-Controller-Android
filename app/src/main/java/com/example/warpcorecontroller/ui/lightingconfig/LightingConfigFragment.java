package com.example.warpcorecontroller.ui.lightingconfig;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.warpcorecontroller.LightingConfigActivity;
import com.example.warpcorecontroller.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LightingConfigFragment extends Fragment {

    private LightingConfigViewModel mViewModel;

    public static LightingConfigFragment newInstance() {
        return new LightingConfigFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lighting_config_fragment, container, false);

        Spinner spinner = (Spinner) v.findViewById(R.id.opModeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.op_mode_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button brightnessButton = (Button) v.findViewById(R.id.brightnessButton);
        BrightnessListener brightnessListener=new BrightnessListener();
        brightnessButton.setOnClickListener(brightnessListener);

        Button delayButton = (Button) v.findViewById(R.id.delayButton);
        DelayListener delayListener=new DelayListener();
        delayButton.setOnClickListener(delayListener);

        Button opModeButton = (Button) v.findViewById(R.id.opModeButton);
        OpModeListener opModeListener=new OpModeListener();
        opModeButton.setOnClickListener(opModeListener);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LightingConfigViewModel.class);
        // TODO: Use the ViewModel
    }

    public class BrightnessListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            EditText brightnessText   = (EditText) getView().findViewById(R.id.brightnessInput);
            String newBrightness = brightnessText.getText().toString();

            //new SendHTTPTask().execute("Http:/192.168.4.1/changeColor?red=255&blue=0&green=0");

            new SendHTTPTask().execute("Http:/192.168.4.1/changeBrightness?amount=" + newBrightness);
        }
    }

    public class DelayListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            EditText delayText   = (EditText) getView().findViewById(R.id.delayInput);
            String newDelay = delayText.getText().toString();
            new SendHTTPTask().execute("Http:/192.168.4.1/changeSpeed?delay=" + newDelay);
        }
    }

    public class OpModeListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            Spinner spinner = (Spinner)getView().findViewById(R.id.opModeSpinner);
            int position = spinner.getSelectedItemPosition();
            new SendHTTPTask().execute("Http:/192.168.4.1/changeOpMode?opMode=" + position);
        }
    }

    private class SendHTTPTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return send(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show(); //Toast the HTTP response
        }
    }

    //Open connection to URL supplied and return the HTTP response
    public String send(String url){
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.i("res", result.toString());

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return "";
    }
}
