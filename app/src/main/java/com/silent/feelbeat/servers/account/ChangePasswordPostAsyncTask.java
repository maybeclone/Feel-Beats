package com.silent.feelbeat.servers.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.configs.ConfigServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by silent on 5/13/2018.
 */
public class ChangePasswordPostAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;

    public ChangePasswordPostAsyncTask(Context context, String oldPassword, String newPassword) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Change the password...");
        this.arguments = new HashMap<>();
        this.arguments.put(ConfigServer.ARGU_OLD_PASSWORD_PASS, oldPassword);
        this.arguments.put(ConfigServer.ARGU_NEW_PASSWORD, newPassword);
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
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            StringBuilder sj = new StringBuilder();
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                sj.append(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
            }
            byte[] out = sj.toString().getBytes();

            httpURLConnection.setFixedLengthStreamingMode(out.length);
            httpURLConnection.setRequestProperty("Authorization", "bearer "+ Instance.nowUser.accessToken);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.connect();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(out);

            return httpURLConnection.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return HttpsURLConnection.HTTP_INTERNAL_ERROR;
    }

    @Override
    protected void onPostExecute(Integer stt) {
        progressDialog.dismiss();
        if(stt == HttpURLConnection.HTTP_OK){
            Toast.makeText(context, "Change the password success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
