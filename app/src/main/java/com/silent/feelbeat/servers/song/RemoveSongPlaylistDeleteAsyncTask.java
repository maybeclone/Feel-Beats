package com.silent.feelbeat.servers.song;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.adapters.DetailPlaylistAdapter;
import com.silent.feelbeat.adapters.PlaylistAdapter;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.Playlist;

import java.io.IOException;
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
public class RemoveSongPlaylistDeleteAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;
    private DetailPlaylistAdapter adapter;

    private int albumPosition;
    private int songPosition;

    public RemoveSongPlaylistDeleteAsyncTask(Context context, int albumPosition, int songPosition, DetailPlaylistAdapter adapter) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.adapter = adapter;
        progressDialog.setMessage("Wait a minute...");
        this.arguments = new HashMap<>();
        this.albumPosition = albumPosition;
        this.songPosition = songPosition;
        this.arguments.put(ConfigServer.ARGU_SONG_ID_REMOVE, String.valueOf(Instance.myPlaylits.get(albumPosition).songArrayList.get(songPosition).id));
        this.arguments.put(ConfigServer.ARGU_PLAYLIST_ID_REMOVE, String.valueOf(Instance.myPlaylits.get(albumPosition).id));
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
            httpURLConnection.setRequestMethod("DELETE");
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
            Instance.myPlaylits.get(albumPosition).songArrayList.remove(songPosition);
            adapter.swapAdapter(Instance.myPlaylits.get(albumPosition).songArrayList);
            Toast.makeText(context, "Remove song success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
