package com.silent.feelbeat.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.BannerAdapter;
import com.silent.feelbeat.adapters.SongServerAdapter;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.utils.SilentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 5/17/2018.
 */
public class HomeFragment extends Fragment implements CallbackService{

    private RecyclerView bannerRecyclerView;
    private ImageView tempImageView;
    private RecyclerView songsRecyclerView;
    private SongServerAdapter songAdapter;

    public static HomeFragment newInstance(String title){
        Bundle args = new Bundle();
        args.putString(SilentUtils.TITLE_FRAGMENT, title);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songAdapter = new SongServerAdapter(getContext());
        new SongsGetAsyncTask().execute(ConfigServer.GET_SONGS_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bannerRecyclerView = (RecyclerView) view.findViewById(R.id.bannerRecyclerView);
        tempImageView = (ImageView) view.findViewById(R.id.tempImageView);
        songsRecyclerView = (RecyclerView) view.findViewById(R.id.songsRecyclerView);

        bannerRecyclerView.setAdapter(new BannerAdapter());
        songsRecyclerView.setAdapter(songAdapter);

    }

    @Override
    public void playMusic(int position, ArrayList<Song> songs) {

    }

    class SongsGetAsyncTask extends AsyncTask<String, Void, ArrayList<Song>> {

        private ProgressDialog progressDialog;

        public SongsGetAsyncTask(){
            this.progressDialog = new ProgressDialog(getContext());
            this.progressDialog.setCancelable(false);
            this.progressDialog.setMessage("Loading...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Song> doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection = null;
            BufferedReader buffer = null;
            StringBuilder jsonBuilder = new StringBuilder();
            String input;
            try {
                url = new URL(strings[0]);
                Log.d("TRUNG", strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "bearer "+ Instance.nowUser.accessToken);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                buffer = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((input = buffer.readLine()) != null){
                    jsonBuilder.append(input);
                }
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    return parserSongList(jsonBuilder.toString());
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

        private ArrayList<Song> parserSongList(String json) throws JSONException {
            ArrayList<Song> songList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
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
        protected void onPostExecute(ArrayList<Song> songList) {
            progressDialog.dismiss();
            if(songList != null){
                Instance.songList = songList;
                songAdapter.swapAdapter(Instance.songList);
            } else {
                Toast.makeText(getContext(), "There are some errors", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
