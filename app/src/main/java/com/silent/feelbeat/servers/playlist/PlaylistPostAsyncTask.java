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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by silent on 5/18/2018.
 */
public class PlaylistPostAsyncTask extends AsyncTask<String, Void, Playlist> {

    private Context context;
    private ProgressDialog progressDialog;
    private Map<String, String> arguments;
    private Playlist playlist;
    private PlaylistAdapter adapter;

    public PlaylistPostAsyncTask(Context context, Playlist playlist, PlaylistAdapter adapter) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.playlist = playlist;
        this.adapter = adapter;
        progressDialog.setMessage("Wait a minute...");
        this.arguments = new HashMap<>();
        this.arguments.put(ConfigServer.ARGU_NAME_PLAYLIST, playlist.name);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        this.arguments.put(ConfigServer.ARGU_DATE_CREATED_PLAYLIST, simpleDateFormat.format(playlist.dateCreated));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Playlist doInBackground(String... strings) {
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
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String json = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    json += line;
                }
                playlist.id = parseJsonPlaylistGetID(json);
                return playlist;
            }
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    private int parseJsonPlaylistGetID(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getInt("id");
    }

    @Override
    protected void onPostExecute(Playlist playlist) {
        progressDialog.dismiss();
        if(playlist != null){
            Instance.myPlaylits.add(playlist);
            adapter.swapAdapter(Instance.myPlaylits);
            Toast.makeText(context, "Add playlist success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There are some errors", Toast.LENGTH_SHORT).show();
        }
    }
}
