package learn.cxb.com.floatwindow;

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
    private QuadrantView mQuadrantViewExpand;

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
        mQuadrantView = findViewById(R.id.quadrant_view);
        mQuadrantViewExpand = findViewById(R.id.quadrant_view_expand);

        mQuadrantView.setColor(red);
        mQuadrantViewExpand.setColor(red);
        cancel = findViewById(R.id.cancel);

        setExpand(false);//默认不显示扩展
        setTranslationX(Integer.MAX_VALUE);// 默认不显示在屏幕中
        setTranslationY(Integer.MAX_VALUE);
    }

    public int getRadius() {
        return mQuadrantView.getRadius();
    }

    public void startAnim() {
        setTranslationX(getRadius());
        setTranslationY(getRadius());
        animate().translationX(0).start();
        animate().translationY(0).start();
    }

    public void startAnimReverse() {
        animate().translationX(getRadius()).start();
        animate().translationY(getRadius()).start();
    }

    public void setExpand(boolean expand) {
        mQuadrantViewExpand.setVisibility(expand ? VISIBLE : INVISIBLE);
    }


}
