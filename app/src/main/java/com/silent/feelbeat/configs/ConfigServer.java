package com.silent.feelbeat.configs;

/**
 * Created by silent on 5/16/2018.
 */
public class ConfigServer {

//    // localhost
    public static final String BASE_URL = "http://10.0.2.2:8008/api/";
//
    // LAN
//    public static final String BASE_URL = "http://192.168.1.4:80/api/";

    public static final String ACCOUNT_URL = BASE_URL + "account/";
    public static final String LOGIN_URL = ACCOUNT_URL + "login";
    public static final String REGISTER_URL = ACCOUNT_URL + "register";
    public static final String PROFILE_URL = ACCOUNT_URL + "me";
    public static final String LOGOUT_URL = ACCOUNT_URL  + "logout";
    public static final String CHANGE_PASSWORD_URL = ACCOUNT_URL + "changepassword";
    public static final String GET_SONGS_URL = BASE_URL + "song";
    public static final String POST_RATING_URL = BASE_URL + "rating";
    public static final String GET_LISTENING_URL = GET_SONGS_URL + "/listening";
    public static final String PLAYLIST_URL = BASE_URL + "playlist";

    public static final String ARGU_USERNAME_LOGIN = "username";
    public static final String ARGU_PASSWORD_LOGIN = "password";
    public static final String ARGU_GRANT_TYPE_LOGIN = "grant_type";

    public static final String ARGU_EMAIL_REGISTER = "Email";
    public static final String ARGU_PASSWORD_REGISTER = "Password";
    public static final String ARGU_NAME_REGISTER = "Name";
    public static final String ARGU_BIRTHDAY_REGISTER = "Birthday";
    public static final String ARGU_GENDER_REGISTER = "Gender";


    public static final String ARGU_OLD_PASSWORD_PASS = "OldPassword";
    public static final String ARGU_NEW_PASSWORD = "NewPassword";

    public static final String ARGU_EMAIL_RATING = "EmailUser";
    public static final String ARGU_SONG_ID_RATING = "SongId";
    public static final String ARGU_RATE_RATING = "Rate";

    public static final String ARGU_ID_PLAYLIST = "Id";
    public static final String ARGU_NAME_PLAYLIST = "Name";
    public static final String ARGU_DATE_CREATED_PLAYLIST = "DateCreated";

}
