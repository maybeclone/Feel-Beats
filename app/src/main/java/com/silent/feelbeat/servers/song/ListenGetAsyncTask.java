package com.silent.feelbeat.servers.song;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.silent.feelbeat.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by silent on 5/18/2018.
 */
public class ListenGetAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ProgressDialog progressDialog;
    private int songId;

    public ListenGetAsyncTask(Context context, int songId){
        this.context = context;
        this.songId = songId;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        URL url;
        HttpURLConnection httpURLConnection = null;
        BufferedReader buffer = null;
        StringBuilder jsonBuilder = new StringBuilder();
        String input;
        try {
            url = new URL(strings[0]+"?SongId="+songId);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "bearer "+ Instance.nowUser.accessToken);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            buffer = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((input = buffer.readLine()) != null){
                jsonBuilder.append(input);
            }
            return httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }


    @Override
    protected void onPostExecute(Integer stt) {
        if(stt == HttpURLConnection.HTTP_OK){
            Log.d("TRUNG", "add 1 view");
        } else {
            Log.e("TRUNG", "add error");
        }
    }
}
