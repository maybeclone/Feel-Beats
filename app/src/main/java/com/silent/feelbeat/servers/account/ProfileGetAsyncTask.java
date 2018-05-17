package com.silent.feelbeat.servers.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.activities.MainActivity;
import com.silent.feelbeat.models.account.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by silent on 5/17/2018.
 */
public class ProfileGetAsyncTask extends AsyncTask<String, Void, User> {

    private Context context;
    private ProgressDialog progressDialog;

    public ProfileGetAsyncTask(Context context){
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage("Get profile...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected User doInBackground(String... strings) {
        URL url;
        HttpURLConnection httpURLConnection = null;
        BufferedReader buffer = null;
        StringBuilder jsonBuilder = new StringBuilder();
        String input;
        try {
            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "bearer "+ Instance.nowUser.accessToken);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            buffer = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((input = buffer.readLine()) != null){
                jsonBuilder.append(input);
            }
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return parserScoreJson(jsonBuilder.toString());
            }
            return null;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    private User parserScoreJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String email = jsonObject.getString("email");
        String name = jsonObject.getString("name");
        int gender = jsonObject.getInt("gender");
        String birthday = jsonObject.getString("birthday");
        User user = new User(email, name, birthday, gender);
        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        progressDialog.dismiss();
        if(user != null) {
            Instance.nowUser.name = user.name;
            Instance.nowUser.birthday = user.birthday;
            Instance.nowUser.gender = user.gender;
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, MainActivity.class));
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
