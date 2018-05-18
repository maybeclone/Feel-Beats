package com.silent.feelbeat.servers.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.adapters.PlaylistAdapter;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.Playlist;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by silent on 5/18/2018.
 */
public class PlaylistPutAsyncTask extends AsyncTask<String, Void, Integer>{

    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;
    private Playlist playlist;
    private PlaylistAdapter adapter;

    public PlaylistPutAsyncTask(Context context, Playlist playlist, PlaylistAdapter adapter) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.playlist = playlist;
        this.adapter = adapter;
        progressDialog.setMessage("Wait a minute...");
        this.arguments = new HashMap<>();
        this.arguments.put(ConfigServer.ARGU_NAME_PLAYLIST, playlist.name);
        this.arguments.put(ConfigServer.ARGU_ID_PLAYLIST, String.valueOf(playlist.id));
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
            httpURLConnection.setRequestMethod("PUT");
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
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    @Override
    protected void onPostExecute(Integer stt) {
        progressDialog.dismiss();
        if(stt == HttpURLConnection.HTTP_OK){
            for(Playlist playlist : Instance.myPlaylits){
                if(playlist.id == this.playlist.id){
                    playlist.name = this.playlist.name;
                }
            }
            Toast.makeText(context, "Edit playlist success", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
