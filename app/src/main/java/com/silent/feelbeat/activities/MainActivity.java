package com.silent.feelbeat.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.fragments.ArtistsFragment;
import com.silent.feelbeat.fragments.DetailAlbumFragment;
import com.silent.feelbeat.fragments.DetailArtistFragment;
import com.silent.feelbeat.fragments.ListFragment;
import com.silent.feelbeat.fragments.QuickControlFragment;
import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.musicplayer.RemoteMusic;
import com.silent.feelbeat.utils.NavigationUtils;
import com.silent.feelbeat.utils.PermissionUtil;
import com.silent.feelbeat.utils.SilentUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CallbackService, CallbackControl,
        ArtistsFragment.CallbackArtistFragment, AlbumsFragment.CallbackAlbumsFragment {

    private QuickControlFragment controlFragment;
    private DetailArtistFragment detailArtistFragment;
    private DetailAlbumFragment detailAlbumFragment;
    private ListFragment listFragment;
    private RemoteMusic remoteMusic;
    private boolean needUpdate = false;

    // Broadcast Receiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (controlFragment == null) {
                return;
            }
            if (intent.getAction().equals(IPlayMusic.RECEIVER_INFO)) {
                Song song = intent.getParcelableExtra(IPlayMusic.EXTRA_SONG);
                boolean playing = intent.getBooleanExtra(IPlayMusic.EXTRA_PLAYING, true);
                int process = intent.getIntExtra(IPlayMusic.EXTRA_PLAYING_POSITION, -1);
                controlFragment.updateInfo(song, playing);
                controlFragment.updateProgress(process);
            } else if (intent.getAction().equals(IPlayMusic.RECEVIER_PROCESS)) {
                int second = intent.getIntExtra(IPlayMusic.EXTRA_PLAYING_POSITION, -1);
                controlFragment.updateProgress(second);
                Log.d("Receiver", second + "");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remoteMusic = RemoteMusic.getInstance();
        if (PermissionUtil.checkAskPermissionRequired()) {
            if (ActivityCompat.checkSelfPermission(this, PermissionUtil.PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionUtil.PERMISSION[0])) {
                    PermissionUtil.showExplain(this);
                } else {
                    PermissionUtil.requestPermission(this, PermissionUtil.REQUEST_CODE_PERMISSION);
                }
            } else {
                if (savedInstanceState == null) {
                    permissionAccepted();
                }
            }
        }

        if (savedInstanceState == null) {
            attachQuickControl(getSupportFragmentManager());
        }
    }

    private void permissionAccepted() {
        if (getIntent() != null) {
            if (getIntent().getAction().equals(NavigationUtils.NAVIGATION_TO_ALBUM)) {
                NavigationUtils.navigationToAlbum(this, getIntent(), detailAlbumFragment);
                attachQuickControl(getSupportFragmentManager());
                needUpdate = true;
                return;
            } else if (getIntent().getAction().equals(NavigationUtils.NAVIGATION_TO_ARTIST)) {
                NavigationUtils.navigationToArtist(this, getIntent(), detailArtistFragment);
                attachQuickControl(getSupportFragmentManager());
                needUpdate = true;
                return;
            } else {
                attachMainContent(getSupportFragmentManager());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccepted();
                } else {
                    PermissionUtil.showExplain(this);
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        needUpdate = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteMusic.bindService(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(IPlayMusic.RECEIVER_INFO);
        filter.addAction(IPlayMusic.RECEVIER_PROCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        if (needUpdate) {
            needUpdate = false;
            remoteMusic.updateInfo();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteMusic.unbindService(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void attachQuickControl(FragmentManager fragmentManager) {
        if (controlFragment == null) {
            controlFragment = QuickControlFragment.newInstance();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.quickControlContainer, controlFragment)
                .commit();
    }

    private void attachMainContent(FragmentManager fragmentManager) {
        if (listFragment == null) {
            listFragment = new ListFragment();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentMainContent, listFragment)
                .commit();
    }

    @Override
    public void playMusic(int position, ArrayList<Song> songs) {
        remoteMusic.play(this, songs, position);
        controlFragment.setActivePlay(true);
    }


    @Override
    public void pause() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            PermissionUtil.showExplain(this);
            return;
        }
        remoteMusic.pause();
    }

    // edit after
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
    public void onItemClick(long artistID, String artist, boolean az) {
        if (detailArtistFragment == null) {
            detailArtistFragment = DetailArtistFragment.newInstance(artistID, artist, az);
        } else {
            Bundle args = detailArtistFragment.getArguments();
            args.putLong(DetailArtistFragment.EXTRA_ARTIST_ID, artistID);
            args.putString(DetailArtistFragment.EXTRA_ARTIST, artist);
            args.putBoolean(SilentUtils.EXTRA_ORDER, az);
            detailArtistFragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentMainContent, detailArtistFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemClick(long id, String artist, String title, String info, boolean az) {
        if (detailAlbumFragment == null) {
            // read from Preference
            detailAlbumFragment = DetailAlbumFragment.newInstance(id, artist, title, info, az);
        } else {
            Bundle args = detailAlbumFragment.getArguments();
            args.putLong(DetailAlbumFragment.EXTRA_ALBUMID, id);
            args.putString(DetailAlbumFragment.EXTRA_TITLE, title);
            args.putString(DetailAlbumFragment.EXTRA_INFO, info);
            args.putBoolean(SilentUtils.EXTRA_ORDER, az);
            detailAlbumFragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentMainContent, detailAlbumFragment)
                .addToBackStack(null)
                .commit();
    }
}

