package ru.niv.bible.mvp.presenter;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.SearchContract;
import ru.niv.bible.mvp.model.SearchModel;
import ru.niv.bible.mvp.view.SearchFragment;

public class SearchPresenter implements SearchContract.Presenter {

    private final SearchModel model;

    public SearchPresenter(SearchContract.View view) {
        model = new SearchModel(((SearchFragment) view).getContext());
    }

    public List<Item> getList(String query,int tab,boolean exact,boolean partial) {
        return model.getList(query,tab,exact,partial);
    }

    public String getTextByIds(String ids) {
        return model.getTextByIds(ids);
    }

}
