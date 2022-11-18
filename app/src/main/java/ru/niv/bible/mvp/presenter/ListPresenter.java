package ru.niv.bible.mvp.presenter;

import java.util.List;

import ru.niv.bible.basic.item.Item;
import ru.niv.bible.mvp.contract.ListContract;
import ru.niv.bible.mvp.model.ListModel;
import ru.niv.bible.mvp.view.ListFragment;

public class ListPresenter implements ListContract.Presenter {

    private final ListModel model;

    public ListPresenter(ListContract.View view) {
        model = new ListModel(((ListFragment) view).getContext());
    }

    @Override
    public List<Item> getList(int tab) {
        return model.getList(tab);
    }

    public int getTabByPosition(int position) {
        return model.getTabByPosition(position);
    }

}
