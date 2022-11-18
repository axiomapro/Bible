package ru.niv.bible.mvp.view;

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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.SearchContract;
import ru.niv.bible.mvp.presenter.SearchPresenter;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchContract.View {

    private SearchPresenter presenter;
    private Go go;
    private RecyclerViewAdapter adapter;
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
        presenter = new SearchPresenter(this);
        go = new Go(getContext());
    }

    private void initRecyclerView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(Static.search,new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                if (isSelect) {
                    if (isBlockListenerCheckBox) return;
                    int id = adapter.getItem(position).getId();
                    boolean checkBox = adapter.getItem(position).isCheckBox();
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
                    adapter.getItem(position).setCheckBox(isChecked);
                    adapter.notifyItemChanged(position);
                    tvSelected.setText(String.valueOf(totalCheckBox));
                } else {
                    closeKeyboard();
                    getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(adapter.getItem(position).getChapter(),adapter.getItem(position).getPage(),adapter.getItem(position).getPosition()),Static.main).addToBackStack(Static.main).commit();
                }
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {
                if (isBlockListenerCheckBox) return;
                int id = adapter.getItem(position).getId();

                if (isChecked) {
                    if (totalCheckBox == max) {
                        adapter.getItem(position).setCheckBox(false);
                        adapter.notifyItemChanged(position);
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
                    adapter.getList().clear();
                    adapter.notifyDataSetChanged();
                    glResult.setVisibility(View.GONE);
                } else ivClear.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (getQuery().length() > 1) redraw(presenter.getList(getQuery(),tab,cbExact.isChecked(),cbPartial.isChecked()));
                return true;
            }
            return false;
        });

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter.getItemCount() > 50) {
                cbSelectAll.setChecked(false);
                ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
            } else {
                isBlockListenerCheckBox = true;
                totalCheckBox = isChecked?adapter.getItemCount():0;
                tvSelected.setText(String.valueOf(totalCheckBox));
                updateItems(isChecked);
            }
        });
        cbExact.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getQuery().length() >= 2) redraw(presenter.getList(getQuery(),tab,isChecked,cbPartial.isChecked()));
        });
        cbPartial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getQuery().length() >= 2 && !cbExact.isChecked()) redraw(presenter.getList(getQuery(),tab,cbExact.isChecked(),isChecked));
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
        for (int i = 0; i < adapter.getItemCount(); i++) {
            adapter.getItem(i).setCheckBox(isChecked);
            if (isChecked) ids += ","+adapter.getItem(i).getId();
        }
        adapter.notifyDataSetChanged();

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
        adapter.getList().clear();
        adapter.getList().addAll(list);
        adapter.setQuery(query);
        adapter.notifyDataSetChanged();
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
        redraw(presenter.getList(getQuery(),tab,cbExact.isChecked(),cbPartial.isChecked()));
    }

    private void copyOrShare(boolean copy) {
        if (isSelect) {
            if (totalCheckBox == 0) ((MainActivity) getActivity()).message(getString(R.string.no_items_selected));
            else if (totalCheckBox > 50) ((MainActivity) getActivity()).message(getString(R.string.max_limit_reached));
            else {
                adapter.setCheckBox(false);
                adapter.notifyDataSetChanged();
                String copyText = presenter.getTextByIds(ids);

                try {
                    if (copy) {
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(getString(R.string.text_copied),copyText);
                        clipboard.setPrimaryClip(clip);
                        ((MainActivity) getActivity()).message(getString(R.string.text_copied));
                    }
                    else go.share(getString(R.string.share_search_title),copyText,getString(R.string.share_popup_search));
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
        adapter.setCheckBox(isSelect);
        adapter.notifyDataSetChanged();
    }

    private String getQuery() {
        return etSearch.getText().toString().trim();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etSearch.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    public boolean checkBackSelect() {
        boolean result = isSelect;
        if (isSelect) visibleSelectAll(false);
        return !result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                closeKeyboard();
                getParentFragmentManager().popBackStack();
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
        if (Static.screen.equals(Static.main) && getQuery().length() > 0) {
            tabClick(1);
        }
    }
}
