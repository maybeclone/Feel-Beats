package com.silent.feelbeat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.silent.feelbeat.models.Playlist;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/19/2017.
 */

public class PlaylistFragment extends Fragment {

    public static PlaylistFragment newInstance(String title){
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(SilentUtils.TITLE_FRAGMENT, title);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

}
