package com.colorful.mqq.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.colorful.mqq.R;
import com.colorful.mqq.adapter.ChatAdapter;
import com.colorful.mqq.smack.SmackManager;
import com.colorful.mqq.ui.Chatkeyboard;
import com.colorful.mqq.ui.TopTitle;
import com.colorful.mqq.util.DateUtil;
import com.colorful.mqq.util.SdCardUtil;
import com.colorful.mqq.util.ValueUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by colorful on 2016/12/20.
 */
public class ChatActivity extends Activity implements Chatkeyboard.ChatKeyboardOperateListener{

    /**
     * 聊天框头部
     */
    @BindView(R.id.chat_title)
    TopTitle chatTitle;
    /**
     * 聊天框内容展示列表
     */
    @BindView(R.id.lv_chat_content)
    ListView lvChatContent;
    /**
     * 发送聊天信息内容
     */
    @BindView(R.id.et_send_content)
    EditText etSendContent;
    /**
     * 发送按钮
     */
    @BindView(R.id.bt_send)
    Button btSend;

    /**
     * 聊天对象用户Jid
     */
    private String friendRosterUser;
    /**
     * 聊天对象昵称
     */
    private String friendNickname;
    /**
     * 聊天窗口对象
     */
    private Chat chat;
    /**
     * 当前自己昵称
     */
    private String currNickname;
    /**
     * 聊天信息发送的对象 全路径
     */
    private String sendUser;
    /**
     * 数据适配
     */
    private ChatAdapter mAdapter;
    /**
     * imageloader加载参数配置
     */
    private DisplayImageOptions options;
    /**
     * 文件存储目录
     */
    private String fileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_layout);
        ButterKnife.bind(this);

        friendRosterUser = getIntent().getStringExtra("user");
        friendNickname = getIntent().getStringExtra("nickname");
        currNickname = SmackManager.getInstance().getAccountName();
        chatTitle.setTitle(friendNickname);
        chatTitle.setLeftClickListener(new TopTitle.LeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        //System.out.println(currNickname);
        String chatJid = SmackManager.getInstance().getChatJidByUser(friendRosterUser);
        //拼接完整的jid文件传输对象
        sendUser = SmackManager.getInstance().getFileTransferJidChatJid(chatJid);
       /* list = new ArrayList<>();*/
        //获取聊天对象管理器，添加监听
        SmackManager.getInstance().getChatManager().addChatListener(chatManagerListener);

        //创建聊天窗口
        chat = SmackManager.getInstance().createChat(chatJid);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)//图片下载是否缓存到sdcard
                .cacheInMemory(true)//图片下载是否缓存到内存
                .bitmapConfig(Bitmap.Config.RGB_565)//图片解码类型，
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.pic_default)
                .showImageOnLoading(R.drawable.pic_default)
        .build();

        fileDir = SdCardUtil.getCacheDir(ChatActivity.this);
        receiveFile();

        List<com.colorful.mqq.bean.Message> list = new ArrayList<>();
        mAdapter = new ChatAdapter(ChatActivity.this, options, list);
        lvChatContent.setAdapter(mAdapter);
    }


    /**
     * 聊天会话管理监听
     */
    private ChatManagerListener chatManagerListener = new ChatManagerListener() {

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    String content = message.getBody();
                    if (content != null) {
                        //接收到消息Message之后进行消息展示处理，这个地方处理所有人的消息
                        com.colorful.mqq.bean.Message msg = new com.colorful.mqq.bean.Message(com.colorful.mqq.bean.Message.MESSAGE_TYPE_TEXT,
                                friendNickname, DateUtil.formatDatetime(new Date()), false);
                        msg.setContent(message.getBody());

                        System.out.println("监听message:" + msg.getContent());
                        handler.obtainMessage(1, msg).sendToTarget();//消息处理
                    }
                }
            });
        }
    };



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    /**
                     * 自定义adapter适配左右布局
                     */
                    mAdapter.add((com.colorful.mqq.bean.Message) msg.obj);
                    break;
                case 2:
                    mAdapter.update((com.colorful.mqq.bean.Message) msg.obj);
                    break;

            }
        }
    };

    /**
     * 发送消息
     * @param message
     */
    @Override
    public void send(final String message) {
        if (ValueUtil.isEmpty(message)){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    chat.sendMessage(message);
                    com.colorful.mqq.bean.Message msg = new com.colorful.mqq.bean.Message(com.colorful.mqq.bean.Message.MESSAGE_TYPE_TEXT,currNickname,DateUtil.formatDatetime(new Date()),true);
                    msg.setContent(message);
                    handler.obtainMessage(1,msg).sendToTarget();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送文件
     * @param file
     * @param type
     */
    public void sendFile(File file,int type){
        OutgoingFileTransfer transfer  = SmackManager.getInstance().getSendFileTransfer(sendUser);
        try {
            transfer.sendFile(file,String.valueOf(type));
            checkTransferStatus(transfer, file, type, true);//检查发送文件的状态
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile(){
        SmackManager.getInstance().addFileTransferListener(new FileTransferListener() {
            @Override
            public void fileTransferRequest(FileTransferRequest request) {
                IncomingFileTransfer transfer = request.accept();
                try {
                    String type = request.getDescription();
                    File file = new File(fileDir,request.getFileName());
                    transfer.recieveFile(file);
                    checkTransferStatus(transfer,file,Integer.parseInt(type),false);
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**\
     * 检查发送文件，接受文件的状态
     * @param transfer
     * @param file  发送或接收的文件
     * @param type  文件类型： 图片，文本，语音
     * @param isSend  是否为发送
     */
    private void checkTransferStatus(final FileTransfer transfer, File file, int type, boolean isSend) {
        String username = friendNickname;
        if(isSend){
            username = currNickname;
        }
        String name = username;
        final com.colorful.mqq.bean.Message msg = new com.colorful.mqq.bean.Message(type,name,DateUtil.formatDatetime(new Date()),isSend);
        msg.setFilePath(file.getAbsolutePath());
        msg.setLoadState(0);
        new Thread(){
            @Override
            public void run() {
                if(transfer.getProgress() < 1){
                    //传输开始
                    handler.obtainMessage(1,msg).sendToTarget();
                }
                while (!transfer.isDone()){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (FileTransfer.Status.complete.equals(transfer.getStatus())){
                    //传输完成
                    msg.setLoadState(1);
                    handler.obtainMessage(2,msg).sendToTarget();
                }else if (FileTransfer.Status.cancelled.equals(transfer.getStatus())){
                    //传输取消
                    msg.setLoadState(-1);
                    handler.obtainMessage(2,msg).sendToTarget();
                }else if (FileTransfer.Status.error.equals(transfer.getStatus())){
                    //传输错误
                    msg.setLoadState(-1);
                    handler.obtainMessage(2,msg).sendToTarget();
                }else if (FileTransfer.Status.refused.equals(transfer.getStatus())){
                    //传输拒绝
                    msg.setLoadState(-1);
                    handler.obtainMessage(2,msg).sendToTarget();
                }

            };
        }.start();

    }
    protected void onDestroy(){
        super.onDestroy();
        SmackManager.getInstance().getChatManager().removeChatListener(chatManagerListener);
    }

    @Override
    public void sendVoice(File audioFile) {

    }

    @Override
    public void recordStart() {

    }

    @Override
    public void functionClick(int index) {

    }


    /**
     * 发送按钮点击事件
     *//*
    @OnClick(R.id.bt_send)
    public void onClick() {
        final String message = etSendContent.getText().toString();
        etSendContent.setText("");
        if (TextUtils.isEmpty(message)) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    chat.sendMessage(message);
                    com.colorful.mqq.bean.Message msg = new com.colorful.mqq.bean.Message(com.colorful.mqq.bean.Message.MESSAGE_TYPE_TEXT, currNickname, DateUtil.formatDatetime(new Date()), true);
                    msg.setContent(message);
                    System.out.println("发送" + msg.getContent() + msg.getDatetime() + msg.getType());
                    handler.obtainMessage(1, msg).sendToTarget();

                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }*/
}
