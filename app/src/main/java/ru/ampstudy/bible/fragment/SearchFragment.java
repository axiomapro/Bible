package ru.ampstudy.bible.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.ampstudy.bible.MainActivity;
import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Content;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.mediator.contract.RecyclerViewContract;
import ru.ampstudy.bible.mediator.core.Mediator;
import ru.ampstudy.bible.mediator.list.item.Item;
import ru.ampstudy.bible.mediator.view.Rview;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Rview rview;
    private Content content;
    private EditText etSearch;
    private View vAllBottom, vOtBottom, vNtBottom;
    private GridLayout glResult;
    private LinearLayout llResult, llSelectAll;
    private RelativeLayout rlAll, rlOt, rlNt;
    private TextView tvResult, tvSelected, tvTotal;
    private CheckBox cbExact, cbPartial, cbSelectAll;
    private ImageView ivCopy, ivShare, ivClose, ivClear, ivBack;
    private String ids = "";
    private boolean isSelect, isBlockListenerCheckBox;
    private final int max = 50;
    private int tab = 1;
    private int totalCheckBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search,container,false);
        initViews(v);
        initClasses();
        initRecyclerView(v);
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivBack = v.findViewById(R.id.imageViewBack);
        ivClear = v.findViewById(R.id.imageViewClear);
        ivCopy = v.findViewById(R.id.imageViewCopy);
        ivShare = v.findViewById(R.id.imageViewShare);
        ivClose = v.findViewById(R.id.imageViewClose);
        llResult = v.findViewById(R.id.linearLayoutResult);
        llSelectAll = v.findViewById(R.id.linearLayoutSelectAll);
        tvResult = v.findViewById(R.id.textViewResult);
        tvSelected = v.findViewById(R.id.textViewSelected);
        tvTotal = v.findViewById(R.id.textViewTotal);
        rlAll = v.findViewById(R.id.relativeLayoutAll);
        rlOt = v.findViewById(R.id.relativeLayoutOt);
        rlNt = v.findViewById(R.id.relativeLayoutNt);
        vAllBottom = v.findViewById(R.id.viewAllBottom);
        vOtBottom = v.findViewById(R.id.viewOtBottom);
        vNtBottom = v.findViewById(R.id.viewNtBottom);
        etSearch = v.findViewById(R.id.editTextSearch);
        cbExact = v.findViewById(R.id.checkBoxExact);
        cbPartial = v.findViewById(R.id.checkBoxPartial);
        cbSelectAll = v.findViewById(R.id.checkBoxSelectAll);
        glResult = v.findViewById(R.id.gridLayoutResult);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
        content = new Content(getContext());
    }

    private void initRecyclerView(View v) {
        rview.setRecyclerView(v.findViewById(R.id.recyclerView));
        rview.initialize(Config.recyclerView().search(), new ArrayList<>(), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                if (isSelect) {
                    if (isBlockListenerCheckBox) return;
                    int id = rview.getItem(position).getId();
                    boolean checkBox = rview.getItem(position).isCheckBox();
                    boolean isChecked = false;
                    if (!checkBox) isChecked = true;

                    if (isChecked) {
                        if (totalCheckBox == max) {
                            ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
                            return;
                        }
                        totalCheckBox++;
                        ids += ","+id;
                    } else {
                        totalCheckBox--;
                        ids = ids.replaceAll(","+id,"");
                    }
                    rview.getItem(position).setCheckBox(isChecked);
                    rview.updateItem(position);
                    tvSelected.setText(String.valueOf(totalCheckBox));
                } else {
                    closeKeyboard();
                    mediator.transition(getParentFragmentManager(),MainFragment.newInstance(rview.getItem(position).getChapter(),rview.getItem(position).getPage(),rview.getItem(position).getPosition()),Config.screen().main(),Static.DOWN_ANIMATION,true,true);
                }
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, int day, boolean isChecked) {
                if (isBlockListenerCheckBox) return;
                int id = rview.getItem(position).getId();

                if (isChecked) {
                    if (totalCheckBox == max) {
                        rview.getItem(position).setCheckBox(false);
                        rview.updateItem(position);
                        ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
                        return;
                    }
                    totalCheckBox++;
                    ids += ","+id;
                } else {
                    totalCheckBox--;
                    ids = ids.replaceAll(","+id,"");
                }
                tvSelected.setText(String.valueOf(totalCheckBox));
            }

            @Override
            public void link(String link) {

            }
        });
    }

    private void setClickListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(getQuery())) {
                    ivClear.setVisibility(View.INVISIBLE);
                    rview.clear();
                    glResult.setVisibility(View.GONE);
                } else ivClear.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (getQuery().length() > 1) redraw(mediator.get().list().search(getQuery(),tab,cbExact.isChecked(),cbPartial.isChecked()));
                return true;
            }
            return false;
        });

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rview.getTotal() > 50) {
                cbSelectAll.setChecked(false);
                ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
            } else {
                isBlockListenerCheckBox = true;
                totalCheckBox = isChecked?rview.getTotal():0;
                tvSelected.setText(String.valueOf(totalCheckBox));
                updateItems(isChecked);
            }
        });
        cbExact.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getQuery().length() >= 2) redraw(mediator.get().list().search(getQuery(),tab,isChecked,cbPartial.isChecked()));
        });
        cbPartial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getQuery().length() >= 2 && !cbExact.isChecked()) redraw(mediator.get().list().search(getQuery(),tab,cbExact.isChecked(),isChecked));
        });
        ivBack.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        ivCopy.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        rlAll.setOnClickListener(this);
        rlOt.setOnClickListener(this);
        rlNt.setOnClickListener(this);
    }

    private void updateItems(boolean isChecked) {
        if (!isChecked) ids = "";
        for (int i = 0; i < rview.getTotal(); i++) {
            rview.getItem(i).setCheckBox(isChecked);
            if (isChecked) ids += ","+rview.getItem(i).getId();
        }
        rview.update();

        new Handler(Looper.getMainLooper()).postDelayed(() -> isBlockListenerCheckBox = false,1000);
    }

    private void redraw(List<Item> list) {
        String query = getQuery();
        if (getQuery().length() < 2) return;
        if (etSearch.isFocused()) {
            etSearch.clearFocus();
            InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }

        int total = list.size();
        rview.clear();

        rview.getList().addAll(list);
        rview.updateQuery(query);
        rview.update();
        glResult.setVisibility(View.VISIBLE);
        tvResult.setText(String.valueOf(total));
        tvSelected.setText("0");
        tvTotal.setText(String.valueOf(total));
        visibleSelectAll(false);
        if (total > 0) {
            ivCopy.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
        } else {
            ivCopy.setVisibility(View.GONE);
            ivShare.setVisibility(View.GONE);
            // show empty layout
        }
    }

    private void tabClick(int tab) {
        this.tab = tab;
        if (tab == 1) {
            vAllBottom.setVisibility(View.VISIBLE);
            vOtBottom.setVisibility(View.INVISIBLE);
            vNtBottom.setVisibility(View.INVISIBLE);
        }
        else if (tab == 2) {
            vAllBottom.setVisibility(View.INVISIBLE);
            vOtBottom.setVisibility(View.VISIBLE);
            vNtBottom.setVisibility(View.INVISIBLE);
        }
        else if (tab == 3) {
            vAllBottom.setVisibility(View.INVISIBLE);
            vOtBottom.setVisibility(View.INVISIBLE);
            vNtBottom.setVisibility(View.VISIBLE);
        }
        if (getQuery().length() > 0) redraw(mediator.get().list().search(getQuery(),tab,cbExact.isChecked(),cbPartial.isChecked()));
    }

    private void copyOrShare(boolean copy) {
        if (isSelect) {
            if (totalCheckBox == 0) ((MainActivity) getActivity()).message(getString(R.string.no_items_selected));
            else if (totalCheckBox > 50) ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
            else {
                rview.updateCheckBox(false);
                rview.update();
                String copyText = mediator.get().data().getTextByIdsSearch(ids);

                try {
                    if (copy) {
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(getString(R.string.text_copied),copyText);
                        clipboard.setPrimaryClip(clip);
                        ((MainActivity) getActivity()).message(getString(R.string.text_copied));
                    }
                    else content.share(getString(R.string.share_search_title),copyText,getString(R.string.share_popup_search));
                } catch (Exception e) {
                    e.printStackTrace();
                    ((MainActivity) getActivity()).message(getString(R.string.app_could_not_copy_text));
                }
                visibleSelectAll(false);
            }
        } else visibleSelectAll(true);
    }

    private void visibleSelectAll(boolean status) {
        isSelect = status;
        if (status) {
            llResult.setVisibility(View.GONE);
            llSelectAll.setVisibility(View.VISIBLE);
            ivClose.setVisibility(View.VISIBLE);
        } else {
            llResult.setVisibility(View.VISIBLE);
            llSelectAll.setVisibility(View.GONE);
            ivClose.setVisibility(View.GONE);
            if (cbSelectAll.isChecked()) cbSelectAll.setChecked(false);
            else {
                totalCheckBox = 0;
                tvSelected.setText(String.valueOf(totalCheckBox));
                updateItems(false);
            }
        }
        rview.updateCheckBox(isSelect);
        rview.update();
    }

    private String getQuery() {
        return etSearch.getText().toString().trim();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etSearch.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    public boolean checkBackSelect() {
        boolean result = getQuery().length() > 0;
        if (result) {
            etSearch.getText().clear();
            closeKeyboard();
            if (isSelect) visibleSelectAll(false);
        }
        return !result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                if (checkBackSelect()) getParentFragmentManager().popBackStack();
                break;
            case R.id.imageViewClear:
                etSearch.getText().clear();
                break;
            case R.id.relativeLayoutAll:
                tabClick(1);
                break;
            case R.id.relativeLayoutOt:
                tabClick(2);
                break;
            case R.id.relativeLayoutNt:
                tabClick(3);
                break;
            case R.id.imageViewCopy:
                copyOrShare(true);
                break;
            case R.id.imageViewShare:
                copyOrShare(false);
                break;
            case R.id.imageViewClose:
                visibleSelectAll(false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Static.screen.equals(Config.screen().main()) && getQuery().length() > 0) {
            tabClick(tab);
        }
    }
}