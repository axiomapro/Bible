package ru.niv.bible.mvp.presenter;

import android.content.Context;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.model.ReadingPlanChildModel;

public class ReadingPlanChildPresenter {

    private final ReadingPlanChildModel model;

    public ReadingPlanChildPresenter(Context context) {
        model = new ReadingPlanChildModel(context);
    }

    public List<Item> getList(int plan,int type,int day) {
        return model.getList(plan,type,day);
    }

    public void updateView(int id) {
        model.updateView(id);
    }

    public void updateItemsByDay(int plan,int type,int day,boolean status) {
        model.updateItemsByDay(plan,type,day,status);
    }

}
