package ru.niv.bible.basic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.niv.bible.basic.component.Converter;

public class Model {

    private final DatabaseHelper databaseHelper;
    protected ContentValue cv;
    protected Converter converter;

    public Model(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
        cv = new ContentValue();
        converter = new Converter();
    }

    protected int insertOrReplace(String table, ContentValues cv) {
        int result;
        Cursor cursor = databaseHelper.getDb().rawQuery("select id from "+table+" where del = 1",null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("id"));
            databaseHelper.update(table,cv,"id = "+result);
        } else result = databaseHelper.insert(table,cv);
        cursor.close();
        return result;
    }

    protected boolean duplicate(String table,String where,String[] args,boolean del) {
        boolean result = true;
        String sortDel = "";
        if (del) sortDel = " and del = 0";
        Cursor cursor = databaseHelper.getDb().rawQuery("select count(1) as total from "+table+" where "+where+sortDel,args);
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            if (total == 0) result = false;
        }
        cursor.close();
        return result;
    }

    protected int total(String table,String where,boolean del) {
        String whereSort = "";
        String delSort = "";
        if (where != null) whereSort = " where "+where;
        if (del) delSort = where == null?" where del = 0":" and del = 0";
        return databaseHelper.total("select id from "+table+whereSort+delSort);
    }

    protected Cursor get(String table, String column, String where, boolean del, String orderBy) {
        String whereSort = "";
        String delSort = "";
        String orderSort = "";
        if (where != null) whereSort = " where "+where;
        if (del) delSort = where == null?" where del = 0":" and del = 0";
        if (orderBy != null) orderSort = " order by "+orderBy;
        return databaseHelper.getDb().rawQuery("select "+column+" from "+table+whereSort+delSort+orderSort,null);
    }

    protected Cursor getWithArgs(String table, String column, String where,String[] args,boolean del,String orderBy) {
        String whereSort = "";
        String delSort = "";
        String orderSort = "";
        if (where != null) whereSort = " where "+where;
        if (del) delSort = where == null?" where del = 0":" and del = 0";
        if (orderBy != null) orderSort = " order by "+orderBy;
        return databaseHelper.getDb().rawQuery("select "+column+" from "+table+whereSort+delSort+orderSort,args);
    }

    protected Cursor getBySql(String sql,String[] args) {
        return databaseHelper.getDb().rawQuery(sql,args);
    }

    protected void set(String table,ContentValues cv,String where) {
        databaseHelper.update(table,cv,where);
    }

    protected void setById(String table,ContentValues cv,int id) {
        databaseHelper.update(table,cv,"id = "+id);
    }
}
