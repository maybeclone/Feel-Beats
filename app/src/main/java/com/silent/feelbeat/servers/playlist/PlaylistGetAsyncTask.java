package com.silent.feelbeat.servers.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.adapters.PlaylistAdapter;
import com.silent.feelbeat.models.Playlist;
import com.silent.feelbeat.models.database.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 5/18/2018.
 */
public class PlaylistGetAsyncTask extends AsyncTask<String, Void, List<Playlist>> {

    private Context context;
    private ProgressDialog progressDialog;
    private PlaylistAdapter adapter;

    public PlaylistGetAsyncTask(Context context, PlaylistAdapter adapter){
        this.context = context;
        this.adapter = adapter;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected List<Playlist> doInBackground(String... strings) {
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
                return parserPlaylistJson(jsonBuilder.toString());
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    private List<Playlist> parserPlaylistJson(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        List<Playlist> playlistList = new ArrayList<>();
        int id;
        String name;
        ArrayList<Song> songArrayList;
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            songArrayList = parserSongList(jsonObject.getJSONArray("songs"));
            Playlist playlist = new Playlist();
            playlist.id = id;
            playlist.name = name;
            playlist.songArrayList = songArrayList;
            playlistList.add(playlist);
        }
        return playlistList;
    }

    private ArrayList<Song> parserSongList(JSONArray jsonArray) throws JSONException {

        ArrayList<Song> songList = new ArrayList<>();
        Song song;
        int id, duration;
        String name, link, linkImage, singer, artist, musicKind;
        JSONObject jsonObject;
        for(int i=0; i<jsonArray.length(); i++){
            song = new Song();
            jsonObject = jsonArray.getJSONObject(i);
            id  = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            link = jsonObject.getString("link");
            linkImage = jsonObject.getString("link_image");
            singer = jsonObject.getString("singer");
            artist = jsonObject.getString("artist");
            musicKind = jsonObject.getString("music_kind");
            duration = jsonObject.getInt("duration");
            song.id = id;
            song.title = name;
            song.link = link;
            song.linkImage = linkImage;
            song.artist = singer;
            song.duration = duration;
            song.composer = artist;
            song.musicKind = musicKind;
            songList.add(song);
            Log.d("TRUNG", song.linkImage+"");
        }
        return songList;
    }


    @Override
    protected void onPostExecute(List<Playlist> playlistList) {
        progressDialog.dismiss();
        if(playlistList != null){
            Instance.myPlaylits = playlistList;
            adapter.swapAdapter(Instance.myPlaylits);
        }

    }
}
