package com.silent.feelbeat.servers.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.activities.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by silent on 5/17/2018.
 */
public class LogoutGetAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ProgressDialog progressDialog;

    public LogoutGetAsyncTask(Context context){
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage("Logout...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        URL url;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(strings[0]);
            Log.d("TRUNG", strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "bearer "+ Instance.nowUser.accessToken);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

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
        progressDialog.dismiss();
        if (stt == HttpURLConnection.HTTP_OK) {
            context.startActivity(new Intent(context, LoginActivity.class));
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
