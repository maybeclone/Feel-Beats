package com.silent.feelbeat.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.silent.feelbeat.database.MusicDBHelper;
import com.silent.feelbeat.database.models.QueryHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 8/13/2017.
 */

public class SearchHistory {

    private static final int MAX_ITEM_IN_DB = 25;
    private static SearchHistory searchHistory = null;
    private MusicDBHelper helper = null;

    private SearchHistory(Context context) {
        helper = MusicDBHelper.getInstance(context);
    }

    public static SearchHistory getInstance(Context context) {
        if (searchHistory == null) {
            searchHistory = new SearchHistory(context);
        }
        return searchHistory;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL," +
                        " %s INTEGER NOT NULL);", SearchHistoryColumns.TABLE_NAME, BaseColumns._ID,
                SearchHistoryColumns.SEARCH, SearchHistoryColumns.TIME);
        sqLiteDatabase.execSQL(create);
    }

    public void insertDB(List<Object> searchStrings) {
        if (searchStrings == null) {
            return;
        }
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            for (Object obj : searchStrings) {
                QueryHistory query = (QueryHistory) obj;
                ContentValues value = new ContentValues(2);
                value.put(SearchHistoryColumns.SEARCH, query.text);
                value.put(SearchHistoryColumns.TIME, query.time);
                sqLiteDatabase.insert(SearchHistoryColumns.TABLE_NAME, null, value);
            }
            deleteOutOfMax(sqLiteDatabase);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("SQLiteDatabase", ex.toString());
        } finally {
            sqLiteDatabase.endTransaction();
            /*
            *   Don't need to call close()
            *   Because the Database Connection is cached
            *   so repeat calls to getWritableDatabase() or getReadableDatabase()
            *   will return the SAME DB Connection instance
            *   as long as the DB Connection isn't closed between the calls
            * */
//            sqLiteDatabase.close();
        }
    }

    public void deleteOutOfMax(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.query(SearchHistoryColumns.TABLE_NAME, new String[]{SearchHistoryColumns.TIME},
                null, null, null, null, SearchHistoryColumns.TIME + " ASC");

        if (cursor != null && cursor.getCount() > MAX_ITEM_IN_DB) {
            try {
                int position = cursor.getCount() - MAX_ITEM_IN_DB;
                cursor.moveToPosition(position);
                long timing = cursor.getLong(cursor.getColumnIndex(SearchHistoryColumns.TIME));
                sqLiteDatabase.delete(SearchHistoryColumns.TABLE_NAME, SearchHistoryColumns.TIME + " < ?",
                        new String[]{String.valueOf(timing)});
            } finally {
                cursor.close();
            }
        }

    }

    private Cursor queryRecentHistorySearch(int limit) {
        return helper.getReadableDatabase().query(SearchHistoryColumns.TABLE_NAME, null, null, null, null, null,
                SearchHistoryColumns.TIME + " DESC", String.valueOf(limit));
    }

    public ArrayList<Object> getListHistorySearch() {
        ArrayList<Object> list = new ArrayList<>(MAX_ITEM_IN_DB);
        Cursor result = queryRecentHistorySearch(MAX_ITEM_IN_DB);
        if (result != null) {
            while (result.moveToNext()) {
                QueryHistory query = new QueryHistory(result.getString(result.getColumnIndex(SearchHistoryColumns.SEARCH)),
                        result.getLong(result.getColumnIndex(SearchHistoryColumns.TIME)));
                list.add(query);
            }
            result.close();
            return list;
        }
        return null;
    }

    public static void loadFakeData(SearchHistory searchHistory) {
        ArrayList<QueryHistory> queryHistories = new ArrayList<>();
        queryHistories.add(new QueryHistory("tr", 3023));
        queryHistories.add(new QueryHistory("ewrq", 33213));
        queryHistories.add(new QueryHistory("ccr", 5434));
        queryHistories.add(new QueryHistory("uyu", 45343));
        queryHistories.add(new QueryHistory("vbb", 21443));
//        searchHistory.insertDB(queryHistories);
    }


    interface SearchHistoryColumns extends BaseColumns {

        String TABLE_NAME = "hisotrytable";

        String SEARCH = "searchquery";

        String TIME = "timing";
    }
}
