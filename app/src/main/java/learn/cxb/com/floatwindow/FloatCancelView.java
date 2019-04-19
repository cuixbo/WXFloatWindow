package learn.cxb.com.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 初始化，右下角 "取消添加" 悬浮窗的1/4圆形View，红色
 */
public class FloatCancelView extends FrameLayout {


    public static int red = Color.parseColor("#fa5151");

    private Context mContext;

    private QuadrantView mQuadrantView;
    private QuadrantView mQuadrantView2;

    private View cancel;

    public FloatCancelView(Context context) {
        super(context);
        init(context);
    }

    public FloatCancelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_float_cancel, this);
        mQuadrantView = findViewById(R.id.qv_1);
        mQuadrantView2 = findViewById(R.id.qv_2);
        mQuadrantView.setColor(red);
        mQuadrantView2.setColor(red);
        cancel = findViewById(R.id.cancel);

        setExpand(false);
        setVisibility(INVISIBLE);
    }

    public int getRadius() {
        return mQuadrantView.getRadius();
    }

    public void startAnim() {
        setVisibility(VISIBLE);
        cancel.setTranslationX(getRadius());
        cancel.setTranslationY(getRadius());
        mQuadrantView.setTranslationX(getRadius());
        mQuadrantView.setTranslationY(getRadius());

        cancel.animate().translationX(0).start();
        cancel.animate().translationY(0).start();
        mQuadrantView.animate().translationX(0).start();
        mQuadrantView.animate().translationY(0).start();

        mQuadrantView.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(VISIBLE);
            }
        });
    }

    public void startAnimReverse() {
        setVisibility(VISIBLE);
        cancel.animate().translationX(getRadius()).start();
        cancel.animate().translationY(getRadius()).start();
        mQuadrantView.animate().translationX(getRadius()).start();
        mQuadrantView.animate().translationY(getRadius()).start();

        mQuadrantView.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
            }
        });
    }

    public void setFraction(float fraction) {
        postInvalidate();
    }

    public void setExpand(boolean expand) {
        mQuadrantView2.setVisibility(expand ? VISIBLE : INVISIBLE);
    }


}
