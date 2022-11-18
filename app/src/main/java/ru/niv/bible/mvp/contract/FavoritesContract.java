package ru.niv.bible.mvp.contract;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;

public interface FavoritesContract {

    interface View {
        void addItem(int id,String name,int position);
        void updateItem(String name,int position);
        void deleteItem(int position);
    }

    interface Presenter {
        List<Item> getList(int tab);
    }

}
