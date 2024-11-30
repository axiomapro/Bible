package ru.niv.bible;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.niv.bible.component.immutable.box.Alarm;
import ru.niv.bible.component.immutable.box.Checker;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Go;
import ru.niv.bible.component.immutable.box.JSON;
import ru.niv.bible.component.immutable.box.Param;
import ru.niv.bible.component.immutable.box.Payment;
import ru.niv.bible.component.immutable.box.Permission;
import ru.niv.bible.component.immutable.box.Speech;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.component.mutable.receiver.RebootReceiver;
import ru.niv.bible.component.mutable.sqlite.DatabaseHelper;
import ru.niv.bible.component.mutable.sqlite.Upgrade;
import ru.niv.bible.fragment.ContentFragment;
import ru.niv.bible.fragment.FavoritesFragment;
import ru.niv.bible.fragment.FeedbackFragment;
import ru.niv.bible.fragment.FolderFragment;
import ru.niv.bible.fragment.ListFragment;
import ru.niv.bible.fragment.MainFragment;
import ru.niv.bible.fragment.SearchFragment;
import ru.niv.bible.fragment.SettingsFragment;
import ru.niv.bible.fragment.interactive.CommonNotesFragment;
import ru.niv.bible.fragment.interactive.DailyVerseFragment;
import ru.niv.bible.fragment.interactive.ReadingPlanFragment;
import ru.niv.bible.mediator.contract.FragmentContract;
import ru.niv.bible.mediator.contract.MessageContract;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.view.Rview;

public class MainActivity extends AppCompatActivity implements FragmentContract.MainChild, FragmentContract.Settings, MessageContract {

    private FragmentManager manager;
    private Mediator mediator;
    private Rview rview;
    private Speech speech;
    private AdView adView;
    private Checker checker;
    private Handler handler;
    private Payment payment;
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
        initSidebar();
        checkDatabase();
        setClickListeners();
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    private void checkTheme() {
        param = new Param(this);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            Static.forcedDarkMode = true;
            Static.lightTheme = false;
            setTheme(R.style.Dark);
        } else {
            Static.forcedDarkMode = false;
            Static.lightTheme = param.getBoolean(Config.param().theme());
            setTheme(Static.lightTheme ? R.style.Theme_Bible : R.style.Dark);
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        adView = findViewById(R.id.adView);
    }

