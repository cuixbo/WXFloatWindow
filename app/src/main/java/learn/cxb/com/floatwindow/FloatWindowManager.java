package learn.cxb.com.floatwindow;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
    private CancelView mCancelView;
    private WindowManager.LayoutParams mFloatParams;
    private WindowManager.LayoutParams mCancelParams;

    private FloatWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        init(context);
    }

    public void init(Context context) {
        initCancelView(context);
        initFloatView(context);
    }

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
        mFloatParams.y = Util.dip2px(16);
        mWindowManager.addView(mFloatView, mFloatParams);
        mFloatView.setVisibility(View.GONE);
        initFloatViewTouchListener(context);
    }

    public void initCancelView(Context context) {
        mCancelView = new CancelView(context);
        mCancelParams = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mCancelParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mCancelParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mCancelParams.format = PixelFormat.RGBA_8888;
        mCancelParams.gravity = Gravity.END | Gravity.BOTTOM;
        mCancelParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mCancelParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mCancelParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mCancelParams.x = 0;
        mCancelParams.y = 0;
        mWindowManager.addView(mCancelView, mCancelParams);
    }

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
                        mCancelView.setCancelColor(Color.RED);
                        mCancelView.setCancelText("取消浮窗");
                        mCancelView.startAnim();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mFloatParams.x = (int) (event.getRawX() - downX);//getRawX相对于屏幕左上角坐标x
                        mFloatParams.y = (int) (event.getRawY() - downY - statusBarHeight);
                        mWindowManager.updateViewLayout(mFloatView, mFloatParams);

                        boolean isInCancel = isInCancel((int) event.getRawX(), (int) event.getRawY());
                        if (isInCancel&&!isLastInCancel) {
                            Util.performVibrate(context);//模拟震动
                        }
                        isLastInCancel=isInCancel;
                        break;
                    case MotionEvent.ACTION_UP:
                        firstEntered = false;
                        mCancelView.startAnimReverse();
                        if (isInCancel((int) event.getRawX(), (int) event.getRawY())) {
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
     * 判断坐标是否在cancelView内
     */
    public boolean isInCancel(float x, float y) {
        int mCancelX = Util.getDisplayWidth();
        int mCancelY = Util.getDisplayHeight();
        int distance = (int) Math.sqrt((x - mCancelX) * (x - mCancelX) + (y - mCancelY) * (y - mCancelY));
        return distance <= 2 * mCancelView.getRadius();
    }

    public void showFloatView(boolean show) {
        mWindowManager.updateViewLayout(mFloatView, mFloatParams);
        mFloatView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public View getFloatView() {
        return mFloatView;
    }

    public CancelView getCancelView() {
        return mCancelView;
    }

}
