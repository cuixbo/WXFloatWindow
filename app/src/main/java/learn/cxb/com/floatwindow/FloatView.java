package learn.cxb.com.floatwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * 悬浮图片View
 */
public class FloatView extends RelativeLayout {

    private Context mContext;
    private ImageView mIvFloat;

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_float, this);
        mIvFloat = findViewById(R.id.iv_float);
    }

    public void setImageUrl(String url) {
        Glide.with(MainApp.getInstance()).load(url).into(mIvFloat);
    }

}
