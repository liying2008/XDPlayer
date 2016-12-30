package lxy.liying.hdtvneu.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.utils.ProgramUrlUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 20:40
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class NEU_RegexAllProgramsHtml {

    public static List<Program> getAllPrograms(String html) {
        // td >旅游卫视 <br /><a href="newplayer?p=lytv">播放
        // td style="background-color: #F0FFFF;">辽宁体育 <br /><a href="newplayer?p=dlut-lnty">播放
        List<Program> programs = new ArrayList<>(150);
        List<String> list = new ArrayList<>(150);
        // 定义规则
        String regex = "td .+播放";
        //将规则封装成对象
        Pattern p = Pattern.compile(regex);
        //让正则对象和要作用的字符串相关联，获取匹配器对象
        Matcher m = p.matcher(html);

        // 将规则作用到字符串上，并进行符合规则的子串查找
        while (m.find()) {
            list.add(m.group());
        }
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            s = s.replace("td >", "").replace("<br /><a href=\"newplayer?p=", "").replace("\">播放", "")
                    .replace("td style=\"background-color: #F0FFFF;\">", "");
            String[] arr = s.split(" ");
            programs.add(new Program(i, arr[1], arr[0], ProgramUrlUtils.getProgramPathFromInfo(new String[]{"1", arr[1]})));
        }
        return programs;
    }
}
