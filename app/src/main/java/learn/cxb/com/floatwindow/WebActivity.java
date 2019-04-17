package learn.cxb.com.floatwindow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class WebActivity extends SwipeBackActivity {
    CancelView mCancelView;

    float downX = 0;
    float downY = 0;
    int width = Util.getDisplayWidth();
    int edgeSize = Util.dip2px(10);// swipeBack的edgeSize

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSwipeBackLayout().setEdgeSize(edgeSize);
        mCancelView = FloatWindowManager.getInstance(this).getCancelView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
        }
        if (downX > edgeSize) {// 不触发滑动返回
            return super.dispatchTouchEvent(event);
        }
        // 触发了滑动返回
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCancelView.setCancelColor(Color.GRAY);
                mCancelView.setCancelText("浮窗");
                break;
            case MotionEvent.ACTION_MOVE:
                mCancelView.setFraction(event.getX() * 2 / width);
                break;
            case MotionEvent.ACTION_UP:
                mCancelView.setFraction(0);// todo 增加动画
                if (FloatWindowManager.getInstance(this).isInCancel(event.getX(), event.getY())) {
                    FloatWindowManager.getInstance(this).showFloatView(true);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
