package ru.niv.bible.basic.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.list.item.Item;

public class Sheet implements View.OnClickListener {

    private BottomSheet listener;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerViewFolder;
    private BottomSheetBehavior bottomSheet;
    private AppCompatButton buttonLeft, buttonRight;
    private ImageView ivArrow, ivAdd, ivNote;
    private GridLayout glFolder, glAdd, glFolderCreate;
    private LinearLayout llMore, llAudioPanel, llPreview;
    private TextView tvFolder, tvMessage, tvAudioTitle;
    private EditText etName, etNote;
    private View llBottomSheet;
    private List<Item> listFolder;
    private String folderName, note;
    private boolean isFolder, isFolderCreate, isAdd, isNote;
    private int folder;

    public interface BottomSheet {
        void onSetItem(String folderName,String type,String note,int folder,int value);
        void onAction(String type);
        void onScroll(int height);
        void onHidden();
        void onSend(String name);
    }

    public void setListener(BottomSheet listener) {
        this.listener = listener;
    }

    public void setListFolder(List<Item> listFolder) {
        this.listFolder = listFolder;
    }

    public void setNote(String note) {
        this.note = note;
        ivNote.setImageResource(note != null && note.length() > 0?R.drawable.ic_note_active:R.drawable.ic_note);
        etNote.setText(note);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViews(View v) {
        LinearLayout llCopy = v.findViewById(R.id.linearLayoutCopy);
        LinearLayout llAudio = v.findViewById(R.id.linearLayoutAudio);
        LinearLayout llShare = v.findViewById(R.id.linearLayoutShare);
        LinearLayout llEdit = v.findViewById(R.id.linearLayoutEdit);
        LinearLayout llNote = v.findViewById(R.id.linearLayoutNote);
        LinearLayout llFavorite = v.findViewById(R.id.linearLayoutFavorite);
        LinearLayout llUnderline = v.findViewById(R.id.linearLayoutUnderline);
        LinearLayout llClear = v.findViewById(R.id.linearLayoutClear);
        LinearLayout llFolder = v.findViewById(R.id.linearLayoutFolder);
        LinearLayout llStop = v.findViewById(R.id.linearLayoutStop);
        LinearLayout llSettings = v.findViewById(R.id.linearLayoutSettings);
        LinearLayout llRoundOne = v.findViewById(R.id.linearLayoutRoundOne);
        LinearLayout llRoundTwo = v.findViewById(R.id.linearLayoutRoundTwo);
        LinearLayout llRoundThree = v.findViewById(R.id.linearLayoutRoundThree);
        LinearLayout llRoundFour = v.findViewById(R.id.linearLayoutRoundFour);
        LinearLayout llRoundFive = v.findViewById(R.id.linearLayoutRoundFive);
        LinearLayout llRoundSix = v.findViewById(R.id.linearLayoutRoundSix);
        LinearLayout llRoundSeven = v.findViewById(R.id.linearLayoutRoundSeven);
        LinearLayout llRoundEight = v.findViewById(R.id.linearLayoutRoundEight);
        LinearLayout llRoundNine = v.findViewById(R.id.linearLayoutRoundNine);
        LinearLayout llRoundTen = v.findViewById(R.id.linearLayoutRoundTen);
        LinearLayout llSend = v.findViewById(R.id.linearLayoutSend);
        ivAdd = v.findViewById(R.id.imageViewAdd);
        ivNote = v.findViewById(R.id.imageViewNote);
        etName = v.findViewById(R.id.editTextName);
        etNote = v.findViewById(R.id.editTextNote);
        tvMessage = v.findViewById(R.id.textViewMessage);
        tvFolder = v.findViewById(R.id.textViewFolder);
        tvAudioTitle = v.findViewById(R.id.textViewAudioTitle);
        buttonLeft = v.findViewById(R.id.buttonLeft);
        buttonRight = v.findViewById(R.id.buttonRight);
        ivArrow = v.findViewById(R.id.imageViewArrow);
        glFolder = v.findViewById(R.id.gridLayoutFolder);
        glAdd = v.findViewById(R.id.gridLayoutAdd);
        glFolderCreate = v.findViewById(R.id.gridLayoutFolderCreate);
        llAudioPanel = v.findViewById(R.id.linearLayoutAudioPanel);
        llBottomSheet = v.findViewById(R.id.linearLayoutBottomSheet);
        llMore = v.findViewById(R.id.linearLayoutMore);
        llPreview = v.findViewById(R.id.linearLayoutPreview);
        bottomSheet = BottomSheetBehavior.from(llBottomSheet);

        folderName = v.getContext().getString(R.string.default_folder);
        initRecyclerViewFolder(v);
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    listener.onHidden();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        llCopy.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llAudio.setOnClickListener(this);
        llStop.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        llEdit.setOnClickListener(this);
        llRoundOne.setOnClickListener(this);
        llRoundTwo.setOnClickListener(this);
        llRoundThree.setOnClickListener(this);
        llRoundFour.setOnClickListener(this);
        llRoundFive.setOnClickListener(this);
        llRoundSix.setOnClickListener(this);
        llRoundSeven.setOnClickListener(this);
        llRoundEight.setOnClickListener(this);
        llRoundNine.setOnClickListener(this);
        llRoundTen.setOnClickListener(this);
        llNote.setOnClickListener(this);
        llFavorite.setOnClickListener(this);
        llUnderline.setOnClickListener(this);
        llClear.setOnClickListener(this);
        llFolder.setOnClickListener(this);
        glFolder.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);
        llSend.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    private void initRecyclerViewFolder(View v) {
        recyclerViewFolder = v.findViewById(R.id.recyclerViewFolder);
        adapter = new RecyclerViewAdapter(Static.bottomSheet,listFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerViewFolder.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                folderName = adapter.getItem(position).getName();
                folder = adapter.getItem(position).getId();
                tvFolder.setText(folderName);
                visibleFolder(false);
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position, boolean isChecked) {

            }
        });
    }

    public void visibleEdit(boolean status) {
        if (status) {
            llPreview.setVisibility(View.GONE);
            llMore.setVisibility(View.VISIBLE);
            buttonLeft.setVisibility(View.VISIBLE);
        } else {
            llMore.setVisibility(View.GONE);
            visibleFolder(false);
            visibleAdd(false,false);
            visibleNote(false);
            buttonLeft.setVisibility(View.GONE);
            llPreview.setVisibility(View.VISIBLE);
        }
    }

    public void showAudio() {
        llPreview.setVisibility(View.GONE);
        llAudioPanel.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.GONE);
    }

    private void visibleFolder(boolean status) {
        isFolder = status;
        if (status) {
            recyclerViewFolder.setVisibility(View.VISIBLE);
            ivArrow.setImageResource(R.drawable.ic_up_bottomsheet);
        } else {
            recyclerViewFolder.setVisibility(View.GONE);
            ivArrow.setImageResource(R.drawable.ic_down_bottomsheet);
        }
        bottomSheet.setDraggable(!isFolder);
        glFolder.setSelected(isFolder);
    }

    private void visibleFolderCreate(boolean status) {
        isFolderCreate = status;
        if (status) {
            glFolderCreate.setVisibility(View.VISIBLE);
        } else {
            glFolderCreate.setVisibility(View.GONE);
            visibleAdd(false,false);
            visibleFolder(false);
        }
    }

    private void visibleAdd(boolean status,boolean delay) {
        isAdd = status;
        if (status) {
            ivAdd.setImageResource(R.drawable.ic_close_bottomsheet);
            glAdd.setVisibility(View.VISIBLE);
        } else {
            ivAdd.setImageResource(R.drawable.ic_add_bottomsheet);
            glAdd.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
            etName.getText().clear();
            etName.clearFocus();
            if (delay) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    InputMethodManager inputMethodManager =(InputMethodManager) etName.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                },1000);
            } else {
                InputMethodManager inputMethodManager =(InputMethodManager) etName.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
            }

        }
    }

    private void visibleNote(boolean status) {
        isNote = status;
        etNote.setVisibility(status?View.VISIBLE:View.GONE);
    }

    public void visibleBottomSheet(boolean status) {
        bottomSheet.setState(status?BottomSheetBehavior.STATE_EXPANDED:BottomSheetBehavior.STATE_HIDDEN);
        if (!status) {
            llMore.setVisibility(View.GONE);
            glFolderCreate.setVisibility(View.GONE);
            llAudioPanel.setVisibility(View.GONE);
            buttonLeft.setVisibility(View.GONE);
            buttonRight.setVisibility(View.VISIBLE);
            llPreview.setVisibility(View.VISIBLE);
            visibleFolder(false);
            visibleAdd(false,true);
            visibleNote(false);
        }
    }

    private int convertDpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public int getHeightBottomSheet() {
        return buttonRight.getVisibility() == View.VISIBLE?llBottomSheet.getHeight():llBottomSheet.getHeight() - buttonRight.getHeight() - convertDpToPx(30) + tvAudioTitle.getHeight();
    }

    private String getNote() {
        return etNote.getText().toString().trim();
    }

    public void message(String message) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
    }

    public void addFolder(int id,String name,int position) {
        visibleFolder(false);
        visibleAdd(false,false);
        folder = id;
        folderName = name;
        tvFolder.setText(name);
        adapter.getList().add(position,new Item().folderBottomSheet(id,name,false));
        adapter.notifyItemInserted(position);

        int total = adapter.getItemCount();
        int top = position - 1;
        adapter.getItem(top).setDivider(true);
        adapter.notifyItemChanged(top);
        if (position != total - 1) {
            adapter.getItem(position).setDivider(true);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutEdit:
                llBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    int bottomSheetHeight = llBottomSheet.getMeasuredHeight();
                    listener.onScroll(bottomSheetHeight);
                });
                listener.onAction("edit");
                break;
            case R.id.linearLayoutCopy:
                listener.onAction("copy");
                break;
            case R.id.linearLayoutShare:
                listener.onAction("share");
                break;
            case R.id.linearLayoutAudio:
                showAudio();
                listener.onAction("audio");
                break;
            case R.id.buttonLeft:
                listener.onSetItem(folderName,"save-note",getNote(),folder,0);
                listener.onAction("save");
            case R.id.buttonRight:
                listener.onAction("cancel");
                break;
            case R.id.linearLayoutStop:
                listener.onAction("stop");
                break;
            case R.id.linearLayoutSettings:
                listener.onAction("settings");
                break;
            case R.id.gridLayoutFolder:
                visibleFolder(!isFolder);
                break;
            case R.id.linearLayoutRoundOne:
                listener.onSetItem(folderName,"color",getNote(),folder,1);
                break;
            case R.id.linearLayoutRoundTwo:
                listener.onSetItem(folderName,"color",getNote(),folder,2);
                break;
            case R.id.linearLayoutRoundThree:
                listener.onSetItem(folderName,"color",getNote(),folder,3);
                break;
            case R.id.linearLayoutRoundFour:
                listener.onSetItem(folderName,"color",getNote(),folder,4);
                break;
            case R.id.linearLayoutRoundFive:
                listener.onSetItem(folderName,"color",getNote(),folder,5);
                break;
            case R.id.linearLayoutRoundSix:
                listener.onSetItem(folderName,"color",getNote(),folder,6);
                break;
            case R.id.linearLayoutRoundSeven:
                listener.onSetItem(folderName,"color",getNote(),folder,7);
                break;
            case R.id.linearLayoutRoundEight:
                listener.onSetItem(folderName,"color",getNote(),folder,8);
                break;
            case R.id.linearLayoutRoundNine:
                listener.onSetItem(folderName,"color",getNote(),folder,9);
                break;
            case R.id.linearLayoutRoundTen:
                listener.onSetItem(folderName,"color",getNote(),folder,10);
                break;
            case R.id.linearLayoutNote:
                visibleNote(!isNote);
                break;
            case R.id.linearLayoutFavorite:
                listener.onSetItem(folderName,"favorite",getNote(),folder,1);
                break;
            case R.id.linearLayoutUnderline:
                listener.onSetItem(folderName,"underline",getNote(),folder,1);
                break;
            case R.id.linearLayoutClear:
                etNote.getText().clear();
                ivNote.setImageResource(R.drawable.ic_note);
                listener.onSetItem(folderName,"clear",getNote(),folder,1);
                break;
            case R.id.linearLayoutFolder:
                visibleFolderCreate(!isFolderCreate);
                break;
            case R.id.linearLayoutSend:
                listener.onSend(etName.getText().toString().trim());
                break;
            case R.id.imageViewAdd:
                visibleAdd(!isAdd,false);
                break;
        }
    }
}
