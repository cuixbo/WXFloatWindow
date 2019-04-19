package learn.cxb.com.floatwindow;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatWindowManager {

    private static volatile FloatWindowManager instance;

    public static FloatWindowManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager(context);
                }
            }
        }
        return instance;
    }

    private WindowManager mWindowManager;
    private FloatView mFloatView;
    private FloatAddView mFloatAddView;
    private FloatCancelView mFloatCancelView;
    private WindowManager.LayoutParams mFloatParams;

    private ValueAnimator mAnimator;

    private FloatWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        init(context);
    }

    public void init(Context context) {
        if (!Util.checkFloatWindowPermission(context)) {
            Util.showFloatWindowPermissionDialog(context);
            return;
        }
        initCancelView(context);
        initAddView(context);
        initFloatView(context);
    }

    /**
     * 初始化圆形悬浮图片View
     */
    private void initFloatView(Context context) {
        if (mFloatView != null) {
            return;
        }
        mFloatView = new FloatView(context);
        mFloatParams = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mFloatParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mFloatParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mFloatParams.format = PixelFormat.RGBA_8888;
        mFloatParams.gravity = Gravity.START | Gravity.TOP;
        mFloatParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mFloatParams.x = Util.dip2px(16);
        mFloatParams.y = Util.dip2px(60);
        mWindowManager.addView(mFloatView, mFloatParams);
        mFloatView.setVisibility(View.GONE);
        initFloatViewTouchListener(context);
    }

    /**
     * 初始化，右下角 "添加" 悬浮窗的1/4圆形View，灰色
     */
    private void initAddView(Context context) {
        if (mFloatAddView != null) {
            return;
        }
        mFloatAddView = new FloatAddView(context);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowManager.addView(mFloatAddView, params);
    }

    /**
     * 初始化，右下角 "取消添加" 悬浮窗的1/4圆形View，红色
     */
    private void initCancelView(Context context) {
        if (mFloatCancelView != null) {
            return;
        }
        mFloatCancelView = new FloatCancelView(context);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowManager.addView(mFloatCancelView, params);
    }

    /**
     * 图片悬浮窗的touch事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initFloatViewTouchListener(final Context context) {
        final int statusBarHeight = Util.getStatusBarHeight(context);
        final int width = Util.getDisplayWidth();
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            float downY = 0;
            int originX = 0;// float view 按下时的params位置
            int originY = 0;
            boolean firstEntered = false;
            boolean isLastInCancel = false;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();//相对于组件左上角坐标x
                        downY = event.getY();
                        originX = mFloatParams.x;
                        originY = mFloatParams.y;
                        mFloatCancelView.startAnim();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mFloatParams.x = (int) (event.getRawX() - downX);//getRawX相对于屏幕左上角坐标x
                        mFloatParams.y = (int) (event.getRawY() - downY - statusBarHeight);
                        mWindowManager.updateViewLayout(mFloatView, mFloatParams);
                        boolean isInCancel = Util.isInCircle(event.getRawX(), event.getRawY(), mFloatCancelView.getRadius());
                        if (isInCancel && !isLastInCancel) {
                            Util.performVibrate(context);//模拟震动
                        }
                        mFloatCancelView.setExpand(isInCancel);
                        isLastInCancel = isInCancel;
                        break;
                    case MotionEvent.ACTION_UP:
                        firstEntered = false;
                        mFloatCancelView.startAnimReverse();
                        if (Util.isInCircle(event.getRawX(), event.getRawY(), mFloatCancelView.getRadius())) {
                            mFloatView.setVisibility(View.INVISIBLE);
                            mFloatParams.x = originX;
                            mFloatParams.y = originY;
                        } else {
                            //判断mView是在Window中的位置，以中间为界
                            int x = (int) event.getRawX();
                            int finalX = Util.dip2px(20);
                            if (x >= width / 2) {
                                finalX = width - mFloatView.getWidth() - Util.dip2px(20);
                            }
                            //执行贴边动画
                            getAnimator().setIntValues(mFloatParams.x, finalX);
                            getAnimator().setDuration(300 * Math.abs(mFloatParams.x - finalX) / (width / 2));
                            getAnimator().start();

                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * floatView释放后贴边的动画
     */
    private ValueAnimator getAnimator() {
        if (mAnimator == null) {
            mAnimator = new ValueAnimator();
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mFloatParams.x = (int) animation.getAnimatedValue();
                    mWindowManager.updateViewLayout(mFloatView, mFloatParams);
                }
            });
        }
        return mAnimator;
    }

    public void showFloatView(boolean show) {
        if (mFloatView == null) {
            return;
        }
        mWindowManager.updateViewLayout(mFloatView, mFloatParams);
        mFloatView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public View getFloatView() {
        return mFloatView;
    }

    public FloatCancelView getFloatCancelView() {
        return mFloatCancelView;
    }

    public FloatAddView getFloatAddView() {
        return mFloatAddView;
    }

}
