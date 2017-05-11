package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected int cod;
    protected String theme;
    static public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme_pref")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user",cod);
            startActivity(intent);
            finish();
        }
    }


    //Asi se quedan abiertas todas y además no carga las card whyyyyyyyyyyyyyyyyyyyy o whyyyyyyyyyyyyyyyyyyyyyyyy

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",cod);
        startActivity(intent);
        finish();
    }
}
