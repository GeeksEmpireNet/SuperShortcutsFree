package net.geekstools.supershortcuts.PRO;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;


public class LicenseValidator extends Service {

    private static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApuPboMEeGV+PziVN44CsuciQVUAeHimLNhY+VcOxBgli+bg3DK2EJ/bI6PJRL1SbDGo4pBpk7IIGk34BhTkS" +
                    "hJsCrLnFeQMEM+Kt7ep9vPGCr59fYJ/IYqOlc5htCTOU5F8c/zi2JvOtkqbuytkC1BV12Bud0xMYc0O6/c11eWjs20JypMadUcaG/Y00JQrX9u28Qroj610ap" +
                    "eClVTuNbckxAwnaGL/WnR6Fsw6Kzpx8UEn3x6ChcwsZgkvGqjYdNJYwATWrRJOycGqNTJ2ks7PWIhJO7vRygyTnd6qdiZrUYxkoF3kx6L3iHTu70rTJTrW1Hh" +
                    "Z3ouyjkWz6fORERQIDAQAB";
    private static final byte[] SALT = new byte[]{
            -16, -13, 30, -128, -103, -57, 74, -64, 53, 88, -97, -45, 77, -113, -36, -113, -11, 32, -64, 89
    };
    FunctionsClass functionsClass;
    LicenseChecker licenseChecker;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (functionsClass.returnAPI() < 26) {
            startForeground(111, bindServiceLOW());
        } else {
            startForeground(111, bindServiceHIGH());
        }

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        licenseChecker = new LicenseChecker(
                getApplicationContext(),
                new ServerManagedPolicy(getApplicationContext(), new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY
        );
        final LicenseCheckerCallback licenseCheckerCallback = new LicenseCheckerCallback() {
            @Override
            public void allow(int reason) {
                functionsClass.saveFileAppendLine(".License", String.valueOf(reason));
                stopSelf();
            }

            @Override
            public void dontAllow(int reason) {
                sendBroadcast(new Intent(getString(R.string.license)));
            }

            @Override
            public void applicationError(int errorCode) {
            }
        };
        licenseChecker.checkAccess(licenseCheckerCallback);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        licenseChecker.onDestroy();
    }

    protected Notification bindServiceHIGH() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(notificationChannel);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setColor(getColor(R.color.default_color));
        mBuilder.setContentTitle(getString(R.string.license_info));
        mBuilder.setContentText(getString(R.string.license_info_desc));
        mBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info) + "</font></b>"));
        mBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font>"));
        mBuilder.setTicker(getString(R.string.license_validating));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setChannelId(getPackageName());

        return mBuilder.build();
    }

    protected Notification bindServiceLOW() {
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setColor(getColor(R.color.default_color));
        mBuilder.setContentTitle(getString(R.string.license_info));
        mBuilder.setContentText(getString(R.string.license_info_desc));
        mBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info) + "</font></b>"));
        mBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font>"));
        mBuilder.setTicker(getString(R.string.license_validating));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setPriority(Notification.PRIORITY_MIN);

        return mBuilder.build();
    }
}
