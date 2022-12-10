package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;
import ru.niv.bible.mvp.contract.DailyVerseContract;

public class DailyVerseModel extends Model {

    public interface Action {
        void onDuplicate();
        void onSuccess(int id,String chapterName,String text,int chapter,int page,int position);
    }

    public interface Refresh {
        void onRefresh(String chapterName,String text,int chapter,int page,int position);
    }

    public DailyVerseModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableDailyVerse,"id,name,chapters,notification",null,true,"updated desc,sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String chapters = cursor.getString(cursor.getColumnIndex("chapters"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));

            Cursor cursorSub = get(Static.tableText,"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+chapters+")",false,"random() limit 1");
            if (cursorSub.moveToFirst()) {
                int chapter = cursorSub.getInt(cursorSub.getColumnIndex("chapter"));
                int page = cursorSub.getInt(cursorSub.getColumnIndex("page"));
                int position = cursorSub.getInt(cursorSub.getColumnIndex("position"));
                String text = cursorSub.getString(cursorSub.getColumnIndex("text"));
                String chapterName = cursorSub.getString(cursorSub.getColumnIndex("chapterName"));
                list.add(new Item().dailyVerse(id,name,text,chapterName,chapters,notification,chapter,page,position));
            }
            cursorSub.close();
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> getListEditor(int type) {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableChapter,"id,name","type = "+type,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().dailyVerseEditor(id,name,false));
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public void refresh(int id,Refresh listener) {
        Cursor cursor = get(Static.tableDailyVerse,"chapters","id = "+id,true,null);
        if (cursor.moveToFirst()) {
            Cursor cursorSub = get(Static.tableText,"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+cursor.getString(cursor.getColumnIndex("chapters"))+")",false,"random() limit 1");
            if (cursorSub.moveToFirst()) {
                int chapter = cursorSub.getInt(cursorSub.getColumnIndex("chapter"));
                int page = cursorSub.getInt(cursorSub.getColumnIndex("page"));
                int position = cursorSub.getInt(cursorSub.getColumnIndex("position"));
                String text = cursorSub.getString(cursorSub.getColumnIndex("text"));
                String chapterName = cursorSub.getString(cursorSub.getColumnIndex("chapterName"));
                listener.onRefresh(chapterName,text,chapter,page,position);
            }
            cursorSub.close();
        }
        cursor.close();
    }

    @SuppressLint("Range")
    public void add(String name,String chapters,String notification, Action listener) {
        if (duplicate(Static.tableDailyVerse,"name = ?",new String[]{name},true)) listener.onDuplicate();
        else {
            int id = insertOrReplace(Static.tableDailyVerse,cv.addDailyVerse(name,chapters,converter.getDatetime(),notification));
            Cursor cursor = get(Static.tableText,"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+chapters+")",false,"random() limit 1");
            if (cursor.moveToFirst()) {
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int page = cursor.getInt(cursor.getColumnIndex("page"));
                int position = cursor.getInt(cursor.getColumnIndex("position"));
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
                listener.onSuccess(id,chapterName,text,chapter,page,position);
            }
            cursor.close();
        }
    }

    @SuppressLint("Range")
    public void edit(int id, String name, String chapters,String notification, Action listener) {
        if (duplicate(Static.tableDailyVerse,"name = ? and id != ?",new String[]{name, String.valueOf(id)},true)) listener.onDuplicate();
        else {
            setById(Static.tableDailyVerse,cv.editDailyVerse(name,chapters,converter.getDatetime(),notification),id);
            Cursor cursor = get(Static.tableText,"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+chapters+")",false,"random() limit 1");
            if (cursor.moveToFirst()) {
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int page = cursor.getInt(cursor.getColumnIndex("page"));
                int position = cursor.getInt(cursor.getColumnIndex("position"));
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
                listener.onSuccess(id,chapterName,text,chapter,page,position);
            }
            cursor.close();
        }
    }

    public void delete(int id) {
        setById(Static.tableDailyVerse,cv.delete(),id);
    }

    public Cursor getNotifications() {
        return getBySql("select id,notification from daily_verse where notification is not null and del = 0 group by notification",null);
    }

}
