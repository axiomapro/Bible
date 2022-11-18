package ru.niv.bible.basic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.niv.bible.basic.component.Static;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;
    private static DatabaseHelper instance;
    private static final int DATABASE_VERSION = 2; // do not edit (17.11.21)
    private static boolean isOpenDb;
    private boolean copiedActualDb, isUpgrade;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context);
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, Static.database, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (copiedActualDb) return; // защита от обновления, когда ты только установил и версия базы данных больше единицы, то при установке срабатывает onUpgrade
        isUpgrade = true;
    }

    public void checkExistDB(Context context) {
        File database = context.getDatabasePath(Static.database);
        if (!database.exists()) {
            copiedActualDb = true;
            getWritableDatabase();
            close();
            copyDatabase(context);
        }
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(Static.database);
            String outFileName = context.getDatabasePath(Static.database).toString();
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public SQLiteDatabase getDb() {
        if (db == null) openDb();
        return db;
    }

    public boolean isUpgrade() {
        return isUpgrade;
    }

}

