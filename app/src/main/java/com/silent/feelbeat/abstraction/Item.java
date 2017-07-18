package com.silent.feelbeat.abstraction;

/**
 * Created by silent on 7/17/2017.
 */

public class Item {

    public long id;
    public String title;

    public Item(){
        this.id = -1;
        this.title = "";
    }

    public Item(long id, String title){
        this.id = id;
        this.title = title;
    }
}
