package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        final ListPreference difficultyLevelPref = (ListPreference) findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level", getResources().getString(R.string.difficulty_expert));
        difficultyLevelPref.setSummary((CharSequence) difficulty);

        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);

                // Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();

                if(newValue.toString().equals(getResources().getString(R.string.difficulty_easy))){
                    ed.putString("difficulty_level", getResources().getString(R.string.difficulty_easy));
                }
                else if(newValue.toString().equals(getResources().getString(R.string.difficulty_harder))){
                    ed.putString("difficulty_level", getResources().getString(R.string.difficulty_harder));
                }
                else if (newValue.toString().equals(getResources().getString(R.string.difficulty_expert))){
                    ed.putString("difficulty_level", getResources().getString(R.string.difficulty_expert));
                }

                ed.commit();
                return true;
            }
        });

        final EditTextPreference victoryMessagePref = (EditTextPreference)
                findPreference("victory_message");
        String victoryMessage = prefs.getString("victory_message",
                getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary((CharSequence) victoryMessage);

        victoryMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                victoryMessagePref.setSummary((CharSequence) newValue);

                // Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("victory_message", newValue.toString());
                ed.commit();
                return true;
            }
        });

        final CheckBoxPreference soundPref = (CheckBoxPreference) findPreference("mSoundOn");
        Boolean soundOn = prefs.getBoolean("mSoundOn", false);
        if (soundOn){
            soundPref.setChecked(true);
        }
        else {
            soundPref.setChecked(false);
        }

        soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                SharedPreferences.Editor ed = prefs.edit();

                if((Boolean) newValue){
                    soundPref.setChecked(true);
                    ed.putBoolean("mSoundOn", true);
                }
                else{
                    soundPref.setChecked(false);
                    ed.putBoolean("mSoundOn", false);
                }

                ed.commit();

                return true;
            }
        });

    }
}

