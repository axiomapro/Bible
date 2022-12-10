package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class ReadingPlanModel extends Model {

    public ReadingPlanModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tablePlan,"id,name,text,type,start,status",null,false,"start desc,sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            list.add(new Item().readingPlan(id));
        }
        cursor.close();
        return list;
    }

    public Cursor getNotifications() {
        return getBySql("select id,notification from plan where notification is not null group by notification",null);
    }

}
