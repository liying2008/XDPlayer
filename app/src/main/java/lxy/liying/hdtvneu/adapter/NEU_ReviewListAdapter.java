package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lxy.liying.hdtvneu.activity.M3U8Player;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.ReviewDate;
import lxy.liying.hdtvneu.domain.ReviewProgram;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.ProgramUrlUtils;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/16 21:36
 * 版本：1.0
 * 描述：节目回看列表适配器
 * 备注：
 * =======================================================
 */
public class NEU_ReviewListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private String p;
    private List<ReviewDate> groupDates;
    private List<List<ReviewProgram>> childPrograms;
    /**
     * 构造方法
     */
    public NEU_ReviewListAdapter(Context context) {
        this.context = context;
    }

    public void setReviewData(List<ReviewDate> groupDates, List<List<ReviewProgram>> childPrograms, String p) {
        this.p = p;
        this.groupDates = groupDates;
        this.childPrograms = childPrograms;
    }

    @Override
    public int getGroupCount() {
        return groupDates.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childPrograms.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDates.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPrograms.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater listContainer = LayoutInflater.from(context);
        ExListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ExListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_neu_review_date, null);
            listItemView.tvReviewDate = (TextView) convertView.findViewById(R.id.tvReviewDate);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ExListItemView) convertView.getTag();
        }

        listItemView.tvReviewDate.setText(groupDates.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String string = childPrograms.get(groupPosition).get(childPosition).getName();
        LayoutInflater listContainer = LayoutInflater.from(context);
        ExListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ExListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_neu_review_program, null);
            listItemView.tvReviewProgram = (TextView) convertView.findViewById(R.id.tvReviewProgram);
            listItemView.llReviewProgram = (LinearLayout) convertView.findViewById(R.id.llReviewProgram);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ExListItemView) convertView.getTag();
        }

        listItemView.tvReviewProgram.setText(string);

        final ReviewProgram program = childPrograms.get(groupPosition).get(childPosition);
        App.reviewPrograms = childPrograms.get(groupPosition);
        App.programType = "2";
        App.reviewP = p;
        listItemView.llReviewProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 节目回看事件计数统计
                Intent intent = new Intent(context, M3U8Player.class);
                String path = ProgramUrlUtils.getProgramPathFromInfo(new String[]{"2", program.getTimeStart(), program.getTimeEnd(), p});
                intent.putExtra(Constants.PROGRAM, path);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /** 自定义控件集合 */
    private static class ExListItemView{
        public TextView tvReviewDate;
        public TextView tvReviewProgram;
        public LinearLayout llReviewProgram;
    }
}
