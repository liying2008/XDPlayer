package lxy.liying.hdtvneu.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.fragment.BYRPListFragment;
import lxy.liying.hdtvneu.fragment.IPv6ListFragment;
import lxy.liying.hdtvneu.fragment.LocalListFragment;
import lxy.liying.hdtvneu.fragment.MarkListFragment;
import lxy.liying.hdtvneu.fragment.MoreFragment;
import lxy.liying.hdtvneu.fragment.NEUPListFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_main);
        SViewPager viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
        FixedIndicatorView indicator = (FixedIndicatorView) findViewById(R.id.tabmain_indicator);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(0xffea8010, Color.GRAY));


        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(4);
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"本地", "IPv6", "收藏", "更多"};
        private int[] tabIcons = {R.drawable.tab_computer, R.drawable.tab_network, R.drawable.tab_mark,
                R.drawable.tab_more};
        private LayoutInflater inflater;

        private MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_main, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                return new LocalListFragment();
            } else if (position == 1) {
                return new IPv6ListFragment();
            } else if (position == 2) {
                return new MarkListFragment();
            } else if (position == 3) {
                return new MoreFragment();
            }
            return null;
        }
    }

    private long lastBackKeyTime;

    /**
     * 点击Back键，如果当前的Fragment有onBackPress方法，则调用其onBackPress方法，
     * 根据其返回值判断是否调用本类onBackPress方法
     */
    @Override
    public void onBackPressed() {
        boolean callFragmentBack = false;
        if (App.getInstance().currentFragment != null &&
                App.getInstance().currentFragment instanceof NEUPListFragment) {
            callFragmentBack = ((NEUPListFragment) App.getInstance().currentFragment).onBackPress();
        } else if (App.getInstance().currentFragment != null &&
                App.getInstance().currentFragment instanceof BYRPListFragment) {
            callFragmentBack = ((BYRPListFragment) App.getInstance().currentFragment).onBackPress();
        }

        if (!callFragmentBack) {
            long delay = Math.abs(System.currentTimeMillis() - lastBackKeyTime);
            if (delay > 3000) {
                // 双击退出程序
                AppToast.showToast(R.string.toast_key_back);
                lastBackKeyTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }
}
