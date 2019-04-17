package learn.cxb.com.floatwindow;

/**
 * 有700ms延时
 */
public abstract class AppLifecycleListener {

    // app moved to foreground
    public abstract void onMoveToForeground();

    // app moved to background
    public abstract void onMoveToBackground();
}
