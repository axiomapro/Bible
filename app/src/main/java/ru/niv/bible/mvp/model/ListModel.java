package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class ListModel extends Model {

    public ListModel(Context context) {
        super(context);
    }

    public List<Item> getList(int tab) {
        String where = null;
        if (tab == 2) where = "type = 1";
        if (tab == 3) where = "type = 2";

        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableChapter,"id,type,name",where,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().list(id,type,name));
        }
        cursor.close();
        return list;
    }

    public int getTabByPosition(int position) {
        int result = 0;
        Cursor cursor = getBySql("select max(id) as id,(select type from chapter where id = chapter) as type from (select id,chapter from text group by chapter, page limit "+position+")",null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("type"));
        }
        cursor.close();
        return result + 1;
    }

}
