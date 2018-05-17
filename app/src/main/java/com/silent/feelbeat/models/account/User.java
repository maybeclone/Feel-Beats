package com.silent.feelbeat.models.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by silent on 5/16/2018.
 */
public class User {

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String name, String birthday, int gender) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        String str = birthday.replace("T", " ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        try {
            this.birthday = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String email;

    public String password;

    public String name;

    public String accessToken;

    public Date birthday;

    public int gender;


}
