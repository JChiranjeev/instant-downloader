package com.jainchiranjeev.instantdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jain Chiranjeev on 18-May-18.
 * Visit http://jainchiranjeev.com
 */

public class InstagramDownloader extends AsyncTask<String, Void, Boolean> {
    Context context;
    public InstagramDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean mediaDownloaded;
        SharedPreferences myPrefs;
        String username = "Image";
        ArrayList<Boolean> areVideos = new ArrayList<>();
        ArrayList<String> urlStrings = new ArrayList<>();
        String urlString = strings[0].substring(0,strings[0].lastIndexOf('/')+1) + "?__a=1";
        try {
            System.out.println(urlString);
            URL imageUrl = new URL(urlString);

            URLConnection connection = imageUrl.openConnection();
            InputStream inputStream = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String JSONbody = IOUtils.toString(inputStream, encoding);
            System.out.println(JSONbody);

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONbody);
            JSONObject graphObject = (JSONObject) jsonObject.get("graphql");
            JSONObject shortcodeMediaObject = (JSONObject) graphObject.get("shortcode_media");
            JSONObject ownerObject = (JSONObject) shortcodeMediaObject.get("owner");
            username = (String) ownerObject.get("username");
            String output = (String) shortcodeMediaObject.get("__typename");
            System.out.println(output);
            JSONObject imagesObject = (JSONObject) shortcodeMediaObject.get("edge_sidecar_to_children");
            if(imagesObject != null) {
                JSONArray edgesArray = (JSONArray) imagesObject.get("edges");
                System.out.println(edgesArray.size());
                for(int looper=0; looper<edgesArray.size();looper++) {
                    JSONObject imageObject = (JSONObject) edgesArray.get(looper);
                    JSONObject nodeObject = (JSONObject) imageObject.get("node");
                    Boolean isVideo = (Boolean) nodeObject.get("is_video");
                    String imageURL = "";
                    if(isVideo) {
                        imageURL = (String) nodeObject.get("video_url");
                    } else {
                        imageURL = (String) nodeObject.get("display_url");
                    }
                    areVideos.add(isVideo);
                    urlStrings.add(imageURL);
                    myPrefs = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE);
                    if(!myPrefs.getBoolean("DownloadMultiple", false)) {
                        break;
                    }
                }
            } else {
                Boolean isVideo = (Boolean) shortcodeMediaObject.get("is_video");
                String imageURL = "";
                if(isVideo) {
                    imageURL =  (String) shortcodeMediaObject.get("video_url");
                } else {
                    imageURL = (String) shortcodeMediaObject.get("display_url");
                }
                areVideos.add(isVideo);
                urlStrings.add(imageURL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mediaDownloaded = false;
        try {
            if(DownloaderProcess(urlStrings,areVideos,username)) {
                mediaDownloaded = true;
            }
        } catch (Exception ex) {
            Log.wtf("Exception", ex.getMessage());
        }
        return mediaDownloaded;
    }

    public boolean DownloaderProcess(ArrayList<String> urlStrings, ArrayList<Boolean> areVideos, String fileName) throws Exception {
        for (int looper=0;looper<urlStrings.size();looper++) {
            String fileNametoSave = fileName;
            if(areVideos.get(looper)) {
                fileNametoSave += ".mp4";
            } else {
                fileNametoSave += ".jpg";
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlStrings.get(looper)));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + "Instant Downloads",fileNametoSave);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) DownloadHandler.context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
        return true;
    }
}
