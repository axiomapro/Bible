package ru.niv.bible.mvp.model;

import android.content.Context;
import android.database.Cursor;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.sqlite.Model;

public class ReadingPlanMaterialModel extends Model {

    public interface Data {
        void onData(String name,String start,String finish,int type);
    }

    public ReadingPlanMaterialModel(Context context) {
        super(context);
    }

    public void getData(int id,Data listener) {
        Cursor cursor = get(Static.tablePlan,"type,name,start,finish","id = "+id,false,null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String start = cursor.getString(cursor.getColumnIndex("start")).substring(0,10);
            String finish = cursor.getString(cursor.getColumnIndex("finish")).substring(0,10);
            listener.onData(name,start,finish,type);
        }
        cursor.close();
    }

    public String getLinks(int plan,int type,int day) {
        int i = 1;
        StringBuilder result = new StringBuilder();
        Cursor cursor = get(Static.tableReadingPlan,"chapter_order,page","plan_id = "+plan+" and type = "+type+" and day = "+day,false,"plan_id asc,type asc,day asc");
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

    public boolean isCheckBox(int plan,int type,int day) {
        return total(Static.tableReadingPlan,"plan_id = "+plan+" and type = "+type+" and day = "+day+" and status = 0",false) == 0;
    }

    public void updateItemsByDay(int plan,int type,int day,boolean status) {
        set(Static.tableReadingPlan,cv.status(status?1:0),"plan_id = "+plan+" and type = "+type+" and day = "+day);
    }

}
