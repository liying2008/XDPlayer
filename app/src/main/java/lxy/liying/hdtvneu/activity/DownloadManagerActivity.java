package lxy.liying.hdtvneu.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.DownloadedListAdapter;
import lxy.liying.hdtvneu.fragment.DownloadedFragment;
import lxy.liying.hdtvneu.fragment.DownloadingFragment;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/11 23:51
 * 版本：1.0
 * 描述：视频下载管理器类
 * 备注：
 * =======================================================
 */
public class DownloadManagerActivity extends BaseActivity {
    private LayoutInflater inflate;
    private String[] tabName = {"正在下载", "下载完成"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });
        ImageView ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadingFragment downloadingFragment = DownloadingFragment.getInstance();
                if (downloadingFragment != null) {
                    // 刷新列表
                    downloadingFragment.refreshList();
                    downloadingFragment.refreshTextMsg();
                }

                DownloadedListAdapter downloadedListAdapter = DownloadedListAdapter.getEdAdapter();
                if (downloadedListAdapter != null) {
                    // 刷新列表
                    downloadedListAdapter.refreshList();
                }
            }
        });
        Resources res = getResources();

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_download_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_download_indicator);
        indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.parseColor("#f3148370"), 5));

        float unSelectSize = 12.0f;
        float selectSize = 12.0f;

        int selectColor = res.getColor(R.color.tab_top_text_2);
        int unSelectColor = res.getColor(R.color.tab_top_text_1);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(4);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        private MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabName.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabName[position]);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                // 正在下载
                return new DownloadingFragment();
            } else if (position == 1) {
                // 下载完成
                return new DownloadedFragment();
            }
            return null;
        }
    }
}
