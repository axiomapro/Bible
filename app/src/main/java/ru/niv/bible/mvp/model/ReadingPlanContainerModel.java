package ru.niv.bible.mvp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.sqlite.Model;

public class ReadingPlanContainerModel extends Model {

    public interface Data {
        void onData(String name,String text,String start,String notification,int type,boolean status);
    }

    public ReadingPlanContainerModel(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public String getListDialog(int plan, int type, int day) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Static.tableReadingPlan,"page,chapter_order","plan_id = "+plan+" and type = "+type+" and day = "+day,false,"plan_id asc,type asc,day asc");
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
    public void getData(int id,Data listener) {
        Cursor cursor = get(Static.tablePlan,"type,name,text,start,notification,status","id = "+id,false,null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String start = cursor.getString(cursor.getColumnIndex("start"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));
            listener.onData(name,text,start != null?start.substring(0,10):null,notification,type,status == 1);
        }
        cursor.close();
    }

    public int getTotalLeftDay(int plan,int type,int day) {
        return total(Static.tableReadingPlan,"plan_id = "+plan+" and type = "+type+" and day = "+day+" and status = 0",false);
    }

    public float getProgress(int plan,int type) {
        return converter.getPercent(getTotalViewed(plan,type),getTotal(plan,type));
    }

    private int getTotalViewed(int plan,int type) {
        return total(Static.tableReadingPlan,"plan_id = "+plan+" and type = "+type+" and status = 1",false);
    }

    private int getTotal(int plan,int type) {
        return total(Static.tableReadingPlan,"plan_id = "+plan+" and type = "+type,false);
    }

    public void active(int id,int type,int day) {
        String start = converter.getDateFormat(converter.getDatetime(),"yyyy-MM-dd HH:mm:ss",day - 1,true,false);
        String finish = converter.getDateFormat(start,"yyyy-MM-dd HH:mm:ss",converter.getDayTotal(type) - 1,true,true);
        setById(Static.tablePlan,cv.readingPlanActive(type,start,finish),id);
        set(Static.tableReadingPlan,cv.status(1),"plan_id = "+id+" and type = "+type+" and day <= "+(day - 1));
    }

    public void inactive(int id) {
        setById(Static.tablePlan,cv.readingPlanInactive(),id);
        set(Static.tableReadingPlan,cv.status(0),"plan_id = "+id);
    }

    public void setNotification(int id,String notification) {
        setById(Static.tablePlan,cv.notificationReadingPlan(notification),id);
    }

}
