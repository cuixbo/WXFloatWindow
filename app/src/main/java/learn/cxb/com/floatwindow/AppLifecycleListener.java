package learn.cxb.com.floatwindow;

import android.app.Activity;

/**
 * App 生命周期回调
 */
public abstract class AppLifecycleListener {

    // app moved to foreground
    public abstract void onMoveToForeground(Activity activity);

    // app moved to background
    public abstract void onMoveToBackground(Activity activity);
}
