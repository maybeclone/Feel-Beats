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
import com.silent.feelbeat.models.database.Album;

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

    public final static String ORDER_BY_NAME_AZ = PROJECTION[1]+" COLLATE LOCALIZED ASC";
    public final static String ORDER_BY_NAME_ZA = PROJECTION[1]+" COLLATE LOCALIZED DESC";

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
    public CursorLoader getCursorLoader(Context context, boolean az) {
        String order = az ? ORDER_BY_NAME_AZ : ORDER_BY_NAME_ZA;
        return new CursorLoader(context,
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null,
                order);
    }

    public CursorLoader getCursorLoader(Context context, long albumID, boolean az) {
        String order = az ? ORDER_BY_NAME_AZ : ORDER_BY_NAME_ZA;
        return new CursorLoader(context,
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[0]+" = ?",
                new String[]{albumID+""},
                order);
    }

    public ArrayList<Album> getList(String title, int limit) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION,
                PROJECTION[1] + " LIKE ?",
                new String[]{ title+"%"},
                ORDER_BY_NAME_AZ);

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
                ORDER_BY_NAME_AZ);
    }

    public CursorLoader getAlbumArtist(Context context, long artistID, boolean az){
        String sortBy = az ? ORDER_BY_NAME_AZ : ORDER_BY_NAME_ZA;
        return new CursorLoader(context,
                MediaStore.Audio.Artists.Albums.getContentUri("external", artistID),
                PROJECTION,
                null,
                null,
                sortBy);
    }
}
