package learn.cxb.com.floatwindow;

import android.os.Bundle;
import android.view.MotionEvent;

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
        mFloatAddView = mFloatWindowManager.getFloatAddView();
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
                    mFloatWindowManager.showFloatView(true);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
