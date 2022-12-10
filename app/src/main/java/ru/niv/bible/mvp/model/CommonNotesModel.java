package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class CommonNotesModel extends Model {

    public CommonNotesModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableNote,"id,name,text,date",null,true,"date desc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            list.add(new Item().commonNotes(id,name,text,date));
        }
        cursor.close();
        return list;
    }

    public void add(String name, String text, FavoritesModel.Action listener) {
        if (duplicate(Static.tableNote,"name = ?",new String[]{name},true)) listener.onDuplicate();
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            int id = insertOrReplace(Static.tableNote,cv.addNote(name,text,formattedDate));
            listener.onSuccess(id,0);
        }
    }

    public void edit(String name, String text, int id, FavoritesModel.Action listener) {
        if (duplicate(Static.tableNote,"name = ? and id != ?",new String[]{name, String.valueOf(id)},true)) listener.onDuplicate();
        else {
            setById(Static.tableNote,cv.editNote(name,text),id);
            listener.onSuccess(0,0);
        }
    }

    public void delete(int id) {
        setById(Static.tableNote,cv.delete(),id);
    }

}
