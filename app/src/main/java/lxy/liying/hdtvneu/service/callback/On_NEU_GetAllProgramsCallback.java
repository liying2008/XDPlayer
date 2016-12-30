package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;

/**
 * 从<a href='http://hdtv.neu6.edu.cn/?hideerror'>东北大学IPv6视频直播测试站</a>获取所有正常节目的回调接口
 */
public interface On_NEU_GetAllProgramsCallback {
    void onGetAllPrograms(List<Program> programsList);
}
