package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.ViewPagerAdapter;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.mvp.contract.MainContract;
import ru.niv.bible.mvp.presenter.MainPresenter;

public class MainFragment extends Fragment implements View.OnClickListener, MainContract.View {

    private MainPresenter presenter;
    private ViewPagerAdapter adapter;
    private Param param;
    private Handler handler;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private GridLayout glChapter;
    private ImageView ivMenu, ivFavorites, ivSearch;
    private TextView tvChapter, tvPage;
    private String screen;
    private int maxPosition, audioPositionFragment, previousPosition, currentPosition;
    private boolean arguments, isCheckedArguments;

    public static MainFragment newInstance(int chapter, int page, int item) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("chapter",chapter);
        bundle.putInt("page",page);
        bundle.putInt("item",item);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final Handler.Callback hc = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (previousPosition != currentPosition) {
                    getMainChild(previousPosition).stop();
                    previousPosition = currentPosition;
                }
                if (audioPositionFragment > 0) {
                    getMainChild(audioPositionFragment).stop();
                }
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main,container,false);
        initViews(v);
        initClasses();
        setParams();
        initViewPager();
        setCurrentItem();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivSearch = v.findViewById(R.id.imageViewSearch);
        ivFavorites = v.findViewById(R.id.imageViewFavorites);
        ivMenu = v.findViewById(R.id.imageViewMenu);
        glChapter = v.findViewById(R.id.gridLayoutChapter);
        tvChapter = v.findViewById(R.id.textViewChapter);
        tvPage = v.findViewById(R.id.textViewPage);
        viewPager = v.findViewById(R.id.viewPager);
        progressBar = v.findViewById(R.id.progressBar);
    }

    private void initClasses() {
        param = new Param(getContext());
        presenter = new MainPresenter(this);
        handler = new Handler(hc);
    }

    private void setParams() {
        if (!isCheckedArguments) {
            isCheckedArguments = true;
            arguments = getArguments() != null;
        }
        Static.font = param.getInt(Static.paramFont);
        Static.fontSize = param.getInt(Static.paramFontSize);
        Static.lineSpacing = param.getInt(Static.paramLineSpacing);
        Static.selection = param.getInt(Static.paramSelection);
        screen = Static.screen;
        maxPosition = param.getInt(Static.paramMaxPosition);
        if (getArguments() != null) {
            tvChapter.setText(presenter.getChapterName(getArguments().getInt("chapter")));
            tvPage.setText(String.valueOf(getArguments().getInt("page")));
        }
    }

    private void initViewPager() {
        adapter = new ViewPagerAdapter(getParentFragmentManager(),0,Static.main,maxPosition);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(false, (v1, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
            v1.setAlpha(opacity);
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                param.setInt(Static.paramPosition,position);
                updateChapterAndPage(position);
                currentPosition = position;
                handler.sendEmptyMessageDelayed(1,1000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.post(() -> {
            viewPager.setAdapter(adapter);
            loading(false);
        });
    }

    private void setCurrentItem() {
        int currentPosition;
        if (arguments) {
            int chapter = getArguments().getInt("chapter");
            int page = getArguments().getInt("page");
            currentPosition = presenter.getPositionByChapterAndPage(chapter,page) - 1;
            param.setInt(Static.paramPosition,currentPosition);
        } else currentPosition = param.getInt(Static.paramPosition);

        if (currentPosition == 0) updateChapterAndPage(0);
        if (screen.equals(Static.main) && !arguments || screen.equals(Static.feedback)) viewPager.setCurrentItem(currentPosition);
        else {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(currentPosition);

            if (arguments) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    arguments = false;
                    getMainChild(getPosition()).toPositionWithDelay(getArguments().getInt("item"));
                },200);
            } else loading(true);
        }
    }

    private void loading(boolean status) {
        if (status) {
            viewPager.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    private void setClickListeners() {
        ivSearch.setOnClickListener(this);
        ivFavorites.setOnClickListener(this);
        glChapter.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
    }

    public void nextPosition() {
        if (viewPager.getCurrentItem() == maxPosition - 1) return;
        audioPositionFragment = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(audioPositionFragment,true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> getMainChild(getPosition()).startAudio(),1000);
    }

    public void shareApp() {
        presenter.shareDialog(getContext());
    }

    public int getChapter() {
        return getMainChild(getPosition()).getChapterId();
    }

    public int getPage() {
        return getMainChild(getPosition()).getChapterPage();
    }

    public int getItemPosition() {
        return getMainChild(getPosition()).getItemPosition();
    }

    public int getPosition() {
        return viewPager.getCurrentItem();
    }

    public MainChildFragment getMainChild(int position) {
        return (MainChildFragment) viewPager
                .getAdapter()
                .instantiateItem(viewPager, position);
    }

    public void updateChapterAndPage(int position) {
        JSONObject jsonObject = ((MainActivity) getActivity()).getJsonInfo(position);
        try {
            String name = jsonObject.getString("name");
            int page = jsonObject.getInt("page");
            tvChapter.setText(name);
            tvPage.setText(String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void message(String message) {
        ((MainActivity) getActivity()).message(message);
    }

    @Override
    public void onClick(View v) {
        String tag = null;
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.gridLayoutChapter:
                tag = Static.list;
                fragment = new ListFragment();
                break;
            case R.id.imageViewSearch:
                tag = Static.search;
                fragment = new SearchFragment();
                break;
            case R.id.imageViewFavorites:
                tag = Static.favorites;
                fragment = new FavoritesFragment();
                break;
            case R.id.imageViewMenu:
                ((MainActivity) getActivity()).openDrawerMenu();
                return;
        }
        getMainChild(getPosition()).stop();

        if (getParentFragmentManager().findFragmentByTag(Static.search) != null || getParentFragmentManager().findFragmentByTag(Static.favorites) != null) {
            String topTag = getParentFragmentManager().getBackStackEntryAt(getParentFragmentManager().getBackStackEntryCount() - 1).getName();
            MainFragment mainFragment = (MainFragment) getParentFragmentManager().findFragmentByTag(topTag);
            int chapter = mainFragment.getChapter();
            int page = mainFragment.getPage();
            int item = mainFragment.getItemPosition();
            for (int i = 0; i < getParentFragmentManager().getBackStackEntryCount(); i++) {
                getParentFragmentManager().popBackStack();
            }
            getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(chapter,page,item),Static.main).commit();
        }

        getParentFragmentManager().beginTransaction().replace(R.id.container,fragment,tag).addToBackStack(tag).commit();
    }
}
