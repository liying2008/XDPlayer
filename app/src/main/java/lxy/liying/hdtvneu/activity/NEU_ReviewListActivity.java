package lxy.liying.hdtvneu.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.NEU_ReviewListAdapter;
import lxy.liying.hdtvneu.domain.ReviewDate;
import lxy.liying.hdtvneu.domain.ReviewList;
import lxy.liying.hdtvneu.domain.ReviewProgram;
import lxy.liying.hdtvneu.service.MainBinder;
import lxy.liying.hdtvneu.service.MainService;
import lxy.liying.hdtvneu.service.callback.On_NEU_GetReviewProgramsCallback;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/16 21:29
 * 版本：1.0
 * 描述：节目回看内容选择界面
 * 备注：
 * =======================================================
 */
public class NEU_ReviewListActivity extends BaseActivity implements On_NEU_GetReviewProgramsCallback {
    private MainBinder mainBinder;
    private ExpandableListView elvReview;
    private List<ReviewDate> groupDates;
    private List<List<ReviewProgram>> childPrograms;
    private NEU_ReviewListAdapter reviewListAdapter;
    private ProgressDialog progressDialog;
    private String p;   // 回看频道

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neu_review_list);

        TextView tvReviewProgram = (TextView) findViewById(R.id.tvReviewProgram);
        elvReview = (ExpandableListView) findViewById(R.id.elvReview);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在获取回看节目列表...");
        progressDialog.show();

        reviewListAdapter = new NEU_ReviewListAdapter(this);

        Intent intentData = getIntent();
        String[] programInfo = intentData.getStringArrayExtra(Constants.PROGRAM);
        p = programInfo[1];
        tvReviewProgram.setText(programInfo[0]);
        Intent intent = new Intent(this, MainService.class);
        this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        if (mainBinder != null) {
            // 初始化数据
            initData();
            progressDialog.dismiss();
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mainBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mainBinder = (MainBinder) service;
            mainBinder.neu_getReviewProgramsHtml(p, NEU_ReviewListActivity.this);
            setDataSource(mainBinder);
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        elvReview.setAdapter(reviewListAdapter);
    }
    @Override
    public void onGetReviewPrograms(ReviewList reviewList) {
        // TODO: 2016/5/19 没有回看节目，要不要显示提示信息 
        this.groupDates = reviewList.getGroupDates();
        this.childPrograms = reviewList.getChildPrograms();
        reviewListAdapter.setReviewData(groupDates, childPrograms, p);
        initData();
        progressDialog.dismiss();
    }

    public void setDataSource(MainBinder mainBinder) {
        this.mainBinder = mainBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
