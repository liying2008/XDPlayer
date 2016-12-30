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
public class QH_RegexAllProgramsHtml {

    public static List<Program> getAllPrograms(String html) {
        // <a href="player.html?vid=btv1" target="_blank" class="btn btn-block btn-primary">北京卫视</a>
        // <a href="player.html?vid=tsinghuatv&type=local" target="_blank" class="btn btn-block btn-primary">清华电视台</a>
        List<Program> programs = new ArrayList<>(100);
        List<String> list = new ArrayList<>(100);
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

        // href="player.html?vid=btv1" target="_blank" class="btn btn-block btn-primary">北京卫视</a
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            s = s.replace("href=\"player.html?vid=", "").replace("&type=local", "").replace("\" ta", "").replace("=\"_blank\" class=\"btn btn-block btn-primary\">", "").replace("</a", "");
            // btv1rget北京卫视
            String[] arr = s.split("rget");
            programs.add(new Program(i, arr[0], arr[1], ProgramUrlUtils.getProgramPathFromInfo(new String[]{"4", arr[0]})));
        }
        return programs;
    }
}
