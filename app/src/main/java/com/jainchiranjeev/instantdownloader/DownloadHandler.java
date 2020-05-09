package com.jainchiranjeev.instantdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Created by Jain Chiranjeev on 18-May-18.
 * Visit http://jainchiranjeev.com
 */

public class DownloadHandler {
    static Context context;
    static String urlString;
    static boolean isValidUrl = false;
    public DownloadHandler(Context context, String urlText) {
        this.context = context;
        this.urlString = urlText;
        if(URLUtil.isValidUrl(urlText)) {
            isValidUrl = true;
        } else {
            isValidUrl = false;
        }
    }
    public boolean Download() {
//        DBHelper dbHelper = DBHelper.getInstance(context);
        if(isValidUrl) {
//            Cursor cursor = dbHelper.getTable();
            if(urlString.contains("instagram.com")) {
                try {
//                    if(!dbHelper.urlExists(urlString)) {
                        new InstagramDownloader(context).execute(urlString);
//                        dbHelper.insertData(urlString,"instagram.com");
//                    }
                } catch (Exception ex) {
                    Log.wtf("Exception", ex.getMessage());
                }
            } else if (urlString.contains("wallhaven.cc")) {
                try {
//                    if(!dbHelper.urlExists(urlString)) {
                        new WallhavenDownloader(context).execute(urlString);
//                        dbHelper.insertData(urlString,"wallhaven.cc");
//                    }
                } catch (Exception ex) {
                    Log.wtf("Exception", ex.getMessage());
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
