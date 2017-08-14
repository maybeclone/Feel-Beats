package com.silent.feelbeat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.silent.feelbeat.database.table.SearchHistory;

/**
 * Created by silent on 8/13/2017.
 */

public class MusicDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "feelbeat";
    private static final int VERSION = 1;
    private Context context;
    private static MusicDBHelper helper = null;

    private MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public static MusicDBHelper getInstance(Context context){
        if(helper == null){
            helper = new MusicDBHelper(context.getApplicationContext());
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SearchHistory.getInstance(context).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
