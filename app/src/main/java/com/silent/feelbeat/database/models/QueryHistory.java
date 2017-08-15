package com.silent.feelbeat.database.models;

/**
 * Created by silent on 8/15/2017.
 */

public class QueryHistory {

    public String text;
    public long time;

    public QueryHistory(){

    }

    public QueryHistory(String text, long time) {
        this.text = text;
        this.time = time;
    }
}
