package ru.niv.bible.mvp.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.DailyVerseContract;
import ru.niv.bible.mvp.presenter.ContentPresenter;
import ru.niv.bible.mvp.presenter.DailyVersePresenter;

public class DailyVerseFragment extends Fragment implements DailyVerseContract.View, View.OnClickListener {

    private DailyVersePresenter presenter;
    private Go go;
    private RecyclerViewAdapter adapter, adapterEditorLeft, adapterEditorRight;
    private RecyclerView recyclerView, recyclerViewEditorLeft, recyclerViewEditorRight;
    private TextView tvToolbar, tvSelected, tvTotal, tvNotification;
    private ImageView ivBack, ivNotification;
    private EditText etName;
    private CheckBox cbSelectAll;
    private GridLayout glNotification;
    private NestedScrollView svEditor;
    private FloatingActionButton fab;
    private String enabledTime;
    private int selected, total, editPosition;
    private boolean isEditor, isAdd, isNotificationEnabled, isIgnoreCheckBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily_verse,container,false);
        initViews(v);
        go = new Go(getContext());
        presenter = new DailyVersePresenter(this);
        initRecyclerView();
        initRecyclerViewEditorLeft();
        initRecyclerViewEditorRight();
        total = adapterEditorLeft.getItemCount() + adapterEditorRight.getItemCount();
        tvTotal.setText(String.valueOf(total));
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivBack = v.findViewById(R.id.imageViewBack);
        ivNotification = v.findViewById(R.id.imageViewNotification);
        tvToolbar = v.findViewById(R.id.textViewToolbar);
        tvSelected = v.findViewById(R.id.textViewSelected);
        tvTotal = v.findViewById(R.id.textViewTotal);
        tvNotification = v.findViewById(R.id.textViewNotification);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerViewEditorLeft = v.findViewById(R.id.recyclerViewEditorLeft);
        recyclerViewEditorRight = v.findViewById(R.id.recyclerViewEditorRight);
        etName = v.findViewById(R.id.editTextName);
        cbSelectAll = v.findViewById(R.id.checkBoxSelectAll);
        glNotification = v.findViewById(R.id.gridLayoutNotification);
        svEditor = v.findViewById(R.id.nestedScrollViewEditor);
        fab = v.findViewById(R.id.fab);
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(Static.dailyVerse,presenter.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListenerDailyVerse(new RecyclerViewAdapter.ClickDailyVerse() {
            @Override
            public void onClick(int position) {
                getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(adapter.getItem(position).getChapter(),adapter.getItem(position).getPage(),adapter.getItem(position).getPosition()),Static.main).addToBackStack(Static.main).commit();
            }

            @Override
            public void onLongClick(int position) {
                presenter.deleteDialog(adapter.getItem(position).getId(),position);
            }

            @Override
            public void onShare(int position) {
                String title = getString(R.string.app_name)+" "+adapter.getItem(position).getChapterName()+" "+adapter.getItem(position).getPage()+":"+adapter.getItem(position).getPosition();
                go.share(null,title+"\n"+adapter.getItem(position).getText(),getString(R.string.share_popup_search));
            }

            @Override
            public void onCopy(int position) {
                String title = adapter.getItem(position).getChapterName()+" "+adapter.getItem(position).getPage()+":"+adapter.getItem(position).getPosition();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.text_copied),title+"\n"+adapter.getItem(position).getText());
                clipboard.setPrimaryClip(clip);
                ((MainActivity) getActivity()).message(getString(R.string.text_copied));
            }

            @Override
            public void onEdit(int position) {
                Item item = adapter.getItem(position);
                editor(false,item.getName(),item.getChapters(),item.getNotification(),position);
            }

            @Override
            public void onRefresh(int position) {
                presenter.refresh(adapter.getItem(position).getId(),position);
            }
        });
    }

    private void initRecyclerViewEditorLeft() {
        adapterEditorLeft = new RecyclerViewAdapter(Static.dailyVerseEditor,presenter.getListEditor(1));
        recyclerViewEditorLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewEditorLeft.setAdapter(adapterEditorLeft);
        adapterEditorLeft.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                adapterEditorLeft.getItem(position).setCheckBox(!adapterEditorLeft.getItem(position).isCheckBox());
                adapterEditorLeft.notifyItemChanged(position);
                if (adapterEditorLeft.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position, boolean isChecked) {
                if (adapterEditorLeft.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }
        });
    }

    private void initRecyclerViewEditorRight() {
        adapterEditorRight = new RecyclerViewAdapter(Static.dailyVerseEditor,presenter.getListEditor(2));
        recyclerViewEditorRight.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewEditorRight.setAdapter(adapterEditorRight);
        adapterEditorRight.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                adapterEditorRight.getItem(position).setCheckBox(!adapterEditorRight.getItem(position).isCheckBox());
                adapterEditorRight.notifyItemChanged(position);
                if (adapterEditorRight.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position, boolean isChecked) {
                if (adapterEditorRight.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }
        });
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(this);
        fab.setOnClickListener(this);
        glNotification.setOnClickListener(this);
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isIgnoreCheckBox) isIgnoreCheckBox = false;
            else toggleCheckBoxItems(isChecked);
        });
    }

    private void checkSelectedAll() {
        if (selected == total) {
            isIgnoreCheckBox = true;
            cbSelectAll.setChecked(true);
        }
        if (selected == total - 1 && cbSelectAll.isChecked()) {
            isIgnoreCheckBox = true;
            cbSelectAll.setChecked(false);
        }
    }

    private void editor(boolean add,String name,String chapters,String notification,int position) {
        isEditor = true;
        isAdd = add;
        tvToolbar.setText(R.string.edit_daily_verses);
        fab.setImageResource(R.drawable.ic_done);
        svEditor.setVisibility(View.VISIBLE);
        svEditor.fullScroll(ScrollView.FOCUS_UP);

        if (add) {
            enabledTime = null;
            isNotificationEnabled = false;
            etName.getText().clear();
            ivNotification.setImageResource(R.drawable.ic_notification);
            tvNotification.setText(getString(R.string.notify));
            cbSelectAll.setChecked(true);
        } else {
            selected = 0;
            editPosition = position;
            etName.setText(name);
            enabledTime = notification;
            isNotificationEnabled = notification != null;
            ivNotification.setImageResource(notification != null?R.drawable.ic_notification_active:R.drawable.ic_notification);
            tvNotification.setText(notification != null?notification:getString(R.string.notify));

            String[] array = chapters.split(",");
            for (int i = 0; i < adapterEditorLeft.getItemCount(); i++) {
                int id = adapterEditorLeft.getItem(i).getId();
                boolean found = Arrays.asList(array).contains(String.valueOf(id));
                adapterEditorLeft.getItem(i).setCheckBox(found);
                if (found) selected++;
            }
            for (int k = 0; k < adapterEditorRight.getItemCount(); k++) {
                int id = adapterEditorRight.getItem(k).getId();
                boolean found = Arrays.asList(array).contains(String.valueOf(id));
                adapterEditorRight.getItem(k).setCheckBox(found);
                if (found) selected++;
            }
            tvSelected.setText(String.valueOf(selected));
            adapterEditorLeft.notifyDataSetChanged();
            adapterEditorRight.notifyDataSetChanged();
            if (selected == total) {
                if (!cbSelectAll.isChecked()) {
                    isIgnoreCheckBox = true;
                    cbSelectAll.setChecked(true);
                }
            } else {
                if (cbSelectAll.isChecked()) {
                    isIgnoreCheckBox = true;
                    cbSelectAll.setChecked(false);
                }
            }
        }
    }

    private void toggleCheckBoxItems(boolean status) {
        selected = status?total:0;
        tvSelected.setText(String.valueOf(selected));
        for (int i = 0; i < adapterEditorLeft.getItemCount(); i++) {
            adapterEditorLeft.getItem(i).setCheckBox(status);
        }
        for (int k = 0; k < adapterEditorRight.getItemCount(); k++) {
            adapterEditorRight.getItem(k).setCheckBox(status);
        }
        adapterEditorLeft.notifyDataSetChanged();
        adapterEditorRight.notifyDataSetChanged();
    }

    private String getSelectedIds() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < adapterEditorLeft.getItemCount(); i++) {
            boolean isChecked = adapterEditorLeft.getItem(i).isCheckBox();
            int id = adapterEditorLeft.getItem(i).getId();
            if (isChecked) result.append(",").append(id);
        }
        for (int k = 0; k < adapterEditorRight.getItemCount(); k++) {
            boolean isChecked = adapterEditorRight.getItem(k).isCheckBox();
            int id = adapterEditorRight.getItem(k).getId();
            if (isChecked) result.append(",").append(id);
        }
        return result.substring(1);
    }

    private void hideEditor() {
        isEditor = false;
        svEditor.setVisibility(View.GONE);
        tvToolbar.setText(R.string.daily_verses);
        fab.setImageResource(R.drawable.ic_add);
    }

    public boolean checkBack() {
        boolean result = true;
        if (isEditor) {
            result = false;
            hideEditor();
        }
        return result;
    }

    @Override
    public void message(String message) {
        ((MainActivity) getActivity()).message(message);
    }

    @Override
    public void notification(String time, boolean status) {
        enabledTime = time;
        isNotificationEnabled = status;
        tvNotification.setText(status?enabledTime:getString(R.string.notify));
        ivNotification.setImageResource(status?R.drawable.ic_notification_active:R.drawable.ic_notification);
    }

    @Override
    public void addItem(int id, String name, String text, String chapterName, String chapters, String notification, int chapter, int page, int position) {
        hideEditor();
        adapter.getList().add(0,new Item().dailyVerse(id,name,text,chapterName,chapters,notification,chapter,page,position));
        adapter.notifyDataSetChanged();
        recyclerView.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public void updateItem(String name, String text, String chapterName, String chapters, String notification, int chapter, int page, int position, int itemPosition) {
        hideEditor();
        adapter.getItem(itemPosition).setName(name);
        adapter.getItem(itemPosition).setText(text);
        adapter.getItem(itemPosition).setChapterName(chapterName);
        adapter.getItem(itemPosition).setChapters(chapters);
        adapter.getItem(itemPosition).setNotification(notification);
        adapter.getItem(itemPosition).setChapter(chapter);
        adapter.getItem(itemPosition).setPage(page);
        adapter.getItem(itemPosition).setPosition(position);
        adapter.notifyItemChanged(itemPosition);
    }

    @Override
    public void deleteItem(int position) {
        adapter.getList().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,adapter.getList().size());
    }

    @Override
    public void refreshItem(String chapterName, String text, int chapter, int page, int position, int itemPosition) {
        adapter.getItem(itemPosition).setChapterName(chapterName);
        adapter.getItem(itemPosition).setText(text);
        adapter.getItem(itemPosition).setChapter(chapter);
        adapter.getItem(itemPosition).setPage(page);
        adapter.getItem(itemPosition).setPosition(position);
        adapter.notifyItemChanged(itemPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                if (isEditor) hideEditor();
                else getParentFragmentManager().popBackStack();
                break;
            case R.id.fab:
                if (isEditor) {
                    String name = etName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) message(getString(R.string.write_the_name));
                    else if (selected == 0) message(getString(R.string.select_items));
                    else {
                        if (isAdd) presenter.add(name,getSelectedIds(),isNotificationEnabled?enabledTime:null);
                        else presenter.edit(adapter.getItem(editPosition).getId(),name,getSelectedIds(),isNotificationEnabled?enabledTime:null,editPosition);
                    }
                } else {
                    editor(true,null,null,null,0);
                }
                break;
            case R.id.gridLayoutNotification:
                presenter.notificationDialog(enabledTime);
                break;
        }
    }
}
