package com.silent.feelbeat.abstraction;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/17/2017.
 */

public abstract class LoaderDB<T extends Item> {
    protected ContentResolver contentResolver;

    public LoaderDB() {

    }

    public LoaderDB(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void setContentResolver(ContentResolver resolver) {
        this.contentResolver = resolver;
    }

    public ContentResolver getResolver() {
        return contentResolver;
    }

    public abstract Cursor getCursor();

    public abstract List<Item> getList();

    public abstract CursorLoader getCursorLoader(Context context);
}
