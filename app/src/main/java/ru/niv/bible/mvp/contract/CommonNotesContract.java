package ru.niv.bible.mvp.contract;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;

public interface CommonNotesContract {

    interface View {
        void addItem(int id,String name,String note,String date,int position);
        void updateItem(String name,String note,int position);
        void deleteItem(int position);
    }

    interface Presenter {
        List<Item> getList();
    }

}
