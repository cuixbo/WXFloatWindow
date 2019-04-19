package learn.cxb.com.floatwindow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * 右下角1/4圆形View
 * 实际是整个圆，只显示了1/4，动画是斜对角位移动画
 *
 * @deprecated see  {@link FloatCancelView}
 */
@Deprecated
public class CancelViewOld extends View {

    public static int red = Color.parseColor("#fa5151");
    public static int gray = Color.parseColor("#fcfcfc");

    private Context mContext;
    private Bitmap mBitmap;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics mFontMetrics;
    private ValueAnimator mAnimator;

    private int mRadius = Util.dip2px(160);
    private int mExpandRadius = Util.dip2px(10);
    private boolean mExpand = false;
    private float mCurTranslateFraction = 0;

    private int mCancelColor = red;
    private String mCancelText = "取消浮窗";
    private float mCancelTextLength = 0;

    public CancelViewOld(Context context) {
        super(context);
        init(context);
    }

    public CancelViewOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint.setColor(mCancelColor);
        mPaint.setTextSize(36);
        mBitmap = Util.getBitmap(mContext.getResources(), R.mipmap.icon_cancel_circle);
        mCancelTextLength = mPaint.measureText(mCancelText);
        mFontMetrics = new Paint.FontMetrics();
        mPaint.getFontMetrics(mFontMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRadius + mExpandRadius, mRadius + mExpandRadius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(mCancelColor);
        // 利用移动画布，实现位移动画
        canvas.translate(mRadius * (1 - mCurTranslateFraction), mRadius * (1 - mCurTranslateFraction));

        // 绘制圆形
        canvas.drawCircle(getWidth(), getHeight(), mRadius + (mExpand ? mExpandRadius : 0), mPaint);

        // 定义图片中心位置，绘制图片
        float cX = getWidth() - Util.dip2px(72);// 距右侧72
        float cY = getHeight() - Util.dip2px(72);// 距底部72
        canvas.drawBitmap(mBitmap, cX - mBitmap.getWidth() / 2F, cY - mBitmap.getHeight() / 2F, mPaint);

        // 绘制文字
        mPaint.setColor(gray);//#fca8a8
        canvas.drawText(mCancelText, cX - mPaint.measureText(mCancelText) / 2F, cY + Math.abs(mFontMetrics.top) + mBitmap.getHeight() / 2F + 20, mPaint);
    }

    public int getRadius() {
        return mRadius;
    }

    public void setCancelColor(@ColorInt int color) {
        mCancelColor = color;
    }

    public void setCancelText(String text) {
        mCancelText = text;
    }

    private ValueAnimator getAnimator() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, 1).setDuration(200);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurTranslateFraction = animation.getAnimatedFraction();
                    invalidate();
                }
            });
        }
        return mAnimator;
    }

    public void startAnim() {
        getAnimator().start();
    }

    public void startAnimReverse() {
        getAnimator().reverse();
    }

    public void setFraction(float fraction) {
        mCurTranslateFraction = Math.min(Math.max(0, fraction), 1);
        postInvalidate();
    }

    public void setExpand(boolean expand) {
        mExpand = expand;
        postInvalidate();
    }
}
