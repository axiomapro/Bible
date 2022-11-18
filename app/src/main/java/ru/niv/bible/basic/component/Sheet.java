package ru.niv.bible.basic.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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
import ru.niv.bible.basic.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.item.Item;

public class Sheet implements View.OnClickListener {

    private BottomSheet listener;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerViewFolder;
    private BottomSheetBehavior bottomSheet;
    private AppCompatButton buttonLeft;
    private ImageView ivArrow, ivAdd;
    private GridLayout glPreview, glAudio, glFolder, glAdd;
    private LinearLayout llMore;
    private TextView tvFolder, tvMessage;
    private EditText etName;
    private View llBottomSheet;
    private List<Item> listFolder;
    private String folderName;
    private boolean isFolder, isAdd;
    private int folder;

    public interface BottomSheet {
        void onSetItem(String folderName,String type,int folder,int value);
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

    @SuppressLint("ClickableViewAccessibility")
    public void initViews(View v) {
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        LinearLayout llCopy = v.findViewById(R.id.linearLayoutCopy);
        LinearLayout llAudio = v.findViewById(R.id.linearLayoutAudio);
        LinearLayout llShare = v.findViewById(R.id.linearLayoutShare);
        LinearLayout llEdit = v.findViewById(R.id.linearLayoutEdit);
        LinearLayout llFavorite = v.findViewById(R.id.linearLayoutFavorite);
        LinearLayout llUnderline = v.findViewById(R.id.linearLayoutUnderline);
        LinearLayout llClear = v.findViewById(R.id.linearLayoutClear);
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
        etName = v.findViewById(R.id.editTextName);
        tvMessage = v.findViewById(R.id.textViewMessage);
        tvFolder = v.findViewById(R.id.textViewFolder);
        buttonLeft = v.findViewById(R.id.buttonLeft);
        ivArrow = v.findViewById(R.id.imageViewArrow);
        glPreview = v.findViewById(R.id.gridLayoutPreview);
        glAudio = v.findViewById(R.id.gridLayoutAudio);
        glFolder = v.findViewById(R.id.gridLayoutFolder);
        glAdd = v.findViewById(R.id.gridLayoutAdd);
        llBottomSheet = v.findViewById(R.id.linearLayoutBottomSheet);
        llMore = v.findViewById(R.id.linearLayoutMore);
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
        llFavorite.setOnClickListener(this);
        llUnderline.setOnClickListener(this);
        llClear.setOnClickListener(this);
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
            glPreview.setVisibility(View.GONE);
            llMore.setVisibility(View.VISIBLE);
            buttonLeft.setVisibility(View.VISIBLE);
        } else {
            llMore.setVisibility(View.GONE);
            visibleFolder(false);
            visibleAdd(false,false);
            buttonLeft.setVisibility(View.GONE);
            glPreview.setVisibility(View.VISIBLE);
        }
    }

    public void showAudio() {
        glPreview.setVisibility(View.GONE);
        glAudio.setVisibility(View.VISIBLE);
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

    public void visibleBottomSheet(boolean status) {
        bottomSheet.setState(status?BottomSheetBehavior.STATE_EXPANDED:BottomSheetBehavior.STATE_HIDDEN);
        if (!status) {
            llMore.setVisibility(View.GONE);
            glAudio.setVisibility(View.GONE);
            buttonLeft.setVisibility(View.GONE);
            glPreview.setVisibility(View.VISIBLE);
            visibleFolder(false);
            visibleAdd(false,true);
        }
    }

    public int getHeightBottomSheet() {
        return llBottomSheet.getHeight();
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
                listener.onSetItem(folderName,"color",folder,1);
                break;
            case R.id.linearLayoutRoundTwo:
                listener.onSetItem(folderName,"color",folder,2);
                break;
            case R.id.linearLayoutRoundThree:
                listener.onSetItem(folderName,"color",folder,3);
                break;
            case R.id.linearLayoutRoundFour:
                listener.onSetItem(folderName,"color",folder,4);
                break;
            case R.id.linearLayoutRoundFive:
                listener.onSetItem(folderName,"color",folder,5);
                break;
            case R.id.linearLayoutRoundSix:
                listener.onSetItem(folderName,"color",folder,6);
                break;
            case R.id.linearLayoutRoundSeven:
                listener.onSetItem(folderName,"color",folder,7);
                break;
            case R.id.linearLayoutRoundEight:
                listener.onSetItem(folderName,"color",folder,8);
                break;
            case R.id.linearLayoutRoundNine:
                listener.onSetItem(folderName,"color",folder,9);
                break;
            case R.id.linearLayoutRoundTen:
                listener.onSetItem(folderName,"color",folder,10);
                break;
            case R.id.linearLayoutFavorite:
                listener.onSetItem(folderName,"favorite",folder,1);
                break;
            case R.id.linearLayoutUnderline:
                listener.onSetItem(folderName,"underline",folder,1);
                break;
            case R.id.linearLayoutClear:
                listener.onSetItem(folderName,"clear",folder,1);
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
