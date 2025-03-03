package ru.ampstudy.bible.model.action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.component.mutable.contentvalue.AddCV;
import ru.ampstudy.bible.component.mutable.contentvalue.ExtraCV;
import ru.ampstudy.bible.component.mutable.sqlite.Model;
import ru.ampstudy.bible.mediator.contract.ResultContract;

public class Add extends Model {

    private final AddCV add;
    private final ExtraCV extra;

    public Add(Context context) {
        super(context);
        add = new AddCV();
        extra = new ExtraCV();
    }

    public void folder(ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().folder(),"name = ?",new String[]{cv.getAsString("name")},true)) listener.duplicate();
        else {
            int id = insertOrReplace(Config.table().folder(),add.folder(cv.getAsString("name")));
            int position = total(Config.table().folder(),"name between(select min(name) from folder where del = 0) and '"+cv.getAsString("name")+"' and del = 0 order by name asc",false);
            listener.extra(extra.item(id,position));
        }
    }

    public void commonNotes(ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().note(),"name = ?",new String[]{cv.getAsString("name")},true)) listener.duplicate();
        else {
            int id = insertOrReplace(Config.table().note(),add.note(cv.getAsString("name"), cv.getAsString("text"),datetime.getDatetime()));
            listener.extra(extra.item(id,0));
        }
    }

    @SuppressLint("Range")
    public void dailyVerse(ContentValues cv, ResultContract listener) {
        if (duplicate(Config.table().dailyVerse(),"name = ?",new String[]{cv.getAsString("name")},true)) listener.duplicate();
        else {
            int id = insertOrReplace(Config.table().dailyVerse(),add.dailyVerse(cv.getAsString("name"),cv.getAsString("chapters"), datetime.getDatetime(),cv.getAsString("notification")));
            Cursor cursor = get(Config.table().text(),"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+cv.getAsString("chapters")+")",false,"random() limit 1");
            if (cursor.moveToFirst()) {
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int page = cursor.getInt(cursor.getColumnIndex("page"));
                int position = cursor.getInt(cursor.getColumnIndex("position"));
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
                listener.extra(extra.dailyVerse(id,chapter,page,position,chapterName,text));
            }
            cursor.close();
        }
    }

}