package com.silent.feelbeat.servers.song;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
 * Created by silent on 5/18/2018.
 */
public class RatingPostAsyncTask extends AsyncTask<String, Void, Integer>{
    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;

    public RatingPostAsyncTask(Context context, String email, int songId, float rate) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage("Creating a account...");
        this.arguments = new HashMap<>();
        this.arguments.put(ConfigServer.ARGU_EMAIL_RATING, email);
        this.arguments.put(ConfigServer.ARGU_SONG_ID_RATING, String.valueOf(songId));
        this.arguments.put(ConfigServer.ARGU_RATE_RATING, String.valueOf(rate));
        Log.d("TRUNG", email);
        Log.d("TRUNG", songId+"");
        Log.d("TRUNG", rate+"");
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
            Log.d("TRUNG", url.toString());
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
            Log.d("TRUNG", httpURLConnection.getResponseMessage());
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
            Toast.makeText(context, "Summit success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
