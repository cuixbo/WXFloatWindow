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
    private WindowManager.LayoutParams mFloatAddParams;
    private WindowManager.LayoutParams mFloatCancelParams;

    private FloatWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        init(context);
    }

    public void init(Context context) {
        initCancelView(context);
        initAddView(context);
        initFloatView(context);
    }

    /**
     * 初始化圆形悬浮图片View
     */
    public void initFloatView(Context context) {
        if (!Util.checkFloatWindowPermission(context)) {
            Util.applyPermission(context);
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
    public void initAddView(Context context) {
        mFloatAddView = new FloatAddView(context);
        mFloatAddParams = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mFloatAddParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mFloatAddParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mFloatAddParams.format = PixelFormat.RGBA_8888;
        mFloatAddParams.gravity = Gravity.END | Gravity.BOTTOM;
        mFloatAddParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatAddParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatAddParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowManager.addView(mFloatAddView, mFloatAddParams);
    }

    /**
     * 初始化，右下角 "取消添加" 悬浮窗的1/4圆形View，红色
     */
    public void initCancelView(Context context) {
        mFloatCancelView = new FloatCancelView(context);
        mFloatCancelParams = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mFloatCancelParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mFloatCancelParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mFloatCancelParams.format = PixelFormat.RGBA_8888;
        mFloatCancelParams.gravity = Gravity.END | Gravity.BOTTOM;
        mFloatCancelParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatCancelParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatCancelParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowManager.addView(mFloatCancelView, mFloatCancelParams);
    }

    /**
     * 图片悬浮窗的touch事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initFloatViewTouchListener(final Context context) {
        final int statusBarHeight = Util.getStatusBarHeight(context);
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
                        boolean isInCancel = isInCircle(event.getRawX(), event.getRawY(), mFloatCancelView.getRadius());
                        if (isInCancel && !isLastInCancel) {
                            Util.performVibrate(context);//模拟震动
                        }
                        mFloatCancelView.setExpand(isInCancel);
                        isLastInCancel = isInCancel;
                        break;
                    case MotionEvent.ACTION_UP:
                        firstEntered = false;
                        mFloatCancelView.startAnimReverse();
                        if (isInCircle(event.getRawX(), event.getRawY(), mFloatCancelView.getRadius())) {
                            mFloatView.setVisibility(View.INVISIBLE);
                            mFloatParams.x = originX;
                            mFloatParams.y = originY;
                        } else {
                            //判断mView是在Window中的位置，以中间为界
                            int x = (int) event.getRawX();
                            int width = 1080;
                            int finalX = Util.dip2px(20);
                            if (x >= width / 2) {
                                finalX = width - mFloatView.getWidth() - Util.dip2px(20);
                            }
                            ValueAnimator animator = ValueAnimator.ofInt(mFloatParams.x, finalX);
                            animator.setDuration(500 * Math.abs(mFloatParams.x - finalX) / 540);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mFloatParams.x = (int) animation.getAnimatedValue();
                                    mWindowManager.updateViewLayout(mFloatView, mFloatParams);
                                }
                            });
                            animator.start();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 判断坐标是否在1/4圆内
     *
     * @param x      屏幕坐标
     * @param y      屏幕坐标
     * @param radius 1/4圆的半径
     * @return
     */
    public boolean isInCircle(float x, float y, int radius) {
        int centerX = Util.getDisplayWidth();//1/4圆的圆心
        int centerY = Util.getDisplayHeight();
        int distance = (int) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        return distance <= radius;
    }


    public void showFloatView(boolean show) {
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
