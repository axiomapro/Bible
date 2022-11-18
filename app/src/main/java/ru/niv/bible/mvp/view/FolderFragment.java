package ru.niv.bible.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.material.tabs.TabLayout;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.ViewPagerAdapter;
import ru.niv.bible.basic.component.Static;

public class FolderFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private GridLayout glTitle, glSearch;
    private ImageView ivSearch, ivClear, ivBack;
    private EditText etSearch;
    private boolean isVisibleSearch;
    private int type, cat, tab;

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
        initViewPager();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        ivBack = v.findViewById(R.id.imageViewBack);
        tabLayout = v.findViewById(R.id.tabLayout);
        viewPager = v.findViewById(R.id.viewPager);
        glTitle = v.findViewById(R.id.gridLayoutTitle);
        glSearch = v.findViewById(R.id.gridLayoutSearch);
        etSearch = v.findViewById(R.id.editTextSearch);
        ivClear = v.findViewById(R.id.imageViewClear);
        ivSearch = v.findViewById(R.id.imageViewSearch);
        tvTitle.setText(getArguments().getString("name"));
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getParentFragmentManager(), 0, Static.folder, 14);
        adapter.setTypeAndCat(type,cat);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab = position;
                if (isVisibleSearch) toggleSearch();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (Static.screen.equals(Static.main)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(tab);
                initTabLayout();
            },200);
        } else initTabLayout();
    }

    private void initTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.all));
        int[] iconTabs = {
                R.drawable.ic_tab_bookmark,R.drawable.ic_tab_note,R.drawable.ic_tab_underline,
                R.drawable.ic_tab_one,R.drawable.ic_tab_two,R.drawable.ic_tab_three,R.drawable.ic_tab_four,R.drawable.ic_tab_five,
                R.drawable.ic_tab_six,R.drawable.ic_tab_seven,R.drawable.ic_tab_eight,R.drawable.ic_tab_nine,R.drawable.ic_tab_ten};
        for (int i = 0; i < iconTabs.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.tab_icon,null);
            TabLayout.Tab tab = tabLayout.getTabAt(i+1);
            view.findViewById(R.id.imageViewIcon).setBackgroundResource(iconTabs[i]);
            if (tab!=null) tab.setCustomView(view);
        }
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
        return (FolderChildFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
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
                closeKeyboard();
                getParentFragmentManager().popBackStack();
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
