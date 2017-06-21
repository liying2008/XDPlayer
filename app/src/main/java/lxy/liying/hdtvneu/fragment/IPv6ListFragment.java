package lxy.liying.hdtvneu.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/14 13:27
 * 版本：1.0
 * 描述：IPv6电视直播列表Fragment
 * 备注：
 * =======================================================
 */
public class IPv6ListFragment extends BaseFragment {
    private LayoutInflater inflate;
    private String[] tabName = {"东北大学HDTV", "北邮人IPTV"};

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_ipv6);
        Resources res = getResources();

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_ipv6_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_ipv6_indicator);
        indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.parseColor("#f3148370"), 5));

        float unSelectSize = 12.0f;
        float selectSize = 12.0f;

        int selectColor = res.getColor(R.color.tab_top_text_2);
        int unSelectColor = res.getColor(R.color.tab_top_text_1);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(2);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());

        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        // 设置默认展示页
        int page = Integer.parseInt(App.getInstance().getSetting(Constants.IPV6_HOME, "0"));
        if (page == 2) page = 1;    // 原清华测试站已删除，使用北邮人代替
        indicatorViewPager.setCurrentItem(page, false);
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
                return new NEUPListFragment();
            } else if (position == 1) {
                return new BYRPListFragment();
            }
            return null;
        }
    }
}
