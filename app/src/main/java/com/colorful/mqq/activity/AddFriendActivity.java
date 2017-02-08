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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by colorful on 2016/12/20.
 */
public class AddFriendActivity extends Activity {
    @BindView(R.id.cet_friend_username)
    EditText cetFriendUsername;
    @BindView(R.id.cet_friend_nickname)
    EditText cetFriendNickname;
    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;
    @BindView(R.id.ttb_addfriend_title)
    TopTitle ttbAddfriendTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_layout);
        ButterKnife.bind(this);
        ttbAddfriendTitle.setTitle("添加好友");
        ttbAddfriendTitle.setLeftClickListener(new TopTitle.LeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    @OnClick(R.id.btn_add_friend)
    public void onClick(View v) {
        String username = cetFriendUsername.getText().toString();
        String nickname = cetFriendNickname.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            cetFriendUsername.setError("好友用户名不能为空");
        }
        if (ValueUtil.isEmpty(nickname)) {
            cetFriendNickname.setError("好友昵称不能为空");
        }
        boolean flag = SmackManager.getInstance().addFriend(username, nickname, null);
        if (flag) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            FriendListActivity.isNeedRefresh = true;
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }

    }
}
