package com.colorful.mqq.ui;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.colorful.mqq.R;
import com.colorful.mqq.inject.InjectHelper;
import com.colorful.mqq.util.ValueUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by colorful on 2017/1/9.
 */

public class Chatkeyboard extends RelativeLayout
        implements SoftKeyboardStateHelper.SoftKeyboardStateListener {


    private Context context;
    /**
     * 软键盘监听助手
     */
    private SoftKeyboardStateHelper mKeyboardHelper;
    /**
     * 发送表情开关按钮
     */
    @BindView(R.id.cb_chat_face_toolbox)
    CheckBox cbChatFaceToolbox;
    /**
     * 发送更多消息类型按钮
     */
    @BindView(R.id.cb_chat_more_toolbox)
    CheckBox cbChatMoreToolbox;
    /**
     * 发送文本消息按钮
     */
    @BindView(R.id.btn_chat_send_txt)
    Button btnChatSendTxt;
    /**
     * 发送语音消息按钮
     */
    @BindView(R.id.cb_chat_send_voice)
    CheckBox cbChatSendVoice;
    /**
     * 当前是否为正在录音界面
     */
    private boolean isRecordVoice = false;

    @BindView(R.id.ll_chat_send_toolbox)
    LinearLayout llChatSendToolbox;
    /**
     * 文本消息输入框
     */
    @BindView(R.id.et_chat_message_toolbox)
    EditText etChatMessageToolbox;


    @BindView(R.id.tv_chat_record_txt)
    TextView tvChatRecordTxt;
    /**
     * 录音控件
     */
    @BindView(R.id.psv_record)
    PressSpeakView psvRecord;
    /**
     * 录音界面
     */
    @BindView(R.id.rl_chat_record_voice_layout)
    RelativeLayout rlChatRecordVoiceLayout;
    /**
     * 选择图片
     */
    @BindView(R.id.ll_chat_more_images)
    LinearLayout llChatMoreImages;
    /**
     * 拍照
     */
    @BindView(R.id.ll_chat_more_photo)
    LinearLayout llChatMorePhoto;
    /**
     * 选择更多聊天功能布局
     */
    @BindView(R.id.rl_chat_keyboard_more)
    RelativeLayout rlChatKeyboardMore;
    /**
     * 聊天操作监听
     */
    private ChatKeyboardOperateListener listener;

    public Chatkeyboard(Context context) {
        super(context);
    }

    public Chatkeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Chatkeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        this.context = context;
        View root = View.inflate(context, R.layout.chat_key_board_layout, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initKeyboardHelper();
        this.initWidget();
    }

    private void initKeyboardHelper() {
        mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) getContext()).getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
    }

    /**
     * 初始化相关控件
     */
    private void initWidget() {
        InjectHelper.inject(this, this);
        psvRecord.setRecordListener(mRecordListener);
        etChatMessageToolbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etChatMessageToolbox.getText().toString();
                if (ValueUtil.isEmpty(text)) {
                    btnChatSendTxt.setVisibility(GONE);
                    cbChatSendVoice.setVisibility(VISIBLE);
                } else {
                    btnChatSendTxt.setVisibility(VISIBLE);
                    cbChatSendVoice.setVisibility(GONE);
                }

            }
        });
    }

    /**
     * 发送文本消息
     *
     * @param v
     */
    public void sendTxtCilck(View v) {
        if (listener != null) {
            String msg = etChatMessageToolbox.getText().toString();
            if (msg == null || msg.length() == 0) {
                return;
            }
            listener.send(msg);
            etChatMessageToolbox.setText("");
        }
    }

    /**
     * 发送语音消息
     *
     * @param v
     */
    public void sendVoiceClick(View v) {
        isRecordVoice = !isRecordVoice;
        changeLayout(1, isRecordVoice);
    }

    /**
     * Chat more功能是否选中
     */
    private boolean isChatMoreClick = false;

    /**
     * 展示more功能按钮选择
     *
     * @param v
     */
    public void chatMoreClick(View v) {
        isChatMoreClick = !isChatMoreClick;
        changeLayout(2, isChatMoreClick);
    }

    /**
     * 更多功能
     *
     * @param v
     */
    public void chatMoreItemClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chat_more_images://选择图片
                if (listener != null) {
                    listener.functionClick(1);
                }
                break;
            case R.id.ll_chat_more_photo:
                if (listener != null) {
                    listener.functionClick(2);
                }
                break;
        }
    }

    /**
     * 录音过程中的监听
     */
    private PressSpeakView.RecordListener mRecordListener = new PressSpeakView.RecordListener() {

        @Override
        public void recordFinish(File audioFile) {
            if (listener != null) {
                listener.sendVoice(audioFile);
            }
        }

        public void recordStart() {
            if (listener != null) {
                listener.recordStart();
            }
        }

        ;
    };

    /**
     * 切换布局文件
     *
     * @param funFlag 功能代码
     * @param isShow  是否显示
     */
    private void changeLayout(int funFlag, boolean isShow) {
        if (isShow == false) {
            showKeyboard(context);
//    		mCbSendVoice.setBackgroundResource(R.drawable.chat_keyboard_start_record_voice_bg_selector);
            etChatMessageToolbox.setVisibility(View.VISIBLE);
            rlChatRecordVoiceLayout.setVisibility(View.GONE);
            rlChatKeyboardMore.setVisibility(View.GONE);
            return;
        }
        hideKeyboard(context);
        switch (funFlag) {
            case 1://语音
                //延迟显示，避免某一时刻视图与键盘同时显示
                postDelayed(new Runnable() {
                    public void run() {
//						mCbSendVoice.setBackgroundResource(R.drawable.chat_keyboard_start_txt_bg_selector);
                        etChatMessageToolbox.setVisibility(View.INVISIBLE);
                        rlChatRecordVoiceLayout.setVisibility(View.VISIBLE);
                        rlChatKeyboardMore.setVisibility(View.GONE);
                    }
                }, 50);
                break;
            case 2://more
                //延迟显示，避免某一时刻视图与键盘同时显示
                postDelayed(new Runnable() {
                    public void run() {
//						mCbSendVoice.setBackgroundResource(R.drawable.chat_keyboard_start_record_voice_bg_selector);
                        etChatMessageToolbox.setVisibility(View.VISIBLE);
                        rlChatRecordVoiceLayout.setVisibility(View.GONE);
                        rlChatKeyboardMore.setVisibility(View.VISIBLE);
                    }
                }, 50);
                break;
        }
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {

    }

    @Override
    public void onSoftKeyboardClosed() {

    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
                    .getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public ChatKeyboardOperateListener getChatKeyboardOperateListener() {
        return listener;
    }

    public void setChatKeyboardOperateListener(ChatKeyboardOperateListener listener) {
        this.listener = listener;
    }


    /**
     * 聊天操作监听接口
     */
    public interface ChatKeyboardOperateListener {
        /**
         * 发送文本消息接口
         *
         * @param message
         */
        public void send(String message);

        /**
         * 录音完成，发送语音文件，UI线程
         *
         * @param audioFile
         */
        public void sendVoice(File audioFile);

        /**
         * 开始录音，UI线程
         */
        public void recordStart();

        /**
         * 点击触发的功能
         *
         * @param index 从1开始，按照展示顺序进行索引
         */
        public void functionClick(int index);
    }
}
