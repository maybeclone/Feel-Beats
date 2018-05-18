package com.silent.feelbeat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.musicplayer.MusicPlayer;
import com.silent.feelbeat.utils.SilentUtils;
import com.squareup.picasso.Picasso;


/**
 * Created by silent on 9/13/2017.
 */

public class NowPlayingFragment extends Fragment implements View.OnClickListener {


    private ImageView albumArt;
    private ImageButton play, ff, rew, repeat, shuffle;
    private SeekBar seekBar;
    private TextView title, artist, start, limit;
    private Song playingSong;
    private CallbackControl control;

    public static NowPlayingFragment newInstance(Song song, String start,
                                                 String limit, int position, int maxProcess) {

        Bundle args = new Bundle();
        args.putString(SilentUtils.EXTRA_TITLE, song.title);
        args.putString(SilentUtils.EXTRA_ARTIST, song.artist);
        args.putString(SilentUtils.EXTRA_START, start);
        args.putString(SilentUtils.EXTRA_LIMIT, limit);
        args.putInt(SilentUtils.EXTRA_POSITION, position);
        args.putInt(SilentUtils.EXTRA_MAX_PROCESS, maxProcess);
        NowPlayingFragment fragment = new NowPlayingFragment();
        fragment.setArguments(args);
        fragment.setPlayingSong(song);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        control = (CallbackControl) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumArt = (ImageView) view.findViewById(R.id.albumArt);
        play = (ImageButton) view.findViewById(R.id.btnPlay);
        ff = (ImageButton) view.findViewById(R.id.btnFF);
        rew = (ImageButton) view.findViewById(R.id.btnRew);
        repeat = (ImageButton) view.findViewById(R.id.btnRepeat);
        shuffle = (ImageButton) view.findViewById(R.id.btnShuffle);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        start = (TextView) view.findViewById(R.id.timeStart);
        limit = (TextView) view.findViewById(R.id.timeLimit);
        title = (TextView) view.findViewById(R.id.title);
        artist = (TextView) view.findViewById(R.id.artist);

        play.setOnClickListener(this);
        ff.setOnClickListener(this);
        rew.setOnClickListener(this);
        repeat.setOnClickListener(this);
        shuffle.setOnClickListener(this);

        Bundle args = getArguments();
        String titleText = args.getString(SilentUtils.EXTRA_TITLE);
        String artistText = args.getString(SilentUtils.EXTRA_ARTIST);
        String startText = args.getString(SilentUtils.EXTRA_START);
        String limitText = args.getString(SilentUtils.EXTRA_LIMIT);
        int position = args.getInt(SilentUtils.EXTRA_POSITION);
        int maxProcess = args.getInt(SilentUtils.EXTRA_MAX_PROCESS);
        title.setText(titleText);
        artist.setText(artistText);
        start.setText(startText);
        limit.setText(limitText);
        seekBar.setMax(maxProcess);
        seekBar.setProgress(position);
        if(playingSong.linkImage != null) {
            Picasso.get().load(playingSong.linkImage).into(albumArt);
        } else {
            Picasso.get().load(AlbumsLoader.getUriAlbumArt(playingSong.albumId)).into(albumArt);
        }
        if(MusicPlayer.getInstance(getContext()).isPlaying()){
            play.setActivated(true);
        } else {
            play.setActivated(false);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
//                    Log.d("TRUNG", "onProgressChanged: "+progress*1000);
//                    control.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPlay:
                if(play.isActivated()){
                    play.setActivated(false);
                    control.pause();
                } else {
                    play.setActivated(true);
                    control.start();
                }
                break;
            case R.id.btnFF:
                control.next();
                break;
            case R.id.btnRew:
                control.previous();
                break;
            case R.id.btnShuffle:

                break;
        }
    }

    public void setPlayingSong(Song song){
        this.playingSong = song;
    }

    public void updateInfo(Song song, boolean playing){
        if(song.linkImage != null){
            Picasso.get().load(song.linkImage).into(albumArt);
        } else {
            Picasso.get().load(AlbumsLoader.getUriAlbumArt(song.albumId)).into(albumArt);
        }
        title.setText(song.title);
        artist.setText(song.artist);
        seekBar.setMax(song.getSeconds());
        seekBar.setProgress(0);
        setPlayingSong(song);
    }

    public void updateProgress(int second){
        seekBar.setProgress(second);
        start.setText(SilentUtils.getStringTimeFromDuration(getContext(), second*1000));
    }
}
