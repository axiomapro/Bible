package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class ReadingPlanChildModel extends Model {

    public ReadingPlanChildModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Item> getList(int plan,int type,int day) {
        List<Item> list = new ArrayList<>();

        Cursor cursor = getBySql("select id,chapter_order,page,status from reading_plan where plan_id = "+plan+" and type = "+type+" and day = "+day,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chapterOrder = cursor.getInt(cursor.getColumnIndex("chapter_order"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            Cursor cursorSecond = getBySql("select max(id) as id,name from (select id,name from chapter order by type asc limit "+chapterOrder+")",null);
            if (cursorSecond.moveToFirst()) {
                int chapter = cursorSecond.getInt(cursorSecond.getColumnIndex("id"));
                String name = cursorSecond.getString(cursorSecond.getColumnIndex("name"));
                list.add(new Item().readingPlanChild(id,name+" "+page,chapter,page,0,status == 1));
            }
            cursorSecond.close();
        }
        cursor.close();
        return list;
    }

    public void updateView(int id) {
        setById(Static.tableReadingPlan,cv.status(1),id);
    }

    public void updateItemsByDay(int plan,int type,int day,boolean status) {
        set(Static.tableReadingPlan,cv.status(status?1:0),"plan_id = "+plan+" and type = "+type+" and day = "+day);
    }

}
