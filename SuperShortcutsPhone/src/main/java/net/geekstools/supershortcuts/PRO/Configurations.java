package net.geekstools.supershortcuts.PRO;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.advanced.AdvanceShortcuts;
import net.geekstools.supershortcuts.PRO.normal.NormalAppSelectionList;
import net.geekstools.supershortcuts.PRO.split.SplitShortcuts;

import io.fabric.sdk.android.Fabric;

public class Configurations extends Activity {

    FunctionsClass functionsClass;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FirebaseApp.initializeApp(getApplicationContext());
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            PublicVariable.actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        PublicVariable.statusBarHeight = result;

        PublicVariable.navigationBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android"));

        functionsClass = new FunctionsClass(getApplicationContext(), Configurations.this);

        try {
            functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(getPackageName()).contains("[BETA]") ? true : false);
            functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(getPackageName()));
            functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(getPackageName()));
            functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.getDeviceName());
            functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.getCountryIso());

            if (functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("ShortcutsModeView", MODE_PRIVATE);
        String tabView = sharedPreferences.getString("TabsView", NormalAppSelectionList.class.getSimpleName());
        if (tabView.equals(NormalAppSelectionList.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
        } else if (tabView.equals(SplitShortcuts.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), SplitShortcuts.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());

        } else if (tabView.equals(AdvanceShortcuts.class.getSimpleName())) {
            startActivity(new Intent(getApplicationContext(), AdvanceShortcuts.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
        } else {
            startActivity(new Intent(getApplicationContext(), NormalAppSelectionList.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
        }

        finish();
    }
}
