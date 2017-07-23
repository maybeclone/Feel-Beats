package com.silent.feelbeat.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
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

    @Override
    public void play(int position){
        Uri uri = SongsLoader.getSongUri(list.get(position).id);
        try {
            setNowPlay(position);
            play.setDataSource(context, uri);
            play.prepare();
            play.start();
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
}
