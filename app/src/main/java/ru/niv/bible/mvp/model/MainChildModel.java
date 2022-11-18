package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class MainChildModel extends Model {

    private final Context context;

    public interface Action {
        void onDuplicate();
        void onSuccess(int id,int newPosition);
    }

    public interface MainChild {
        void getCurrentState(String chapterName,int chapter,int page);
    }

    public MainChildModel(Context context) {
        super(context);
        this.context = context;
    }

    public void getInformation(int position, MainChild listener) {
        Cursor cursor = getBySql("select max(id) as id,chapter,page from (select id,chapter,page from text group by chapter, page limit "+position+")",null);
        if (cursor.moveToFirst()) {
            int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));

            Cursor cursorChapter = get(Static.tableChapter,"name","id = "+chapter,false,null);
            if (cursorChapter.moveToFirst()) {
                String chapterName = cursorChapter.getString(cursorChapter.getColumnIndex("name"));
                listener.getCurrentState(chapterName,chapter,page);
            }
            cursorChapter.close();
        }
        cursor.close();
    }

    public List<Item> getList(int chapter,int page) {
        int i = 1;
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableText,"id,text","chapter = "+chapter+" and page = "+page,false,null);
        while (cursor.moveToNext()) {
            String folderName = null;
            int folder = 0;
            boolean favorite = false;
            boolean underline = false;
            int color = 0;
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String text = cursor.getString(cursor.getColumnIndex("text"));

            if (total(Static.tableFavorite,"text_id = "+id,true) != 0) {
                Cursor cursorFavorite = get(Static.tableFavorite,"folder,favorite,underline,color,(select name from folder where id = folder and del = 0) as folderName","text_id = "+id,true,null);
                if (cursorFavorite.moveToFirst()) {
                    folderName = cursorFavorite.getString(cursorFavorite.getColumnIndex("folderName"));
                    folder = cursorFavorite.getInt(cursorFavorite.getColumnIndex("folder"));
                    favorite = cursorFavorite.getInt(cursorFavorite.getColumnIndex("favorite")) == 1;
                    underline = cursorFavorite.getInt(cursorFavorite.getColumnIndex("underline")) == 1;
                    color = cursorFavorite.getInt(cursorFavorite.getColumnIndex("color"));

                    folderName = folderName == null?context.getString(R.string.default_folder):folderName;
                }
                cursorFavorite.close();
            }
            list.add(new Item().main(id,i+" "+text,folderName,folder,favorite,underline,color,false));
            i++;
        }
        cursor.close();

        return list;
    }

    public List<Item> getListFolder() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableFolder,"id,name",null,true,"name asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().folderBottomSheet(id,name,true));
        }
        list.add(0,new Item().folderBottomSheet(0,context.getString(R.string.default_folder),true));
        list.get(list.size() - 1).setDivider(false);
        cursor.close();
        return list;
    }

    public void add(String name, Action listener) {
        if (duplicate(Static.tableFolder,"name = ?",new String[]{name},true)) listener.onDuplicate();
        else {
            int id = insertOrReplace(Static.folder,cv.addFolder(name));
            int position = total(Static.tableFolder,"name between(select min(name) from folder where del = 0) and '"+name+"' and del = 0 order by name asc",false);
            listener.onSuccess(id,position);
        }
    }

    public void deleteFavorite(int textId) {
        set(Static.tableFavorite,cv.delete(),"text_id = "+textId);
    }

    public void setFavorite(int folder,int textId, int favorite, int underline, int color) {
        if (duplicate(Static.tableFavorite,"folder = "+folder+" and text_id = "+textId,null,true)) {
            set(Static.tableFavorite,cv.editFavorite(folder,favorite,underline,color),"text_id = "+textId);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            insertOrReplace(Static.tableFavorite,cv.addFavorite(folder,textId,favorite,underline,color,formattedDate));
        }
    }

    public boolean getStateReadButton(int position) {
        return total(Static.tableRead,"position = "+position,true) != 0;
    }

    public boolean toggleReadButtonPosition(int position) {
        boolean result = false;
        if (duplicate(Static.tableRead,"position = "+position,null,true)) {
            set(Static.tableRead,cv.delete(),"position = "+position);
        } else {
            result = true;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            insertOrReplace(Static.tableRead,cv.addRead(position,formattedDate));
        }
        return result;
    }

}
