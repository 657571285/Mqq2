package com.colorful.mqq.activity;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by colorful on 2016/12/19.
 */

public class MyOperation {

    private XMPPTCPConnection connection;

    /**
     * 连接服务器
     * @return
     */
    public  XMPPTCPConnection getConnection() {
        String server = "192.168.168.104";
        int port = 5222;
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(server);
        builder.setHost(server);//设置ip地址
        builder.setPort(port);//设置端口
        builder.setCompressionEnabled(false);//是否开启压缩模式
        builder.setDebuggerEnabled(true);//是否开启调试模式
        builder.setSendPresence(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
        return connection;
    }

}
