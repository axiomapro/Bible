package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.ViewPagerAdapter;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.mvp.contract.ReadingPlanContainerContract;
import ru.niv.bible.mvp.presenter.ReadingPlanContainerPresenter;

public class ReadingPlanContainerFragment extends Fragment implements View.OnClickListener, ReadingPlanContainerContract.View, CompoundButton.OnCheckedChangeListener {

    private ReadingPlanContainerPresenter presenter;
    private Converter converter;
    private LinearLayout llTop, llBottom;
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private ImageView ivNotification;
    private TextView tvTitle, tvProgress, tvName, tvText, tvNotification;
    private AppCompatButton buttonPlan, buttonStop, buttonYear, buttonHalfYear, buttonQuarterYear;
    private String startPlan, notificationPlan;
    private int id, total, typePlan, startPosition;

    public static ReadingPlanContainerFragment newInstance(int id) {
        ReadingPlanContainerFragment fragment = new ReadingPlanContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_plan_container,container,false);
        id = getArguments().getInt("id");
        initViews(v);
        initClasses();
        setParams();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        llTop = v.findViewById(R.id.linearLayoutTop);
        llBottom = v.findViewById(R.id.linearLayoutBottom);
        tvTitle = v.findViewById(R.id.textViewTitle);
        tvProgress = v.findViewById(R.id.textViewProgress);
        tvName = v.findViewById(R.id.textViewName);
        tvText = v.findViewById(R.id.textViewText);
        tvNotification = v.findViewById(R.id.textViewNotification);
        ivNotification = v.findViewById(R.id.imageViewNotification);
        viewPager = v.findViewById(R.id.viewPager);
        progressBar = v.findViewById(R.id.progressBar);
        checkBox = v.findViewById(R.id.checkBox);
        buttonPlan = v.findViewById(R.id.buttonPlan);
        buttonStop = v.findViewById(R.id.buttonStop);
        buttonYear = v.findViewById(R.id.buttonYear);
        buttonHalfYear = v.findViewById(R.id.buttonHalfYear);
        buttonQuarterYear = v.findViewById(R.id.buttonQuarterYear);
    }

    private void initClasses() {
        presenter = new ReadingPlanContainerPresenter(this);
        converter = new Converter();
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0,Static.readingPlan,total);
        adapter.setReadingPlan(startPlan,id,typePlan);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCheckBox(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (startPosition == 0) updateCheckBox(0);
        else viewPager.setCurrentItem(startPosition);
    }

    private void setParams() {
        presenter.getData(id, (name, text, start, notification, type, status) -> {
            typePlan = type;
            total = converter.getDayTotal(type);
            startPosition = converter.getDayPassed(start);
            startPlan = start;
            notificationPlan = notification;

            tvTitle.setText(name);
            tvName.setText(name);
            tvText.setText(text);
            if (status) {
                llTop.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                ivNotification.setImageResource(notification != null?R.drawable.ic_notification_active:R.drawable.ic_notification);
                tvNotification.setText(notification);
                initViewPager();
            } else {
                llTop.setVisibility(View.GONE);
                llBottom.setVisibility(View.VISIBLE);
            }

            updateProgressbar();
        });
    }

    private void setClickListeners() {
        buttonPlan.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonYear.setOnClickListener(this);
        buttonHalfYear.setOnClickListener(this);
        buttonQuarterYear.setOnClickListener(this);
        ivNotification.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
    }

    private void updateCheckBox(int position) {
        checkBox.setOnCheckedChangeListener(null);
        if (presenter.getTotalLeftDay(id,typePlan,position + 1) == 0) checkBox.setChecked(true);
        else checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(ReadingPlanContainerFragment.this);
    }

    private void updateProgressbar() {
        float percent = presenter.getProgress(id,typePlan);
        tvProgress.setText(converter.convertPercentToCeil(percent));
        progressBar.setProgress((int) percent);
    }

    @Override
    public void redraw() {
        ((ReadingPlanFragment) getParentFragmentManager().findFragmentByTag(Static.readingPlan)).redraw();
    }

    @Override
    public void notification(String notification) {
        notificationPlan = notification;
        ivNotification.setImageResource(notification != null?R.drawable.ic_notification_active:R.drawable.ic_notification);
        tvNotification.setText(notification);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ReadingPlanChildFragment readingPlanChildFragment = (ReadingPlanChildFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
        readingPlanChildFragment.setActive(isChecked);
        updateProgressbar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlan:
                getParentFragmentManager().beginTransaction().replace(R.id.container,ReadingPlanMaterialFragment.newInstance(id), Static.readingPlanMaterial).addToBackStack(Static.readingPlanMaterial).commit();
                break;
            case R.id.buttonStop:
                presenter.dialogInactive(id);
                break;
            case R.id.buttonYear:
                presenter.dialog(id,1,365);
                break;
            case R.id.buttonHalfYear:
                presenter.dialog(id,2,180);
                break;
            case R.id.buttonQuarterYear:
                presenter.dialog(id,3,90);
                break;
            case R.id.imageViewNotification:
                presenter.dialogNotification(id,notificationPlan);
                break;
        }
    }
}
