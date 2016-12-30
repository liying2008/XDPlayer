package lxy.liying.hdtvneu.utils;


/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/20 20:27
 * 版本：1.0
 * 描述：节目地址工具类
 * 备注：
 * =======================================================
 */
public class ProgramUrlUtils {
    /**
     * 从传入的intent数据中得到节目地址
     *
     * @param programInfo
     * @return 节目地址
     */
    public static String getProgramPathFromInfo(String[] programInfo) {
        if ("1".equals(programInfo[0])) {
            // NEU IPv6视频直播
            String p = programInfo[1];
            if ("".equals(p)) {
                return "";
            }
            return "http://media2.neu6.edu.cn/hls/" + p + ".m3u8";
        } else if ("2".equals(programInfo[0])) {
            // NEU IPv6视频回看
            String p = programInfo[3];
            if ("".equals(p) || "".equals(programInfo[1]) || "".equals(programInfo[2])) {
                return "";
            }
            return "http://media2.neu6.edu.cn/review/program-" + programInfo[1] + "-" + programInfo[2] + "-" + p + ".m3u8";
        } else if ("3".equals(programInfo[0])) {
            // 北京邮电大学IPTV视频直播
            String p = programInfo[1];
            if ("".equals(p)) {
                return "";
            }
            return "http://tv6.byr.cn/hls/" + p + ".m3u8";
        } else if ("4".equals(programInfo[0])) {
            // 清华大学IPTV测试
            String p = programInfo[1];
            if ("".equals(p)) {
                return "";
            } else if ("tsinghuatv".equals(p) || "fhwszwt".equals(p) || "fhwszxt".equals(p)) {
                return "http://iptv.tsinghua.edu.cn/local/" + p + ".m3u8";
            }
            return "http://iptv.tsinghua.edu.cn/hls/" + p + ".m3u8";
        } else {
            return "";
        }
    }
}
