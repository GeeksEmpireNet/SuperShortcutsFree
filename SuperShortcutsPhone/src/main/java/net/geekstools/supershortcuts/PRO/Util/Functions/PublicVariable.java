package net.geekstools.supershortcuts.PRO.Util.Functions;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PublicVariable {

    public static String categoryName = "AdvancedShortcuts";
    public static String categoryNameSelected = "AdvancedShortcuts";
    public static String setAppIndex, setAppIndexUrl;

    public static Uri BASE_URL =
            Uri.parse("https://www.geeksempire.net/FAQ/super.html/");

    public static int maxAppShortcuts;
    public static int maxAppShortcutsCounter;

    public static int advanceShortcutsMaxAppShortcuts;
    public static int advanceShortcutsMaxAppShortcutsCounter;

    public static int advMaxAppShortcuts;
    public static int advMaxAppShortcutsCounter;

    public static int SplitShortcutsMaxAppShortcuts;
    public static int SplitShortcutsMaxAppShortcutsCounter;

    public static int SplitMaxAppShortcuts;
    public static int SplitMaxAppShortcutsCounter;

    public static int actionBarHeight;
    public static int statusBarHeight;
    public static int navigationBarHeight;

    public static float confirmButtonX, confirmButtonY;

    public static boolean firstLoad = true;
    public static boolean adsEligible = false;

    public static List<String> customIconsPackages = new ArrayList<String>();
}
