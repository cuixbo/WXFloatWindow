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
    private QuadrantView mQuadrantView2;
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
        mQuadrantView = findViewById(R.id.qv_1);
        mQuadrantView2 = findViewById(R.id.qv_2);
        mQuadrantView.setColor(gray);
        mQuadrantView2.setColor(gray);
        cancel = findViewById(R.id.cancel);

        setExpand(false);
        setVisibility(INVISIBLE);
    }

    public int getRadius() {
        return mQuadrantView.getRadius();
    }

    public void setFraction(float fraction) {
        float dis = getRadius() * (1 - Math.min(1, fraction));
        cancel.setTranslationX(dis);
        cancel.setTranslationY(dis);
        mQuadrantView.setTranslationX(dis);
        mQuadrantView.setTranslationY(dis);

        setVisibility(fraction > 0 ? VISIBLE : INVISIBLE);
    }

    public void setExpand(boolean expand) {
        mQuadrantView2.setVisibility(expand ? VISIBLE : INVISIBLE);
    }
}
