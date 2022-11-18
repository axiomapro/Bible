package ru.niv.bible.basic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.niv.bible.basic.component.Static;
import ru.niv.bible.mvp.view.FolderChildFragment;
import ru.niv.bible.mvp.view.MainChildFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String screen;
    private final int total;
    private int type, cat;

    public void setTypeAndCat(int type,int cat) {
        this.type = type;
        this.cat = cat;
    }

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,String screen,int total) {
        super(fm, behavior);
        this.screen = screen;
        this.total = total;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (screen.equals(Static.main)) fragment = MainChildFragment.newInstance(position);
        else fragment = FolderChildFragment.newInstance(type,cat,position);
        return fragment;
    }

    @Override
    public int getCount() {
        return total;
    }

}
