package learn.cxb.com.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class WebActivity extends SwipeBackActivity {

    FloatWindowManager mFloatWindowManager;
    FloatAddView mFloatAddView;
    float downX = 0;
    float downY = 0;
    int width = Util.getDisplayWidth();
    int edgeSize = Util.dip2px(10);// swipeBack的edgeSize
    boolean isLastInCancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSwipeBackLayout().setEdgeSize(edgeSize);
//        getSwipeBackLayout().setScrollThresHold(0.5F);
        mFloatWindowManager = FloatWindowManager.getInstance(this);
        mFloatWindowManager.init(this);
//        mFloatAddView = mFloatWindowManager.getFloatAddView();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mFloatAddView = addFloatAddView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
        }
        if (downX > edgeSize || mFloatAddView == null) {// 不触发滑动返回
            return super.dispatchTouchEvent(event);
        }
        // 触发了滑动返回
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mFloatAddView.setFraction(event.getX() * 2 / width);
                boolean isInCancel = Util.isInCircle(event.getX(), event.getY(), mFloatAddView.getRadius());
                if (isInCancel && !isLastInCancel) {
                    Util.performVibrate(this);//模拟震动
                }
                mFloatAddView.setExpand(isInCancel);
                isLastInCancel = isInCancel;
                break;
            case MotionEvent.ACTION_UP:
                mFloatAddView.setFraction(0);
                if (Util.isInCircle(event.getX(), event.getY(), mFloatAddView.getRadius())) {
                    if (!Util.checkFloatWindowPermission(this)) {
                        Util.showFloatWindowPermissionDialog(this);
                    } else {
                        mFloatWindowManager.showFloatView(true);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private FloatAddView addFloatAddView() {
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        FloatAddView floatAddView = new FloatAddView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        decor.addView(floatAddView, params);
        return floatAddView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            mFloatWindowManager.showFloatView(true);
//            if (Util.checkFloatWindowPermission(this)) {// 8.0系统返回不准，有bug
//                // 悬浮窗权限已开启
//                Util.showToast(this, "悬浮窗权限已开启");
//            } else {
//                Util.showToast(this, "悬浮窗权限已关闭");
//            }
        }
    }
}
