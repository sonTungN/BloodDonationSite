package com.sontung.blood.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sontung.blood.model.User;
import com.sontung.blood.shared.Keys;

public class LocalStorageManager {
    private Context context;
    private final SharedPreferences preferences;
    
    public LocalStorageManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(Keys.LOCAL_STORAGE_KEY, Context.MODE_PRIVATE);
    }
    
    public void setCurrentUser(User currentUser) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String data = gson.toJson(currentUser);
        editor.putString(Keys.CURRENT_USER_KEY, data);
        editor.apply();
    }
    
    public User getCurrentUser() {
        String data = preferences.getString(Keys.CURRENT_USER_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(data, User.class);
    }
    
    public void setCacheUser(User cacheUser) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String currentUserJson = gson.toJson(cacheUser);
        editor.putString(Keys.CACHE_USER_KEY, currentUserJson);
        editor.apply();
    }
    
    public User getCacheUser() {
        String data = preferences.getString(Keys.CACHE_USER_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(data, User.class);
    }
    
    public void clearCacheUser() {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String data = gson.toJson(null);
        editor.putString(Keys.CACHE_USER_KEY, data);
        editor.apply();
    }
    
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
    
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    
    public String getString(String key) {
        return preferences.getString(key, null);
    }
    
    public void clearEditor() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
