package com.example.warpcorecontroller;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference ssidPreference = findPreference("network_ssid");
        EditTextPreference passwordPreference = findPreference("network_password");

        if (ssidPreference != null) {
            ssidPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    String text = preference.getText();
                    if (TextUtils.isEmpty(text)){
                        return "Not set";
                    }
                    return "SSID: " + text;
                }
            });
        }

        if (passwordPreference != null) {
            passwordPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    String text = preference.getText();
                    if (TextUtils.isEmpty(text)){
                        return "Not set";
                    }
                    return "Password: " + text;
                }
            });
        }
    }
}
