package com.silent.feelbeat.servers.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.silent.feelbeat.activities.RegisterActivity;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.account.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by silent on 4/5/2018.
 */
public class RegisterPostUserAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;

    public RegisterPostUserAsyncTask(Context context, User user) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage("Creating a account...");
        this.arguments = new HashMap<>();
        this.arguments.put(ConfigServer.ARGU_EMAIL_REGISTER, user.email);
        this.arguments.put(ConfigServer.ARGU_PASSWORD_REGISTER, user.password);
        this.arguments.put(ConfigServer.ARGU_NAME_REGISTER, user.email);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        this.arguments.put(ConfigServer.ARGU_BIRTHDAY_REGISTER, simpleDateFormat.format(user.birthday));
        this.arguments.put(ConfigServer.ARGU_GENDER_REGISTER, String.valueOf(user.gender));
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
            Toast.makeText(context, "Create a account success", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, RegisterActivity.class));
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
