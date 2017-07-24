package com.silent.feelbeat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackControl;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by silent on 7/20/2017.
 */

public class QuickControlFragment extends Fragment implements View.OnClickListener {

    private ImageButton play;
    private CallbackControl control;
    private TextView title, artist;
    private ImageView imageView;

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
        imageView = (ImageView) view.findViewById(R.id.imageView);
        play.setOnClickListener(this);
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
        }
    }

    public void updateInfo(long idAlbum, String _title, String _artist){
        Picasso.with(getContext()).load(AlbumsLoader.getUriAlbumArt(idAlbum)).into(imageView);
        title.setText(_title);
        artist.setText(_artist);
    }

    public void setActivePlay(){
        play.setActivated(true);
    }
}
