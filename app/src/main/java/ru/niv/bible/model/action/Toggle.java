package ru.niv.bible.model.action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.mutable.contentvalue.ExtraCV;
import ru.niv.bible.component.mutable.contentvalue.ToggleCV;
import ru.niv.bible.component.mutable.sqlite.Model;

public class Toggle extends Model {

    private final ToggleCV cv;
    private final ExtraCV extra;

    public Toggle(Context context) {
        super(context);
        cv = new ToggleCV();
        extra = new ExtraCV();
    }

    @SuppressLint("Range")
    public boolean execute(String table,String column,int id) {
        int status = 0;
        Cursor cursor = get(table,column,"id = "+id,false,null);
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(column)) == 1?0:1;
            setById(table,getCv(column,status),id);
        }
        cursor.close();
        return status == 1;
    }

    public boolean toggleReadButtonPositionMainChild(int position) {
        boolean result = false;
        if (duplicate(Config.table().read(),"position = "+position,null,true)) {
            set(Config.table().read(),cv.del(),"position = "+position);
        } else {
            result = true;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String formattedDate = formatter.format(date);

            ContentValues cv = new ContentValues();
            cv.clear();
            cv.put("position",position);
            cv.put("date",formattedDate);
            cv.put("del",0);
            insertOrReplace(Config.table().read(),cv);
        }
        return result;
    }

    public void deleteFavoriteMainChild(int textId) {
        set(Config.table().favorite(),cv.del(),"text_id = "+textId);
    }

    public void deleteFolder(int id) {
        setById(Config.table().folder(),cv.del(),id);
        set(Config.table().favorite(),cv.del(),"folder = "+id);
    }

    private ContentValues getCv(String column, int status) {
        ContentValues result;
        if (column.equals(Config.column().favorite())) result = cv.favorite(status);
        else if (column.equals(Config.column().status())) result = cv.status(status);
        else result = cv.del(status);
        return result;
    }

}