package ru.niv.bible.component.mutable.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Static;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;
    private static DatabaseHelper instance;
    private static final int DATABASE_VERSION = 3; // real version 3, do not edit (05.12.22)
    private int oldVersionDb;
    private static boolean isOpenDb;
    private boolean isActualDb, isUpgrade;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context);
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, Static.DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(Config.log().general(),"Construct");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Config.log().general(),"onCreate");
        // db.execSQL("create table main (id integer primary key autoincrement,name text,status integer,del integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("testLog","onUpgrade: "+oldVersion+"/"+newVersion);
        if (isActualDb) return; // защита от обновления, когда ты только установил и версия базы данных больше единицы, то при установке срабатывает onUpgrade
        isUpgrade = true;
        this.oldVersionDb = oldVersion;
    }

    public void checkExistDB(Context context) {
        File database = context.getDatabasePath(Static.DATABASE_NAME);
        if (!database.exists()) {
            isActualDb = true;
            getWritableDatabase();
            close();
            copyDatabase(context);
        }
    }

    public void copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(Static.DATABASE_NAME);
            OutputStream outputStream = new FileOutputStream(context.getDatabasePath(Static.DATABASE_NAME).toString());
            byte[]buff = new byte[1024];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String table, ContentValues cv, String where) {
        getDb().update(table, cv, where, null);
    }

    public int insert(String table,ContentValues cv) {
        return Integer.parseInt(String.valueOf(getDb().insert(table,null,cv)));
    }

    public int total(String sql) {
        Cursor cursor = getDb().rawQuery(sql,null);
        int total = cursor.getCount();
        cursor.close();
        return total;
    }

    public void openDb() {
        if (!isOpenDb) {
            db = instance.getWritableDatabase();
            isOpenDb = true;
        }
    }

    public void closeDb() {
        if (!isOpenDb) return;
        instance.close();
        isOpenDb = false;
    }

    public SQLiteDatabase getDb() {
        if (db == null) openDb();
        return db;
    }

    public boolean isUpgrade() {
        return isUpgrade;
    }

    public int getOldVersionDb() {
        return oldVersionDb;
    }

}