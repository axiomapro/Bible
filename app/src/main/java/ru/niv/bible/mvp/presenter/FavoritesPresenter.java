package ru.niv.bible.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.FavoritesContract;
import ru.niv.bible.mvp.model.FavoritesModel;
import ru.niv.bible.mvp.view.FavoritesFragment;

public class FavoritesPresenter implements FavoritesContract.Presenter {

    private final FavoritesContract.View view;
    private final Context context;
    private final FavoritesModel model;
    private final Dialog dialog;
    private final Converter converter;

    public FavoritesPresenter(FavoritesContract.View view) {
        model = new FavoritesModel(((FavoritesFragment) view).getContext());
        dialog = new Dialog(((FavoritesFragment) view).getContext());
        converter = new Converter();
        this.view = view;
        this.context = ((FavoritesFragment) view).getContext();
    }

    @Override
    public List<Item> getList(int tab) {
        return model.getList(tab);
    }

    public void folderDialog(boolean statusAdd,String name,int id,int position) {
        if (!statusAdd && id == 0) return; // edit
        dialog.folder(statusAdd,name, new Dialog.Folder() {
            @Override
            public void onResult(String name, AlertDialog dialog, Dialog.Message listener) {
                String correctName = converter.getNameUppercase(name,false);
                if (TextUtils.isEmpty(correctName)) listener.onMessage(context.getString(R.string.write_the_name));
                else if (correctName.equals(context.getString(R.string.default_folder))) listener.onMessage(context.getString(R.string.name_is_not_available));
                else {
                    if (statusAdd) {
                        model.add(correctName, new FavoritesModel.Action() {
                            @Override
                            public void onDuplicate() {
                                listener.onMessage(context.getString(R.string.duplicate_folder));
                            }

                            @Override
                            public void onSuccess(int id,int newPosition) {
                                dialog.dismiss();
                                view.addItem(id,correctName,newPosition);
                            }
                        });
                    } else {
                        model.edit(correctName, id, new FavoritesModel.Action() {
                            @Override
                            public void onDuplicate() {
                                listener.onMessage(context.getString(R.string.duplicate_folder));
                            }

                            @Override
                            public void onSuccess(int id,int newPosition) {
                                dialog.dismiss();
                                view.updateItem(correctName,position);
                            }
                        });
                    }
                }
            }

            @Override
            public void onDelete() {
                dialog.delete(context.getString(R.string.dialog_delete_folder), () -> {
                    model.delete(id);
                    view.deleteItem(position);
                });
            }
        });
    }

}
