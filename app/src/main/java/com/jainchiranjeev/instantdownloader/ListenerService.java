package com.jainchiranjeev.instantdownloader;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ListenerService extends Service {
    String clipboardText;
    SharedPreferences myPrefs;
    SharedPreferences.Editor prefsEditor;
    public ListenerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service Status","Service Started");
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                clipboardText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
                DownloadHandler downloadHandler = new DownloadHandler(getApplicationContext(), clipboardText);
                downloadHandler.Download();
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service Checker","Service Stopped");
        Log.d("Prefs Value: ","False");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
