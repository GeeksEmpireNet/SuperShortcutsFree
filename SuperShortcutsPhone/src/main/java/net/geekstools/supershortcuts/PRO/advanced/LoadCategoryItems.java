package net.geekstools.supershortcuts.PRO.advanced;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.CustomIconManager.LoadCustomIcons;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;

public class LoadCategoryItems extends Activity {

    FunctionsClass functionsClass;

    RelativeLayout wholeLow, popupAnchorView;

    Intent intent;
    String categoryName;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.category_show_ui);
        functionsClass = new FunctionsClass(getApplicationContext(), LoadCategoryItems.this);

        intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("load_category_action")) {
                    categoryName = intent.getStringExtra("categoryName");
                } else if (intent.getAction().equals("load_category_action_shortcut")) {
                    categoryName = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            } else {
                finish();
                return;
            }
        } else {
            finish();
            return;
        }

        wholeLow = (RelativeLayout) findViewById(R.id.wholeLow);
        popupAnchorView = (RelativeLayout) findViewById(R.id.popupAnchorView);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals("load_category_action")) {
                    functionsClass.showPopupCategoryItem(popupAnchorView, categoryName.replace(".CategorySelected", ""), loadCustomIcons);
                } else if (intent.getAction().equals("load_category_action_shortcut")) {
                    functionsClass.showPopupCategoryItem(popupAnchorView, categoryName, loadCustomIcons);
                }
            }
        }, 250);

        wholeLow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
