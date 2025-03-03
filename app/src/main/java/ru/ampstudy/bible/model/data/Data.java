package ru.ampstudy.bible.model.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.component.mutable.sqlite.Model;
import ru.ampstudy.bible.mediator.contract.DataContract;

public class Data extends Model {

    public Data(Context context) {
        super(context);
    }

    public boolean isSupportHeadMain() {
        boolean result;
        try {
            total(Config.table().text(),"head = 1",false);
            result = true;
        } catch (SQLiteException e) {
            result = false;
        }
        return result;
    }

    @SuppressLint("Range")
    public void getChapterAndPageMain(int position,DataContract.ChapterAndPageMain listener) {
        Cursor cursor = getBySql("select id,chapterId,chapter,page from (select id,chapter as chapterId,(select name from chapter where id = text.chapter) as chapter,page from text group by chapter,page limit "+(position + 1)+") order by id desc limit 1",null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("chapterId"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            String chapter = cursor.getString(cursor.getColumnIndex("chapter"));
            listener.data(id,page,chapter);
        }
        cursor.close();
    }

    @SuppressLint("Range")
    public int getMaxPositionMain() {
        int result = 0;
        Cursor cursor = getBySql("select count(1) as total from (select id from text group by chapter,page)",null);
        if (cursor.moveToFirst()) result = cursor.getInt(cursor.getColumnIndex("total"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public int getPositionByChapterAndPageMain(int chapter,int page) {
        int result = 0;
        Cursor cursor = get(Config.table().text(),"id","chapter = "+chapter+" and page = "+page,false,null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            result = total(Config.table().text(),"id between(select min(id) from text) and "+id+" group by chapter,page",false);
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public void checkOneNotificationMain(DataContract.Main listener) {
        Cursor cursor = get(Config.table().plan(),"id","notification is not null",false,"id asc limit 1");
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            listener.data(id,"reading plan");
        } else {
            Cursor cursorDailyVerse = get(Config.table().dailyVerse(),"id","notification is not null",true,"id asc limit 1");
            if (cursorDailyVerse.moveToFirst()) {
                int id = cursorDailyVerse.getInt(cursorDailyVerse.getColumnIndex("id"));
                listener.data(id,"daily verse");
            } else listener.data(0,"empty");
            cursorDailyVerse.close();
        }
        cursor.close();
    }

    @SuppressLint("Range")
    public int getCorrectPositionMainChild(int chapter,int page,int position) {
        int result = 0;
        Cursor cursor = getBySql("select count(1) as total from text where chapter = "+chapter+" and page = "+page+" and id between(select min(id) from text where chapter = "+chapter+" and page = "+page+") and (select id from text where chapter = "+chapter+" and page = "+page+" and position = "+position+")",null);
        if (cursor.moveToFirst()) result = cursor.getInt(cursor.getColumnIndex("total")) - 1;
        cursor.close();
        return result;
    }

    public boolean getStateReadButtonMainChild(int position) {
        return total(Config.table().read(),"position = "+position,true) != 0;
    }

    @SuppressLint("Range")
    public int getTabByPositionList(int position) {
        int result = 0;
        Cursor cursor = getBySql("select max(id) as id,(select type from chapter where id = chapter) as type from (select id,chapter from text group by chapter, page limit "+position+")",null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("type"));
        }
        cursor.close();
        return result + 1;
    }

    @SuppressLint("Range")
    public String getTextByIdsSearch(String ids) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Config.table().text()+" t inner join chapter ch on t.chapter = ch.id","ch.name,t.page,t.position,t.text","t.id in("+ids.substring(1)+")",false,"t.id asc");
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

    public Cursor getNotificationsReadingPlan() {
        return getBySql("select id,notification from plan where notification is not null group by notification",null);
    }

    @SuppressLint("Range")
    public String getListDialogReadingPlanContainer(int plan, int type, int day) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Config.table().readingPlan(),"page,chapter_order","plan_id = "+plan+" and type = "+type+" and day = "+day,false,"plan_id asc,type asc,day asc");
        while (cursor.moveToNext()) {
            int chapter_order = cursor.getInt(cursor.getColumnIndex("chapter_order"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            Cursor cursorChapter = getBySql("select max(id),name from (select id,name from chapter order by type asc limit "+chapter_order+")",null);
            if (cursorChapter.moveToFirst()) {
                String comma = "";
                if (i != cursor.getCount()) comma = ", ";
                String name = cursorChapter.getString(cursorChapter.getColumnIndex("name"));

                String item = name+" "+page+comma;
                result.append(item);
                i++;
            }
            cursorChapter.close();
        }
        cursor.close();
        return result.toString();
    }

    @SuppressLint("Range")
    public void getDataReadingPlanContainer(int id, DataContract.ReadingPlanContainer listener) {
        Cursor cursor = get(Config.table().plan(),"type,name,text,start,notification,status","id = "+id,false,null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String start = cursor.getString(cursor.getColumnIndex("start"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));
            listener.data(type,status == 1,name,text,start != null?start.substring(0,10):null,notification);
        }
        cursor.close();
    }

    public int getTotalLeftDayReadingPlanContainer(int plan,int type,int day) {
        return total(Config.table().readingPlan(),"plan_id = "+plan+" and type = "+type+" and day = "+day+" and status = 0",false);
    }

    public int getTotalViewedReadingPlanContainer(int plan,int type) {
        return total(Config.table().readingPlan(),"plan_id = "+plan+" and type = "+type+" and status = 1",false);
    }

    public int getTotalReadingPlanContainer(int plan,int type) {
        return total(Config.table().readingPlan(),"plan_id = "+plan+" and type = "+type,false);
    }

    @SuppressLint("Range")
    public void getDataReadingPlanMaterial(int id, DataContract.ReadingPlanMaterial listener) {
        Cursor cursor = get(Config.table().plan(),"type,name,start,finish","id = "+id,false,null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String start = cursor.getString(cursor.getColumnIndex("start")).substring(0,10);
            String finish = cursor.getString(cursor.getColumnIndex("finish")).substring(0,10);
            listener.data(type,name,start,finish);
        }
        cursor.close();
    }

    @SuppressLint("Range")
    public String getLinksReadingPlanMaterial(int plan,int type,int day) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Config.table().readingPlan(),"chapter_order,page","plan_id = "+plan+" and type = "+type+" and day = "+day,false,"plan_id asc,type asc,day asc");
        while (cursor.moveToNext()) {
            int chapterOrder = cursor.getInt(cursor.getColumnIndex("chapter_order"));
            int page = cursor.getInt(cursor.getColumnIndex("page"));
            Cursor cursorChapter = getBySql("select max(id) as id,name from (select id,name from chapter order by type asc limit "+chapterOrder+")",null);
            if (cursorChapter.moveToFirst()) {
                int id = cursorChapter.getInt(cursorChapter.getColumnIndex("id"));
                String name = cursorChapter.getString(cursorChapter.getColumnIndex("name"));
                String comma = "";
                if (i != cursor.getCount()) comma = ", ";

                String item = "<a href=\""+id+":"+page+"\">"+name+" "+page+"</a>"+comma;
                result.append(item);
                i++;
            }
            cursorChapter.close();
        }
        cursor.close();
        return result.toString();
    }

    public boolean isCheckBoxReadingPlanMaterial(int plan,int type,int day) {
        return total(Config.table().readingPlan(),"plan_id = "+plan+" and type = "+type+" and day = "+day+" and status = 0",false) == 0;
    }

    @SuppressLint("Range")
    public void refreshDailyVerse(int id, DataContract.DailyVerse listener) {
        Cursor cursor = get(Config.table().dailyVerse(),"chapters","id = "+id,true,null);
        if (cursor.moveToFirst()) {
            Cursor cursorSub = get(Config.table().text(),"(select name from chapter where id = chapter) as chapterName,chapter,page,position,text",(Static.supportHead?"head != 1 and ":"")+"chapter in ("+cursor.getString(cursor.getColumnIndex("chapters"))+")",false,"random() limit 1");
            if (cursorSub.moveToFirst()) {
                int chapter = cursorSub.getInt(cursorSub.getColumnIndex("chapter"));
                int page = cursorSub.getInt(cursorSub.getColumnIndex("page"));
                int position = cursorSub.getInt(cursorSub.getColumnIndex("position"));
                String text = cursorSub.getString(cursorSub.getColumnIndex("text"));
                String chapterName = cursorSub.getString(cursorSub.getColumnIndex("chapterName"));
                listener.data(chapter,page,position,chapterName,text);
            }
            cursorSub.close();
        }
        cursor.close();
    }

    public Cursor getNotificationsDailyVerse() {
        return getBySql("select id,notification from daily_verse where notification is not null and del = 0 group by notification",null);
    }

}