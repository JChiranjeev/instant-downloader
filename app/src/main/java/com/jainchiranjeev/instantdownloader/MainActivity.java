package com.jainchiranjeev.instantdownloader;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SharedPreferences myPrefs;
    SharedPreferences.Editor prefsEditor;
    private static InterstitialAd interstitialAd;
    Toolbar toolbarMainActivity;
    AppCompatButton optimisationButton;
    SwitchCompat activatorSwitch,startOnBootSwitch,downloadMultipleSwitch;
    CustomCalculations customCalculations;
    static Intent serviceIntent;

    String admobID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Admob ID
        admobID = getString(R.string.admob_id);
        MobileAds.initialize(this, admobID);

        optimisationButton = (AppCompatButton) findViewById(R.id.optimisationButton);
        customCalculations = new CustomCalculations(getApplicationContext());
        myPrefs = getSharedPreferences("Preferences",Context.MODE_PRIVATE);
        activatorSwitch = (SwitchCompat) findViewById(R.id.activateSwitch);
        startOnBootSwitch = (SwitchCompat) findViewById(R.id.startOnBootSwitch);
        downloadMultipleSwitch = (SwitchCompat) findViewById(R.id.downloadMultiple);

        optimisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent batteryManagementIntent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(batteryManagementIntent);
            }
        });

        if(myPrefs.getBoolean("StartOnBoot",false)) {
            startOnBootSwitch.setChecked(true);
        } else {
            startOnBootSwitch.setChecked(false);
        }

        if(myPrefs.getBoolean("DownloadMultiple", false)) {
            downloadMultipleSwitch.setChecked(true);
        } else {
            downloadMultipleSwitch.setChecked(false);
        }

        startOnBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    prefsEditor = myPrefs.edit();
                    prefsEditor.putBoolean("StartOnBoot",true);
                    prefsEditor.commit();
                } else {
                    prefsEditor = myPrefs.edit();
                    prefsEditor.putBoolean("StartOnBoot",false);
                    prefsEditor.commit();
                }
            }
        });

        downloadMultipleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    prefsEditor = myPrefs.edit();
                    prefsEditor.putBoolean("DownloadMultiple", true);
                    prefsEditor.commit();
                } else {
                    prefsEditor = myPrefs.edit();
                    prefsEditor.putBoolean("DownloadMultiple", false);
                    prefsEditor.commit();
                }
            }
        });

        if(isServiceRunning(ListenerService.class)) {
            activatorSwitch.setOnCheckedChangeListener(null);
            activatorSwitch.setChecked(true);
            startOnBootSwitch.setEnabled(true);
            downloadMultipleSwitch.setEnabled(true);
            activatorSwitch.setOnCheckedChangeListener(this);
        } else {
            activatorSwitch.setOnCheckedChangeListener(null);
            activatorSwitch.setChecked(false);
            startOnBootSwitch.setEnabled(false);
            downloadMultipleSwitch.setEnabled(false);
            activatorSwitch.setOnCheckedChangeListener(this);
        }

        //Custom Toolbar
        toolbarMainActivity = (Toolbar) findViewById(R.id.toolbar_main_activity);
        toolbarMainActivity.setTitle(R.string.app_name);
        toolbarMainActivity.setPadding(
                customCalculations.dpToPx(32),
                customCalculations.dpToPx(20),
                customCalculations.dpToPx(20),
                customCalculations.dpToPx(20));
        setSupportActionBar(toolbarMainActivity);

        displayAd(this);

        if((ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }

    public void displayAd(Context context) {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(admobID);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                showInterstitialAd();
            }
        });
    }

    public void showInterstitialAd() {
        if(interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void startDownloaderService() {
        serviceIntent = new Intent(getBaseContext(),ListenerService.class);
        startService(serviceIntent);
    }

    public void stopDownloaderService() {
        stopService(serviceIntent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            startDownloaderService();
            startOnBootSwitch.setEnabled(true);
            downloadMultipleSwitch.setEnabled(true);
        } else {
            stopDownloaderService();
            startOnBootSwitch.setEnabled(false);
            downloadMultipleSwitch.setEnabled(false);
        }
    }
}
