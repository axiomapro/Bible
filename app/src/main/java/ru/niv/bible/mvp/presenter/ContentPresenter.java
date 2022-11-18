package ru.niv.bible.mvp.presenter;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.ContentContract;
import ru.niv.bible.mvp.model.ContentModel;
import ru.niv.bible.mvp.view.ContentFragment;

public class ContentPresenter implements ContentContract.Presenter {

    private final ContentModel model;

    public ContentPresenter(ContentContract.View view) {
        model = new ContentModel(((ContentFragment) view).getContext());
    }

    public List<Item> getList(int chapter,int page) {
        return model.getList(chapter,page);
    }

}
