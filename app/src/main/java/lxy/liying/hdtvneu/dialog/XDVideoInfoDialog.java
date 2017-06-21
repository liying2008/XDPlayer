package lxy.liying.hdtvneu.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.CommonUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/10 22:14
 * 版本：1.0
 * 描述：本地视频信息对话框
 * 备注：
 * =======================================================
 */
public class XDVideoInfoDialog extends BaseDialog<XDVideoInfoDialog> {
    private XDVideo video;
    private Button btnOK;

    public XDVideoInfoDialog(Context context, XDVideo video) {
        super(context);
        this.video = video;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(App.mBasIn);
        dismissAnim(App.mBasOut);
        View contentView = View.inflate(mContext, R.layout.dialog_xdvideo_info, null);
        contentView.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        ((TextView) contentView.findViewById(R.id.tvTitle)).setText(video.getTitle());
        ((TextView) contentView.findViewById(R.id.tvData)).setText(video.getData());
        ((TextView) contentView.findViewById(R.id.tvAlbum)).setText(video.getAlbum());
        ((TextView) contentView.findViewById(R.id.tvArtist)).setText(video.getArtist());
        ((TextView) contentView.findViewById(R.id.tvSize)).
                setText(CommonUtils.getSizeString(video.getSize()));
        ((TextView) contentView.findViewById(R.id.tvDuration)).
                setText(CommonUtils.formatTime((int) (video.getDuration() / 1000)));
        ((TextView) contentView.findViewById(R.id.tvMimeType)).setText(video.getMimeType());
        ((TextView) contentView.findViewById(R.id.tvResolution)).setText(video.getResolution());

        btnOK = (Button) contentView.findViewById(R.id.btnOK);
        return contentView;
    }

    @Override
    public void setUiBeforShow() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
