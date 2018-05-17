package com.silent.feelbeat.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.fragments.ListPlayingFragment;
import com.silent.feelbeat.fragments.NowPlayingFragment;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.musicplayer.MusicPlayer;
import com.silent.feelbeat.musicplayer.RemoteMusic;
import com.silent.feelbeat.utils.SilentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 9/11/2017.
 */

public class PlayingActivity extends AppCompatActivity implements CallbackControl {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAdapter tabAdapter;
    private List<Fragment> listFragment;
    private RemoteMusic remoteMusic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        findView();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listFragment = new ArrayList<>();
        if (savedInstanceState == null) {
            MusicPlayer musicPlayer = MusicPlayer.getInstance(getApplicationContext());
            if(musicPlayer.getList().size() <= 0){
                return;
            }
            Song song = musicPlayer.getSong(musicPlayer.getNowPlay());

            String title = song.title;
            String artist = song.artist;
            String start = SilentUtils.getStringTimeFromDuration(this, musicPlayer.getCurrentProcess());
            String limit = SilentUtils.getStringTimeFromDuration(this, song.duration);
            int max = song.duration / 1000;
            int position = musicPlayer.getCurrentProcess() / 1000;
            listFragment.add(NowPlayingFragment.newInstance(title, artist, start, limit, position, max));
            listFragment.add(ListPlayingFragment.newInstance());
        }
        tabAdapter = new TabAdapter(getSupportFragmentManager(), listFragment);
        viewPager.setAdapter(tabAdapter);
    }

    public void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
        toolbar.setTitle(getResources().getString(R.string.title_now_playing));
    }

    @Override
    public void pause() {

    }

    @Override
    public void start() {

    }

    @Override
    public void seekTo(long position) {

    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }
}
