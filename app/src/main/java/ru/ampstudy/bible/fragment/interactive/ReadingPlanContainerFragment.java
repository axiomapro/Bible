package ru.ampstudy.bible.fragment.interactive;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
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

import ru.ampstudy.bible.MainActivity;
import ru.ampstudy.bible.component.immutable.box.Alarm;
import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Convert;
import ru.ampstudy.bible.component.immutable.box.Datetime;
import ru.ampstudy.bible.fragment.child.ReadingPlanChildFragment;
import ru.ampstudy.bible.fragment.material.ReadingPlanMaterialFragment;
import ru.ampstudy.bible.mediator.contract.DialogContract;
import ru.ampstudy.bible.mediator.core.Mediator;
import ru.ampstudy.bible.mediator.view.Vp;
import ru.ampstudy.bible.R;

public class ReadingPlanContainerFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Mediator mediator;
    private Vp vp;
    private Convert convert;
    private Datetime datetime;
    private Alarm alarm;
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
        setParams(v);
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
        mediator = new Mediator(getContext());
        vp = mediator.view().vp();
        convert = new Convert();
        datetime = new Datetime();
        alarm = new Alarm(getContext());
    }

    private void setParams(View v) {
        mediator.get().data().getDataReadingPlanContainer(id, (type, status, name, text, start, notification) -> {
            typePlan = type;
            total = convert.getDayTotal(type);
            startPosition = convert.getDayPassed(datetime.getDate(),start);
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
                initViewPager(v);
            } else {
                llTop.setVisibility(View.GONE);
                llBottom.setVisibility(View.VISIBLE);
            }

            updateProgressbar();
        });
    }

    private void initViewPager(View v) {
        vp.setReadingPlanContainer(startPlan,id,typePlan);
        vp.initialize(v, getChildFragmentManager(), Config.viewPager().readingPlanContainer(), total, new ViewPager.OnPageChangeListener() {
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
        if (mediator.get().data().getTotalLeftDayReadingPlanContainer(id,typePlan,position + 1) == 0) checkBox.setChecked(true);
        else checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(ReadingPlanContainerFragment.this);
    }

    private void updateProgressbar() {
        float percent = convert.getPercent(mediator.get().data().getTotalViewedReadingPlanContainer(id,typePlan),mediator.get().data().getTotalReadingPlanContainer(id,typePlan));
        tvProgress.setText(convert.convertPercentToCeil(percent));
        progressBar.setProgress((int) percent);
    }

    private void redraw() {
        ((ReadingPlanFragment) getParentFragmentManager().findFragmentByTag(Config.screen().readingPlan())).redraw();
    }

    private void notification(String notification) {
        notificationPlan = notification;
        ivNotification.setImageResource(notification != null?R.drawable.ic_notification_active:R.drawable.ic_notification);
        tvNotification.setText(notification);
    }

    private void dialog(int id,int type,int total) {
        mediator.show().dialog().readingPlan(mediator.get().data().getListDialogReadingPlanContainer(id,type,1),total, new DialogContract.ReadingPlanContainer() {
            @Override
            public void number(int number,DialogContract.GetList listener) {
                listener.list(mediator.get().data().getListDialogReadingPlanContainer(id,type,number));
            }

            @Override
            public void result(int number) {
                String start = convert.getDateFormat(datetime.getDatetime(),"yyyy-MM-dd HH:mm:ss",number - 1,true,false);
                String finish = convert.getDateFormat(start,"yyyy-MM-dd HH:mm:ss",convert.getDayTotal(type) - 1,true,true);
                mediator.update().activeReadingPlanContainer(id,type,number,start,finish);
                redraw();
            }
        });
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
                getParentFragmentManager().beginTransaction().replace(R.id.container, ReadingPlanMaterialFragment.newInstance(id), Config.screen().readingPlanMaterial()).addToBackStack(Config.screen().readingPlanMaterial()).commit();
                break;
            case R.id.buttonStop:
                mediator.show().dialog().delete(getString(R.string.dialog_stop_reading_plan), () -> {
                    alarm.cancel(id,true);
                    mediator.update().inactiveReadingPlanContainer(id);
                    redraw();
                });
                break;
            case R.id.buttonYear:
                dialog(id,1,365);
                break;
            case R.id.buttonHalfYear:
                dialog(id,2,180);
                break;
            case R.id.buttonQuarterYear:
                dialog(id,3,90);
                break;
            case R.id.imageViewNotification:
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    ((MainActivity) getActivity()).checkExactAlarm();
                    return;
                }

                mediator.show().dialog().notification(notificationPlan, (time, status) -> {
                    String correctTime = alarm.restoreTime(time);
                    if (status) alarm.set(id,alarm.getTime(correctTime),true);
                    else alarm.cancel(id,true);
                    mediator.update().setNotificationReadingPlanContainer(id,status?correctTime:null);
                    notification(status?correctTime:null);
                });
                break;
        }
    }
}