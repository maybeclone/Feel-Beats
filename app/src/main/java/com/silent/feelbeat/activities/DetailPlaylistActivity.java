package com.silent.feelbeat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.BannerAdapter;
import com.silent.feelbeat.adapters.DetailPlaylistAdapter;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.musicplayer.RemoteMusic;

import java.util.ArrayList;

public class DetailPlaylistActivity extends AppCompatActivity implements CallbackService {

    private RecyclerView songsRecyclerView;
    private DetailPlaylistAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_playlist);

        ArrayList<Song> songs = (ArrayList<Song>) getIntent().getSerializableExtra("list");
        String title = getIntent().getStringExtra("title");
        int albumPosition = getIntent().getIntExtra("position", -1);

        adapter = new DetailPlaylistAdapter(this, albumPosition, songs);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        songsRecyclerView = (RecyclerView) findViewById(R.id.songsRecyclerView);
        songsRecyclerView.setAdapter(adapter);

        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void playMusic(int position, ArrayList<Song> songs) {
        RemoteMusic.getInstance().play(this, songs, position);
    }
}
