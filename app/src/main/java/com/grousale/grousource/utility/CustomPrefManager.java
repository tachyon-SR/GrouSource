package com.grousale.grousource.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomPrefManager {
    private SharedPreferences sharedPreferences;

    public CustomPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_MANAGER, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}
