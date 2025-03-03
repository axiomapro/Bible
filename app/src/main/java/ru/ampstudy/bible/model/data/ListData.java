package ru.ampstudy.bible.model.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.mediator.list.item.Item;
import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Convert;
import ru.ampstudy.bible.component.mutable.sqlite.Model;

public class ListData extends Model {

    private final Context context;
    private Convert convert;

    public ListData(Context context) {
        super(context);
        this.context = context;
        convert = new Convert();
    }

    public List<Item> sidebarMain() {
        List<Item> list = new ArrayList<>();
        String[] items = {context.getString(R.string.favorites),context.getString(R.string.reading_plan),context.getString(R.string.daily_verse),context.getString(R.string.common_notes),context.getString(R.string.settings),context.getString(R.string.feedback),context.getString(R.string.menu_share_app),context.getString(R.string.menu_day_night)};
        int[] icons = {R.drawable.ic_menu_favorites,R.drawable.ic_menu_reading_plan,R.drawable.ic_menu_daily_verse,R.drawable.ic_menu_common_notes,R.drawable.ic_menu_settings,R.drawable.ic_menu_feedback,R.drawable.ic_share_sidebar,R.drawable.ic_menu_day};
        for (int i = 0; i < items.length; i++) {
            list.add(new Item().sidebar(items[i],icons[i],true));
        }
        list.add(new Item().sidebar(context.getString(R.string.remove_ads),R.drawable.ic_menu_remove_ads,false));
        if (Static.forcedDarkMode) list.remove(list.size() - 2);
        return list;
    }

