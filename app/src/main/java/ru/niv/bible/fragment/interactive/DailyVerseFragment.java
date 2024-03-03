package ru.niv.bible.fragment.interactive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Alarm;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Content;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Datetime;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.fragment.MainFragment;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.contract.ResultContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.list.item.Item;
import ru.niv.bible.mediator.view.Rview;

public class DailyVerseFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Rview rview, rviewEditorLeft, rviewEditorRight;
    private Content content;
    private Convert convert;
    private Datetime datetime;
    private Alarm alarm;
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
        initClasses();
        initRecyclerView();
        initRecyclerViewEditorLeft();
        initRecyclerViewEditorRight();
        total = rviewEditorLeft.getTotal() + rviewEditorRight.getTotal();
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

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
        rviewEditorLeft = mediator.view().rview();
        rviewEditorRight = mediator.view().rview();
        content = new Content(getContext());
        convert = new Convert();
        datetime = new Datetime();
        alarm = new Alarm(getContext());
    }

    private void initRecyclerView() {
        rview.setRecyclerView(recyclerView);
        rview.setDailyVerseListener(new RecyclerViewContract.DailyVerse() {
            @Override
            public void click(int position) {
                mediator.transition(getParentFragmentManager(),MainFragment.newInstance(rview.getItem(position).getChapter(),rview.getItem(position).getPage(),rview.getItem(position).getPosition()),Config.screen().main(), Static.DOWN_ANIMATION,true,true);
            }

            @Override
            public void longClick(int position) {
                mediator.show().dialog().delete(getString(R.string.dialog_delete_daily_verse), () -> {
                    alarm.cancel(rview.getItem(position).getId(),false);
                    mediator.handler().toggle().execute(Config.table().dailyVerse(),Config.column().del(),rview.getItem(position).getId());
                    rview.removeItem(position);
                });
            }

            @Override
            public void share(int position) {
                String title = getString(R.string.app_name)+" "+rview.getItem(position).getChapterName()+" "+rview.getItem(position).getPage()+":"+rview.getItem(position).getPosition();
                content.share(null,title+"\n"+rview.getItem(position).getText(),getString(R.string.share_popup_search));
            }

            @Override
            public void copy(int position) {
                String title = rview.getItem(position).getChapterName()+" "+rview.getItem(position).getPage()+":"+rview.getItem(position).getPosition();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.text_copied),title+"\n"+rview.getItem(position).getText());
                clipboard.setPrimaryClip(clip);
                ((MainActivity) getActivity()).message(getString(R.string.text_copied));
            }

            @Override
            public void edit(int position) {
                editor(false,rview.getItem(position).getName(),rview.getItem(position).getChapters(),rview.getItem(position).getNotification(),position);
            }

            @Override
            public void refresh(int position) {
                mediator.get().data().refreshDailyVerse(rview.getItem(position).getId(), (chapter, page, position1, chapterName, text) -> {
                    rview.getItem(position).setChapter(chapter);
                    rview.getItem(position).setPage(page);
                    rview.getItem(position).setPosition(position1);
                    rview.getItem(position).setChapterName(chapterName);
                    rview.getItem(position).setText(text);
                    rview.updateItem(position);
                });
            }
        });
        rview.initialize(Config.recyclerView().dailyVerse(), mediator.get().list().dailyVerse(), new LinearLayoutManager(getContext()),null);
    }

    private void initRecyclerViewEditorLeft() {
        rviewEditorLeft.setRecyclerView(recyclerViewEditorLeft);
        rviewEditorLeft.initialize(Config.recyclerView().dailyVerseEditor(), mediator.get().list().getListEditorDailyVerse(1), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                rviewEditorLeft.getItem(position).setCheckBox(!rviewEditorLeft.getItem(position).isCheckBox());
                rviewEditorLeft.updateItem(position);
                if (rviewEditorLeft.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, boolean isChecked) {
                if (rviewEditorLeft.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }
        });
    }

    private void initRecyclerViewEditorRight() {
        rviewEditorRight.setRecyclerView(recyclerViewEditorRight);
        rviewEditorRight.initialize(Config.recyclerView().dailyVerseEditor(), mediator.get().list().getListEditorDailyVerse(2), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                rviewEditorRight.getItem(position).setCheckBox(!rviewEditorRight.getItem(position).isCheckBox());
                rviewEditorRight.updateItem(position);
                if (rviewEditorRight.getItem(position).isCheckBox()) selected++;
                else selected--;
                tvSelected.setText(String.valueOf(selected));
                checkSelectedAll();
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, boolean isChecked) {
                if (rviewEditorRight.getItem(position).isCheckBox()) selected++;
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
            for (int i = 0; i < rviewEditorLeft.getTotal(); i++) {
                int id = rviewEditorLeft.getItem(i).getId();
                boolean found = Arrays.asList(array).contains(String.valueOf(id));
                rviewEditorLeft.getItem(i).setCheckBox(found);
                if (found) selected++;
            }
            for (int k = 0; k < rviewEditorRight.getTotal(); k++) {
                int id = rviewEditorRight.getItem(k).getId();
                boolean found = Arrays.asList(array).contains(String.valueOf(id));
                rviewEditorRight.getItem(k).setCheckBox(found);
                if (found) selected++;
            }
            tvSelected.setText(String.valueOf(selected));
            rviewEditorLeft.update();
            rviewEditorRight.update();
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
        for (int i = 0; i < rviewEditorLeft.getTotal(); i++) {
            rviewEditorLeft.getItem(i).setCheckBox(status);
        }
        for (int k = 0; k < rviewEditorRight.getTotal(); k++) {
            rviewEditorRight.getItem(k).setCheckBox(status);
        }
        rviewEditorLeft.update();
        rviewEditorRight.update();
    }

    private String getSelectedIds() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rviewEditorLeft.getTotal(); i++) {
            boolean isChecked = rviewEditorLeft.getItem(i).isCheckBox();
            int id = rviewEditorLeft.getItem(i).getId();
            if (isChecked) result.append(",").append(id);
        }
        for (int k = 0; k < rviewEditorRight.getTotal(); k++) {
            boolean isChecked = rviewEditorRight.getItem(k).isCheckBox();
            int id = rviewEditorRight.getItem(k).getId();
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

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etName.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
    }

    public boolean checkBack() {
        boolean result = true;
        if (isEditor) {
            result = false;
            hideEditor();
        }
        return result;
    }

    public void notification(String time, boolean status) {
        enabledTime = time;
        isNotificationEnabled = status;
        tvNotification.setText(status?enabledTime:getString(R.string.notify));
        ivNotification.setImageResource(status?R.drawable.ic_notification_active:R.drawable.ic_notification);
    }

    public void add(String name,String chapters,String notification) {
        String correctName = convert.getNameUppercase(name,false);
        mediator.handler().add().dailyVerse(mediator.form().send().dailyVerse(name,chapters,notification).get(), new ResultContract() {
            @Override
            public void duplicate() {
                ((MainActivity) getActivity()).message(getString(R.string.duplicate_daily_verse));
            }

            @Override
            public void extra(ContentValues cv) {
                if (notification != null) alarm.set(cv.getAsInteger("id"),alarm.getTime(notification),false);

                hideEditor();
                closeKeyboard();
                rview.addItem(0,new Item().dailyVerse(cv.getAsInteger("id"),correctName,cv.getAsString("text"), cv.getAsString("chapterName"), chapters,notification,cv.getAsInteger("chapter"),cv.getAsInteger("page"),cv.getAsInteger("position")));
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        });
    }

    public void edit(int id,String name,String chapters,String notification,int itemPosition) {
        String correctName = convert.getNameUppercase(name,false);
        mediator.handler().edit().dailyVerse(id,mediator.form().send().dailyVerse(correctName, chapters, datetime.getDatetime(), notification).get(), new ResultContract() {
            @Override
            public void duplicate() {
                ((MainActivity) getActivity()).message(getString(R.string.duplicate_daily_verse));
            }

            @Override
            public void extra(ContentValues cv) {
                if (notification != null) alarm.set(id,alarm.getTime(notification),false);
                else alarm.cancel(id,false);

                hideEditor();
                closeKeyboard();
                rview.getItem(itemPosition).setName(correctName);
                rview.getItem(itemPosition).setText(cv.getAsString("text"));
                rview.getItem(itemPosition).setChapterName(cv.getAsString("chapterName"));
                rview.getItem(itemPosition).setChapters(chapters);
                rview.getItem(itemPosition).setNotification(notification);
                rview.getItem(itemPosition).setChapter(cv.getAsInteger("chapter"));
                rview.getItem(itemPosition).setPage(cv.getAsInteger("page"));
                rview.getItem(itemPosition).setPosition(cv.getAsInteger("position"));
                rview.updateItem(itemPosition);
            }
        });
    }

    public void notificationDialog(String notification) {
        mediator.show().dialog().notification(notification, (time, status) -> notification(alarm.restoreTime(time),status));
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
                    if (TextUtils.isEmpty(name)) ((MainActivity) getActivity()).message(getString(R.string.write_the_name));
                    else if (selected == 0) ((MainActivity) getActivity()).message(getString(R.string.select_items));
                    else {
                        if (isAdd) add(name,getSelectedIds(),isNotificationEnabled?enabledTime:null);
                        else edit(rview.getItem(editPosition).getId(),name,getSelectedIds(),isNotificationEnabled?enabledTime:null,editPosition);
                    }
                } else {
                    editor(true,null,null,null,0);
                }
                break;
            case R.id.gridLayoutNotification:
                notificationDialog(enabledTime);
                break;
        }
    }
}