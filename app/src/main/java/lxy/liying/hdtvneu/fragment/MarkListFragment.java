package lxy.liying.hdtvneu.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.dialog.MarkHelpDialog;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/14 14:45
 * 版本：1.0
 * 描述：收藏列表
 * 备注：
 * =======================================================
 */
public class MarkListFragment extends BaseFragment {
    private LayoutInflater inflate;
    private String[] tabName = {"IPv6电视", "本地视频", "B&A", "其他"};

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_mark_list);
        LinearLayout llHow = (LinearLayout) findViewById(R.id.llHow);
        LinearLayout llRemove = (LinearLayout) findViewById(R.id.llRemove);
        llHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkHelpDialog dialog = new MarkHelpDialog(getActivity());
                dialog.show();
            }
        });
        llRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除所有收藏
                final NormalDialog dialog = new NormalDialog(MarkListFragment.this.getActivity());
                dialog.isTitleShow(false)//
                        .bgColor(Color.parseColor("#383838"))//
                        .cornerRadius(5)//
                        .content("确定删除所有收藏?")//
                        .contentGravity(Gravity.CENTER)//
                        .contentTextColor(Color.parseColor("#ffffff"))//
                        .dividerColor(Color.parseColor("#222222"))//
                        .btnTextSize(15.5f, 15.5f)//
                        .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                        .btnPressColor(Color.parseColor("#2B2B2B"))//
                        .widthScale(0.85f)//
                        .showAnim(App.mBasIn)//
                        .dismissAnim(App.mBasOut)//
                        .show();

                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                // 取消
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                // 确定
                                deleteAllMarks();
                                dialog.dismiss();
                            }
                        });
            }
        });
        Resources res = getResources();

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_mark_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_mark_indicator);
        indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.parseColor("#f3148370"), 5));

        float unSelectSize = 12.0f;
        float selectSize = 12.0f;

        int selectColor = res.getColor(R.color.tab_top_text_2);
        int unSelectColor = res.getColor(R.color.tab_top_text_1);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(4);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        // 设置默认展示页
        int page = Integer.parseInt(App.getInstance().getSetting(Constants.MARK_HOME, "0"));
        indicatorViewPager.setCurrentItem(page, false);
    }

    /**
     * 清空收藏
     */
    private void deleteAllMarks() {
        AppToast.showToast("正在删除……");
        App.markService.removeAllMarks();
        // 更新Fragment显示
        if (App.ipv6MarkItems != null) {
            App.ipv6MarkItems.clear();
        }
        if (App.localMarkItems != null) {
            App.localMarkItems.clear();
        }
        if (App.biliMarkItems != null) {
            App.biliMarkItems.clear();
        }
        if (App.onlineMarkItems != null) {
            App.onlineMarkItems.clear();
        }
        MarkIPv6Fragment ipv6Instance = MarkIPv6Fragment.getInstance();
        MarkLocalFragment localInstance = MarkLocalFragment.getInstance();
        MarkBiliFragment biliInstance = MarkBiliFragment.getInstance();
        MarkOnlineFragment onlineInstance = MarkOnlineFragment.getInstance();

        if (ipv6Instance != null) {
            ipv6Instance.markIPv6Adapter.setData(new ArrayList<MarkItem>(1));
            ipv6Instance.markIPv6Adapter.notifyDataSetChanged();
            ipv6Instance.tvMarkNone.setVisibility(View.VISIBLE);
        }
        if (localInstance != null) {
            localInstance.markLocalAdapter.setData(new ArrayList<MarkItem>(1));
            localInstance.markLocalAdapter.notifyDataSetChanged();
            localInstance.tvMarkNone.setVisibility(View.VISIBLE);
        }
        if (biliInstance != null) {
            biliInstance.markBiliAdapter.setData(new ArrayList<MarkItem>(1));
            biliInstance.markBiliAdapter.notifyDataSetChanged();
            biliInstance.tvMarkNone.setVisibility(View.VISIBLE);
        }
        if (onlineInstance != null) {
            onlineInstance.markOnlineAdapter.setData(new ArrayList<MarkItem>(1));
            onlineInstance.markOnlineAdapter.notifyDataSetChanged();
            onlineInstance.tvMarkNone.setVisibility(View.VISIBLE);
        }
        AppToast.showToast("所有收藏已删除。");
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
                // IPv6电视
                return new MarkIPv6Fragment();
            } else if (position == 1) {
                // 本地视频
                return new MarkLocalFragment();
            } else if (position == 2) {
                // 哔哩哔哩/AcFun
                return new MarkBiliFragment();
            } else if (position == 3) {
                // 其他
                return new MarkOnlineFragment();
            }
            return null;
        }
    }
}
