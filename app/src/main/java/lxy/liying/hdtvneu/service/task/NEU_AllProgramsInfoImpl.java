package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.regex.NEU_RegexAllProgramsHtml;
import lxy.liying.hdtvneu.utils.CommonUtils;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/15 18:52
 * 版本：1.0
 * 描述：获取所有正常节目实现类
 * 备注：
 * =======================================================
 */
public class NEU_AllProgramsInfoImpl implements NEU_AllProgramsInfo {
    @Override
    public List<Program> getAllPrograms() {
        String html = CommonUtils.getHtml(Constants.NEU_HDTV_URL_ONLINE, "UTF-8");
        return NEU_RegexAllProgramsHtml.getAllPrograms(html);
    }

}
