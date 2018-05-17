package com.silent.feelbeat.dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.silent.feelbeat.abstraction.Item;
import com.silent.feelbeat.abstraction.LoaderDB;
import com.silent.feelbeat.models.database.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/14/2017.
 */

public class ArtistLoader extends LoaderDB {

    public static final String PROJECTION[] = {MediaStore.Audio.Artists._ID,
                                            MediaStore.Audio.Artists.ARTIST,
                                            MediaStore.Audio.Artists.ARTIST_KEY,
                                            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                                            MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

    public final static String ORDER_BY_NAME_AZ = PROJECTION[1]+" COLLATE LOCALIZED ASC";
    public final static String ORDER_BY_NAME_ZA =  PROJECTION[1]+" COLLATE LOCALIZED DESC";

    public ArtistLoader(){

    }

    public ArtistLoader(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    public Cursor getCursor() {
        if(contentResolver == null){
            return null;
        }
        return contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION, null, null, ORDER_BY_NAME_AZ);
    }

    @Override
    public List<Item> getList() {
        if(contentResolver == null){
            return null;
        }
        Cursor cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        List<Item> artists = new ArrayList<>();
        while(cursor.moveToNext()){
            artists.add(new Artist(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4)));
        }
        cursor.close();
        return artists;
    }

    @Override
    public CursorLoader getCursorLoader(Context context, boolean az) {
        String order = az ? ORDER_BY_NAME_AZ : ORDER_BY_NAME_ZA;
        return new CursorLoader(context,
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION,
                    null,
                    null,
                    order);
    }

    public ArrayList<Artist> getList(String title, int limit) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[1] + " LIKE ?",
                new String[]{title+"%"},
                ORDER_BY_NAME_AZ);

        if (cursor == null) {
            return null;
        }

        ArrayList<Artist> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Artist artist = new Artist(cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4));
          items.add(artist);
        }
        cursor.close();
        return  items.size()<=limit ? items : (ArrayList<Artist>) items.subList(0, limit);
    }

    public CursorLoader getCursorLoader(Context context, String artist) {
        return new CursorLoader(context,
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[6] + " = ? AND " + PROJECTION[1] + " = ? ",
                new String[]{"1", artist+""},
                ORDER_BY_NAME_AZ);
    }
}
