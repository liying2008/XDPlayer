package lxy.liying.hdtvneu.regex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lxy.liying.hdtvneu.domain.ReviewDate;
import lxy.liying.hdtvneu.domain.ReviewList;
import lxy.liying.hdtvneu.domain.ReviewProgram;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/17 19:46
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class NEU_RegexReviewHtml {

    private Date date;
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    public ReviewList getReviewPrograms(String html) {
        /*
        div id="2016-05-16">
        <div id="noon">
        <div id="list_item">00:05 实况录像-2016年国际田联钻石联赛上海站精选</div>
        <div id="list_status"><a href="player-review?timeline=1463328300-1463333700-cctv5hd">回看</a></div>
         */

        List<ReviewDate> groupDates = new ArrayList<>(8);
        List<List<ReviewProgram>> childPrograms = new ArrayList<>(8);
        List<ReviewProgram> reviewPrograms = new ArrayList<>(20);

        List<String> list = new ArrayList<>(160);
        // 定义规则
        String regex1 = "list_item.+div>\\s+.+回看";
        //将规则封装成对象
        Pattern pattern1 = Pattern.compile(regex1);
        //让正则对象和要作用的字符串相关联，获取匹配器对象
        Matcher m1 = pattern1.matcher(html);

        // 将规则作用到字符串上，并进行符合规则的子串查找
        while (m1.find()) {
            list.add(m1.group());
        }

        // 分组用
        String groupName = "";
        for (int i = 0; i < list.size(); i++) {
            /*
            list_item">00:05 实况录像-2016年国际田联钻石联赛上海站精选><a href="player-review?timeline=1463328300-1463333700-cctv5hd">回看
             */
            String s = list.get(i);
            s = s.replace("list_item\">", "").replaceAll("</div>\\s+<div id=\"list_status\">", "")
                    .replace("<a href=\"player-review?", "").replace("\">回看", "");

//             00:05 实况录像-2016年国际田联钻石联赛上海站精选timeline=1463328300-1463333700-cctv5hd
            String[] arr = s.split("timeline=");
            String reviewProgramName = arr[0];  // 节目的名字
            String[] timeline = arr[1].split("-");
            long timeStart = Long.parseLong(timeline[0]);   // 节目开始时间

            String toDate = timeToDate(timeStart);
            if (groupName.equals(toDate)) {
                reviewPrograms.add(new ReviewProgram(reviewProgramName, timeline[0], timeline[1]));
            } else {
                groupDates.add(new ReviewDate(toDate));
                if (reviewPrograms.size() > 0) {
                    childPrograms.add(reviewPrograms);
                }
                reviewPrograms = new ArrayList<>(20);
                reviewPrograms.add(new ReviewProgram(reviewProgramName, timeline[0], timeline[1]));
                groupName = toDate;
            }
        }

        childPrograms.add(reviewPrograms);
        return new ReviewList(groupDates, childPrograms);
    }

    /** 由StartTime获取日期 */
    public String timeToDate(long second) {
        date = new Date(second * 1000L);
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
//        if (w < 0) w = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateStr = format.format(date) + "【"+weekDays[w]+"】";
        return dateStr;
    }
}
