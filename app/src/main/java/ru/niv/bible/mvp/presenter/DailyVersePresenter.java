package ru.niv.bible.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Alarm;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.DailyVerseContract;
import ru.niv.bible.mvp.contract.FavoritesContract;
import ru.niv.bible.mvp.model.DailyVerseModel;
import ru.niv.bible.mvp.model.FavoritesModel;
import ru.niv.bible.mvp.view.DailyVerseFragment;
import ru.niv.bible.mvp.view.FavoritesFragment;

public class DailyVersePresenter implements DailyVerseContract.Presenter {

    private final DailyVerseContract.View view;
    private final Context context;
    private final DailyVerseModel model;
    private final Converter converter;
    private final Alarm alarm;
    private final Dialog dialog;

    public DailyVersePresenter(DailyVerseContract.View view) {
        model = new DailyVerseModel(((DailyVerseFragment) view).getContext());
        dialog = new Dialog(((DailyVerseFragment) view).getContext());
        alarm = new Alarm(((DailyVerseFragment) view).getContext());
        converter = new Converter();
        this.view = view;
        this.context = ((DailyVerseFragment) view).getContext();
    }

    @Override
    public List<Item> getList() {
        return model.getList();
    }

    @Override
    public List<Item> getListEditor(int type) {
        return model.getListEditor(type);
    }

    public void add(String name,String chapters,String notification) {
        String correctName = converter.getNameUppercase(name,false);
        model.add(correctName, chapters, notification, new DailyVerseModel.Action() {
            @Override
            public void onDuplicate() {
                view.message(context.getString(R.string.duplicate_daily_verse));
            }

            @Override
            public void onSuccess(int id, String chapterName, String text, int chapter, int page, int position) {
                if (notification != null) alarm.set(id,alarm.getTime(notification),false);
                view.addItem(id,correctName,text,chapterName,chapters,notification,chapter,page,position);
            }
        });
    }

    public void edit(int id,String name,String chapters,String notification,int itemPosition) {
        String correctName = converter.getNameUppercase(name,false);
        model.edit(id,correctName, chapters, notification, new DailyVerseModel.Action() {
            @Override
            public void onDuplicate() {
                view.message(context.getString(R.string.duplicate_daily_verse));
            }

            @Override
            public void onSuccess(int id, String chapterName, String text, int chapter, int page, int position) {
                if (notification != null) alarm.set(id,alarm.getTime(notification),false);
                else alarm.cancel(id,false);
                view.updateItem(correctName,text,chapterName,chapters,notification,chapter,page,position,itemPosition);
            }
        });
    }

    public void refresh(int id,int itemPosition) {
        model.refresh(id, (chapterName, text, chapter, page, position) -> view.refreshItem(chapterName,text,chapter,page,position,itemPosition));
    }

    public void notificationDialog(String notification) {
        dialog.notification(notification, (time, status) -> view.notification(alarm.restoreTime(time),status));
    }

    public void deleteDialog(int id,int position) {
        dialog.delete(context.getString(R.string.dialog_delete_daily_verse), () -> {
            alarm.cancel(id,false);
            model.delete(id);
            view.deleteItem(position);
        });
    }

}
