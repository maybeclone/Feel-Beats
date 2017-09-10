package com.silent.feelbeat.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.silent.feelbeat.database.MusicDBHelper;
import com.silent.feelbeat.models.Song;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by silent on 8/16/2017.
 */

public class PlaybackHistory {

    private MusicDBHelper helper;

    private static PlaybackHistory playbackHistory;

    public static PlaybackHistory getInstance(Context context) {
        if (playbackHistory == null) {
            playbackHistory = new PlaybackHistory();
        }
        return playbackHistory;
    }


    private PlaybackHistory() {

    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createPlayback = String.format("CREATE TABLE IF NOT EXIST %s " +
                        "(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL," +
                        "%s INTEGER NOT NULL, %s INTEGER NOT NULL);",
                PlaybackColumns.TABLE_NAME, PlaybackColumns._ID, PlaybackColumns.TITLE,
                PlaybackColumns.ARTIST, PlaybackColumns.ALBUM_ID, PlaybackColumns.DURATION);
        String createNowPlay = String.format("CREATE TABLE IF NOT EXIST %s " +
                        "(%s INTEGER NOT NULL, %s INTEGER NOT NULL);", NowPlayingColumns.TABLE_NAME,
                NowPlayingColumns.POSITION, NowPlayingColumns.PROCESS);

        sqLiteDatabase.execSQL(createPlayback);
        sqLiteDatabase.execSQL(createNowPlay);
    }

    public void insertDB(List<Song> list, int position, int process) {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();

        deleteALL(sqLiteDatabase);

        sqLiteDatabase.beginTransaction();

        try {
            for (Song song : list) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PlaybackColumns._ID, song.id);
                contentValues.put(PlaybackColumns.TITLE, song.title);
                contentValues.put(PlaybackColumns.ARTIST, song.artist);
                contentValues.put(PlaybackColumns.ALBUM_ID, song.albumId);
                contentValues.put(PlaybackColumns.DURATION, song.duration);
                sqLiteDatabase.insert(PlaybackColumns.TABLE_NAME, null, contentValues);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("Playback History", "insert " + PlaybackColumns.TABLE_NAME + " table");
        } finally {
            sqLiteDatabase.endTransaction();
        }

        try {
            for (Song song : list) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NowPlayingColumns.POSITION, position);
                contentValues.put(NowPlayingColumns.PROCESS, process);
                sqLiteDatabase.insert(NowPlayingColumns.TABLE_NAME, null, contentValues);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("Playback History", "insert " + NowPlayingColumns.TABLE_NAME + " table");
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    public void deleteALL(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(PlaybackColumns.TABLE_NAME, null, null);
            sqLiteDatabase.delete(NowPlayingColumns.TABLE_NAME, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public List<Song> getPlayback() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(PlaybackColumns.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            List<Song> songs = new ArrayList<>();
            try {
                while (cursor.moveToNext()) {
                    Song song = new Song();
                    song.id = cursor.getLong(cursor.getColumnIndex(PlaybackColumns._ID));
                    song.title = cursor.getString(cursor.getColumnIndex(PlaybackColumns.TITLE));
                    song.artist = cursor.getString(cursor.getColumnIndex(PlaybackColumns.ARTIST));
                    song.albumId = cursor.getLong(cursor.getColumnIndex(PlaybackColumns.ALBUM_ID));
                    song.duration = cursor.getInt(cursor.getColumnIndex(PlaybackColumns.DURATION));
                    songs.add(song);
                }
            } finally {
                cursor.close();
            }
            return songs;
        }
        return null;
    }

    public LinkedList<Integer> getNowPlaying(){
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(NowPlayingColumns.TABLE_NAME, null, null, null, null, null, null);
        if(cursor != null){
            LinkedList<Integer> list = new LinkedList<>();
            try{
                list.add(cursor.getInt(cursor.getColumnIndex(NowPlayingColumns.POSITION)));
                list.add(cursor.getInt(cursor.getColumnIndex(NowPlayingColumns.PROCESS)));
            } finally {
                cursor.close();
            }
            return list;
        }
        return null;
    }

    interface PlaybackColumns extends BaseColumns {
        String TABLE_NAME = "Playback";

        String TITLE = "title";
        String ARTIST = "artist";
        String ALBUM_ID = "albumid";
        String DURATION = "time";
    }


    interface NowPlayingColumns {
        String TABLE_NAME = "NowPlaying";

        String POSITION = "position";
        String PROCESS = "process";
    }
}
