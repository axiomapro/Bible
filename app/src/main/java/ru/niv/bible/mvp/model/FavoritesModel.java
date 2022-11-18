package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class FavoritesModel extends Model {

    private final Context context;

    public interface Action {
        void onDuplicate();
        void onSuccess(int id,int newPosition);
    }

    public FavoritesModel(Context context) {
        super(context);
        this.context = context;
    }

    public List<Item> getList(int tab) {
        List<Item> list = new ArrayList<>();
        Cursor cursor;
        if (tab == 1) cursor = get(Static.tableFolder,"id,name",null,true,"name asc");
        else cursor = get(Static.tableChapter,"id,name",null,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int total = total(Static.tableFavorite,"folder = "+id,true);
            if (tab == 2) {
                total = get(Static.tableFavorite+" f inner join text t on f.text_id = t.id",null,"t.chapter = "+id+" and f.del = 0",false,null).getCount();
            }
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().favorites(id,name,total));
        }
        if (tab == 1) list.add(0,new Item().favorites(0,context.getString(R.string.default_folder),total(Static.tableFavorite,"folder = 0",true)));

        cursor.close();
        return list;
    }

    public void add(String name,Action listener) {
        if (duplicate(Static.tableFolder,"name = ?",new String[]{name},true)) listener.onDuplicate();
        else {
            int id = insertOrReplace(Static.folder,cv.addFolder(name));
            int position = total(Static.tableFolder,"name between(select min(name) from folder where del = 0) and '"+name+"' and del = 0 order by name asc",false);
            listener.onSuccess(id,position);
        }
    }

    public void edit(String name,int id,Action listener) {
        if (duplicate(Static.folder,"name = ? and id != ?",new String[]{name, String.valueOf(id)},true)) listener.onDuplicate();
        else {
            setById(Static.folder,cv.editFolder(name),id);
            listener.onSuccess(0,0);
        }
    }

    public void delete(int id) {
        setById(Static.folder,cv.delete(),id);
        set(Static.tableFavorite,cv.delete(),"folder = "+id);
    }

}
