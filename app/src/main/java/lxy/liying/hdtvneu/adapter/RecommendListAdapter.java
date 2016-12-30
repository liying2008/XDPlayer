package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.RecommendActivity;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/8 18:39
 * 版本：1.0
 * 描述：应用推荐列表适配器
 * 备注：
 * =======================================================
 */
public class RecommendListAdapter extends BaseAdapter {
    // 图标数组
    private int[] icons;
    // 软件名称数组
    private int[] name;
    // 软件描述数组
    private int[] desc;

    private Context context;

    public RecommendListAdapter(Context context, int[] icons, int[] name, int[] desc) {
        this.context = context;
        this.icons = icons;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater listContainer = LayoutInflater.from(context);
        ListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_recommend, null);
            listItemView.ivRecommendIcon = (ImageView) convertView.findViewById(R.id.ivRecommendIcon);
            listItemView.tvRecommendName = (TextView) convertView.findViewById(R.id.tvRecommendName);
            listItemView.tvRecommendDesc = (TextView) convertView.findViewById(R.id.tvRecommendDesc);
            listItemView.btnDownload = (Button) convertView.findViewById(R.id.btnDownload);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.ivRecommendIcon.setImageResource(icons[position]);
        listItemView.tvRecommendName.setText(name[position]);
        listItemView.tvRecommendDesc.setText(desc[position]);
        listItemView.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendActivity.getInstance().goToDownload(RecommendActivity.packageName[position]);
            }
        });
        return convertView;
    }

    /**
     * 自定义控件集合
     */
    private static class ListItemView {
        public ImageView ivRecommendIcon;
        public TextView tvRecommendName, tvRecommendDesc;
        public Button btnDownload;
    }
}
