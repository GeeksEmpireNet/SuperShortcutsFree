package net.geekstools.supershortcuts.PRO.Util.IAP;

import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.supershortcuts.PRO.Util.IAP.billing.BillingProvider;

public class InAppBilling extends FragmentActivity implements BillingProvider {

    private static final String TAG = "InAppBilling";
    private static final String DIALOG_TAG = "InAppBillingDialogue";

    FunctionsClass functionsClass;

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.GeeksEmpire_Material_IAP_LIGHT);

        functionsClass = new FunctionsClass(getApplicationContext(), InAppBilling.this);

        if (savedInstanceState != null) {
            acquireFragment = (AcquireFragment) getFragmentManager().findFragmentByTag(DIALOG_TAG);
        }

        billingManager = new BillingManager(InAppBilling.this, getIntent().hasExtra("UserEmailAddress") ? getIntent().getStringExtra("UserEmailAddress") : null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                proceedToPurchaseFragment();
            }
        }, 777);

        showRefreshedUi();
    }

    @Override
    public BillingManager getBillingManager() {
        return billingManager;
    }

    public void proceedToPurchaseFragment() {
        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getFragmentManager(), DIALOG_TAG);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public void showRefreshedUi() {
        if (isAcquireFragmentShown()) {
            acquireFragment.refreshUI();
        }
    }

    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }
}
