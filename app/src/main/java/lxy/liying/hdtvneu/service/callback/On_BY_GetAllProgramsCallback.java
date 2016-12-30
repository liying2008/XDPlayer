package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;

/**
 * 从<a href='http://tv.byr.cn/mobile/'>北京邮电大学IPTV</a>获取所有节目的回调接口
 */
public interface On_BY_GetAllProgramsCallback {
    void onGetAllPrograms(List<Program> programsList);
}
