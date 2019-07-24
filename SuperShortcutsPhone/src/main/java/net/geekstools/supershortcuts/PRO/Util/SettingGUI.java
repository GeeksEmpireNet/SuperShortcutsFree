package net.geekstools.supershortcuts.PRO.Util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.IAP.InAppBilling;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.CustomIconsThemeAdapter;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.supershortcuts.PRO.Util.NavAdapter.RecycleViewSmoothLayout;
import net.geekstools.supershortcuts.PRO.advanced.AdvanceShortcuts;
import net.geekstools.supershortcuts.PRO.normal.NormalAppSelectionList;
import net.geekstools.supershortcuts.PRO.split.SplitShortcuts;

import java.util.ArrayList;
import java.util.List;

public class SettingGUI extends FragmentActivity {

    FunctionsClass functionsClass;
    Activity activity;
    Context context;

    RelativeLayout smartView, splitView, mixView, customIconView, supportView, newsView,
            floatingView;
    ImageView customIconIcon,
            prefIconNews;
    Button shareAll, rate, facebook, twitter;
    Switch prefSwitch, splitSwitch, mixSwitch;
    TextView customIconDesc,
            prefDescFloating;

    FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_gui);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        activity = this;
        context = getApplicationContext();
        functionsClass = new FunctionsClass(context, activity);
        functionsClass.ChangeLog(false);

        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color_darker)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.pref) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + functionsClass.appVersionName(getPackageName()) + "</font></small>", Html.FROM_HTML_MODE_LEGACY));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color_darker));
        window.setNavigationBarColor(getColor(R.color.light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        smartView = (RelativeLayout) findViewById(R.id.smartView);
        splitView = (RelativeLayout) findViewById(R.id.splitView);
        mixView = (RelativeLayout) findViewById(R.id.mixView);
        customIconView = (RelativeLayout) findViewById(R.id.customIconView);
        supportView = (RelativeLayout) findViewById(R.id.supportView);
        newsView = (RelativeLayout) findViewById(R.id.newsView);
        floatingView = (RelativeLayout) findViewById(R.id.floatingView);

        customIconIcon = (ImageView) findViewById(R.id.customIconIcon);
        prefIconNews = (ImageView) findViewById(R.id.prefIconNews);

        customIconDesc = (TextView) findViewById(R.id.customIconDesc);

        shareAll = (Button) findViewById(R.id.share);
        rate = (Button) findViewById(R.id.rate);
        twitter = (Button) findViewById(R.id.twitter);
        facebook = (Button) findViewById(R.id.facebook);

        prefSwitch = (Switch) findViewById(R.id.prefSwitch);
        splitSwitch = (Switch) findViewById(R.id.splitSwitch);
        mixSwitch = (Switch) findViewById(R.id.mixSwitch);

        prefDescFloating = (TextView) findViewById(R.id.prefDescfloating);

        prefIconNews.setImageDrawable(getDrawable(R.drawable.ic_launcher));
        customIconDesc.setText(functionsClass.loadCustomIcons() ? functionsClass.appName(functionsClass.readDefaultPreference("customIcon", getPackageName())) : getString(R.string.customIconDesc));

        if (!functionsClass.mixShortcutsPurchased() || !functionsClass.alreadyDonated()) {
            BillingClient billingClient = BillingClient.newBuilder(SettingGUI.this).setListener(new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {

                    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {

                    } else {

                    }

                }
            }).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                    if (billingResponseCode == BillingClient.BillingResponse.OK) {
                        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                        for (Purchase purchase : purchases) {
                            System.out.println("*** Purchased Item: " + purchase + " ***");

                            functionsClass.savePreference(".PurchasedItem", purchase.getSku(), true);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        prefIconNews.setImageDrawable(getDrawable(R.drawable.ic_launcher));

        smartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("smart", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (sharedPreferences.getBoolean("smartPick", false) == true) {
                    prefSwitch.setChecked(false);
                    editor.putBoolean("smartPick", false);
                    editor.apply();

                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                    finish();
                } else if (sharedPreferences.getBoolean("smartPick", false) == false) {
                    functionsClass.UsageAccess(prefSwitch);
                }
            }
        });

        splitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!functionsClass.AccessibilityServiceEnabled()) {
                    functionsClass.AccessibilityService();
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        mixView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionsClass.mixShortcutsPurchased()) {
                    try {
                        functionsClass.deleteSelectedFiles();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("mix", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getBoolean("mixShortcuts", false) == true) {
                        mixSwitch.setChecked(false);
                        editor.putBoolean("mixShortcuts", false);
                        editor.apply();
                    } else if (sharedPreferences.getBoolean("mixShortcuts", false) == false) {
                        mixSwitch.setChecked(true);
                        editor.putBoolean("mixShortcuts", true);
                        editor.apply();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), InAppBilling.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionsClass.ChangeLog(true);
            }
        });

        supportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] contactOption = new String[]{
                        "Send an Email",
                        "Send a Message",
                        "Contact via Forum",
                        "Join Beta Program",
                        "Rate & Write Review"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingGUI.this, R.style.GeeksEmpire_Dialogue_Light);
                builder.setTitle(getString(R.string.supportTitle));
                builder.setSingleChoiceItems(contactOption, 0, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0) {
                            String textMsg = "\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase();
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support)});
                            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(getPackageName()) + "] ");
                            email.putExtra(Intent.EXTRA_TEXT, textMsg);
                            email.setType("message/*");
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(email, getString(R.string.feedback_tag)));
                        } else if (selectedPosition == 1) {
                            Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app)));
                            startActivity(r);
                        } else if (selectedPosition == 2) {
                            Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_xda)));
                            startActivity(r);
                        } else if (selectedPosition == 3) {
                            Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_alpha)));
                            startActivity(a);

                            functionsClass.Toast(context.getString(R.string.alphaTitle), Gravity.BOTTOM);
                        } else if (selectedPosition == 4) {
                            Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_play_store) + getPackageName()));
                            startActivity(r);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
        });

        floatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_floating_shortcuts))));
            }
        });

        shareAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAll();
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_play_store) + getPackageName())));
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_twitter))));
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook))));
            }
        });

        customIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 313, getResources().getDisplayMetrics());

                layoutParams.width = dialogueWidth;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.windowAnimations = android.R.style.Animation_Dialog;

                final Dialog dialog = new Dialog(SettingGUI.this);
                dialog.setContentView(R.layout.custom_icons);
                dialog.setTitle(Html.fromHtml("<font color='" + getColor(R.color.dark) + "'>" + getString(R.string.customIconTitle) + "</font>", Html.FROM_HTML_MODE_LEGACY));
                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().getDecorView().setBackgroundColor(getColor(R.color.light));
                dialog.setCancelable(true);

                TextView defaultTheme = (TextView) dialog.findViewById(R.id.setDefault);
                RecyclerView customIconList = (RecyclerView) dialog.findViewById(R.id.customIconList);

                RecycleViewSmoothLayout recyclerViewLayoutManager = new RecycleViewSmoothLayout(getApplicationContext(), OrientationHelper.VERTICAL, false);
                customIconList.setLayoutManager(recyclerViewLayoutManager);
                customIconList.removeAllViews();
                final ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
                navDrawerItems.clear();
                for (String packageName : PublicVariable.customIconsPackages) {
                    navDrawerItems.add(new NavDrawerItem(
                            functionsClass.appName(packageName),
                            packageName,
                            functionsClass.appIconDrawable(packageName)
                    ));
                }
                CustomIconsThemeAdapter customIconsThemeAdapter = new CustomIconsThemeAdapter(SettingGUI.this, getApplicationContext(), navDrawerItems);
                customIconList.setAdapter(customIconsThemeAdapter);

                defaultTheme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendBroadcast(new Intent("CUSTOM_DIALOGUE_DISMISS"));

                        functionsClass.saveDefaultPreference("customIcon", getPackageName());
                        customIconIcon.setImageDrawable(getDrawable(R.drawable.draw_pref_custom_icon));
                        dialog.dismiss();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        navDrawerItems.clear();
                    }
                });
                dialog.show();

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("CUSTOM_DIALOGUE_DISMISS");
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals("CUSTOM_DIALOGUE_DISMISS")) {
                            customIconIcon.setImageDrawable(functionsClass.loadCustomIcons() ? functionsClass.appIconDrawable(functionsClass.readDefaultPreference("customIcon", getPackageName())) : getDrawable(R.drawable.draw_pref_custom_icon));
                            customIconDesc.setText(functionsClass.loadCustomIcons() ? functionsClass.appName(functionsClass.readDefaultPreference("customIcon", getPackageName())) : getString(R.string.customIconDesc));
                            if (functionsClass.loadCustomIcons()) {
                                if (functionsClass.mixShortcuts()) {
                                    functionsClass.addMixAppShortcutsCustomIconsPref();
                                } else if (functionsClass.AppShortcutsMode().equals("AppShortcuts")) {
                                    functionsClass.addAppShortcutsCustomIconsPref();
                                } else if (functionsClass.AppShortcutsMode().equals("SplitShortcuts")) {
                                    functionsClass.addAppsShortcutSplitCustomIconsPref();
                                } else if (functionsClass.AppShortcutsMode().equals("CategoryShortcuts")) {
                                    functionsClass.addAppsShortcutCategoryCustomIconsPref();
                                }
                            }
                            dialog.dismiss();
                        }
                    }
                };
                registerReceiver(broadcastReceiver, intentFilter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(SettingGUI.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        functionsClass.upcomingChangeLog(
                                                SettingGUI.this,
                                                firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                                                String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                                        );
                                    } else {

                                    }
                                    if (firebaseRemoteConfig.getBoolean("boolean_new_floating_shortcuts_pref_desc")) {
                                        prefDescFloating.setText(Html.fromHtml(firebaseRemoteConfig.getString("string_floating_shortcuts_pref_desc"), Html.FROM_HTML_MODE_LEGACY));
                                    }
                                }
                            });
                        } else {

                        }
                    }
                });

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("smart", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
            int mode = appOps.checkOp("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                prefSwitch.setChecked(true);
                editor.putBoolean("smartPick", true);
                editor.apply();
            } else {
                prefSwitch.setChecked(false);
                editor.putBoolean("smartPick", false);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        if (functionsClass.AccessibilityServiceEnabled()) {
            splitSwitch.setChecked(true);
        } else {
            splitSwitch.setChecked(false);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("mix", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("mixShortcuts", false) == true) {
            mixSwitch.setChecked(true);
        } else if (sharedPreferences.getBoolean("mixShortcuts", false) == false) {
            mixSwitch.setChecked(false);
        }

        customIconIcon.setImageDrawable(functionsClass.loadCustomIcons() ? functionsClass.appIconDrawable(functionsClass.readDefaultPreference("customIcon", getPackageName())) : getDrawable(R.drawable.draw_pref_custom_icon));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
            String tabView = sharedPreferences.getString("TabsView", NormalAppSelectionList.class.getSimpleName());
            if (tabView.equals(NormalAppSelectionList.class.getSimpleName())) {
                startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
            } else if (tabView.equals(SplitShortcuts.class.getSimpleName())) {
                startActivity(new Intent(getApplicationContext(), SplitShortcuts.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());

            } else if (tabView.equals(AdvanceShortcuts.class.getSimpleName())) {
                startActivity(new Intent(getApplicationContext(), AdvanceShortcuts.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
            } else {
                startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
            }
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);

        MenuItem gift = menu.findItem(R.id.donate);

        if (!functionsClass.alreadyDonated()) {

        } else {
            gift.setVisible(false);
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
            case R.id.facebook: {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app))));
                break;
            }
            case R.id.donate: {
                startActivity(new Intent(getApplicationContext(), InAppBilling.class));

                break;
            }
            case android.R.id.home: {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
                    String tabView = sharedPreferences.getString("TabsView", NormalAppSelectionList.class.getSimpleName());
                    if (tabView.equals(NormalAppSelectionList.class.getSimpleName())) {
                        startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                    } else if (tabView.equals(SplitShortcuts.class.getSimpleName())) {
                        startActivity(new Intent(getApplicationContext(), SplitShortcuts.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());

                    } else if (tabView.equals(AdvanceShortcuts.class.getSimpleName())) {
                        startActivity(new Intent(getApplicationContext(), AdvanceShortcuts.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                    } else {
                        startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, R.anim.go_up).toBundle());
                    }
                    this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareAll() {
        String shareText = getString(R.string.invitation_title) +
                "\n" + getString(R.string.invitation_message) +
                "\n" + getString(R.string.link_play_store) + getPackageName();
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, shareText);
        s.setType("text/plain");
        startActivity(s);
    }
}
