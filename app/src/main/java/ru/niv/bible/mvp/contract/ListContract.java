package ru.niv.bible.mvp.contract;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;

public interface ListContract {

    interface View {

    }

    interface Presenter {
        List<Item> getList(int tab);
    }

}
