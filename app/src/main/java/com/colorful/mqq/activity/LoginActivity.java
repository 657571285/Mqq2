package com.colorful.mqq.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.colorful.mqq.R;
import com.colorful.mqq.smack.SmackManager;
import com.colorful.mqq.util.DialogUtil;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by colorful on 2016/12/16.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final int SIGN_IN_SUCCESS = 0;
    private static final int SIGN_IN_WRONG = 1;
    private EditText et_username;
    private EditText et_password;
    private Button bt_sign_in,bt_register;
    private XMPPTCPConnection connection;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SIGN_IN_SUCCESS:
                    Intent intent = new Intent(LoginActivity.this,FriendListActivity.class);
                    startActivity(intent);
                    finish();//关闭activity
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//跳转动画
                    Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                    break;
                case SIGN_IN_WRONG:
                    Toast.makeText(LoginActivity.this,"登陆连接失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity1);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_sign_in = (Button) findViewById(R.id.bt_sign_in);
       // bt_sign_out = (Button) findViewById(R.id.bt_sign_out);
        bt_register = (Button) findViewById(R.id.bt_register);

        MyOperation myOperation = new MyOperation();
        connection = myOperation.getConnection();
        //bt_sign_out.setOnClickListener(this);
        bt_sign_in.setOnClickListener(this);
        bt_register.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sign_in:
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<String,Integer,JSONObject>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        DialogUtil.showProgressDialog(LoginActivity.this,"正在登陆,请稍等·····");
                    }

                    @Override
                    protected JSONObject doInBackground(String... strings) {
                        JSONObject json = new JSONObject();
                        //
                        try {
                            boolean flag  = SmackManager.getInstance().login(strings[0],strings[1]);
                            json.put("flag",flag);
                            if(flag==false){
                                json.put("err","用户名或密码错误");
                            }
                        } catch (Exception e) {
                            try {
                                json.put("flag",false);
                                json.put("err",e.getMessage());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                        return json;
                    }

                    @Override
                    protected void onPostExecute(JSONObject result) {
                        DialogUtil.hideProgressDialog();

                        try {
                            if(!result.getBoolean("flag")){
                                Toast.makeText(LoginActivity.this,result.get("err").toString(),Toast.LENGTH_SHORT).show();
                            }else {
                                Message msg = new Message();
                                msg.what = SIGN_IN_SUCCESS;
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(username,password);

                /**
                 * 通过手动连接服务器，handle的方式在子线程中启动
                 */
                /*final String username = et_username.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //链接服务器
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection.connect();
                            connection.login(username, password);
                            Presence presence = new Presence(Presence.Type.available);
                            presence.setStatus("我已经在线了");
                            connection.sendStanza(presence);
                            if(connection.isConnected()){
                                Message msg = new Message();
                                msg.what = SIGN_IN_SUCCESS;
                                handler.sendMessage(msg);

                            }else {
                                Message msg = new Message();
                                msg.what = SIGN_IN_WRONG;
                                handler.sendMessage(msg);
                            }

                        } catch (SmackException | IOException | XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/
                break;
            /*case R.id.bt_sign_out:
                connection.disconnect();
                Toast.makeText(getApplicationContext(),"断开链接",Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.bt_register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

        }
    }
}
