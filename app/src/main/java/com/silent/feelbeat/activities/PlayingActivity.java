package com.silent.feelbeat.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.fragments.ListPlayingFragment;
import com.silent.feelbeat.fragments.NowPlayingFragment;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.musicplayer.MusicPlayer;
import com.silent.feelbeat.musicplayer.RemoteMusic;
import com.silent.feelbeat.servers.song.RatingGetAsyncTask;
import com.silent.feelbeat.servers.song.RatingPostAsyncTask;
import com.silent.feelbeat.utils.PermissionUtil;
import com.silent.feelbeat.utils.SilentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 9/11/2017.
 */

public class PlayingActivity extends AppCompatActivity implements CallbackControl, Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAdapter tabAdapter;
    private List<Fragment> listFragment;
    private RemoteMusic remoteMusic;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IPlayMusic.RECEIVER_INFO)) {
                Song song = intent.getParcelableExtra(IPlayMusic.EXTRA_SONG);
                boolean playing = intent.getBooleanExtra(IPlayMusic.EXTRA_PLAYING, true);
                int process = intent.getIntExtra(IPlayMusic.EXTRA_PLAYING_POSITION, -1);
                ((NowPlayingFragment) listFragment.get(0)).updateInfo(song, playing);
                ((NowPlayingFragment) listFragment.get(0)).updateProgress(process);
            } else if (intent.getAction().equals(IPlayMusic.RECEVIER_PROCESS)) {
                if(listFragment.get(0) != null) {
                    int second = intent.getIntExtra(IPlayMusic.EXTRA_PLAYING_POSITION, -1);
                    ((NowPlayingFragment) listFragment.get(0)).updateProgress(second);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        findView();
        remoteMusic = RemoteMusic.getInstance();
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
            String start = SilentUtils.getStringTimeFromDuration(this, musicPlayer.getCurrentProcess());
            String limit = SilentUtils.getStringTimeFromDuration(this, song.duration);
            int max = song.duration / 1000;
            int position = musicPlayer.getCurrentProcess() / 1000;
            listFragment.add(NowPlayingFragment.newInstance(song, start, limit, position, max));
            listFragment.add(ListPlayingFragment.newInstance());
        }
        tabAdapter = new TabAdapter(getSupportFragmentManager(), listFragment);
        viewPager.setAdapter(tabAdapter);
        toolbar.inflateMenu(R.menu.menu_now_playing);
        toolbar.setOnMenuItemClickListener(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.pause();
    }

    @Override
    public void start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.start();
    }

    @Override
    public void seekTo(long position) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.seekTo(position);
    }

    @Override
    public void next() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.next();
    }

    @Override
    public void previous() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.previous();
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteMusic.bindService(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(IPlayMusic.RECEIVER_INFO);
        filter.addAction(IPlayMusic.RECEVIER_PROCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        remoteMusic.unbindService(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.rating_nav:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Rate");
                View view = getLayoutInflater().inflate(R.layout.dialog_rating, null);
                final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
                builder.setView(view);
                builder.setPositiveButton("Summit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ratingBar.getRating() != 0) {
                            new RatingPostAsyncTask(PlayingActivity.this, Instance.nowUser.email,
                                    (int) MusicPlayer.getInstance(PlayingActivity.this).getSong(MusicPlayer.getInstance(PlayingActivity.this).getNowPlay()).id,
                                    ratingBar.getRating()).execute(ConfigServer.POST_RATING_URL);
                        }
                    }
                });
                new RatingGetAsyncTask(this, (int) MusicPlayer.getInstance(PlayingActivity.this).getSong(MusicPlayer.getInstance(PlayingActivity.this).getNowPlay()).id,
                        Instance.nowUser.email, ratingBar, builder).execute(ConfigServer.POST_RATING_URL);

                break;
            case R.id.add_playlist_nav:

                break;
        }
        return false;
    }
}
