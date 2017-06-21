package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.Program;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/15 18:44
 * 版本：1.0
 * 描述：获取NEU HDTV所有正常节目的接口
 * 备注：
 * =======================================================
 */
public interface NEU_AllProgramsInfo {
    /** 获取所有正常节目 */
    List<Program> getAllPrograms();
}
