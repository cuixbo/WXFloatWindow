package learn.cxb.com.floatwindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * 右下角1/4圆形View
 * 实际是整个圆，只显示了1/4，动画是斜对角位移动画
 */
public class QuadrantView extends View {

    private Context mContext;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mColor = 0;

    public QuadrantView(Context context) {
        super(context);
        init(context);
    }

    public QuadrantView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint.setColor(mColor);
        if (isInEditMode()) {
            mColor = Color.RED;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        // 绘制圆形
        canvas.drawCircle(getWidth(), getHeight(), getWidth(), mPaint);
    }

    public int getRadius() {
        return getWidth();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(@ColorInt int color) {
        mColor = color;
    }
}
