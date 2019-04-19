package learn.cxb.com.floatwindow;

import android.app.Application;

public class MainApp extends Application {
    private static MainApp INSTANCE;
    AppLifecycleTracker mAppLifecycleTracker = new AppLifecycleTracker();;

    public static MainApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        registerActivityLifecycleCallbacks(mAppLifecycleTracker);
    }

}
