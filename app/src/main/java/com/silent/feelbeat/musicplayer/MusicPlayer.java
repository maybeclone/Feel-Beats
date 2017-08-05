package com.silent.feelbeat.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/23/2017.
 */

public class MusicPlayer implements IPlayMusic, MediaPlayer.OnCompletionListener {

    private static MusicPlayer musicPlayer;

    private ArrayList<Song> list;
    private MediaPlayer play;
    private int nowPlay;
    private Context context;
    private boolean uniqueHandler = false;

    public static MusicPlayer getInstance(Context context){
       if(musicPlayer==null){
           musicPlayer = new MusicPlayer(context);
           return musicPlayer;
       }
       return musicPlayer;
   }

   private MusicPlayer(Context context){
       this.context = context;
       play = new MediaPlayer();
       list = new ArrayList<>();
       play.setAudioStreamType(AudioManager.STREAM_MUSIC);
       nowPlay = -1;
       play.setOnCompletionListener(this);
   }

    public ArrayList<Song> getList() {
        return list;
    }

    public void setList(ArrayList<Song> list) {
        this.list = list;
    }


    public void setNowPlay(int nowPlay) {
        this.nowPlay = nowPlay;
    }

    public void updateInfo(int position){
        if(list == null){
            return;
        }
        Intent intent = new Intent(RECEIVER_INFO);
        intent.putExtra(IPlayMusic.EXTRA_SONG, list.get(position));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public int getNowPlay() {
        return nowPlay;
    }

    @Override
    public void play(int position){
        play.reset();
        Uri uri = SongsLoader.getSongUri(list.get(position).id);
        try {
            setNowPlay(position);
            play.setDataSource(context, uri);
            play.prepare();
            play.start();
            updateInfo(position);
            if(!uniqueHandler){
                uniqueHandler = true;
                handler.postDelayed(runnable, 1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int next() {
        nowPlay++;
        if(nowPlay == list.size()){
            nowPlay = 0;
        }
        playNew(nowPlay);
        return 0;
    }

    @Override
    public int previous() {
        nowPlay--;
        if(nowPlay == -1){
            nowPlay = list.size()-1;
        }
        playNew(nowPlay);
        return 0;
    }

    @Override
    public void pause() {
        play.pause();
    }

    @Override
    public void start() {
        play.start();
        if(!uniqueHandler){
            uniqueHandler = true;
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void seekTo(long position) {
        if (position<0){
            position = 0;
        }
        play.seekTo((int) position);
    }

    @Override
    public void playNew(int position) {
        play.reset();
        play(position);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public void destroy(){
        list = null;
        context = null;
        play.release();
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(play.isPlaying()){
                Intent intent = new Intent(IPlayMusic.RECEVIER_PROCESS);
                intent.putExtra(IPlayMusic.EXTRA_PLAYING_POSITION, play.getCurrentPosition()/1000);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                handler.postDelayed(this, 1000);
            } else {
                uniqueHandler = false;
            }
        }
    };

}
