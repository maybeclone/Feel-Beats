package com.silent.feelbeat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silent.feelbeat.R;
import com.silent.feelbeat.fragments.DetailAlbumFragment;
import com.silent.feelbeat.fragments.DetailArtistFragment;

/**
 * Created by silent on 8/3/2017.
 */

public class NavigationUtils {

    public static final String NAVIGATION_TO_ALBUM = "toalbum";
    public static final String NAVIGATION_TO_ARTIST = "toartist";


    public static void navigationToAlbum(Context context, Intent data, DetailAlbumFragment detailAlbumFragment){
        long albumID = data.getLongExtra(DetailAlbumFragment.EXTRA_ALBUMID, -1);
        String title = data.getStringExtra(DetailAlbumFragment.EXTRA_TITLE);
        String info =  data.getStringExtra(DetailAlbumFragment.EXTRA_INFO);

        if(detailAlbumFragment == null){
            detailAlbumFragment = DetailAlbumFragment.newInstance(albumID,title, info);
        } else {
            Bundle args = detailAlbumFragment.getArguments();
            args.putLong(DetailAlbumFragment.EXTRA_ALBUMID, albumID);
            args.putString(DetailAlbumFragment.EXTRA_TITLE, title);
            args.putString(DetailAlbumFragment.EXTRA_INFO, info);
            detailAlbumFragment.setArguments(args);
        }

        ((AppCompatActivity) context).getSupportFragmentManager()
                .beginTransaction().replace(R.id.fragmentMainContent, detailAlbumFragment).commit();

    }

    public static void navigationToArtist(Context context, Intent data, DetailArtistFragment detailArtistFragment){
        long artistID = data.getLongExtra(DetailArtistFragment.EXTRA_ARTIST_ID, -1);
        String title = data.getStringExtra(DetailArtistFragment.EXTRA_ARTIST);

        if(detailArtistFragment == null){
            detailArtistFragment = DetailArtistFragment.newInstance(artistID, title);
        } else {
            Bundle args = detailArtistFragment.getArguments();
            args.putLong(DetailArtistFragment.EXTRA_ARTIST_ID, artistID);
            args.putString(DetailArtistFragment.EXTRA_ARTIST, title);
        }

        ((AppCompatActivity) context).getSupportFragmentManager()
                .beginTransaction().replace(R.id.fragmentMainContent, detailArtistFragment).commit();
    }
}
