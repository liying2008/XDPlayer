package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.LocalListVideoAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/21 15:20
 * 版本：1.0
 * 描述：搜索结果列表Activity
 * 备注：
 * =======================================================
 */
public class SearchListActivity extends BaseActivity implements LocalListVideoAdapter.OnItemClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_list);

        TextView tvListInfo = (TextView) findViewById(R.id.tvListInfo);
        TextView tvNullInfo = (TextView) findViewById(R.id.tvNullInfo);
        RecyclerView rvVideoList = (RecyclerView) findViewById(R.id.rvVideoList);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });

        tvListInfo.setText("搜索结果");
        if (App.xdVideos == null) {
            tvNullInfo.setVisibility(View.VISIBLE);
        } else {
            tvNullInfo.setVisibility(View.GONE);
            rvVideoList.setLayoutManager(new LinearLayoutManager(this));
            LocalListVideoAdapter adapter = new LocalListVideoAdapter(this, App.xdVideos);
            rvVideoList.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, LocalVideoPlayer.class);
        intent.putExtra(Constants.XDVIDEO, App.xdVideos.get(position));
        startActivity(intent);
    }
}
