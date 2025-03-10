package ru.ampstudy.bible.component.immutable.box;

import android.content.Context;
import android.content.SharedPreferences;

public class Param {

    private SharedPreferences sPref;
    private final Context mContext;
    private final String FILENAME = "config";

    public Param(Context context) {
        mContext = context;
    }

    public void setString(String type, String param) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(type, param);
        ed.apply();
    }

    public void setInt(String type, int num) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(type, num);
        ed.apply();
    }

    public void setBoolean(String type, boolean status) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(type, status);
        ed.apply();
    }

    public String getString(String type) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        return sPref.getString(type, null);
    }

    public int getInt(String type) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        int defValue = 0;
        if (type.equals(Config.param().font()) || type.equals(Config.param().language())) defValue = 1;
        if (type.equals(Config.param().readingSpeed())) defValue = 6;
        return sPref.getInt(type, defValue);
    }

    public boolean getBoolean(String type) {
        sPref = mContext.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        boolean defValue = false;
        if (type.equals(Config.param().theme()) || type.equals(Config.param().rate())) defValue = true;
        return sPref.getBoolean(type, defValue);
    }

}