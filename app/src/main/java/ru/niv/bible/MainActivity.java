package ru.niv.bible;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.JSON;
import ru.niv.bible.basic.component.Payment;
import ru.niv.bible.basic.component.Speech;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Alarm;
import ru.niv.bible.basic.component.Checker;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.basic.receiver.AlertReceiver;
import ru.niv.bible.basic.receiver.RebootReceiver;
import ru.niv.bible.basic.sqlite.DatabaseHelper;
import ru.niv.bible.basic.sqlite.Upgrade;
import ru.niv.bible.mvp.model.MainModel;
import ru.niv.bible.mvp.presenter.MainPresenter;
import ru.niv.bible.mvp.view.CommonNotesFragment;
import ru.niv.bible.mvp.view.ContentFragment;
import ru.niv.bible.mvp.view.DailyVerseFragment;
import ru.niv.bible.mvp.view.FavoritesFragment;
import ru.niv.bible.mvp.view.FeedbackFragment;
import ru.niv.bible.mvp.view.FolderFragment;
import ru.niv.bible.mvp.view.ListFragment;
import ru.niv.bible.mvp.view.MainFragment;
import ru.niv.bible.mvp.view.ReadingPlanFragment;
import ru.niv.bible.mvp.view.SearchFragment;
import ru.niv.bible.mvp.view.SettingsFragment;

public class MainActivity extends AppCompatActivity implements Go.Message {

