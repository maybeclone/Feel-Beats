package com.silent.feelbeat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.musicplayer.MusicPlayer;

import java.util.List;


/**
 * Created by silent on 7/21/2017.
 */

public class PlayingService extends Service {

    // Extras
    public final static String EXTRA_POSITION = "position";
    public final static String EXTRA_LIST = "list";

    private MusicPlayer musicPlayer;
    private Messenger messenger;


    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = MusicPlayer.getInstance(getApplicationContext());
        messenger = new Messenger(new ReceiveHandler());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicPlayer.setList(intent.<Song>getParcelableArrayListExtra(EXTRA_LIST));
        Message message = Message.obtain(null, IPlayMusic.PLAY, intent.getIntExtra(EXTRA_POSITION, 0), 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicPlayer.destroy();
    }

    // Handler request from Activity or Fragment
    private class ReceiveHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IPlayMusic.PLAY:
                    musicPlayer.play(msg.arg1);
                    break;
                case IPlayMusic.PLAY_NEW:
                    musicPlayer.playNew(msg.arg1);
                    break;
                case IPlayMusic.PAUSE:
                    musicPlayer.pause();
                    break;
                case IPlayMusic.START:
                    musicPlayer.start();
                    break;
                case IPlayMusic.NEXT:
                    musicPlayer.next();
                    break;
                case IPlayMusic.PREVIOUS:
                    musicPlayer.previous();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
