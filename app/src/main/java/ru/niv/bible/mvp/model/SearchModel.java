package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class SearchModel extends Model {

    public SearchModel(Context context) {
        super(context);
    }

    public List<Item> getList(String query,int tab,boolean exact,boolean partial) {
        String sortBy = "";
        String[] arg;
        if (exact) arg = new String[]{query+"%"};
        else if (partial) arg = new String[]{"% "+query+" %"};
        else arg = new String[]{"%"+query+"%"};

        if (tab == 2) sortBy = " and ch.type = 1";
        if (tab == 3) sortBy = " and ch.type = 2";

        List<Item> list = new ArrayList<>();
        Cursor cursor = getBySql("select t.id,ch.name,t.chapter,t.page,t.position,t.text from text t inner join chapter ch on t.chapter = ch.id where t.text like ?"+sortBy+" order by t.id asc",arg);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            list.add(new Item().search(id,name+" "+page+":"+position,text,chapter,page,position,false));
        }
        cursor.close();
        return list;
    }

    public String getTextByIds(String ids) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Static.tableText+" t inner join chapter ch on t.chapter = ch.id","ch.name,t.page,t.position,t.text","t.id in("+ids.substring(1)+")",false,"t.id asc");
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));

            String end = "";
            if (i != cursor.getCount()) {
                end = "\n\n";
                i++;
            }
            String item = name+" "+page+":"+position+"\n"+text+end;
            result.append(item);
        }
        cursor.close();
        return result.toString();
    }

}