    private FragmentManager manager;
    private Speech speech;
    private AdView adView;
    private Checker checker;
    private Handler handler;
    private JSON json;
    private Payment payment;
    private Go go;
    private Param param;
    private RecyclerViewAdapter adapter;
    private List<Item> list;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private String jsonPath;
    private boolean isAd = true;

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
        Converter converter = new Converter();
        go = new Go(this);
        json = new JSON();
        payment = new Payment(this, new Payment.Status() {
            @Override
            public void paid() {
                param.setBoolean(Static.paramPurchase,true);
                handler.removeMessages(1);
                adView.post(() -> {
                    visibleItemRemoveAds(false);
                    adView.setVisibility(View.GONE);
                });
            }

            @Override
            public void notPaid(boolean launch) {
                param.setBoolean(Static.paramPurchase,false);
                // handler.sendEmptyMessageDelayed(1,15000);
                visibleItemRemoveAds(true);
                payment.getProducts(launch);
            }

            @Override
            public void verifyPayment() {
                adView.post(() -> {
                    param.setBoolean(Static.paramPurchase,true);
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

        int voiceLanguage = param.getInt(Static.paramLanguage);
        speech = new Speech();
        speech.check(voiceLanguage == 4?"es":"en",converter.getCountry(voiceLanguage),this);
        speech.setSpeed((float) (1 + param.getInt(Static.paramReadingSpeed)) / 10);
        speech.setPitch((float) (10 + param.getInt(Static.paramSpeechPitch)) / 10);

        speech.getTextToSpeech().setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                if (Static.screen.equals(Static.main)) getMainFragment().getMainChild(getMainFragment().getPosition()).onDone();
                else ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(Static.settings)).audio(false);
            }

            @Override
            public void onError(String utteranceId) {
                if (Static.screen.equals(Static.main)) getMainFragment().getMainChild(getMainFragment().getPosition()).stop();
                else ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(Static.settings)).audio(false);
            }
        });
    }

    private void setParams() {
        manager = getSupportFragmentManager();
        Static.screen = Static.main;

        jsonPath = getFilesDir()+"/"+"book.json";
        if (!param.getBoolean(Static.paramFirstLaunch)) {
            MainModel model = new MainModel(this);
            // maxPosition
            int maxPosition = param.getInt(Static.paramMaxPosition);
            if (maxPosition == 0) param.setInt(Static.paramMaxPosition,model.getMaxPosition());
            // isSupportHead
            param.setBoolean(Static.paramSupportHead,model.isSupportHead());
            // create json
            json.create(model.createJson(),jsonPath);
            param.setBoolean(Static.paramFirstLaunch,true);
        }
        Static.supportHead = param.getBoolean(Static.paramSupportHead);
    }

    private void checkDatabase() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.checkExistDB(this);
        databaseHelper.openDb();

        if (databaseHelper.isUpgrade()) {
            param.setBoolean(Static.paramFirstLaunch,false);
            new Upgrade(this, databaseHelper, this::launch).execute();
        } else launch();
    }

    private void launch() {
        if (checker.internet()) checkRateDialog();
        setParams();
        manager.beginTransaction().add(R.id.container,new MainFragment(),Static.main).commit();
        // checkPurchase();
        checkNotifications();
    }

    private void checkNotifications() {
        Alarm alarm = new Alarm(this);
        MainModel model = new MainModel(this);
        model.checkOneNotification((type, id) -> {
            if (id > 0 && !alarm.checkAlarm(id,type.equals("reading plan"))) {
                RebootReceiver rebootReceiver = new RebootReceiver();
                rebootReceiver.onReceive(getApplicationContext(),null);
            }
        });
    }

    private void checkPurchase() {
        payment.initializeBillingClient();
        if (!checker.internet() && param.getBoolean(Static.paramPurchase)) return;
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
            else Static.screen = Static.main;
            if (Static.screen.equals(Static.main)) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // if (!param.getBoolean(Static.paramPurchase)) handler.sendEmptyMessageDelayed(1,3000);
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

    private void visibleItemRemoveAds(boolean status) {
        list.get(8).setVisible(status);
        adapter.notifyDataSetChanged();
    }

    public JSONObject getJsonInfo(int position) {
        JSONObject result = null;
        try {
            JSONArray jsonArray = new JSONArray(json.get(jsonPath));
            result = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Speech getSpeech() {
        return speech;
    }

    public MainFragment getMainFragment() {
        return ((MainFragment) manager.findFragmentByTag(Static.main));
    }

    private void initSidebar() {
        list = new ArrayList<>();
        String[] items = {getString(R.string.favorites),getString(R.string.reading_plan),getString(R.string.daily_verse),getString(R.string.common_notes),getString(R.string.settings),getString(R.string.feedback),getString(R.string.menu_share_app),getString(R.string.menu_day_night)};
        int[] icons = {R.drawable.ic_menu_favorites,R.drawable.ic_menu_reading_plan,R.drawable.ic_menu_daily_verse,R.drawable.ic_menu_common_notes,R.drawable.ic_menu_settings,R.drawable.ic_menu_feedback,R.drawable.ic_share_sidebar,R.drawable.ic_menu_day};
        for (int i = 0; i < items.length; i++) {
            list.add(new Item().sidebar(items[i],icons[i],true));
        }
        // list.add(new Item().sidebar(getString(R.string.remove_ads),R.drawable.ic_menu_remove_ads,false));
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSidebar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(Static.sidebar,list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (position == 8) {
                        if (payment.getSkuDetails() == null) {
                            if (checker.internet()) payment.connectGooglePlayBilling(true);
                            else message(getString(R.string.turn_on_the_internet));
                        } else payment.launchPurchaseFlow();
                    } else {
                        String item = adapter.getItem(position).getName();
                        Fragment fragment = null;

                        switch (item) {
                            case Static.favorites:
                                fragment = new FavoritesFragment();
                                break;
                            case Static.readingPlan:
                                fragment = new ReadingPlanFragment();
                                break;
                            case Static.dailyVerse:
                                fragment = new DailyVerseFragment();
                                break;
                            case Static.commonNotes:
                                fragment = new CommonNotesFragment();
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
                        else if (item.equals(Static.commonNotes)) manager.beginTransaction().add(R.id.container,new CommonNotesFragment(),Static.commonNotes).addToBackStack(Static.commonNotes).commit();
                        else manager.beginTransaction().replace(R.id.container,fragment,item).addToBackStack(item).commit();
                    }
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
        Static.screen = Static.main;
        for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
            manager.popBackStack();
        }
        manager.beginTransaction().add(R.id.container,new MainFragment(),Static.main).commit();
    }

    private void checkLoadAd() {
        if (isAd || !checker.internet() || param.getBoolean(Static.paramPurchase)) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.VISIBLE);
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
        else if (Static.screen.equals(Static.commonNotes) && !((CommonNotesFragment) manager.findFragmentByTag(Static.commonNotes)).checkBackSearch()) return;
        else if (Static.screen.equals(Static.dailyVerse) && !((DailyVerseFragment) manager.findFragmentByTag(Static.dailyVerse)).checkBack()) return;
        else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // payment.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speech != null) speech.destroy();
    }
}