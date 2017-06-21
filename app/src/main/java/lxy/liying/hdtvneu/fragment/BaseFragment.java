package lxy.liying.hdtvneu.fragment;

import com.shizhefei.fragment.LazyFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/21 12:58
 * 版本：1.0
 * 描述：所有Fragment的基类
 * 备注：
 * =======================================================
 */
public class BaseFragment extends LazyFragment {

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }
}
