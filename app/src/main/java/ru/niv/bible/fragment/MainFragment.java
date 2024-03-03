package ru.niv.bible.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Content;
import ru.niv.bible.component.immutable.box.Go;
import ru.niv.bible.component.immutable.box.Param;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.fragment.child.MainChildFragment;
import ru.niv.bible.mediator.contract.DialogContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.list.adapter.ViewPagerAdapter;

public class MainFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private ViewPagerAdapter adapter;
    private Param param;
    private Content content;
    private Go go;
    private Handler handler;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private LinearLayout llChapter;
    private ImageView ivMenu, ivFavorites, ivSearch;
    private TextView tvChapter, tvPage;
    private int maxPosition, audioPositionFragment, previousPosition, currentPosition;
    private boolean isRestore;

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
        if (getArguments() != null) toPosition();
        else currentItem();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivSearch = v.findViewById(R.id.imageViewSearch);
        ivFavorites = v.findViewById(R.id.imageViewFavorites);
        ivMenu = v.findViewById(R.id.imageViewMenu);
        llChapter = v.findViewById(R.id.linearLayoutChapter);
        tvChapter = v.findViewById(R.id.textViewChapter);
        tvPage = v.findViewById(R.id.textViewPage);
        progressBar = v.findViewById(R.id.progressBar);
        viewPager = v.findViewById(R.id.viewPager);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        param = new Param(getContext());
        go = new Go(getContext());
        content = new Content(getContext());
        handler = new Handler(hc);
    }

    private void setParams() {
        Static.font = param.getInt(Config.param().font());
        Static.fontSize = param.getInt(Config.param().fontSize());
        Static.lineSpacing = param.getInt(Config.param().lineSpacing());
        Static.selection = param.getInt(Config.param().selection());
        maxPosition = param.getInt(Config.param().maxPosition());
        currentPosition = param.getInt(Config.param().position());
    }

    private void currentItem() {
        loading(true);
        updateChapterAndPage(currentPosition);
        adapter = new ViewPagerAdapter(getParentFragmentManager(),0,Config.viewPager().main(),maxPosition);//
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                param.setInt(Config.param().position(),position);
                updateChapterAndPage(position);
                currentPosition = position;
                handler.sendEmptyMessageDelayed(1,1000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.post(() -> {
            if (isRestore) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    loading(false);
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(currentPosition);
                    adapter.notifyDataSetChanged();
                },1000);
            } else loading(false);

            isRestore = true;
        });
    }

    private void toPosition() {
        int chapter = getArguments().getInt("chapter");
        int page = getArguments().getInt("page");
        int position = getArguments().getInt("item");
        currentPosition = mediator.get().data().getPositionByChapterAndPageMain(chapter,page) - 1;
        param.setInt(Config.param().position(),currentPosition);

        loading(true);
        updateChapterAndPage(currentPosition);
        adapter = new ViewPagerAdapter(getParentFragmentManager(),0,Config.viewPager().main(),maxPosition);//
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                param.setInt(Config.param().position(),position);
                updateChapterAndPage(position);
                currentPosition = position;
                handler.sendEmptyMessageDelayed(1,1000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.post(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(currentPosition);
            adapter.notifyDataSetChanged();

            new Handler(Looper.getMainLooper()).postDelayed(() -> getMainChild(getPosition()).toPositionWithDelay(position),500);
            loading(false);
            setArguments(null);
            isRestore = true;
        },1000));
    }

    private void loading(boolean status) {
        viewPager.setVisibility(status?View.INVISIBLE:View.VISIBLE);
        progressBar.setVisibility(status?View.VISIBLE:View.GONE);
    }

    private void clearBackStack() {
        int chapter = getChapter();
        int page = getPage();
        int item = getItemPosition();
        for (int i = 0; i < getParentFragmentManager().getBackStackEntryCount(); i++) {
            getParentFragmentManager().popBackStack();
        }
        mediator.transition(getParentFragmentManager(),MainFragment.newInstance(chapter,page,item),Config.screen().main(),0,true,false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        llChapter.setOnTouchListener((v1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) llChapter.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) llChapter.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });
        ivSearch.setOnTouchListener((v2, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivSearch.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivSearch.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });
        ivFavorites.setOnTouchListener((v3, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivFavorites.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivFavorites.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });

        ivSearch.setOnClickListener(this);
        ivFavorites.setOnClickListener(this);
        llChapter.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
    }

    public void nextPosition() {
        if (viewPager.getCurrentItem() == maxPosition - 1) return;
        audioPositionFragment = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(audioPositionFragment,true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> getMainChild(getPosition()).startAudio(),1000);
    }

    public void shareApp() {
        String app = "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();
        mediator.show().dialog().shareApp(new DialogContract.Share() {
            @Override
            public void twitter() {
                go.twitter(app,((MainActivity) getActivity()));
            }

            @Override
            public void share() {
                content.share("",getString(R.string.dialog_share_text)+":\n"+app,getString(R.string.share_popup_dialog));
            }
        });
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
        mediator.get().data().getChapterAndPageMain(position, (id, page, chapter) -> {
            tvChapter.setText(chapter);
            tvPage.setText(String.valueOf(page));
        });
    }

    @Override
    public void onClick(View v) {
        getMainChild(getPosition()).stop();
        if (getParentFragmentManager().findFragmentByTag(Config.screen().search()) != null || getParentFragmentManager().findFragmentByTag(Config.screen().favorites()) != null) {
            clearBackStack();
        }

        switch (v.getId()) {
            case R.id.linearLayoutChapter:
                mediator.transition(getParentFragmentManager(),new ListFragment(),Config.screen().list(),Static.UP_ANIMATION,true,true);
                break;
            case R.id.imageViewSearch:
                mediator.transition(getParentFragmentManager(),new SearchFragment(),Config.screen().search(),Static.UP_ANIMATION,true,true);
                break;
            case R.id.imageViewFavorites:
                mediator.transition(getParentFragmentManager(),new FavoritesFragment(),Config.screen().favorites(),Static.RIGHT_ANIMATION,true,true);
                break;
            case R.id.imageViewMenu:
                ((MainActivity) getActivity()).openDrawerMenu();
        }
    }
}