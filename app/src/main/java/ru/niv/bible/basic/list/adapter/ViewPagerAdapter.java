package ru.niv.bible.basic.list.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.mvp.view.FolderChildFragment;
import ru.niv.bible.mvp.view.MainChildFragment;
import ru.niv.bible.mvp.view.ReadingPlanChildFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String screen;
    private String start;
    private final int total;
    private int type, cat, plan;

    public void setTypeAndCat(int type,int cat) {
        this.type = type;
        this.cat = cat;
    }

    public void setReadingPlan(String start,int plan,int type) {
        this.start = start;
        this.plan = plan;
        this.type = type;
    }

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,String screen,int total) {
        super(fm, behavior);
        this.screen = screen;
        this.total = total;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (screen.equals(Static.main)) fragment = MainChildFragment.newInstance(position);
        else if (screen.equals(Static.readingPlan)) fragment = ReadingPlanChildFragment.newInstance(start,plan,type,position);
        else fragment = FolderChildFragment.newInstance(type,cat,position);
        return fragment;
    }

    @Override
    public int getCount() {
        return total;
    }

}
