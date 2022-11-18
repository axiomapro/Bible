package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.sqlite.Model;

public class MainModel extends Model {

    public interface Main {
        void getCurrentState(String chapter,int page);
    }

    public MainModel(Context context) {
        super(context);
    }

    public int getMaxPosition() {
        int result = 0;
        Cursor cursor = getBySql("select count(1) as total from (select id from text group by chapter,page)",null);
        if (cursor.moveToFirst()) result = cursor.getInt(cursor.getColumnIndex("total"));
        cursor.close();
        return result;
    }

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

    public String getChapterName(int chapter) {
        String result = null;
        Cursor cursor = get(Static.tableChapter,"name","id = "+chapter,false,null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return result;
    }

    public void getChapterAndPage(int position,Main listener) {
        Cursor cursor = getBySql("select max(id) as id,chapter,page from (select id,chapter,page from text group by chapter, page limit "+position+")",null);
        if (cursor.moveToFirst()) {
            int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));

            Cursor cursorChapter = get(Static.tableChapter,"name","id = "+chapter,false,null);
            if (cursorChapter.moveToFirst()) {
                String chapterName = cursorChapter.getString(cursorChapter.getColumnIndex("name"));
                listener.getCurrentState(chapterName,page);
            }
            cursorChapter.close();
        }
        cursor.close();
    }

}
