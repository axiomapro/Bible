package ru.niv.bible.mvp.contract;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;

public interface DailyVerseContract {

    interface View {
        void message(String message);
        void notification(String time,boolean status);
        void addItem(int id,String name,String text,String chapterName,String chapters,String notification,int chapter,int page,int position);
        void updateItem(String name,String text,String chapterName,String chapters,String notification,int chapter,int page,int position,int itemPosition);
        void deleteItem(int position);
        void refreshItem(String chapterName,String text,int chapter,int page,int position,int itemPosition);
    }

    interface Presenter {
        List<Item> getList();
        List<Item> getListEditor(int type);
    }

}
