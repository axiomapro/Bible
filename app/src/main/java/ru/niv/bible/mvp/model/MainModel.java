package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.sqlite.Model;

public class MainModel extends Model {

    public MainModel(Context context) {
        super(context);
    }

    public interface Action {
        void onCheck(String type,int id);
    }

    public boolean isSupportHead() {
        boolean result;
        try {
            total(Static.tableText,"head = 1",false);
            result = true;
        } catch (SQLiteException e) {
            result = false;
        }
        return result;
    }

    @SuppressLint("Range")
    public String createJson() {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = getBySql("select chapter as id,(select name from chapter where id = chapter) as name,page from text group by chapter, page",null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",id);
                jsonObject.put("name",name);
                jsonObject.put("page",page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        cursor.close();
        return jsonArray.toString();
    }

    @SuppressLint("Range")
    public int getMaxPosition() {
        int result = 0;
        Cursor cursor = getBySql("select count(1) as total from (select id from text group by chapter,page)",null);
        if (cursor.moveToFirst()) result = cursor.getInt(cursor.getColumnIndex("total"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public int getPositionByChapterAndPage(int chapter,int page) {
        int result = 0;
        Cursor cursor = get(Static.tableText,"id","chapter = "+chapter+" and page = "+page,false,null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            result = total(Static.tableText,"id between(select min(id) from text) and "+id+" group by chapter,page",false);
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public String getChapterName(int chapter) {
        String result = null;
        Cursor cursor = get(Static.tableChapter,"name","id = "+chapter,false,null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public void checkOneNotification(Action listener) {
        Cursor cursor = get(Static.tablePlan,"id","notification is not null",false,"id asc limit 1");
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            listener.onCheck("reading plan",id);
        } else {
            Cursor cursorDailyVerse = get(Static.tableDailyVerse,"id","notification is not null",true,"id asc limit 1");
            if (cursorDailyVerse.moveToFirst()) {
                int id = cursorDailyVerse.getInt(cursorDailyVerse.getColumnIndex("id"));
                listener.onCheck("daily verse",id);
            } else listener.onCheck("empty",0);
            cursorDailyVerse.close();
        }
        cursor.close();
    }

}
