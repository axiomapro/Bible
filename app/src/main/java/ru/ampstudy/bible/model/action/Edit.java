package ru.ampstudy.bible.model.action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.component.mutable.contentvalue.ExtraCV;
import ru.ampstudy.bible.component.mutable.sqlite.Model;
import ru.ampstudy.bible.mediator.contract.ResultContract;

public class Edit extends Model {

    private final ExtraCV extra;

    public Edit(Context context) {
        super(context);
        extra = new ExtraCV();
    }

    public void favorite(int id, ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().folder(),"name = ? and id != ?",new String[]{cv.getAsString("name"), String.valueOf(id)},true)) listener.duplicate();
        else {
            setById(Config.table().folder(),cv,id);
            listener.extra(null);
        }
    }

    public void commonNotes(int id, ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().note(),"name = ? and id != ?",new String[]{cv.getAsString("name"), String.valueOf(id)},true)) listener.duplicate();
        else {
            setById(Config.table().note(),cv,id);
            listener.extra(null);
        }
    }

    @SuppressLint("Range")
    public void dailyVerse(int id,ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().dailyVerse(),"name = ? and id != ?",new String[]{cv.getAsString("name"), String.valueOf(id)},true)) listener.duplicate();
        else {
            setById(Config.table().dailyVerse(),cv,id);
            Cursor cursor = get(Config.table().text(),"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+cv.getAsString("chapters")+")",false,"random() limit 1");
            if (cursor.moveToFirst()) {
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int page = cursor.getInt(cursor.getColumnIndex("page"));
                int position = cursor.getInt(cursor.getColumnIndex("position"));
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
                listener.extra(extra.dailyVerse(0,chapter,page,position,chapterName,text));
            }
            cursor.close();
        }
    }

}