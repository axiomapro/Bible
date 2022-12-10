package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class MainChildModel extends Model {

    private final Context context;

    public interface Action {
        void onDuplicate();
        void onSuccess(int id,int newPosition);
    }

    public MainChildModel(Context context) {
        super(context);
        this.context = context;
    }

    @SuppressLint("Range")
    public List<Item> getList(int chapter,int page) {
        String columnHead = Static.supportHead?",head":"";
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableText,"id,text"+columnHead,"chapter = "+chapter+" and page = "+page,false,null);
        while (cursor.moveToNext()) {
            String note = null;
            String folderName = null;
            int folder = 0;
            boolean favorite = false;
            boolean underline = false;
            boolean head = false;
            if (Static.supportHead) head = cursor.getInt(cursor.getColumnIndex("head")) == 1;

            int color = 0;
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String text = cursor.getString(cursor.getColumnIndex("text"));

            if (total(Static.tableFavorite,"text_id = "+id,true) != 0) {
                Cursor cursorFavorite = get(Static.tableFavorite,"folder,note,favorite,underline,color,(select name from folder where id = folder and del = 0) as folderName","text_id = "+id,true,null);
                if (cursorFavorite.moveToFirst()) {
                    note = cursorFavorite.getString(cursorFavorite.getColumnIndex("note"));
                    folderName = cursorFavorite.getString(cursorFavorite.getColumnIndex("folderName"));
                    folder = cursorFavorite.getInt(cursorFavorite.getColumnIndex("folder"));
                    favorite = cursorFavorite.getInt(cursorFavorite.getColumnIndex("favorite")) == 1;
                    underline = cursorFavorite.getInt(cursorFavorite.getColumnIndex("underline")) == 1;
                    color = cursorFavorite.getInt(cursorFavorite.getColumnIndex("color"));

                    folderName = folderName == null?context.getString(R.string.default_folder):folderName;
                }
                cursorFavorite.close();
            }
            list.add(new Item().main(id,text.replaceAll("(?i)<br */?>","\n").replaceAll("<(.*?)>",""),note,folderName,folder,favorite,underline,color,head,false));
        }
        cursor.close();

        return list;
    }

    @SuppressLint("Range")
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

    @SuppressLint("Range")
    public int getCorrectPosition(int chapter,int page,int position) {
        int result = 0;
        Cursor cursor = getBySql("select count(1) as total from text where chapter = "+chapter+" and page = "+page+" and id between(select min(id) from text where chapter = "+chapter+" and page = "+page+") and (select id from text where chapter = "+chapter+" and page = "+page+" and position = "+position+")",null);
        if (cursor.moveToFirst()) result = cursor.getInt(cursor.getColumnIndex("total")) - 1;
        cursor.close();
        return result;
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

    public void setFavorite(int folder,int textId, String note, int favorite, int underline, int color) {
        if (duplicate(Static.tableFavorite,"text_id = "+textId,null,true)) {
            set(Static.tableFavorite,cv.editFavorite(folder,note,favorite,underline,color),"text_id = "+textId);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            insertOrReplace(Static.tableFavorite,cv.addFavorite(folder,textId,note,favorite,underline,color,formattedDate));
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
