package com.silent.feelbeat.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.PlaylistAdapter;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.Playlist;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.servers.playlist.PlaylistGetAsyncTask;
import com.silent.feelbeat.servers.playlist.PlaylistPostAsyncTask;
import com.silent.feelbeat.utils.SilentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by silent on 7/19/2017.
 */

public class PlaylistFragment extends Fragment implements View.OnClickListener{

    private FloatingActionButton addFloatingAction;
    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter adapter;

    public static PlaylistFragment newInstance(String title){
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(SilentUtils.TITLE_FRAGMENT, title);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlaylistAdapter();
        new PlaylistGetAsyncTask().execute(ConfigServer.PLAYLIST_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addFloatingAction = (FloatingActionButton) view.findViewById(R.id.addFloatingActionButton);
        playlistRecyclerView = (RecyclerView) view.findViewById(R.id.playlistRecyclerView);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistRecyclerView.setAdapter(adapter);
        addFloatingAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFloatingActionButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.add_playlist_dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.nameText);
                builder.setView(view);
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().isEmpty()){
                            Playlist playlist = new Playlist();
                            playlist.name = editText.getText().toString();
                            playlist.dateCreated = Calendar.getInstance().getTime();
                            new PlaylistPostAsyncTask(getContext(), playlist, adapter).execute(ConfigServer.PLAYLIST_URL);
                        } else{
                            Toast.makeText(getContext(), "Please complete all information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    class PlaylistGetAsyncTask extends AsyncTask<String, Void, List<Playlist>> {

        private ProgressDialog progressDialog;

        public PlaylistGetAsyncTask(){
            progressDialog = new ProgressDialog(getContext());
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
                Log.d("TRUNG", httpURLConnection.getResponseCode()+"");
                Log.d("TRUNG", httpURLConnection.getResponseMessage());
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
            }
            return songList;
        }


        @Override
        protected void onPostExecute(List<Playlist> playlistList) {
            progressDialog.dismiss();
            if(playlistList != null){
                Instance.myPlaylits = playlistList;
                adapter.swapAdapter(Instance.myPlaylits);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
