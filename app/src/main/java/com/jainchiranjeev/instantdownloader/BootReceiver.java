package com.jainchiranjeev.instantdownloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jain Chiranjeev on 28-May-18.
 * Visit http://jainchiranjeev.com
 */

public class BootReceiver extends BroadcastReceiver {
    SharedPreferences myPrefs;
    @Override
    public void onReceive(Context context, Intent intent) {
        myPrefs = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE);
        if(myPrefs.getBoolean("StartOnBoot",false)) {
            Intent myIntent = new Intent(context,ListenerService.class);
            try {
                context.startService(myIntent);
            } catch (Exception ex) {
                Log.wtf("Exception", ex.getMessage());
            }

        }
    }
}
