package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;

/**
 * 从<a href='http://iptv.tsinghua.edu.cn/'>清华大学IPTV测试</a>获取所有节目的回调接口
 */
public interface On_QH_GetAllProgramsCallback {
    void onGetAllPrograms(List<Program> programsList);
}
