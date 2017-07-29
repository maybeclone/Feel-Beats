package com.silent.feelbeat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.models.Song;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 7/20/2017.
 */

public class QuickControlFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

    private ImageButton play, ff, rew;
    private CallbackControl control;
    private TextView title, artist;
    private ImageView imageView;
    private ProgressBar progressBar;
    private long timeLongClick;

    public static QuickControlFragment newInstance(){
        return new QuickControlFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CallbackControl){
            control = (CallbackControl) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quick_control, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (TextView) view.findViewById(R.id.textName);
        artist = (TextView) view.findViewById(R.id.textArtist);
        play = (ImageButton) view.findViewById(R.id.playIB);
        ff = (ImageButton) view.findViewById(R.id.ffIB);
        rew = (ImageButton) view.findViewById(R.id.rewIB);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        play.setOnClickListener(this);

        ff.setOnTouchListener(this);
        rew.setOnTouchListener(this);

        ff.setOnClickListener(this);
        rew.setOnClickListener(this);

        rew.setOnLongClickListener(this);
        ff.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playIB:
                if(play.isActivated()){
                    play.setActivated(false);
                    control.pause();
                } else {
                    play.setActivated(true);
                    control.start();
                }
                break;
            case R.id.ffIB:
                if(!play.isActivated()){
                    play.setActivated(true);
                }
                control.next();
                break;
            case R.id.rewIB:
                if(!play.isActivated()){
                    play.setActivated(true);
                }
                control.previous();
                break;
        }
    }

    public void updateInfo(Song song){
        Picasso.with(getContext()).load(AlbumsLoader.getUriAlbumArt(song.albumId)).into(imageView);
        title.setText(song.title);
        artist.setText(song.artist);
        progressBar.setMax(song.getSeconds());
        progressBar.setProgress(0);
    }

    public void updateProgress(int second){
        progressBar.setProgress(second);
    }

    public void setActivePlay(){
        play.setActivated(true);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventAction = event.getAction();
        if(eventAction == MotionEvent.ACTION_DOWN){
            timeLongClick = System.currentTimeMillis();
            switch (v.getId()){
                case R.id.ffIB:
                    touchFF = true;
                    handler.postDelayed(fastForward, 0);
                    break;
                case R.id.rewIB:
                    touchRew = true;
                    handler.postDelayed(rewind, 0);
                    break;
            }

        } else if (eventAction == MotionEvent.ACTION_UP){
            timeLongClick = 0;
            if(v.getId() == R.id.ffIB){
                touchFF = false;
            } else if (v.getId() == R.id.rewIB){
                touchRew = false;
            }
        }
        return false;
    }

    private Handler handler = new Handler();
    private boolean touchFF = true;
    private Runnable fastForward = new Runnable() {
        @Override
        public void run() {
            if(touchFF){
                long time = System.currentTimeMillis() - timeLongClick;
                if (time > 300 && time < 1500){
                    control.seekTo(progressBar.getProgress()*1000+5000);
                } else if (time >1000 ){
                    control.seekTo(progressBar.getProgress()*1000+10000);
                } else if(time > 4000){
                    control.seekTo(progressBar.getProgress()*1000+15000);
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    private boolean touchRew = false;
    private Runnable rewind = new Runnable() {
        @Override
        public void run() {
            if(touchRew){
                long time = System.currentTimeMillis() - timeLongClick;
                if (time > 300 && time < 1500){
                    control.seekTo(progressBar.getProgress()*1000-5000);
                } else if (time > 1000){
                    control.seekTo(progressBar.getProgress()*1000-10000);
                } else if(time > 4000){
                    control.seekTo(progressBar.getProgress()*1000-15000);
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public boolean onLongClick(View v) {
        return true;
    }
}
