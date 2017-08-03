package com.silent.feelbeat.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.silent.feelbeat.models.Song;
import com.silent.feelbeat.service.PlayingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 8/2/2017.
 */

public class RemoteMusic {

    private static RemoteMusic navigation;

    private List<Song> oldList;
    private boolean bound = false;
    private Messenger messenger;
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


    public static RemoteMusic getInstance(){
        if(navigation == null){
            navigation = new RemoteMusic();
        }
        return navigation;
    }

    private RemoteMusic(){
    }

    public void bindService(Context context){
        Intent intent = new Intent(context, PlayingService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        bound = true;
    }

    public void play(Context context, ArrayList<Song> list, int position){
        if(list == null){
            return;
        }
        if(list.equals(oldList)){
            Message message = Message.obtain(null, IPlayMusic.PLAY, position, 0);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            oldList = list;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(PlayingService.EXTRA_LIST, list);
                Message message = Message.obtain(null, IPlayMusic.PLAY_NEW_LIST, position, 0);
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        }
    }

    public void setConnection(boolean bound){
        this.bound = bound;
    }

    public void start(){
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

    public void pause(){
        if(!bound){
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.PAUSE, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void next(){
        if(!bound){
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.NEXT, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void previous(){
        Message message = Message.obtain(null, IPlayMusic.PREVIOUS, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void seekTo(long position){
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

    public void unbindService(Context context){
        if(bound){
            context.unbindService(connection);
            bound = false;
        }
    }

    public void updateInfo(){
        if(!bound){
            return;
        }
        Message message = Message.obtain(null, IPlayMusic.UPDATE_INFO, 0, 0);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
