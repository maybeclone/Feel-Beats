package com.silent.feelbeat.servers.song;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RatingBar;

import com.silent.feelbeat.Instance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by silent on 5/18/2018.
 */
public class RatingGetAsyncTask extends AsyncTask<String, Void, Float> {

    private Context context;
    private ProgressDialog progressDialog;
    private int songId;
    private String emailUser;
    private RatingBar ratingBar;
    private AlertDialog.Builder builder;

    public RatingGetAsyncTask(Context context, int songId, String emailUser, RatingBar ratingBar, AlertDialog.Builder builder) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.songId = songId;
        this.emailUser = emailUser;
        this.ratingBar = ratingBar;
        this.builder = builder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Float doInBackground(String... strings) {
        URL url;
        HttpURLConnection httpURLConnection = null;
        BufferedReader buffer = null;
        StringBuilder jsonBuilder = new StringBuilder();
        String input;
        try {
            url = new URL(strings[0] + "?SongId=" + songId + "&EmailUser=" + emailUser);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "bearer " + Instance.nowUser.accessToken);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            buffer = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((input = buffer.readLine()) != null) {
                jsonBuilder.append(input);
            }
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            return (float) jsonObject.getDouble("rate");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return 0.0f;
    }


    @Override
    protected void onPostExecute(Float stt) {
        progressDialog.dismiss();
        ratingBar.setRating(stt);
        builder.show();
    }
}
