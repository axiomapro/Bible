package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.sqlite.Model;

public class FolderChildModel extends Model {

    private final Context context;

    public FolderChildModel(Context context) {
        super(context);
        this.context = context;
    }

    @SuppressLint("Range")
    public List<Item> getList(int type,int cat,int tab) {
        String where;
        if (type == 1) {
            if (tab == 1) where = "f.folder = "+cat;
            else if (tab == 2) where = "f.folder = "+cat+" and f.favorite = 1";
            else if (tab == 3) where = "f.folder = "+cat+" and f.note != ''";
            else if (tab == 4) where = "f.folder = "+cat+" and f.underline = 1";
            else where = "f.folder = "+cat+" and f.color = "+(tab - 4);
        } else if (type == 2) {
            if (tab == 1) where = "t.chapter = "+cat;
            else if (tab == 2) where = "t.chapter = "+cat+" and f.favorite = 1";
            else if (tab == 3) where = "t.chapter = "+cat+" and f.note != ''";
            else if (tab == 4) where = "t.chapter = "+cat+" and f.underline = 1";
            else where = "t.chapter = "+cat+" and f.color = "+(tab - 4);
        } else {
            if (tab == 1) where = "f.id > 0";
            else if (tab == 2) where = "f.favorite = 1";
            else if (tab == 3) where = "f.note != ''";
            else if (tab == 4) where = "f.underline = 1";
            else where = "f.color = "+(tab - 4);
        }

        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Static.tableFavorite+" f inner join text t on f.text_id = t.id","f.id,t.text,(select name from chapter where id = t.chapter) as name,(select name from folder where id = folder and del = 0) as folderName,f.note,f.favorite,f.underline,f.color,f.date,t.chapter,t.page,t.position",where+" and f.del = 0",false,"f.date desc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            int underline = cursor.getInt(cursor.getColumnIndex("underline"));
            int color = cursor.getInt(cursor.getColumnIndex("color"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String folderName = cursor.getString(cursor.getColumnIndex("folderName"));
            String note = cursor.getString(cursor.getColumnIndex("note"));
            folderName = folderName == null?context.getString(R.string.default_folder):folderName;
            list.add(new Item().folder(id,name+" "+page+":"+position,text.replaceAll("(?i)<br */?>","\n").replaceAll("<(.*?)>",""),type == 1?null:folderName,note,date,chapter,page,position,favorite == 1,underline == 1,color));
        }
        cursor.close();
        return list;
    }

    public void deleteItem(int id) {
        setById(Static.tableFavorite,cv.delete(),id);
    }

}
