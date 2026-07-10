package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "FoodSaverSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Simpan data user yang berhasil login
    public void saveSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_UID, user.getUid());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    // Hapus semua data sesi saat logout
    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUid() {
        return pref.getString(KEY_UID, null);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public boolean isPartner() {
        return Constants.ROLE_PARTNER.equals(getRole());
    }

    public boolean isUser() {
        return Constants.ROLE_USER.equals(getRole());
    }
}