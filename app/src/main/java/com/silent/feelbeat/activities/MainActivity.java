package com.silent.feelbeat.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.musicplayer.RemoteMusic;
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.fragments.ArtistsFragment;
import com.silent.feelbeat.fragments.DetailAlbumFragment;
import com.silent.feelbeat.fragments.DetailArtistFragment;
import com.silent.feelbeat.fragments.ListFragment;
import com.silent.feelbeat.fragments.QuickControlFragment;
import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.utils.NavigationUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CallbackService, CallbackControl,
                                                    ArtistsFragment.CallbackArtistFragment, AlbumsFragment.CallbackAlbumsFragment {

    private QuickControlFragment controlFragment;
    private DetailArtistFragment detailArtistFragment;
    private DetailAlbumFragment detailAlbumFragment;
    private ListFragment listFragment;
    private RemoteMusic remoteMusic;
    private boolean needUpdate = false;

    // Broacast Reciever
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (controlFragment == null) {
                return;
            }
            if (intent.getAction().equals(IPlayMusic.RECEIVER_INFO)) {
                Song song = intent.getParcelableExtra(IPlayMusic.EXTRA_SONG);
                controlFragment.updateInfo(song);
            } else if (intent.getAction().equals(IPlayMusic.RECEVIER_PROCESS)) {
                int second = intent.getIntExtra(IPlayMusic.EXTRA_PLAYING_POSITION, -1);
                controlFragment.updateProgress(second);
                Log.d("Receiver", second+"");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        remoteMusic = RemoteMusic.getInstance();

        if(getIntent()!=null){
            if(getIntent().getAction().equals(NavigationUtils.NAVIGATION_TO_ALBUM)){
                NavigationUtils.navigationToAlbum(this, getIntent(), detailAlbumFragment);
                attachQuickControl(getSupportFragmentManager());
                needUpdate = true;
                return;
            } else if(getIntent().getAction().equals(NavigationUtils.NAVIGATION_TO_ARTIST)){
                NavigationUtils.navigationToArtist(this, getIntent(), detailArtistFragment);
                attachQuickControl(getSupportFragmentManager());
                needUpdate = true;
                return;
            }
        }

        if (savedInstanceState == null) {
            attachQuickControl(getSupportFragmentManager());
            attachMainContent(getSupportFragmentManager());
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
        if(needUpdate){
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
        controlFragment.setActivePlay();
    }


    @Override
    public void pause() {
      remoteMusic.pause();
    }

    // edit after
    @Override
    public void start() {
       remoteMusic.start();
    }

    @Override
    public void seekTo(long position) {
        remoteMusic.seekTo(position);
    }

    @Override
    public void next() {
        remoteMusic.next();
    }

    @Override
    public void previous() {
        remoteMusic.previous();
    }

    @Override
    public void onItemClick(long artistID, String artist) {
        if(detailArtistFragment == null){
            detailArtistFragment = DetailArtistFragment.newInstance(artistID, artist);
        } else{
            Bundle args = detailArtistFragment.getArguments();
            args.putLong(DetailArtistFragment.EXTRA_ARTIST_ID, artistID);
            args.putString(DetailArtistFragment.EXTRA_ARTIST, artist);
            detailArtistFragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentMainContent, detailArtistFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemClick(long id, String title, String info) {
        if(detailAlbumFragment == null){
            detailAlbumFragment = DetailAlbumFragment.newInstance(id, title, info);
        } else {
            Bundle args = detailAlbumFragment.getArguments();
            args.putLong(DetailAlbumFragment.EXTRA_ALBUMID, id);
            args.putString(DetailAlbumFragment.EXTRA_TITLE, title);
            args.putString(DetailAlbumFragment.EXTRA_INFO, info);
            detailAlbumFragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentMainContent, detailAlbumFragment)
                .addToBackStack(null)
                .commit();
    }
}

