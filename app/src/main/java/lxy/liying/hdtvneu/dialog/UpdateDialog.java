package lxy.liying.hdtvneu.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.UpdateMsg;
import lxy.liying.hdtvneu.utils.ApkDownloader;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/28 10:12
 * 版本：1.0
 * 描述：发现新版本对话框
 * 备注：
 * =======================================================
 */

public class UpdateDialog extends BaseDialog<UpdateDialog> {
    private Context context;
    private UpdateMsg updateMsg;
    private Button btnCancel, btnUpdate;
    private String downloadUrl;

    public UpdateDialog(Context context, UpdateMsg updateMsg) {
        super(context);
        this.context = context;
        this.updateMsg = updateMsg;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(App.mBasIn);
        dismissAnim(App.mBasOut);
        View contentView = View.inflate(mContext, R.layout.dialog_update, null);
        contentView.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        ((TextView) contentView.findViewById(R.id.tvVersionName)).setText(updateMsg.getVersionName());
        ((TextView) contentView.findViewById(R.id.tvSize)).setText(updateMsg.getSize());
        ((TextView) contentView.findViewById(R.id.tvUpdateDate)).setText(updateMsg.getUpdateDate());
        ((TextView) contentView.findViewById(R.id.tvUpdateLog)).setText(updateMsg.getUpdateLog());

        downloadUrl = updateMsg.getDownloadUrl();
        btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        btnUpdate = (Button) contentView.findViewById(R.id.btnUpdate);
        return contentView;
    }

    @Override
    public void setUiBeforShow() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                AppToast.showToast("正在下载新版本安装包");
                ApkDownloader apkDownloader = new ApkDownloader();
                apkDownloader.downloadFile(context, downloadUrl);
            }
        });
    }
}