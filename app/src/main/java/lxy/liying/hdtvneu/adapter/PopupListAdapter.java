package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.PopupItem;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.ProgramUrlUtils;
import lxy.liying.hdtvneu.view.BaseMediaController;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/6/22 12:33
 * 版本：1.0
 * 描述：PopupWindow列表适配器
 * 备注：
 * =======================================================
 */
public class PopupListAdapter extends BaseAdapter {
    private Context context;
    private BaseMediaController controller;
    /**
     * 节目列表集合
     */
    private List<PopupItem> programList = new ArrayList<>(150);
    public PopupListAdapter(Context context, BaseMediaController controller) {
        this.context = context;
        this.controller = controller;
        this.programList.clear();
        if (App.programType.equals("2")) {
            // 回看节目类型
            for (int i = 0; i < App.reviewPrograms.size(); i++) {
                this.programList.add(new PopupItem(App.reviewP, App.reviewPrograms.get(i).getName(), App.reviewPrograms.get(i).getTimeStart(), App.reviewPrograms.get(i).getTimeEnd()));
            }
        } else if (App.programType.equals("1") || App.programType.equals("3")) {
            // 1、3：电视直播
            for (Program program : App.programsList) {
                this.programList.add(new PopupItem(program.getPath(), program.getName()));
            }
        } else if (App.programType.equals("5")) {
          // 本地视频列表
            for (XDVideo video : App.xdVideos) {
                this.programList.add(new PopupItem(video.getTitle(), video));
            }
        } else {
            // 其他
        }
    }
    @Override
    public int getCount() {
        return programList.size();
    }

    @Override
    public PopupItem getItem(int position) {
        return programList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final PopupItem popupItem = programList.get(position);
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_program, parent, false);
            viewHolder.llProgram = (LinearLayout) convertView.findViewById(R.id.llPopProgram);
            viewHolder.tvProgram = (TextView) convertView.findViewById(R.id.tvPopProgram);
            convertView.setTag(viewHolder);
        }
        viewHolder.tvProgram.setText(popupItem.getName());
        viewHolder.llProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 换频道
                if ("1".equals(App.programType) || "3".equals(App.programType)) {
                    // IPv6视频直播
                    Uri videoUri = Uri.parse(popupItem.getPath());
                    controller.rePlay(videoUri);
                } else if ("2".equals(App.programType)) {
                    // NEU 视频回看
                    String[] info = {"2", popupItem.getTimeStart(), popupItem.getTimeEnd(), popupItem.getP()};
                    String uri = ProgramUrlUtils.getProgramPathFromInfo(info);
                    Uri videoUri = Uri.parse(uri);
                    controller.rePlay(videoUri);
                } else if ("5".equals(App.programType)) {
                    // 本地视频
                    controller.rePlay(popupItem.getXdVideo());
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvProgram;
        public LinearLayout llProgram;
    }
}
