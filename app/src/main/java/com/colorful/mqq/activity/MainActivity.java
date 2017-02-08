package com.colorful.mqq.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.colorful.mqq.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.colorful.mqq.R.id.et_send_content;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.et_to)
    EditText etTo;
    @BindView(R.id.lv_content)
    ListView lvContent;
    @BindView(et_send_content)
    EditText etSendContent;
    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.linaer)
    LinearLayout linaer;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }


    @OnClick(R.id.bt_send)
    public void onClick() {
        String send_content =  etSendContent.getText().toString();
        if (TextUtils.isEmpty(send_content) && send_content.length() == 0) {
            Toast.makeText(this, "请重新输入", Toast.LENGTH_SHORT).show();
        }
    }
}
