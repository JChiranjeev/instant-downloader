package com.jainchiranjeev.instantdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Jain Chiranjeev on 3/24/2019.
 * Visit http://jainchiranjeev.com
 */
public class WallhavenDownloader extends AsyncTask<String, Void, Boolean> {
    Context context;

    public WallhavenDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean mediaDownloaded;
        System.out.println(strings[0]);
        String username = "WallHaven-";
        String pageUrlString = strings[0];
        URI imageUri;
        String imageExt = ".jpg";
        String[] pageUriSegments;
        String imageID = "", imageDownloadUrl = "", imageSubID;
        Document doc;
        try {
            imageUri = new URI(pageUrlString);
            pageUriSegments = imageUri.getPath().split("/");
            System.out.println(pageUriSegments[pageUriSegments.length - 1]);
            imageID = pageUriSegments[pageUriSegments.length - 1];
            imageSubID = imageID.substring(0, 2);
            imageDownloadUrl = "https://w.wallhaven.cc/full/" + imageSubID + "/wallhaven-" + imageID;
            URL url = new URL(imageDownloadUrl + imageExt);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            int responseCode = conn.getResponseCode();
            imageExt = (responseCode == 200) ? ".jpg" : ".png";
            imageDownloadUrl += imageExt;
            System.out.println("ImageID: " + imageID + "\tImageDownloadURL: " + imageDownloadUrl);
            System.out.println(imageDownloadUrl);
            URL imageUrl = new URL(imageDownloadUrl);
            username += imageID;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mediaDownloaded = false;
        try {
            if (DownloaderProcess(imageDownloadUrl, username, imageExt)) {
                mediaDownloaded = true;
            }
        } catch (Exception ex) {
            Log.wtf("Exception", ex.getMessage());
        }
        return mediaDownloaded;
    }

    public boolean DownloaderProcess(String imageDownloadUrl, String fileName, String imageExt) throws Exception {
        String fileNametoSave = fileName + imageExt;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageDownloadUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + "Instant Downloads", fileNametoSave);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager manager = (DownloadManager) DownloadHandler.context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        return true;
    }
}
