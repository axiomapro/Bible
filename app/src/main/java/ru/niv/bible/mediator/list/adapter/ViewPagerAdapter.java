package ru.niv.bible.mediator.list.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.fragment.child.FolderChildFragment;
import ru.niv.bible.fragment.child.MainChildFragment;
import ru.niv.bible.fragment.child.ReadingPlanChildFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String screen;
    private String start;
    private final int total;
    private int type, cat, plan;

    public void setTypeAndCat(int type,int cat) {
        this.type = type;
        this.cat = cat;
    }

    public void setReadingPlanContainer(String start,int plan,int type) {
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
        if (screen.equals(Config.viewPager().main())) fragment = MainChildFragment.newInstance(position);
        else if (screen.equals(Config.viewPager().readingPlanContainer())) fragment = ReadingPlanChildFragment.newInstance(start,plan,type,position);
        else fragment = FolderChildFragment.newInstance(type,cat,position);
        return fragment;
    }

    @Override
    public int getCount() {
        return total;
    }
}