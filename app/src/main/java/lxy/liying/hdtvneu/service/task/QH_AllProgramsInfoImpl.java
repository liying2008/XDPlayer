package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.regex.QH_RegexAllProgramsHtml;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.HtmlGetter;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/15 18:52
 * 版本：1.0
 * 描述：清华大学IPTV测试——获取所有节目实现类
 * 备注：
 * =======================================================
 */
public class QH_AllProgramsInfoImpl implements QH_AllProgramsInfo {
    @Override
    public List<Program> getAllPrograms() {
        String html = HtmlGetter.getHtml(Constants.QH_IPTV_URL, "UTF-8");
        return QH_RegexAllProgramsHtml.getAllPrograms(html);
    }
}
