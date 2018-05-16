package com.silent.feelbeat.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.silent.feelbeat.configs.Config;
import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.musicplayer.IPlayMusic;
import com.silent.feelbeat.musicplayer.MusicPlayer;
import com.silent.feelbeat.utils.NotificationUtils;

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
                case IPlayMusic.PAUSE:
                    musicPlayer.pause();
                    stopForeground(false);
                    break;
                case IPlayMusic.START:
                    startForeground(Config.ID_NOTIFICATION_FOREGROUND, NotificationUtils.getNotificationForegroundService(PlayingService.this));
                    musicPlayer.start();
                    break;
                case IPlayMusic.NEXT:
                    musicPlayer.next();
                    break;
                case IPlayMusic.PREVIOUS:
                    musicPlayer.previous();
                    break;
                case IPlayMusic.SEEK_TO:
                    musicPlayer.seekTo(msg.arg1);
                    break;
                case IPlayMusic.PLAY_NEW_LIST:
                    startForeground(Config.ID_NOTIFICATION_FOREGROUND, NotificationUtils.getNotificationForegroundService(PlayingService.this));
                    Bundle bundle = msg.getData();
                    musicPlayer.setList(bundle.<Song>getParcelableArrayList(EXTRA_LIST));
                    musicPlayer.play(msg.arg1);
                    break;
                case IPlayMusic.UPDATE_INFO:
                    musicPlayer.updateInfo(musicPlayer.getNowPlay());
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
