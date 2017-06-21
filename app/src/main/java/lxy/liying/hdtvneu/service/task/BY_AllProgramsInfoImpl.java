package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.regex.BYR_RegexAllProgramsHtml;
import lxy.liying.hdtvneu.utils.CommonUtils;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/15 18:52
 * 版本：1.0
 * 描述：北京邮电大学IPTV——获取所有节目实现类
 * 备注：
 * =======================================================
 */
public class BY_AllProgramsInfoImpl implements BY_AllProgramsInfo {
    @Override
    public List<Program> getAllPrograms() {
        String html = CommonUtils.getHtml(Constants.BY_IPTV_URL_6, "UTF-8");
        return BYR_RegexAllProgramsHtml.getAllPrograms(html);
    }
}
