package learn.cxb.com.floatwindow;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 右下角 "添加" 悬浮窗的1/4圆形View，灰色
 */
public class FloatAddView extends FrameLayout {

    public static int gray = Color.parseColor("#666666");

    private Context mContext;
    private QuadrantView mQuadrantView;
    private QuadrantView mQuadrantViewExpand;
    private View cancel;

    public FloatAddView(Context context) {
        super(context);
        init(context);
    }

    public FloatAddView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_float_add, this);
        mQuadrantView = findViewById(R.id.quadrant_view);
        mQuadrantViewExpand = findViewById(R.id.quadrant_view_expand);
        mQuadrantView.setColor(gray);
        mQuadrantViewExpand.setColor(gray);
        cancel = findViewById(R.id.cancel);

        setExpand(false);//默认不显示扩展
        setTranslationX(Integer.MAX_VALUE);// 默认不显示在屏幕中
        setTranslationY(Integer.MAX_VALUE);
    }

    public int getRadius() {
        return mQuadrantView.getRadius();
    }

    public void setFraction(float fraction) {
        float dis = getRadius() * (1 - Math.min(1, fraction));
        setTranslationX(dis);
        setTranslationY(dis);
    }

    /**
     * 是否显示扩展的圆形
     */
    public void setExpand(boolean expand) {
        mQuadrantViewExpand.setVisibility(expand ? VISIBLE : INVISIBLE);
    }
}
