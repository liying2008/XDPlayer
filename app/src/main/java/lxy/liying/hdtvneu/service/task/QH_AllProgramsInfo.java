package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/15 18:44
 * 版本：1.0
 * 描述：清华大学IPTV测试——获取所有节目的接口
 * 备注：
 * =======================================================
 */
public interface QH_AllProgramsInfo {
    List<Program> getAllPrograms();
}
