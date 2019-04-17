package learn.cxb.com.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

/**
 * 根据Activity生命周期,判断App在前后台（相对即时性，如果使用ProcessLifecycleOwner则会有700ms延时）
 */
public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

    private int numStarted = 0;

    private AppLifecycleListener mLifecycleListener = new AppLifecycleListener() {
        @Override
        public void onMoveToForeground() {
            FloatWindowManager.getInstance(MainApp.getInstance()).getFloatView().setVisibility(View.VISIBLE);
        }

        @Override
        public void onMoveToBackground() {
            FloatWindowManager.getInstance(MainApp.getInstance()).getFloatView().setVisibility(View.INVISIBLE);
        }
    };


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (numStarted == 0) {
            // app on foreground
            mLifecycleListener.onMoveToForeground();
        }
        numStarted++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            // app on background
            mLifecycleListener.onMoveToBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
