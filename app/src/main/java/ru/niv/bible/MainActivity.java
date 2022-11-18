package ru.niv.bible;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Alarm;
import ru.niv.bible.basic.component.Checker;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Speech;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.basic.sqlite.DatabaseHelper;
import ru.niv.bible.basic.sqlite.Model;
import ru.niv.bible.mvp.view.ContentFragment;
import ru.niv.bible.mvp.view.FavoritesFragment;
import ru.niv.bible.mvp.view.FeedbackFragment;
import ru.niv.bible.mvp.view.FolderFragment;
import ru.niv.bible.mvp.view.ListFragment;
import ru.niv.bible.mvp.view.MainChildFragment;
import ru.niv.bible.mvp.view.MainFragment;
import ru.niv.bible.mvp.view.SearchFragment;
import ru.niv.bible.mvp.view.SettingsFragment;

public class MainActivity extends AppCompatActivity implements Go.Message {

    private FragmentManager manager;
    private Handler handler;
    private AdView adView;
    private Checker checker;
    private Go go;
    private Param param;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private boolean isAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        iniClasses();
        setParams();
        checkDatabase();
        initSidebar();

        if (checker.internet()) checkRateDialog();
        // checkAlarm();
        manager.beginTransaction().add(R.id.container,new MainFragment(),Static.main).commit();
        handler.sendEmptyMessageDelayed(1,15000);
        setClickListeners();
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    private void checkTheme() {
        param = new Param(this);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.Dark);
            Static.lightTheme = false;
        }
        else {
            setTheme(param.getBoolean(Static.paramTheme) ? R.style.Theme_Bible : R.style.Dark);
            Static.lightTheme = param.getBoolean(Static.paramTheme);
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        adView = findViewById(R.id.adView);
    }

    private void iniClasses() {
        go = new Go(this);
        checker = new Checker(this);
        handler = new Handler(msg -> {
            if (msg.what == 1) checkLoadAd();
            return false;
        });
    }

    private void setParams() {
        manager = getSupportFragmentManager();
        Static.screen = Static.main;
    }

    private void checkDatabase() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.checkExistDB(this);
    }

    private void setClickListeners() {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (isAd) return;
                isAd = true;
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        manager.addOnBackStackChangedListener(() -> {
            int total = manager.getBackStackEntryCount();
            if (total > 0) Static.screen = manager.getBackStackEntryAt(total - 1).getName();
            else Static.screen = Static.main;
            if (Static.screen.equals(Static.main)) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            checkLoadAd();
        });
    }

    private void checkRateDialog() {
        boolean rate = param.getBoolean(Static.paramRate);
        if (!rate) return;
        int visit = param.getInt(Static.paramVisit);
        if (visit >= 5) {
            visit = 0;
            Dialog dialog = new Dialog(this);
            dialog.rate(type -> {
                if (type.equals("rate")) {
                    param.setBoolean(Static.paramRate,false);
                    go.browser("market://details?id=" + getPackageName(), getString(R.string.google_play_not_found), MainActivity.this);
                }
                if (type.equals("never")) {
                    param.setBoolean(Static.paramRate,false);
                }
            });
        } else visit++;
        param.setInt(Static.paramVisit,visit);
    }

    public MainFragment getMainFragment() {
        return ((MainFragment) manager.findFragmentByTag(Static.main));
    }

    private void initSidebar() {
        List<Item> list = new ArrayList<>();
        String[] items = {getString(R.string.favorites),getString(R.string.settings),getString(R.string.feedback),getString(R.string.menu_share_app),getString(R.string.menu_day_night)};
        int[] icons = {R.drawable.ic_menu_favorites,R.drawable.ic_menu_settings,R.drawable.ic_menu_feedback,R.drawable.ic_share_sidebar,R.drawable.ic_menu_day};
        for (int i = 0; i < items.length; i++) {
            list.add(new Item().sidebar(items[i],icons[i]));
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSidebar);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(Static.sidebar,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String item = adapter.getItem(position).getName();
                    Fragment fragment = null;

                    switch (item) {
                        case Static.favorites:
                            fragment = new FavoritesFragment();
                            break;
                        case Static.settings:
                            fragment = new SettingsFragment();
                            break;
                        case Static.feedback:
                            fragment = new FeedbackFragment();
                            break;
                        case Static.shareApp:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            MainFragment mainFragment = (MainFragment) manager.findFragmentByTag(Static.main);
                            mainFragment.shareApp();
                            return;
                        case Static.dayNight:
                            if (Static.lightTheme) {
                                param.setBoolean(Static.paramTheme, false);
                            } else {
                                param.setBoolean(Static.paramTheme, true);
                            }
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            return;
                    }

                    MainFragment mainFragment = (MainFragment) manager.findFragmentByTag(Static.main);
                    mainFragment.getMainChild(mainFragment.getPosition()).stop();
                    if (item.equals(Static.feedback)) manager.beginTransaction().add(R.id.container,new FeedbackFragment(),Static.feedback).addToBackStack(Static.feedback).commit();
                    else manager.beginTransaction().replace(R.id.container,fragment,item).addToBackStack(item).commit();
                },300);
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

            }
        });

    }

    public void openDrawerMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        clearBackStack();
    }

    private void clearBackStack() {
        Static.screen = Static.main;
        if (manager.getBackStackEntryCount() > 0) {
            for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
                manager.popBackStack();
            }
        }
        manager.beginTransaction().replace(R.id.container,new MainFragment(),Static.main).commit();
    }

    private void checkAlarm() {
        Alarm alarm = new Alarm(this);
        if (!alarm.checkAlarm(1)) alarm.setRepeating(1);
    }

    private void checkLoadAd() {
        if (!isAd && checker.internet()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    public void message(String message) {
        Snackbar.make(coordinatorLayout,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (Static.screen.equals(Static.list) && !((ListFragment) manager.findFragmentByTag(Static.list)).checkBackSearch()) return;
        else if (Static.screen.equals(Static.content) && !((ContentFragment) manager.findFragmentByTag(Static.content)).checkBack()) return;
        else if (Static.screen.equals(Static.folder) && !((FolderFragment) manager.findFragmentByTag(Static.folder)).checkBackSearch()) return;
        else if (Static.screen.equals(Static.search) && !((SearchFragment) manager.findFragmentByTag(Static.search)).checkBackSelect()) return;
        else if (Static.screen.equals(Static.main) && !((MainFragment) manager.findFragmentByTag(Static.main)).getMainChild(((MainFragment) manager.findFragmentByTag(Static.main)).getPosition()).checkBack()) return;
        else super.onBackPressed();
    }
}