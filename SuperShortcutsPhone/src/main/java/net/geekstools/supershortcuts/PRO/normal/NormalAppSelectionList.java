package net.geekstools.supershortcuts.PRO.normal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.supershortcuts.PRO.BuildConfig;
import net.geekstools.supershortcuts.PRO.Configurations;
import net.geekstools.supershortcuts.PRO.LicenseValidator;
import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.Util.SettingGUI;
import net.geekstools.supershortcuts.PRO.Util.SimpleGestureFilterSwitch;
import net.geekstools.supershortcuts.PRO.advanced.AdvanceShortcuts;
import net.geekstools.supershortcuts.PRO.normal.nav.SavedListAdapter;
import net.geekstools.supershortcuts.PRO.normal.nav.SelectionListAdapter;
import net.geekstools.supershortcuts.PRO.split.SplitShortcuts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NormalAppSelectionList extends Activity implements View.OnClickListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    Activity activity;
    Context context;
    FunctionsClass functionsClass;

    RecyclerView recyclerView;
    RecyclerView.Adapter selectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    ScrollView nestedScrollView, nestedIndexScrollView;
    ListPopupWindow listPopupWindow;
    RelativeLayout popupAnchorView;
    RelativeLayout wholeAuto, confirmLayout;
    LinearLayout indexView, autoSelect;
    RelativeLayout loadingSplash;
    TextView desc, counterView, popupIndex;
    ImageView tempIcon, loadIcon;
    Button apps, split, categories;
    ProgressBar loadingBarLTR;

    MenuItem mixShortcutsMenuItem;

    List<String> indexAppName;
    Map<String, Integer> mapIndex;
    Map<Integer, String> mapRangeIndex;
    ArrayList<NavDrawerItem> navDrawerItems, navDrawerItemsSaved;
    SavedListAdapter savedListAdapter;

    String PackageName, className, AppName = "Application";
    Drawable AppIcon;

    int limitCounter;
    BroadcastReceiver counterReceiver;
    boolean resetAdapter = false;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    FirebaseRemoteConfig firebaseRemoteConfig;
    LoadCustomIcons loadCustomIcons;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.normal_app_selection);
        PublicVariable.eligibleLoadShowAds = true;

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);
        functionsClass = new FunctionsClass(getApplicationContext(), this);
        functionsClass.ChangeLog(false);
        if (functionsClass.mixShortcuts() == true) {
            PublicVariable.maxAppShortcuts
                    = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.maxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".autoSuper");
            getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
            PublicVariable.maxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
        }
        context = getApplicationContext();
        activity = this;

        listPopupWindow = new ListPopupWindow(activity);
        desc = (TextView) findViewById(R.id.desc);
        counterView = (TextView) findViewById(R.id.counter);
        loadIcon = (ImageView) findViewById(R.id.loadLogo);
        tempIcon = (ImageView) findViewById(R.id.tempIcon);
        tempIcon.bringToFront();
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);
        nestedIndexScrollView = (ScrollView) findViewById(R.id.nestedIndexScrollView);
        indexView = (LinearLayout) findViewById(R.id.side_index);
        popupIndex = (TextView) findViewById(R.id.popupIndex);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        confirmLayout = (RelativeLayout) findViewById(R.id.confirmLayout);
        confirmLayout.bringToFront();
        autoSelect = (LinearLayout) findViewById(R.id.autoSelect);
        apps = (Button) findViewById(R.id.autoApps);
        split = (Button) findViewById(R.id.autoSplit);
        categories = (Button) findViewById(R.id.autoCategories);

        recyclerView = (RecyclerView) findViewById(R.id.listFav);
        recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        nestedScrollView = (ScrollView) findViewById(R.id.scrollListFav);
        nestedScrollView.setSmoothScrollingEnabled(true);

        wholeAuto.setBackgroundColor(getColor(R.color.light));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.app_name) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItemsSaved = new ArrayList<NavDrawerItem>();
        indexAppName = new ArrayList<String>();
        mapIndex = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();

        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);
        counterView.setTypeface(face);
        counterView.bringToFront();

        loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.loadOffline));
        intentFilter.addAction(context.getString(R.string.counterAction));
        intentFilter.addAction(context.getString(R.string.savedAction));
        intentFilter.addAction(context.getString(R.string.savedActionHide));
        intentFilter.addAction(context.getString(R.string.checkboxAction));
        intentFilter.addAction(context.getString(R.string.dynamicShortcuts));
        intentFilter.addAction(context.getString(R.string.license));
        counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.loadOffline))) {
                    loadDataOff();
                } else if (intent.getAction().equals(context.getString(R.string.counterAction))) {
                    counterView.setText(String.valueOf(functionsClass.countLine(".autoSuper")));
                } else if (intent.getAction().equals(context.getString(R.string.savedAction))) {
                    if (getFileStreamPath(".autoSuper").exists() && functionsClass.countLine(".autoSuper") > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(".autoSuper");
                        for (String aSavedLine : savedLine) {
                            try {
                                final String packageName = aSavedLine.split("\\|")[0];
                                final String className = aSavedLine.split("\\|")[1];
                                ActivityInfo activityInfo = getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0);
                                navDrawerItemsSaved.add(new NavDrawerItem(
                                        functionsClass.activityLabel(activityInfo),
                                        packageName,
                                        className,
                                        AppIcon = functionsClass.loadCustomIcons() ?
                                                loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.activityIcon(activityInfo))
                                                :
                                                functionsClass.activityIcon(activityInfo)

                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        savedListAdapter = new SavedListAdapter(activity, context, navDrawerItemsSaved);
                        listPopupWindow = new ListPopupWindow(activity);
                        listPopupWindow.setAdapter(savedListAdapter);
                        listPopupWindow.setAnchorView(popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);
                        try {
                            listPopupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityAction)));
                            }
                        });
                    }
                } else if (intent.getAction().equals(getString(R.string.savedActionHide))) {
                    if (listPopupWindow.isShowing()) {
                        listPopupWindow.dismiss();
                    } else {
                        listPopupWindow.dismiss();
                    }
                } else if ((intent.getAction().equals(getString(R.string.checkboxAction)))) {
                    resetAdapter = true;
                    loadDataOff();
                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityAction)));
                } else if (intent.getAction().equals(getString(R.string.dynamicShortcuts))) {
                    if (functionsClass.mixShortcuts() == true) {
                        PublicVariable.maxAppShortcuts
                                = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".mixShortcuts");
                        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + PublicVariable.maxAppShortcuts + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        limitCounter = functionsClass.getSystemMaxAppShortcut() - functionsClass.countLine(".autoSuper");
                        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + getString(R.string.maximum) + "</font>" + "<b><font color='" + getColor(R.color.light) + "'>" + limitCounter + "</font></b></small>", Html.FROM_HTML_MODE_LEGACY));
                        PublicVariable.maxAppShortcuts = functionsClass.getSystemMaxAppShortcut();
                    }
                } else if (intent.getAction().equals(getString(R.string.license))) {
                    functionsClass.dialogueLicense();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopService(new Intent(getApplicationContext(), LicenseValidator.class));
                        }
                    }, 1000);
                }
            }
        };
        context.registerReceiver(counterReceiver, intentFilter);

        RippleDrawable drawApps = (RippleDrawable) getDrawable(R.drawable.auto_apps_enable);
        GradientDrawable backApps = (GradientDrawable) drawApps.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backAppsRipple = (GradientDrawable) drawApps.findDrawableByLayerId(android.R.id.mask);
        backApps.setColor(getColor(R.color.default_color));
        backAppsRipple.setColor(getColor(R.color.default_color_darker));
        drawApps.setColor(ColorStateList.valueOf(getColor(R.color.default_color_darker)));
        apps.setBackground(drawApps);

        RippleDrawable drawSplit = (RippleDrawable) getDrawable(R.drawable.auto_split_enable);
        GradientDrawable backSplit = (GradientDrawable) drawSplit.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backSplitRipple = (GradientDrawable) drawSplit.findDrawableByLayerId(android.R.id.mask);
        backSplit.setColor(getColor(R.color.default_color_darker));
        backSplitRipple.setColor(getColor(R.color.default_color));
        drawSplit.setColor(ColorStateList.valueOf(getColor(R.color.default_color)));
        split.setBackground(drawSplit);

        RippleDrawable drawCategories = (RippleDrawable) getDrawable(R.drawable.auto_categories_enable);
        GradientDrawable backCategories = (GradientDrawable) drawCategories.findDrawableByLayerId(R.id.category_item);
        GradientDrawable backCategoriesRipple = (GradientDrawable) drawCategories.findDrawableByLayerId(android.R.id.mask);
        backCategories.setColor(getColor(R.color.default_color_darker));
        backCategoriesRipple.setColor(getColor(R.color.default_color));
        drawCategories.setColor(ColorStateList.valueOf(getColor(R.color.default_color)));
        categories.setBackground(drawCategories);

        loadDataOff();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicVariable.eligibleLoadShowAds = true;

        try {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
            firebaseRemoteConfig.fetch(0)
                    .addOnCompleteListener(NormalAppSelectionList.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                            getActionBar().setDisplayHomeAsUpEnabled(true);
                                            LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                            BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                            gradientDrawableNewUpdate.setTint(getColor(R.color.default_color_light));

                                            Bitmap tempBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate);
                                            Bitmap scaleBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() / 4, tempBitmap.getHeight() / 4, false);
                                            Drawable logoDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                                            activity.getActionBar().setHomeAsUpIndicator(logoDrawable);

                                            functionsClass.notificationCreator(
                                                    getString(R.string.updateAvailable),
                                                    firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                                    (int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())
                                            );
                                        } else {

                                        }
                                    }
                                });
                            } else {

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection() == true) {
            if (!BuildConfig.DEBUG || !functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                startService(new Intent(getApplicationContext(), LicenseValidator.class));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(AdvanceShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (functionsClass.networkConnection()) {
                try {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user == null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", null);
                                    } else {

                                    }


                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (functionsClass.readPreference(".UserInformation", "userEmail", null) == null) {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webClientId))
                            .requestEmail()
                            .build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
                    try {
                        googleSignInClient.signOut();
                        googleSignInClient.revokeAccess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 666);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        PublicVariable.eligibleLoadShowAds = false;

        SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TabsView", NormalAppSelectionList.class.getSimpleName());
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseUser.reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (functionsClass.UsageAccessEnabled()) {
            finish();
        } else {
            Intent homeScreen = new Intent(Intent.ACTION_MAIN);
            homeScreen.addCategory(Intent.CATEGORY_HOME);
            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreen);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.simpleGestureFilterSwitch.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterSwitch.SWIPE_LEFT:
                System.out.println("Swipe Left");
                try {
                    functionsClass.overrideBackPress(SplitShortcuts.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selection_menu, menu);

        mixShortcutsMenuItem = menu.findItem(R.id.mixShortcuts);

        if (functionsClass.mixShortcuts()) {
            LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
            Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
            backDrawMixHint.setTint(getColor(R.color.default_color_light));

            mixShortcutsMenuItem.setIcon(drawMixHint);
            mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsEnable));
        } else {
            LayerDrawable drawMixHint = (LayerDrawable) getDrawable(R.drawable.draw_mix_hint);
            Drawable backDrawMixHint = drawMixHint.findDrawableByLayerId(R.id.backtemp);
            backDrawMixHint.setTint(getColor(R.color.dark));

            mixShortcutsMenuItem.setIcon(drawMixHint);
            mixShortcutsMenuItem.setTitle(getString(R.string.mixShortcutsDisable));
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs: {
                startActivity(new Intent(getApplicationContext(), SettingGUI.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.up_down, android.R.anim.fade_out).toBundle());
                finish();
                break;
            }
            case R.id.mixShortcuts: {
                try {
                    functionsClass.deleteSelectedFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("mix", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (sharedPreferences.getBoolean("mixShortcuts", false) == true) {
                    editor.putBoolean("mixShortcuts", false);
                    editor.apply();
                } else if (sharedPreferences.getBoolean("mixShortcuts", false) == false) {
                    editor.putBoolean("mixShortcuts", true);
                    editor.apply();
                }

                if (FunctionsClass.interstitialAdMixShortcuts == null) {
                    Intent intent = new Intent(getApplicationContext(), Configurations.class);
                    startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                } else {
                    if (FunctionsClass.interstitialAdMixShortcuts.isLoaded()) {
                        sendBroadcast(new Intent("SHOW_ADS_MIX"));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Configurations.class);
                        startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                    }
                }

                break;
            }
            case android.R.id.home: {
                functionsClass.upcomingChangeLog(
                        NormalAppSelectionList.this,
                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                        String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                );
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            try {
                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.getEmail());
                                    }
                                } else {

                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDataOff() {
        loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
        loadInstalledCustomIcons.execute();

        if (functionsClass.UsageAccessEnabled()) {
            loadingBarLTR.setVisibility(View.INVISIBLE);
            loadIcon.setImageDrawable(getDrawable(R.drawable.draw_smart));
            desc.setText(Html.fromHtml(getString(R.string.smartInfo), Html.FROM_HTML_MODE_LEGACY));

            try {
                functionsClass.deleteSelectedFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                retrieveFreqUsedApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                navDrawerItems.clear();

                if (!functionsClass.mixShortcuts()) {
                    if (context.getFileStreamPath(".mixShortcuts").exists()) {
                        String[] mixContent = functionsClass.readFileLine(".mixShortcuts");
                        for (String mixLine : mixContent) {
                            if (mixLine.contains(".CategorySelected")) {
                                context.deleteFile(functionsClass.categoryNameSelected(mixLine));
                            } else if (mixLine.contains(".SplitSelected")) {
                                context.deleteFile(functionsClass.splitNameSelected(mixLine));
                            } else {
                                context.deleteFile(functionsClass.packageNameSelected(mixLine));
                            }
                        }
                        context.deleteFile(".mixShortcuts");
                    }
                }
                if (context.getFileStreamPath(".superFreq").exists()) {
                    context.deleteFile(".superFreq");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
            loadApplicationsOff.execute();
        }
    }

    public List<String> letMeKnow(Activity activity, int maxValue, long startTime /*‪86400000‬ = 1 days*/, long endTime  /*System.currentTimeMillis()*/) {
        /*‪86400000 = 24h --- 82800000 = 23h‬*/
        List<String> freqApps = new ArrayList<String>();
        try {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> queryUsageStats = mUsageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                            System.currentTimeMillis() - startTime,
                            endTime);
            Collections.sort(
                    queryUsageStats,
                    new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats left, UsageStats right) {
                            return Long.compare(
                                    right.getTotalTimeInForeground(), left.getTotalTimeInForeground()
                            );
                        }
                    }
            );
            for (int i = 0; i < maxValue; i++) {
                String aPackageName = queryUsageStats.get(i).getPackageName();
                try {
                    if (!aPackageName.equals(context.getPackageName())) {
                        if (functionsClass.appInstalledOrNot(aPackageName)) {
                            if (!functionsClass.ifSystem(aPackageName)) {
                                if (!functionsClass.ifDefaultLauncher(aPackageName)) {
                                    if (functionsClass.canLaunch(aPackageName)) {
                                        freqApps.add(aPackageName);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Set<String> stringHashSet = new LinkedHashSet<>(freqApps);
            freqApps.clear();
            freqApps.addAll(stringHashSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return freqApps;
    }

    public void retrieveFreqUsedApp() throws Exception {
        List<String> freqApps = letMeKnow(activity, 25, (86400000 * 7), System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            functionsClass.saveFileAppendLine(
                    ".superFreq",
                    freqApps.get(i));
        }
        functionsClass.addAppShortcutsFreqApps();
    }

    private class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                recyclerView.getRecycledViewPool().clear();

                navDrawerItems.clear();
                indexAppName.clear();
                indexView.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!PublicVariable.firstLoad) {
                autoSelect.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                Intent installedIntent = new Intent();
                installedIntent.setAction(Intent.ACTION_MAIN);
                installedIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> applicationInfoList = packageManager.queryIntentActivities(installedIntent, 0);

                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    if (BuildConfig.DEBUG) {
                        System.out.println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                Collections.sort(applicationInfoList, new ResolveInfo.DisplayNameComparator(packageManager));
                for (ResolveInfo resolveInfo : applicationInfoList) {
                    try {
                        if (packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName) != null) {
                            try {
                                PackageName = resolveInfo.activityInfo.packageName;
                                className = resolveInfo.activityInfo.name;
                                AppName = functionsClass.activityLabel(resolveInfo.activityInfo);
                                indexAppName.add(AppName);
                                AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.activityIcon(resolveInfo.activityInfo)) : functionsClass.activityIcon(resolveInfo.activityInfo);

                                navDrawerItems.add(new NavDrawerItem(
                                        AppName,
                                        PackageName,
                                        className,
                                        AppIcon
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                selectionListAdapter = new SelectionListAdapter(activity, context, navDrawerItems);
                selectionListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            if (functionsClass.UsageAccessEnabled()) {
                return;
            }

            recyclerView.setAdapter(selectionListAdapter);
            registerForContextMenu(recyclerView);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Animation slideDown_select = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_button);
                    if (PublicVariable.firstLoad) {
                        PublicVariable.firstLoad = false;

                        autoSelect.startAnimation(slideDown_select);
                        slideDown_select.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                autoSelect.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }

                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);

                    if (resetAdapter == false) {
                        loadingSplash.startAnimation(anim);
                    }
                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityAction)));

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    counterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            counterView.setText(String.valueOf(functionsClass.countLine(".autoSuper")));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    PublicVariable.maxAppShortcutsCounter = functionsClass.countLine(".autoSuper");
                    resetAdapter = false;
                }
            }, 100);

            LoadApplicationsIndex loadApplicationsIndex = new LoadApplicationsIndex();
            loadApplicationsIndex.execute();
        }
    }

    private class LoadApplicationsIndex extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int navItem = 0; navItem < indexAppName.size(); navItem++) {
                try {
                    String index = (indexAppName.get(navItem)).substring(0, 1).toUpperCase();
                    if (mapIndex.get(index) == null) {
                        mapIndex.put(index, navItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            LayerDrawable drawIndex = (LayerDrawable) getDrawable(R.drawable.draw_index);
            GradientDrawable backIndex = (GradientDrawable) drawIndex.findDrawableByLayerId(R.id.backtemp);
            backIndex.setColor(Color.TRANSPARENT);

            TextView textView = (TextView) getLayoutInflater()
                    .inflate(R.layout.side_index_item, null);
            ;
            List<String> indexList = new ArrayList<String>(mapIndex.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setBackground(drawIndex);
                textView.setText(index.toUpperCase());
                textView.setTextColor(getColor(R.color.dark));
                indexView.addView(textView);
            }

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (indexView.getY() - finalTextView.getHeight());
                    for (int i = 0; i < indexView.getChildCount(); i++) {
                        String indexText = ((TextView) indexView.getChildAt(i)).getText().toString();
                        int indexRange = (int) (indexView.getChildAt(i).getY() + indexView.getY() + finalTextView.getHeight());
                        for (int jRange = upperRange; jRange <= (indexRange); jRange++) {
                            mapRangeIndex.put(jRange, indexText);
                        }

                        upperRange = indexRange;
                    }

                    setupFastScrollingIndexing();
                }
            }, 700);
        }
    }

    private class LoadInstalledCustomIcons extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();

                //ACTION: com.novalauncher.THEME
                //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
                Intent intentCustomIcons = new Intent();
                intentCustomIcons.setAction("com.novalauncher.THEME");
                intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER");
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0);
                try {
                    PublicVariable.customIconsPackages.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (BuildConfig.DEBUG) {
                        System.out.println("*** CustomIconPackages ::: " + resolveInfo.activityInfo.packageName);
                    }
                    PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName);
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupFastScrollingIndexing() {
        Drawable popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon).mutate();
        popupIndexBackground.setTint(getColor(R.color.default_color_darker));
        popupIndex.setBackground(popupIndexBackground);

        nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        nestedIndexScrollView.setVisibility(View.VISIBLE);

        float popupIndexOffsetY = PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + PublicVariable.navigationBarHeight + functionsClass.DpToInteger(7);
        nestedIndexScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        String indexText = mapRangeIndex.get((((int) motionEvent.getY())));

                        if (indexText != null) {
                            popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                            popupIndex.setText(indexText);
                            popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                            popupIndex.setVisibility(View.VISIBLE);
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                        if (indexText != null) {
                            if (!popupIndex.isShown()) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                popupIndex.setVisibility(View.VISIBLE);
                            }
                            popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                            popupIndex.setText(indexText);

                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) recyclerView.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY()) - functionsClass.DpToInteger(37)
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (popupIndex.isShown()) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                popupIndex.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (popupIndex.isShown()) {
                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) recyclerView.getChildAt(mapIndex.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                            popupIndex.setVisibility(View.INVISIBLE);
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }
}
