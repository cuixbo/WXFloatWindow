package learn.cxb.com.floatwindow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    TopicAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        mAdapter = new TopicAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String url = "https://mp.weixin.qq.com/s/8opnRvTaiwkUWBBuIhhM8Q";
                String logoUrl = "https://avatar.csdn.net/C/3/E/3_csdnnews.jpg";
                WebActivity.open(getApplication(), url, logoUrl);
            }
        });

//        tvAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), WebActivity.class));
//            }
//        });
        //startActivity(new Intent(this, WebActivity.class));
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>(10);
        for (int i = 0; i < 2048; i++) {
            list.add("");
        }
        return list;
    }


    public static class TopicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public TopicAdapter(@Nullable List<String> data) {
            super(R.layout.list_item_subscribe, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            String logo = "https://avatar.csdn.net/C/3/E/3_csdnnews.jpg";
            Glide.with(helper.itemView).load(logo).into((ImageView) helper.getView(R.id.iv_logo));

            String url = "https://mmbiz.qpic.cn/mmbiz_jpg/QicyPhNHD5va3dibicJpUuNoP9okh3lhVHWPW91zZwkPCYqv8CxBoJE43NIvlB2uaSQatjpvIBHbXapV7fvvCwKJw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1";
            Glide.with(helper.itemView).load(url).into((ImageView) helper.getView(R.id.iv_big_image));

            url = "https://mmbiz.qpic.cn/mmbiz_jpg/BJnibHupq5dGzkSDicaHCMfGRVzcO4lj20puYLzjVLKdVCr2VdpoiaklicDIYYxaBjpKYTEambthMjiaYibhUf8hsxfw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1";
            Glide.with(helper.itemView).load(url).into((ImageView) helper.getView(R.id.iv_small_image));

        }
    }


}
