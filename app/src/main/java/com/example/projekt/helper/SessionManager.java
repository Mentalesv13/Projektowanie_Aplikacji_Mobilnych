package com.example.projekt.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
 
    // Shared Preferences
    SharedPreferences pref;
 
    Editor editor;
    Context _context;
 
    // Shared pref mode
    int PRIVATE_MODE = 0;
 
    // Shared preferences file name
    private static final String PREF_NAME = "Projekt";
     
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_EVENTUP = "isEventUp";
    private static final String KEY_IS_SPECTALEUP = "isSpectacleUp";
    private static final String KEY_IS_REPERTOIREUP = "isRepertoireUp";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    public void setLogin(boolean isLoggedIn) {
 
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
 
        // commit changes
        editor.commit();
 
        Log.d(TAG, "User login session modified!");
    }

    public void setEvent(boolean isEventUp) {

        editor.putBoolean(KEY_IS_EVENTUP, isEventUp);

        // commit changes
        editor.commit();

        Log.d(TAG, "Event list session modified!");
    }
    public void setSpectacle(boolean isSpectacleUp) {

        editor.putBoolean(KEY_IS_SPECTALEUP, isSpectacleUp);

        // commit changes
        editor.commit();

        Log.d(TAG, "Spectacle list session modified!");
    }

    public void setRepertoire(boolean isRepertoireUp) {

        editor.putBoolean(KEY_IS_REPERTOIREUP, isRepertoireUp);

        // commit changes
        editor.commit();

        Log.d(TAG, "Repertoire list session modified!");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public boolean isEventUp(){
        return pref.getBoolean(KEY_IS_EVENTUP, false);
    }
    public boolean isSpectacleUp(){
        return pref.getBoolean(KEY_IS_SPECTALEUP, false);
    }
    public boolean isRepertoireUp(){
        return pref.getBoolean(KEY_IS_REPERTOIREUP, false);
    }


}