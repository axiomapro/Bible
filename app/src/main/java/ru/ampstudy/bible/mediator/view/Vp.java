package ru.ampstudy.bible.mediator.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.fragment.child.FolderChildFragment;
import ru.ampstudy.bible.mediator.list.adapter.ViewPagerAdapter;

public class Vp {

    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String start;
    private int tab, cat, type, plan;

    public void setCatAndType(int cat,int type) {
        this.cat = cat;
        this.type = type;
    }

    public void setReadingPlanContainer(String start,int plan,int type) {
        this.start = start;
        this.plan = plan;
        this.type = type;
    }

    public void initialize(View v, FragmentManager fm, String screen, int mode, int total, ViewPager.OnPageChangeListener listener) {
        viewPager = v.findViewById(R.id.viewPager);
        tabLayout = v.findViewById(R.id.tabLayout);
        adapter = new ViewPagerAdapter(fm,0,screen,total);
        adapter.setTypeAndCat(type,cat);
        viewPager.setAdapter(adapter);
        tabLayout.setTabMode(mode);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(listener);

        viewPager.post(() -> {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(viewPager.getCurrentItem());
        });
    }

    public void initialize(View v, FragmentManager fm, String screen, int total, ViewPager.OnPageChangeListener listener) {
        viewPager = v.findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(fm,0,screen,total);
        adapter.setTypeAndCat(type,cat);
        adapter.setReadingPlanContainer(start,plan,type);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(listener);

        viewPager.post(() -> {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(viewPager.getCurrentItem());
        });
    }

    public void folder(View v, LayoutInflater layoutInflater, FragmentManager fm,boolean restore,ViewPager.OnPageChangeListener listener) {
        viewPager = v.findViewById(R.id.viewPager);
        tabLayout = v.findViewById(R.id.tabLayout);
        adapter = new ViewPagerAdapter(fm,0, Config.viewPager().folder(),14);
        adapter.setTypeAndCat(type,cat);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(listener);

        if (restore) {
            viewPager.post(() -> {
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(viewPager.getCurrentItem());
                initTabLayout(v.getContext(),layoutInflater);
            });
        } else initTabLayout(v.getContext(),layoutInflater);
    }

    public void initTabLayout(Context context,LayoutInflater layoutInflater) {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(context.getString(R.string.all));
        int[] iconTabs = {
                R.drawable.ic_tab_bookmark,R.drawable.ic_tab_note,R.drawable.ic_tab_underline,
                R.drawable.ic_tab_one,R.drawable.ic_tab_two,R.drawable.ic_tab_three,R.drawable.ic_tab_four,R.drawable.ic_tab_five,
                R.drawable.ic_tab_six,R.drawable.ic_tab_seven,R.drawable.ic_tab_eight,R.drawable.ic_tab_nine,R.drawable.ic_tab_ten};
        for (int i = 0; i < iconTabs.length; i++) {
            View view = layoutInflater.inflate(R.layout.tab_icon,null);
            TabLayout.Tab tab = tabLayout.getTabAt(i+1);
            view.findViewById(R.id.imageViewIcon).setBackgroundResource(iconTabs[i]);
            if (tab!=null) tab.setCustomView(view);
        }
    }

    public int getTab() {
        return tab;
    }

    public Fragment getChildFragment() {
        return (FolderChildFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
    }

    public void update() {
        adapter.notifyDataSetChanged();
    }

}