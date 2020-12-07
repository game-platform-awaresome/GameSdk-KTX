package cn.flyfun.ktx.gamesdk.core.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.flyfun.support.DensityUtils;
import cn.flyfun.support.ResUtils;

/**
 * Created by #Suyghur, on 2019/06/29.
 * Description :
 */
public class NoticeDialog extends Dialog {

    private TextView titleView, contentView;
    private Button leftBtn, rightBtn;
    private Window noticeWindow;
    private ImageView noticeClose;
    private WindowManager.LayoutParams noticeLayoutParams;


    public NoticeDialog(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        setContentView(ResUtils.getResId(context, "ffg_notice_dialog", "layout"));

        titleView = findViewById(ResUtils.getResId(context, "ffg_dialog_title", "id"));
        contentView = findViewById(ResUtils.getResId(context, "ffg_dialog_content", "id"));
        noticeClose = findViewById(ResUtils.getResId(context, "ffg_close", "id"));

        leftBtn = findViewById(ResUtils.getResId(context, "ffg_dialog_btn_left", "id"));
        rightBtn = findViewById(ResUtils.getResId(context, "ffg_dialog_btn_right", "id"));

        leftBtn.setVisibility(View.GONE);
        rightBtn.setVisibility(View.GONE);

        noticeClose.setVisibility(View.GONE);

        this.noticeWindow = getWindow();
        this.noticeLayoutParams = this.noticeWindow.getAttributes();
        recalculation(context);
    }

    private void recalculation(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.noticeLayoutParams.width = (int) (DensityUtils.getHeightAndWidth(context)[0] * 0.85);
            this.noticeLayoutParams.height = (int) (DensityUtils.getHeightAndWidth(context)[0] * 0.75);
        } else {
            this.noticeLayoutParams.width = (int) (DensityUtils.getHeightAndWidth(context)[0] * 0.75);
            this.noticeLayoutParams.height = (int) (DensityUtils.getHeightAndWidth(context)[1] * 0.8);
        }
        this.noticeLayoutParams.gravity = Gravity.CENTER;
        this.noticeWindow.setAttributes(this.noticeLayoutParams);
    }


    public void setDialogTitle(String title) {
        this.titleView.setText(title);

    }

    public void showNoticeClose() {
        //noticeClose.setVisibility(View.VISIBLE);
        //设计说 关闭按钮“X”的这个逻辑，不需要了哦，后面放到广告再去做，注释先
        //noticeClose.setVisibility(View.GONE);
    }

    public void hideNoticeClose() {
        noticeClose.setVisibility(View.GONE);
    }

    public void setDialogContent(String content) {
        //Logger.i(" ------ setDialogContent ------ ");
        contentView.setText(Html.fromHtml(content));
//        interceptHyperlink(contentView);
    }

    public void setLeftClickText(String leftClickText) {
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setText(leftClickText);
    }

    public void setRightClickText(String rightClickText) {
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText(rightClickText);
    }

    public void setLeftClickListener(View.OnClickListener listener) {
        leftBtn.setOnClickListener(listener);
    }

    public void setRightClickListener(View.OnClickListener listener) {
        rightBtn.setOnClickListener(listener);
    }

    private void setDialogAction() {
        noticeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


//    private void interceptHyperlink(TextView textView) {
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        CharSequence text = textView.getText();
//        if (text instanceof Spannable) {
//            int end = text.length();
//            Spannable spannable = (Spannable) textView.getText();
//            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
//            if (urlSpans.length == 0) {
//                //Logger.d("urlspans is null");
//                return;
//            }
//            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
//            for (URLSpan uri : urlSpans) {
//                String url = uri.getURL();
//                FuseDialogClickUrlSpan customUrlSpan = new FuseDialogClickUrlSpan(url, noticeOnClickListener);
//                spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
//                        spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            }
//            textView.setText(spannableStringBuilder);
//        }
//    }


//    public interface FuseDialogOnClickListener {
//        /**
//         * @param view
//         * @param type
//         * @param action
//         * @param url      跳转链接
//         * @param url_type 0:内部浏览器/1:外部浏览器/ 2代表不跳转
//         */
//        void onClick(View view, int type, int action, String url, int url_type);
//
//        void onUrlClick(View view, String url);
//    }
//
//    public class FuseDialogClickUrlSpan extends ClickableSpan {
//
//        String url;
//        FuseDialogOnClickListener mListener;
//
//        public FuseDialogClickUrlSpan(String url, FuseDialogOnClickListener listener) {
//            this.url = url;
//            this.mListener = listener;
//        }
//
//        @Override
//        public void onClick(View widget) {
//            mListener.onUrlClick(widget, this.url);
//        }
//    }


}
