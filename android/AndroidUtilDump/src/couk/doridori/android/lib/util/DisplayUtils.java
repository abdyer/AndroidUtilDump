package couk.doridori.android.lib.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DisplayUtils
{
    /**
     * May not be accurate due to system bars / OEMs etc. See http://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically
     *
     * @param activity
     * @return
     */
    public static float getSmallestWidthDp(Activity activity)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);
        return smallestWidth;
    }
}
