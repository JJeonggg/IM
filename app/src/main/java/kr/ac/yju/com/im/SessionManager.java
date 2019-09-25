package kr.ac.yju.com.im;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";
    public static final String PASSWORD = "PASSWORD";
    public static final String CLASSFI = "CLASSFI";
    public static final String COMPANY = "COMPANY";
    public static final String TELE = "TELE";
    public static final String ADDRESS = "ADDRESS";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String NICKNAME = "NICKNAME";
    public static final String PHOTO = "PHOTO";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String id, String Password, String classfi, String company, String tele, String address, String birthday, String nickname, String photo){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(ID, id);
        editor.putString(PASSWORD,Password);
        editor.putString(CLASSFI, classfi);
        editor.putString(COMPANY, company);
        editor.putString(TELE, tele);
        editor.putString(ADDRESS, address);
        editor.putString(BIRTHDAY, birthday);
        editor.putString(NICKNAME, nickname);
        editor.putString(PHOTO, photo);

        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){

        if (!this.isLoggin()){
            Intent i = new Intent(context, loginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        user.put(CLASSFI, sharedPreferences.getString(CLASSFI, null));
        user.put(COMPANY, sharedPreferences.getString(COMPANY, null));
        user.put(TELE, sharedPreferences.getString(TELE, null));
        user.put(ADDRESS, sharedPreferences.getString(ADDRESS, null));
        user.put(BIRTHDAY, sharedPreferences.getString(BIRTHDAY, null));
        user.put(NICKNAME, sharedPreferences.getString(NICKNAME, null));
        user.put(PHOTO, sharedPreferences.getString(PHOTO, null));

        return user;
    }

    public void logout(){

        editor.clear();
        editor.commit();
        Intent i = new Intent(context, loginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();

    }
}