package com.colorful.mqq.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.colorful.mqq.R;
import com.colorful.mqq.smack.SmackManager;
import com.colorful.mqq.ui.TopTitle;
import com.colorful.mqq.util.ValueUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by colorful on 2017/1/4.
 */

public class RegisterActivity extends Activity {

    @BindView(R.id.cet_register_username)
    EditText cetRegisterUsername;
    @BindView(R.id.cet_register_nickname)
    EditText cetRegisterNickname;
    @BindView(R.id.cet_register_password)
    EditText cetRegisterPassword;
    @BindView(R.id.cet_register_repassword)
    EditText cetRegisterRepassword;
    @BindView(R.id.btn_register_cancel)
    Button btnRegisterCancel;
    @BindView(R.id.btn_register_ok)
    Button btnRegisterOk;
    @BindView(R.id.ttb_register_title)
    TopTitle ttbRegisterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);
        ButterKnife.bind(this);
        ttbRegisterTitle.setTitle("注册用户");
        ttbRegisterTitle.setLeftClickListener(new TopTitle.LeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    public void onRegisterOk(View v) {
        final String username = cetRegisterUsername.getText().toString();
        final String nickname = cetRegisterNickname.getText().toString();
        String password = cetRegisterPassword.getText().toString();
        final String repassword = cetRegisterRepassword.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            cetRegisterUsername.setError("用户名不能为空");
            return;
        }
        if (ValueUtil.isEmpty(nickname)) {
            cetRegisterNickname.setError("昵称不能为空");
            return;
        }
        if (ValueUtil.isEmpty(password)) {
            cetRegisterPassword.setError("密码不能为空");
            return;
        }
        if (ValueUtil.isEmpty(repassword)) {
            cetRegisterRepassword.setError("密码确认不能为空");
            return;
        }
        if (!password.equals(repassword)) {
            cetRegisterRepassword.setError("两次密码不相同，请重新确认");
            cetRegisterRepassword.setText("");
            return;
        }
        new Thread() {
            public void run() {
                register(username, nickname, repassword);
            }

            ;
        }.start();
    }

    public void register(String username, String nickname, String password) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", nickname);
        final boolean flag = SmackManager.getInstance().registerUser(username, password, attributes);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onRegisterCancel(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
