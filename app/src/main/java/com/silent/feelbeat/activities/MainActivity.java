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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.fragments.ArtistsFragment;
import com.silent.feelbeat.fragments.DetailAlbumFragment;
import com.silent.feelbeat.fragments.DetailArtistFragment;
import com.silent.feelbeat.fragments.ListFragment;
import com.silent.feelbeat.fragments.QuickControlFragment;
import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.service.PlayingService;


public class MainActivity extends AppCompatActivity implements CallbackService, CallbackControl,
                                                    ArtistsFragment.CallbackArtistFragment, AlbumsFragment.CallbackAlbumsFragment {


    private QuickControlFragment controlFragment;
    private DetailArtistFragment detailArtistFragment;
    private DetailAlbumFragment detailAlbumFragment;
    private ListFragment listFragment;

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

        IntentFilter filter = new IntentFilter();
        filter.addAction(IPlayMusic.RECEIVER_INFO);
        filter.addAction(IPlayMusic.RECEVIER_PROCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (savedInstanceState == null) {
            attachQuickControl(getSupportFragmentManager());
            attachMainContent(getSupportFragmentManager());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            Message message = Message.obtain(null, IPlayMusic.ON_STOP, 0, 0);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (bound) {
            Message message = Message.obtain(null, IPlayMusic.ON_RESTART, 0, 0);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if (bound) {
            unbindService(connection);
            bound = false;
        }
        Intent intent = new Intent(this, PlayingService.class);
        stopService(intent);
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

    private Cursor oldCursor = null;

    @Override
    public void playMusic(int position, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        if(cursor.equals(oldCursor)){
            Log.d("Play Music", "Old Cursor");
            Message message = Message.obtain(null, IPlayMusic.PLAY_NEW, position, 0);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            oldCursor = cursor;
            Log.d("Play Music", "New Cursor");
            if(!bound) {
                Intent intent = new Intent(this, PlayingService.class);
                intent.putParcelableArrayListExtra(PlayingService.EXTRA_LIST, SongsLoader.getList(cursor));
                intent.putExtra(PlayingService.EXTRA_POSITION, position);
                startService(intent);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(PlayingService.EXTRA_LIST, SongsLoader.getList(cursor));
                Message message = Message.obtain(null, IPlayMusic.PLAY_NEW_LIST, position, 0);
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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


    // edit after
    @Override
    public void start() {
        if (!bound) {
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.START, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(long position) {
        if(!bound){
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.SEEK_TO, (int) position, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void next() {
        Message message = Message.obtain(null, IPlayMusic.NEXT, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void previous() {
        Message message = Message.obtain(null, IPlayMusic.PREVIOUS, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