    @SuppressLint("Range")
    public List<Item> mainChild(int chapter,int page) {
        String columnHead = Static.supportHead?",t.head":"";
        List<Item> list = new ArrayList<>();
        Cursor cursor = getBySql("select t.id,t.text"+columnHead+",f.folder,f.note,f.favorite,f.underline,f.color,(select name from folder where id = f.folder and del = 0) as folderName,f.del from text t left join favorite f on t.id = f.text_id where t.chapter = "+chapter+" and t.page = "+page,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            boolean head = cursor.getInt(cursor.getColumnIndex("head")) == 1;

            String del = cursor.getString(cursor.getColumnIndex("del"));
            if (del != null && Integer.parseInt(del) != 1) {
                int folder = cursor.getInt(cursor.getColumnIndex("folder"));
                int color = cursor.getInt(cursor.getColumnIndex("color"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String folderName = cursor.getString(cursor.getColumnIndex("folderName"));
                boolean favorite = cursor.getInt(cursor.getColumnIndex("favorite")) == 1;
                boolean underline = cursor.getInt(cursor.getColumnIndex("underline")) == 1;

                list.add(new Item().main(id,text.replaceAll("(?i)<br */?>","\n").replaceAll("<(.*?)>",""),note,folderName == null?context.getString(R.string.default_folder):folderName,folder,favorite,underline,color,head,false));
            } else {
                list.add(new Item().main(id,text.replaceAll("(?i)<br */?>","\n").replaceAll("<(.*?)>",""),null,null,0,false,false,0,head,false));
            }
        }
        cursor.close();

        return list;
    }

    @SuppressLint("Range")
    public List<Item> getListFolderMainChild() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().folder(),"id,name",null,true,"name asc");
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
    public List<Item> list(int tab) {
        String where = null;
        if (tab == 2) where = "type = 1";
        if (tab == 3) where = "type = 2";

        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().chapter(),"id,type,name",where,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().list(id,type,name));
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> content(int chapter,int page) {
        int total = 0;
        Cursor cursor;
        String excludeHead = Static.supportHead?" and head != 1":"";
        List<Item> list = new ArrayList<>();
        if (page == 0) cursor = get(Config.table().text(),"max(page) as total","chapter = "+chapter,false,null);
        else cursor = get(Config.table().text(),"count(1) as total","chapter = "+chapter+" and page = "+page+excludeHead,false,null);

        if (cursor.moveToFirst()) total = cursor.getInt(cursor.getColumnIndex("total"));
        for (int i = 1; i <= total; i++) {
            list.add(new Item().content(i));
        }

        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> favorite(int tab) {
        List<Item> list = new ArrayList<>();
        Cursor cursor;
        if (tab == 1) cursor = get(Config.table().folder(),"id,name",null,true,"name asc");
        else cursor = get(Config.table().chapter(),"id,name",null,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int total = total(Config.table().favorite(),"folder = "+id,true);
            if (tab == 2) {
                total = get(Config.table().favorite()+" f inner join text t on f.text_id = t.id",null,"t.chapter = "+id+" and f.del = 0",false,null).getCount();
            }
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().favorites(id,name,total));
        }
        if (tab == 1) list.add(0,new Item().favorites(0,context.getString(R.string.default_folder),total(Config.table().favorite(),"folder = 0",true)));

        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> folder(int type,int cat,int tab) {
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
        Cursor cursor = get(Config.table().favorite()+" f inner join text t on f.text_id = t.id","f.id,t.text,(select name from chapter where id = t.chapter) as name,(select name from folder where id = folder and del = 0) as folderName,f.note,f.favorite,f.underline,f.color,f.date,t.chapter,t.page,t.position",where+" and f.del = 0",false,"f.date desc");
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

    @SuppressLint("Range")
    public List<Item> search(String query, int tab, boolean exact, boolean partial) {
        String sortBy = "";
        String[] arg;
        if (exact) arg = new String[]{query+"%"};
        else if (partial) arg = new String[]{"% "+query+" %"};
        else arg = new String[]{"%"+query+"%"};

        if (tab == 2) sortBy = " and ch.type = 1";
        if (tab == 3) sortBy = " and ch.type = 2";

        String excludeHead = Static.supportHead?" and head != 1":"";
        List<Item> list = new ArrayList<>();
        Cursor cursor = getBySql("select t.id,ch.name,t.chapter,t.page,t.position,t.text from text t inner join chapter ch on t.chapter = ch.id where t.text like ?"+sortBy+excludeHead+" order by t.id asc",arg);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            list.add(new Item().search(id,name+" "+page+":"+position,text.replaceAll("(?i)<br */?>","\n").replaceAll("<(.*?)>",""),chapter,page,position,false));
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> commonNotes() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().note(),"id,name,text,date",null,true,"date desc");
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

    @SuppressLint("Range")
    public List<Item> readingPlan() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().plan(),"id,name,text,type,start,status",null,false,"start desc,sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            list.add(new Item().readingPlan(id));
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> readingPlanChild(int plan,int type,int day) {
        List<Item> list = new ArrayList<>();

        Cursor cursor = getBySql("select id,chapter_order,page,status from reading_plan where plan_id = "+plan+" and type = "+type+" and day = "+day,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chapterOrder = cursor.getInt(cursor.getColumnIndex("chapter_order"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            Cursor cursorSecond = getBySql("select max(id) as id,name from (select id,name from chapter order by type asc limit "+chapterOrder+")",null);
            if (cursorSecond.moveToFirst()) {
                int chapter = cursorSecond.getInt(cursorSecond.getColumnIndex("id"));
                String name = cursorSecond.getString(cursorSecond.getColumnIndex("name"));
                list.add(new Item().readingPlanChild(id,name+" "+page,chapter,page,0,status == 1));
            }
            cursorSecond.close();
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> dailyVerse() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().dailyVerse(),"id,name,chapters,notification",null,true,"updated desc,sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String chapters = cursor.getString(cursor.getColumnIndex("chapters"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));

            Cursor cursorSub = get(Config.table().text(),"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+chapters+")",false,"random() limit 1");
            if (cursorSub.moveToFirst()) {
                int chapter = cursorSub.getInt(cursorSub.getColumnIndex("chapter"));
                int page = cursorSub.getInt(cursorSub.getColumnIndex("page"));
                int position = cursorSub.getInt(cursorSub.getColumnIndex("position"));
                String text = cursorSub.getString(cursorSub.getColumnIndex("text"));
                String chapterName = cursorSub.getString(cursorSub.getColumnIndex("chapterName"));
                list.add(new Item().dailyVerse(id,name,text,chapterName,chapters,notification,chapter,page,position));
            }
            cursorSub.close();
        }
        cursor.close();
        return list;
    }

    @SuppressLint("Range")
    public List<Item> getListEditorDailyVerse(int type) {
        List<Item> list = new ArrayList<>();
        Cursor cursor = get(Config.table().chapter(),"id,name","type = "+type,false,null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(new Item().dailyVerseEditor(id,name,false));
        }
        cursor.close();
        return list;
    }

}