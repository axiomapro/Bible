package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class ContentModel extends Model {

    public ContentModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Item> getList(int chapter,int page) {
        int total = 0;
        Cursor cursor;
        String excludeHead = Static.supportHead?" and head != 1":"";
        List<Item> list = new ArrayList<>();
        if (page == 0) cursor = get(Static.tableText,"max(page) as total","chapter = "+chapter,false,null);
        else cursor = get(Static.tableText,"count(1) as total","chapter = "+chapter+" and page = "+page+excludeHead,false,null);

        if (cursor.moveToFirst()) total = cursor.getInt(cursor.getColumnIndex("total"));
        for (int i = 1; i <= total; i++) {
            list.add(new Item().content(i));
        }

        cursor.close();
        return list;
    }

}
