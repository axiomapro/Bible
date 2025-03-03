package ru.ampstudy.bible.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.fragment.child.FolderChildFragment;
import ru.ampstudy.bible.mediator.core.Mediator;
import ru.ampstudy.bible.mediator.view.Vp;

public class FolderFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Vp vp;
    private GridLayout glTitle, glSearch;
    private ImageView ivSearch, ivClear, ivBack;
    private EditText etSearch;
    private boolean isVisibleSearch;
    private int type, cat;

    public static FolderFragment newInstance(String name,int type,int cat) {
        FolderFragment fragment = new FolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putInt("type",type);
        bundle.putInt("cat",cat);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder,container,false);
        type = getArguments().getInt("type");
        cat = getArguments().getInt("cat");
        initViews(v);
        initClasses();
        initViewPager(v);
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        ivBack = v.findViewById(R.id.imageViewBack);
        glTitle = v.findViewById(R.id.gridLayoutTitle);
        glSearch = v.findViewById(R.id.gridLayoutSearch);
        etSearch = v.findViewById(R.id.editTextSearch);
        ivClear = v.findViewById(R.id.imageViewClear);
        ivSearch = v.findViewById(R.id.imageViewSearch);
        tvTitle.setText(getArguments().getString("name"));
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        vp = mediator.view().vp();
    }

    private void initViewPager(View v) {
        vp.setCatAndType(cat,type);
        vp.folder(v, getLayoutInflater(), getParentFragmentManager(),Static.screen.equals(Config.screen().main()), new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isVisibleSearch) toggleSearch();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                String query = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(query)) ivClear.setVisibility(View.GONE);
                else ivClear.setVisibility(View.VISIBLE);
                getFolderChildFragment().search(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivClear.setOnClickListener(this);
    }

    private void toggleSearch() {
        if (isVisibleSearch) {
            isVisibleSearch = false;
            etSearch.getText().clear();
            glTitle.setVisibility(View.VISIBLE);
            glSearch.setVisibility(View.GONE);
            ivSearch.setImageResource(R.drawable.ic_search);
            closeKeyboard();
        } else {
            isVisibleSearch = true;
            glTitle.setVisibility(View.GONE);
            glSearch.setVisibility(View.VISIBLE);
            ivSearch.setImageResource(R.drawable.ic_close_search);
        }
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etSearch.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    public FolderChildFragment getFolderChildFragment() {
        return (FolderChildFragment) vp.getChildFragment();
    }

    public boolean checkBackSearch() {
        boolean result = isVisibleSearch;
        if (isVisibleSearch) toggleSearch();
        return !result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                if (isVisibleSearch) toggleSearch();
                else getParentFragmentManager().popBackStack();
                break;
            case R.id.imageViewSearch:
                toggleSearch();
                break;
            case R.id.imageViewClear:
                etSearch.getText().clear();
                break;
        }
    }
}