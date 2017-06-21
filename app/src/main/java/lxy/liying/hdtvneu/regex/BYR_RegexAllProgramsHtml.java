package lxy.liying.hdtvneu.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.utils.ProgramUrlUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/14 20:40
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class BYR_RegexAllProgramsHtml {

    public static List<Program> getAllPrograms(String html) {
        // <a href="//tv6.byr.cn/hls/cctv5hd.m3u8" target="_blank" class="btn btn-block btn-primary">CCTV-5高清</a>
        List<Program> programs = new ArrayList<>(150);

        List<String> list = new ArrayList<>(150);
        // 定义规则
        String regex = "href.+primary.+a";
        //将规则封装成对象
        Pattern pattern = Pattern.compile(regex);
        //让正则对象和要作用的字符串相关联，获取匹配器对象
        Matcher m = pattern.matcher(html);

        // 将规则作用到字符串上，并进行符合规则的子串查找
        while (m.find()) {
            list.add(m.group());
        }

        // href="//tv6.byr.cn/hls/cctv5hd.m3u8" target="_blank" class="btn btn-block btn-primary">CCTV-5高清</a
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            s = s.replace("href=\"//tv6.byr.cn/hls/", "").replace(".m3u8\" ", "").replace("=\"_blank\" class=\"btn btn-block btn-primary\">", "").replace("</a", "");
            // zjhdtarget浙江卫视高清
            String[] arr = s.split("target");
            programs.add(new Program(i, arr[0], arr[1], ProgramUrlUtils.getProgramPathFromInfo(new String[]{"3", arr[0]})));
        }
        return programs;
    }
}
