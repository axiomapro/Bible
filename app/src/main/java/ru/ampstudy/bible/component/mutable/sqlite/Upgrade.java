package ru.ampstudy.bible.component.mutable.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;

public class Upgrade {

    private final UpgradeDatabase listener;
    private final Context context;
    private final DatabaseHelper databaseHelper;

    public interface UpgradeDatabase {
        void onSuccess();
    }

    public Upgrade(Context context, DatabaseHelper databaseHelper, UpgradeDatabase listener) {
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.listener = listener;
    }

    public void execute() {
        if (databaseHelper.getOldVersionDb() == 1) secondUpgrade(); // 1 to 2: folder, read, favorite
        if (databaseHelper.getOldVersionDb() == 2) thirdUpgrade(); // 2 to 3: folder, read, favorite, note, plan, reading plan
    }

    @SuppressLint("Range")
    private void secondUpgrade() {
        Cursor cursorFolder = databaseHelper.getDb().rawQuery("select name,del from folder",null);
        Cursor cursorRead = databaseHelper.getDb().rawQuery("select position,date,del from read",null);
        Cursor cursorFavorite = databaseHelper.getDb().rawQuery("select folder,text_id,favorite,underline,note,color,date,del from favorite",null);
        Log.d("testLog","Upgrade: folder: "+cursorFolder.getCount()+"; read: "+cursorRead.getCount()+"; favorite: "+cursorFavorite.getCount());
        databaseHelper.closeDb();
        context.deleteDatabase(Static.DATABASE_NAME);
        databaseHelper.checkExistDB(context);
        databaseHelper.openDb();

        ContentValues cvFolder = new ContentValues();
        while (cursorFolder.moveToNext()) {
            cvFolder.clear();
            cvFolder.put("name",cursorFolder.getString(cursorFolder.getColumnIndex("name")));
            cvFolder.put("del",cursorFolder.getInt(cursorFolder.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().folder(),null,cvFolder);
        }

        ContentValues cvRead = new ContentValues();
        while (cursorRead.moveToNext()) {
            cvRead.clear();
            cvRead.put("position",cursorRead.getInt(cursorRead.getColumnIndex("position")));
            cvRead.put("date",cursorRead.getString(cursorRead.getColumnIndex("date")));
            cvRead.put("del",cursorRead.getInt(cursorRead.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().read(),null,cvRead);
        }

        ContentValues cvFavorite = new ContentValues();
        while (cursorFavorite.moveToNext()) {
            cvFavorite.clear();
            cvFavorite.put("folder",cursorFavorite.getInt(cursorFavorite.getColumnIndex("folder")));
            cvFavorite.put("text_id",cursorFavorite.getInt(cursorFavorite.getColumnIndex("text_id")));
            cvFavorite.put("favorite",cursorFavorite.getInt(cursorFavorite.getColumnIndex("favorite")));
            cvFavorite.put("underline",cursorFavorite.getInt(cursorFavorite.getColumnIndex("underline")));
            cvFavorite.put("color",cursorFavorite.getInt(cursorFavorite.getColumnIndex("color")));
            cvFavorite.put("note",cursorFavorite.getString(cursorFavorite.getColumnIndex("note")));
            cvFavorite.put("date",cursorFavorite.getString(cursorFavorite.getColumnIndex("date")));
            cvFavorite.put("del",cursorFavorite.getInt(cursorFavorite.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().favorite(),null,cvFavorite);
        }

        cursorFolder.close();
        cursorRead.close();
        cursorFavorite.close();
        listener.onSuccess();
    }

    @SuppressLint("Range")
    private void thirdUpgrade() {
        Cursor cursorFolder = databaseHelper.getDb().rawQuery("select name,del from folder",null);
        Cursor cursorRead = databaseHelper.getDb().rawQuery("select position,date,del from read",null);
        Cursor cursorFavorite = databaseHelper.getDb().rawQuery("select folder,text_id,favorite,underline,note,color,date,del from favorite",null);
        Cursor cursorNote = databaseHelper.getDb().rawQuery("select name,text,date,del from note",null);
        Cursor cursorPlan = databaseHelper.getDb().rawQuery("select id,type,start,finish,notification,status from `plan`",null);
        Cursor cursorReadingPlan = databaseHelper.getDb().rawQuery("select id,status from reading_plan where status = 1",null);
        Log.d("testLog","Upgrade: folder: "+cursorFolder.getCount()+"; read: "+cursorRead.getCount()+"; favorite: "+cursorFavorite.getCount()+"; note: "+cursorNote.getCount()+"; plan: "+cursorPlan.getCount()+"; readingPlan: "+cursorReadingPlan.getCount());
        databaseHelper.closeDb();
        context.deleteDatabase(Static.DATABASE_NAME);
        databaseHelper.checkExistDB(context);
        databaseHelper.openDb();

        ContentValues cvFolder = new ContentValues();
        while (cursorFolder.moveToNext()) {
            cvFolder.clear();
            cvFolder.put("name",cursorFolder.getString(cursorFolder.getColumnIndex("name")));
            cvFolder.put("del",cursorFolder.getInt(cursorFolder.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().folder(),null,cvFolder);
        }

        ContentValues cvRead = new ContentValues();
        while (cursorRead.moveToNext()) {
            cvRead.clear();
            cvRead.put("position",cursorRead.getInt(cursorRead.getColumnIndex("position")));
            cvRead.put("date",cursorRead.getString(cursorRead.getColumnIndex("date")));
            cvRead.put("del",cursorRead.getInt(cursorRead.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().read(),null,cvRead);
        }

        ContentValues cvFavorite = new ContentValues();
        while (cursorFavorite.moveToNext()) {
            cvFavorite.clear();
            cvFavorite.put("folder",cursorFavorite.getInt(cursorFavorite.getColumnIndex("folder")));
            cvFavorite.put("text_id",cursorFavorite.getInt(cursorFavorite.getColumnIndex("text_id")));
            cvFavorite.put("favorite",cursorFavorite.getInt(cursorFavorite.getColumnIndex("favorite")));
            cvFavorite.put("underline",cursorFavorite.getInt(cursorFavorite.getColumnIndex("underline")));
            cvFavorite.put("color",cursorFavorite.getInt(cursorFavorite.getColumnIndex("color")));
            cvFavorite.put("note",cursorFavorite.getString(cursorFavorite.getColumnIndex("note")));
            cvFavorite.put("date",cursorFavorite.getString(cursorFavorite.getColumnIndex("date")));
            cvFavorite.put("del",cursorFavorite.getInt(cursorFavorite.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().favorite(),null,cvFavorite);
        }

        ContentValues cvNote = new ContentValues();
        while (cursorNote.moveToNext()) {
            cvNote.clear();
            cvNote.put("name",cursorNote.getString(cursorNote.getColumnIndex("name")));
            cvNote.put("text",cursorNote.getString(cursorNote.getColumnIndex("text")));
            cvNote.put("date",cursorNote.getString(cursorNote.getColumnIndex("date")));
            cvNote.put("del",cursorNote.getInt(cursorNote.getColumnIndex("del")));
            databaseHelper.getDb().insert(Config.table().note(),null,cvNote);
        }

        ContentValues cvPlan = new ContentValues();
        while (cursorPlan.moveToNext()) {
            cvPlan.clear();
            cvPlan.put("type",cursorPlan.getInt(cursorPlan.getColumnIndex("type")));
            cvPlan.put("start",cursorPlan.getString(cursorPlan.getColumnIndex("start")));
            cvPlan.put("finish",cursorPlan.getString(cursorPlan.getColumnIndex("finish")));
            cvPlan.put("notification",cursorPlan.getString(cursorPlan.getColumnIndex("notification")));
            cvPlan.put("status",cursorPlan.getInt(cursorPlan.getColumnIndex("status")));
            databaseHelper.getDb().update(Config.table().plan(),cvPlan,"id = "+cursorPlan.getInt(cursorPlan.getColumnIndex("id")),null);
        }

        ContentValues cvReadingPlan = new ContentValues();
        while (cursorReadingPlan.moveToNext()) {
            cvReadingPlan.clear();
            cvReadingPlan.put("status",cursorReadingPlan.getInt(cursorReadingPlan.getColumnIndex("status")));
            databaseHelper.getDb().update(Config.table().readingPlan(),cvReadingPlan,"id = "+cursorReadingPlan.getInt(cursorReadingPlan.getColumnIndex("id")),null);
        }

        cursorFolder.close();
        cursorRead.close();
        cursorFavorite.close();
        cursorNote.close();
        cursorPlan.close();
        cursorReadingPlan.close();
        listener.onSuccess();
    }

}