    private void iniClasses() {
        manager = getSupportFragmentManager();
        mediator = new Mediator(this);
        rview = mediator.view().rview();
        Convert convert = new Convert();
        go = new Go(this);
        payment = new Payment(this, new Payment.Status() {
            @Override
            public void paid() {
                param.setBoolean(Config.param().purchase(),true);
                handler.removeMessages(1);
                adView.post(() -> {
                    visibleItemRemoveAds(false);
                    adView.setVisibility(View.GONE);
                });
            }

            @Override
            public void notPaid(boolean launch) {
                param.setBoolean(Config.param().purchase(),false);
                handler.sendEmptyMessageDelayed(1,15000);
                visibleItemRemoveAds(true);
                payment.getProducts(launch);
            }

            @Override
            public void verifyPayment() {
                adView.post(() -> {
                    param.setBoolean(Config.param().purchase(),true);
                    visibleItemRemoveAds(false);
                    adView.setVisibility(View.GONE);
                });
            }
        });
        checker = new Checker(this);
        handler = new Handler(msg -> {
            if (msg.what == 1) checkLoadAd();
            return false;
        });

        int voiceLanguage = param.getInt(Config.param().language());
        speech = new Speech();
        speech.check(voiceLanguage == 4?"es":"en",convert.getCountry(voiceLanguage),this);
        speech.setSpeed((float) (1 + param.getInt(Config.param().readingSpeed())) / 10);
        speech.setPitch((float) (10 + param.getInt(Config.param().speechPitch())) / 10);

        speech.getTextToSpeech().setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                if (Static.screen.equals(Config.screen().main())) getMainFragment().getMainChild(getMainFragment().getPosition()).onDone();
                else ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(Config.screen().settings())).audio(false);
            }

            @Override
            public void onError(String utteranceId) {
                if (Static.screen.equals(Config.screen().main())) getMainFragment().getMainChild(getMainFragment().getPosition()).stop();
                else ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(Config.screen().settings())).audio(false);
            }
        });
    }

    private void setParams() {
        Static.screen = Config.screen().main();

        if (!param.getBoolean(Config.param().firstLaunch())) {
            // maxPosition
            int maxPosition = param.getInt(Config.param().maxPosition());
            if (maxPosition == 0) param.setInt(Config.param().maxPosition(),mediator.get().data().getMaxPositionMain());
            // isSupportHead
            param.setBoolean(Config.param().supportHead(),mediator.get().data().isSupportHeadMain());
            param.setBoolean(Config.param().firstLaunch(),true);
        }
        Static.supportHead = param.getBoolean(Config.param().supportHead());
    }

    private void checkDatabase() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.checkExistDB(this);
        databaseHelper.openDb();

        if (databaseHelper.isUpgrade()) {
            param.setBoolean(Config.param().firstLaunch(),false);
            new Upgrade(this, databaseHelper, this::launch).execute();
        } else launch();
    }

    private void launch() {
        setParams();
        mediator.transition(manager,new MainFragment(),Config.screen().main(),0,false,false);
        if (checker.internet()) checkRateDialog();
        checkNotifications();
        checkPurchase();
        checkExactAlarm();
    }

    private void checkNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},200);
            }
        }

        Alarm alarm = new Alarm(this);
        mediator.get().data().checkOneNotificationMain((id, type) -> {
            if (id > 0 && !alarm.checkAlarm(id,type.equals("reading plan"))) {
                RebootReceiver rebootReceiver = new RebootReceiver();
                rebootReceiver.onReceive(getApplicationContext(),null);
            }
        });
    }

    private void checkPurchase() {
        payment.initializeBillingClient();
        if (!checker.internet() && param.getBoolean(Config.param().purchase())) return;
        payment.connectGooglePlayBilling(false);
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
            else Static.screen = Config.screen().main();
            if (Static.screen.equals(Config.screen().main())) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            if (!isAd && !param.getBoolean(Config.param().purchase())) handler.sendEmptyMessageDelayed(1,3000);
        });
    }

    private void checkRateDialog() {
        boolean rate = param.getBoolean(Config.param().rate());
        if (!rate) return;
        int visit = param.getInt(Config.param().visit());
        if (visit >= 5) {
            visit = 0;
            mediator.show().dialog().rate(type -> {
                if (type.equals("rate")) {
                    param.setBoolean(Config.param().rate(),false);
                    go.browser("market://details?id=" + getPackageName(), getString(R.string.google_play_not_found), MainActivity.this);
                }
                if (type.equals("never")) {
                    param.setBoolean(Config.param().rate(),false);
                }
            });
        } else visit++;
        param.setInt(Config.param().visit(),visit);
    }

    private void visibleItemRemoveAds(boolean status) {
        runOnUiThread(() -> {
            rview.getItem(rview.getTotal() - 1).setVisible(status);
            rview.update();
        });
    }

    public Speech getSpeech() {
        return speech;
    }

    public MainFragment getMainFragment() {
        return ((MainFragment) manager.findFragmentByTag(Config.screen().main()));
    }

    private void initSidebar() {
        rview.setRecyclerView(findViewById(R.id.recyclerViewSidebar));
        rview.initialize(Config.recyclerView().sidebar(), mediator.get().list().sidebarMain(), new LinearLayoutManager(this), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!rview.getItem(position).getName().equals(getString(R.string.menu_share_app)) && !rview.getItem(position).getName().equals(getString(R.string.menu_day_night)) && !rview.getItem(position).getName().equals(getString(R.string.remove_ads))) {
                        MainFragment mainFragment = (MainFragment) manager.findFragmentByTag(Config.screen().main());
                        mainFragment.getMainChild(mainFragment.getPosition()).stop();
                    }

                    if (rview.getItem(position).getName().equals(getString(R.string.favorites))) {
                        mediator.transition(manager,new FavoritesFragment(),Config.screen().favorites(),Static.ALPHA_ANIMATION,true,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.reading_plan))) {
                        mediator.transition(manager,new ReadingPlanFragment(),Config.screen().readingPlan(),Static.ALPHA_ANIMATION,true,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.daily_verse))) {
                        mediator.transition(manager,new DailyVerseFragment(),Config.screen().dailyVerse(),Static.ALPHA_ANIMATION,true,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.common_notes))) {
                        mediator.transition(manager,new CommonNotesFragment(),Config.screen().commonNotes(),Static.ALPHA_ANIMATION,false,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.settings))) {
                        mediator.transition(manager,new SettingsFragment(),Config.screen().settings(),Static.ALPHA_ANIMATION,true,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.feedback))) {
                        mediator.transition(manager,new FeedbackFragment(),Config.screen().feedback(),Static.ALPHA_ANIMATION,false,true);
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.menu_share_app))) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        ((MainFragment) manager.findFragmentByTag(Config.screen().main())).shareApp();
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.menu_day_night))) {
                        param.setBoolean(Config.param().theme(),!Static.lightTheme);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else if (rview.getItem(position).getName().equals(getString(R.string.remove_ads))) {
                        if (payment.getProductDetails() == null) {
                            if (checker.internet()) payment.connectGooglePlayBilling(true);
                            else message(getString(R.string.turn_on_the_internet));
                        } else payment.launchPurchaseFlow();
                    }
                },300);
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, int day, boolean status) {

            }

            @Override
            public void link(String link) {

            }
        });
    }

    public void openDrawerMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void checkExactAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        }
    }

    private void checkLoadAd() {
        if (isAd || !checker.internet() || param.getBoolean(Config.param().purchase())) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.VISIBLE);
    }

    public void message(String message) {
        Snackbar.make(coordinatorLayout,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (Static.screen.equals(Config.screen().list()) && !((ListFragment) manager.findFragmentByTag(Config.screen().list())).checkBackSearch()) return;
        else if (Static.screen.equals(Config.screen().content()) && !((ContentFragment) manager.findFragmentByTag(Config.screen().content())).checkBack()) return;
        else if (Static.screen.equals(Config.screen().folder()) && !((FolderFragment) manager.findFragmentByTag(Config.screen().folder())).checkBackSearch()) return;
        else if (Static.screen.equals(Config.screen().search()) && !((SearchFragment) manager.findFragmentByTag(Config.screen().search())).checkBackSelect()) return;
        else if (Static.screen.equals(Config.screen().main()) && !((MainFragment) manager.findFragmentByTag(Config.screen().main())).getMainChild(((MainFragment) manager.findFragmentByTag(Config.screen().main())).getPosition()).checkBack()) return;
        else if (Static.screen.equals(Config.screen().commonNotes()) && !((CommonNotesFragment) manager.findFragmentByTag(Config.screen().commonNotes())).checkBackSearch()) return;
        else if (Static.screen.equals(Config.screen().dailyVerse()) && !((DailyVerseFragment) manager.findFragmentByTag(Config.screen().dailyVerse())).checkBack()) return;
        else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        payment.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speech != null) speech.destroy();
    }
}