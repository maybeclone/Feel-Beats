package com.silent.feelbeat.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.silent.feelbeat.abstraction.Item;
import com.silent.feelbeat.abstraction.LoaderDB;
import com.silent.feelbeat.models.Album;
import com.silent.feelbeat.models.Artist;
import com.silent.feelbeat.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/11/2017.
 */

public class AlbumsLoader extends LoaderDB {

    public static final String PROJECTION[] = {MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ALBUM_KEY,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ARTIST};

    public final static String ORDER_BY_NAME = PROJECTION[1]+" COLLATE LOCALIZED ASC";

    public AlbumsLoader() {

    }

    public AlbumsLoader(ContentResolver contentResolver) {
        super(contentResolver);
    }

    public static Uri getUriAlbumArt(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    @Override
    public Cursor getCursor() {
        return contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public List<Item> getList() {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        if (cursor == null) {
            return null;
        }
        List<Item> listAlbum = new ArrayList<>();
        while (cursor.moveToNext()) {
            listAlbum.add(new Album(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6)));
        }
        cursor.close();
        return listAlbum;
    }

    @Override
    public CursorLoader getCursorLoader(Context context) {
        return new CursorLoader(context,
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null,
                null);
    }

    public ArrayList<Album> getList(String title, int limit) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[1] + " LIKE ?",
                new String[]{ title+"%"},
                ORDER_BY_NAME);

        if (cursor == null) {
            return null;
        }

        ArrayList<Album> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Album album = new Album(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6));
            items.add(album);
        }
        cursor.close();
        return items.size()<=limit ? items : (ArrayList<Album>) items.subList(0, limit);
    }

    public CursorLoader getCursorLoader(Context context, String album) {
        return new CursorLoader(context,
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[6] + " = ? AND " + PROJECTION[1] + " = ? ",
                new String[]{"1", album+""},
                ORDER_BY_NAME);
    }
}
