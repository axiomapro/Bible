package ru.ampstudy.bible.model.action;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.mutable.contentvalue.AddCV;
import ru.ampstudy.bible.component.mutable.contentvalue.ToggleCV;
import ru.ampstudy.bible.component.mutable.contentvalue.UpdateCV;
import ru.ampstudy.bible.component.mutable.sqlite.Model;

public class Update extends Model {

    private final AddCV add;
    private final UpdateCV update;
    private final ToggleCV cv;

    public Update(Context context) {
        super(context);
        cv = new ToggleCV();
        add = new AddCV();
        update = new UpdateCV();
    }

    public void setFavoriteMainChild(int folder,int textId, String note, int favorite, int underline, int color) {
        if (duplicate(Config.table().favorite(),"text_id = "+textId,null,true)) {
            set(Config.table().favorite(),update.favorite(folder,note,favorite,underline,color),"text_id = "+textId);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            insertOrReplace(Config.table().favorite(),add.favorite(folder,textId,note,favorite,underline,color,formattedDate));
        }
    }

    public void activeReadingPlanContainer(int id,int type,int day,String start,String finish) {
        setById(Config.table().plan(),update.readingPlanActive(type,start,finish),id);
        set(Config.table().readingPlan(),cv.status(1),"plan_id = "+id+" and type = "+type+" and day <= "+(day - 1));
    }

    public void inactiveReadingPlanContainer(int id) {
        setById(Config.table().plan(),update.readingPlanInactive(),id);
        set(Config.table().readingPlan(),cv.status(0),"plan_id = "+id);
    }

    public void setNotificationReadingPlanContainer(int id,String notification) {
        setById(Config.table().plan(),update.notificationReadingPlan(notification),id);
    }

    public void updateViewReadingPlanChild(int id) {
        setById(Config.table().readingPlan(),cv.status(1),id);
    }

    public void updateItemsByDayReadingPlanMaterial(int plan,int type,int day,boolean status) {
        set(Config.table().readingPlan(),cv.status(status?1:0),"plan_id = "+plan+" and type = "+type+" and day = "+day);
    }

    public void updateItemsByDayReadingPlanChild(int plan,int type,int day,boolean status) {
        set(Config.table().readingPlan(),cv.status(status?1:0),"plan_id = "+plan+" and type = "+type+" and day = "+day);
    }

}