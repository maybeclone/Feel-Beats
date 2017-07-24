package com.silent.feelbeat.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.fragments.ArtistsFragment;
import com.silent.feelbeat.fragments.PlaylistFragment;
import com.silent.feelbeat.fragments.QuickControlFragment;
import com.silent.feelbeat.fragments.SongsFragment;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.service.PlayingService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CallbackService, CallbackControl {

    // View
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> list;
    private TabAdapter adapter;
    private QuickControlFragment controlFragment;

    // Connect Service
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
    private Messenger messenger;
    private boolean bound = false;

    // Broacast Reciever
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(IPlayMusic.RECEIVER_INFO)){
                if(controlFragment!=null){
                    controlFragment.updateInfo(intent.getLongExtra(IPlayMusic.EXTRA_ALBUMID, -1),
                            intent.getStringExtra(IPlayMusic.EXTRA_TITLE),
                            intent.getStringExtra(IPlayMusic.EXTRA_ARTIST));

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IPlayMusic.RECEIVER_INFO);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        if(bound){
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PlayingService.class);
        stopService(intent);
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        list = new ArrayList<>();
        list.add(ArtistsFragment.newInstance("Artists"));
        list.add(SongsFragment.newInstance("Songs"));
        list.add(AlbumsFragment.newInstance("Albums"));
        list.add(PlaylistFragment.newInstance("Playlist"));

        tabLayout.setupWithViewPager(viewPager);
        adapter = new TabAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        attachQuickControl(getSupportFragmentManager());
    }

    private void attachQuickControl(FragmentManager fragmentManager){
        if(controlFragment == null){
            controlFragment = QuickControlFragment.newInstance();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.quickControlContainer, controlFragment)
                .commit();
    }

    @Override
    public void playMusic(int position, Cursor cursor){
        if(!bound){
            Intent intent = new Intent(this, PlayingService.class);
            intent.putParcelableArrayListExtra(PlayingService.EXTRA_LIST, SongsLoader.getList(cursor));
            intent.putExtra(PlayingService.EXTRA_POSITION, position);
            startService(intent);
            bindService(intent,connection, Context.BIND_AUTO_CREATE);
        } else {
            Message message = Message.obtain(null, IPlayMusic.PLAY_NEW, position, 0);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        controlFragment.setActivePlay();
    }


    @Override
    public void pause() {
        Message message = Message.obtain(null, IPlayMusic.PAUSE, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if(messenger==null){
            SongsFragment songsFragment = (SongsFragment) adapter.getItem(1);
            playMusic(0, songsFragment.getAdapter().getCursor());
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.START, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
