package ru.niv.bible.mvp.presenter;

import android.content.Context;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.mvp.contract.FolderChildContract;
import ru.niv.bible.mvp.model.FolderChildModel;
import ru.niv.bible.mvp.view.FolderChildFragment;

public class FolderChildPresenter implements FolderChildContract.Presenter {

    private final FolderChildContract.View view;
    private final Context context;
    private final FolderChildModel model;
    private final Dialog dialog;

    public FolderChildPresenter(FolderChildContract.View view) {
        model = new FolderChildModel(((FolderChildFragment) view).getContext());
        dialog = new Dialog(((FolderChildFragment) view).getContext());
        this.view = view;
        this.context = ((FolderChildFragment) view).getContext();
    }

    public List<Item> getList(int type,int cat,int tab) {
        return model.getList(type,cat,tab);
    }

    public void deleteDialog(int id,int position) {
        dialog.delete(context.getString(R.string.dialog_delete_item), () -> {
            model.deleteItem(id);
            view.removeItem(position);
        });
    }

}
