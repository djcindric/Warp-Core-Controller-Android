package com.example.warpcorecontroller.ui.lightingconfig;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LightingConfigFragment extends Fragment {

    private LightingConfigViewModel mViewModel;

    private int[] previewColor = {255, 255, 255, 255}; //ARGB color value of white

    public static LightingConfigFragment newInstance() {
        return new LightingConfigFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lighting_config_fragment, container, false);

        Spinner opModeSpinner = (Spinner) v.findViewById(R.id.opModeSpinner);
        ArrayAdapter<CharSequence> opModeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.op_mode_values, android.R.layout.simple_spinner_item);
        opModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opModeSpinner.setAdapter(opModeAdapter);

        Spinner colorModeSpinner = (Spinner) v.findViewById(R.id.colorModeSpinner);
        ArrayAdapter<CharSequence> colorModeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.color_mode_values, android.R.layout.simple_spinner_item);
        colorModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorModeSpinner.setAdapter(colorModeAdapter);

        Button brightnessButton = (Button) v.findViewById(R.id.brightnessButton);
        BrightnessListener brightnessListener=new BrightnessListener();
        brightnessButton.setOnClickListener(brightnessListener);

        Button delayButton = (Button) v.findViewById(R.id.delayButton);
        DelayListener delayListener=new DelayListener();
        delayButton.setOnClickListener(delayListener);

        Button opModeButton = (Button) v.findViewById(R.id.opModeButton);
        OpModeListener opModeListener=new OpModeListener();
        opModeButton.setOnClickListener(opModeListener);

        Button colorButton = (Button) v.findViewById(R.id.colorModeButton);
        ColorModeListener colorModeListener=new ColorModeListener();
        colorButton.setOnClickListener(colorModeListener);

        AlphaTileView  colorPreview = (AlphaTileView ) v.findViewById(R.id.warpColorPreview);
        ColorPreviewListener colorPreviewListener=new ColorPreviewListener();
        colorPreview.setOnClickListener(colorPreviewListener);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LightingConfigViewModel.class);
    }

    public class BrightnessListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            EditText brightnessText   = (EditText) getView().findViewById(R.id.brightnessInput);
            String newBrightness = brightnessText.getText().toString();
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

    //Handle clicks on op mode update button
    public class OpModeListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            //Get the position of currently selected op mode (Pulse, solid etc)
            Spinner spinner = (Spinner)getView().findViewById(R.id.opModeSpinner);
            int position = spinner.getSelectedItemPosition();

            //Send HTTP request to update op mode
            new SendHTTPTask().execute("Http:/192.168.4.1/changeOpMode?opMode=" + position);
        }
    }

    //Handle clicks on color mode update button
    public class ColorModeListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            //Get the position of currently selected color option (primary, secondary etc)
            Spinner spinner = (Spinner)getView().findViewById(R.id.colorModeSpinner);
            int position = spinner.getSelectedItemPosition();

            //Populate RGB from saved ARGB color (Alpha not used)
            String red = "" + previewColor[1];
            String green = "" + previewColor[2];
            String blue = "" + previewColor[3];

            //Base URL for change color endpoint with no args
            String baseURL = "Http:/192.168.4.1/changeColor";

            String finalURL = baseURL + "?whichLight=" + position + "&red=" + red + "&green=" + green + "&blue=" + blue;
            Log.i("what URL", finalURL);
            new SendHTTPTask().execute(finalURL);
        }
    }

    //Listens for clicks on the color preview and opens color picker. Updates preview if color is selected
    public class ColorPreviewListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            ColorPickerDialog.Builder colorPickerBuilder = new ColorPickerDialog.Builder(v.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            colorPickerBuilder.setTitle("Select Color")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton("Confirm",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    AlphaTileView colorPreview = (AlphaTileView)getView().findViewById(R.id.warpColorPreview);
                                    colorPreview.setPaintColor(envelope.getColor()); //Set background of color preview
                                    previewColor = envelope.getArgb(); // Save color for sending over HTTP
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                    .attachAlphaSlideBar(false)
                    .attachBrightnessSlideBar(false);

            ColorPickerView colorPickerView = colorPickerBuilder.getColorPickerView();
            colorPickerView.setFlagView(new CustomFlag(v.getContext(), R.layout.layout_flag)); // sets a custom flagView
            colorPickerBuilder.show(); // shows the dialog
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
