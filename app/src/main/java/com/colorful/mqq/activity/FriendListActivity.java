package com.colorful.mqq.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.colorful.mqq.R;
import com.colorful.mqq.mypopwin.ActionItem;
import com.colorful.mqq.mypopwin.TitlePopup;
import com.colorful.mqq.smack.SmackManager;
import com.colorful.mqq.ui.TopTitle;
import com.colorful.mqq.util.DialogUtil;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * 好友列表
 * Created by colorful on 2016/12/19.
 */

public class FriendListActivity extends Activity {


    private String[] user;
    /**
     * 好友列表头
     */
    @BindView(R.id.ttb_friendlist_title)
    TopTitle ttbFriendlistTitle;

    /**
     * 好友列表栏
     */
    @BindView(R.id.lv_friend_list)
    ListView lvFriendList;

    private TitlePopup titlePopup;

    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist_layout);
        ButterKnife.bind(this);
        ttbFriendlistTitle.setTitle("好友列表");
        ttbFriendlistTitle.setLeftClickListener(new TopTitle.LeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        getAllFriends();
        inint();
    }


    private void inint() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "发起群聊",
                R.drawable.icon_menu_group));
        titlePopup.addAction(new ActionItem(this, "添加朋友",
                R.drawable.icon_menu_addfriend));
        titlePopup.addAction(new ActionItem(this, "注销",
                R.drawable.icon_menu_sao));
        titlePopup.addAction(new ActionItem(this, "操作",
                R.drawable.abv));
    }

    /**
     * 操作下拉框点击事件
     */
    private TitlePopup.OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {
        @Override
        public void onItemClick(ActionItem item, int position) {
            switch (position) {
                case 0://发起群聊
                    break;
                case 1://添加朋友
                    startActivity(new Intent(FriendListActivity.this, AddFriendActivity.class));
                    break;
                case 2://注销
                    Boolean flag = SmackManager.getInstance().logout();
                    Boolean flag1 = SmackManager.getInstance().disconnect();
                    if (flag && flag1) {
                        Log.i(TAG, "onItemClick: ");
                        startActivity(new Intent(FriendListActivity.this, LoginActivity.class));
                    }
                    break;
                case 3://操作
                    break;
            }
        }
    };


    public static boolean isNeedRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            getAllFriends();
            isNeedRefresh = false;
        }
    }

    /**
     * 好友列表下拉框
     *
     * @param view
     */
    public void showPop(View view) {
        titlePopup.show(findViewById(R.id.iv_show_Pop));
    }


    /**
     * 获取好友列表
     */
    private void getAllFriends() {
        AsyncTask<Void, Void, List<RosterEntry>> task = new AsyncTask<Void, Void, List<RosterEntry>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.showProgressDialog(FriendListActivity.this, "正在查询好友信息，请稍后...");
            }

            @Override
            protected List<RosterEntry> doInBackground(Void... params) {
                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<RosterEntry> list = new ArrayList<>();
                for (RosterEntry friend : friends) {
                    list.add(friend);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<RosterEntry> result) {
                super.onPostExecute(result);
                DialogUtil.hideProgressDialog();
                initListView(result);//初始化listview
            }
        };
        task.execute();

    }

    /**
     * SimpleAdapter适配ListView
     */
    public void initListView(List<RosterEntry> list) {

        if (adapter == null) {
            List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("user", list.get(i).getUser());
                listem.put("name", list.get(i).getName());
                listem.put("status", list.get(i).getStatus());
                listem.put("type", list.get(i).getType());
                listem.put("groups", list.get(i).getGroups());
                listems.add(listem);
            }
            adapter = new SimpleAdapter(this,
                    listems,
                    R.layout.activity_friendlist_item,
                    new String[]{"user"},
                    new int[]{R.id.friendlist_item_user});
            lvFriendList.setAdapter(adapter);
            lvFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lv = (ListView) parent;
                    final HashMap<String, Object> friend = (HashMap<String, Object>) lv.getItemAtPosition(position);
                    DialogUtil.showDialog(FriendListActivity.this, null, "确定要和[" + friend.get("user") + "]聊天？", null, null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startChat(friend);
                        }
                    }, null);
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }

        /**
         * 自定义基础适配器,未完善
         */
       /* if(mAdapter == null) {
            mAdapter = new FriendAdapter(this, R.id.lv_friend_list , list);
            lvFriendList.setAdapter(mAdapter);

           *//* lvFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    final RosterEntry friend = mAdapter.getItem(position);
                    DialogUtil.showDialog(FriendListActivity.this, null, "确定要与[" + friend.getName() + "]聊天吗？", null, null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startChat(friend);
                        }
                    }, null);
                }
            });*//*
        } else {
            mAdapter.resetData(list);
        }*/
    }

    /**
     * simple 开始聊天
     *
     * @param friend
     */
    private void startChat(HashMap<String, Object> friend) {
        Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
        String user = (String) friend.get("user");
        String name = (String) friend.get("name");
        intent.putExtra("user", user);//需要传递的参数
        intent.putExtra("nickname", name);//需要传递的参数
        System.out.println(name);
        startActivity(intent);
    }


    /**
     * 开始聊天,暂时废弃
     *
     * @param friend
     */
    public void startChat(RosterEntry friend) {
        Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
        intent.putExtra("user", friend.getUser());//需要传递的参数
        intent.putExtra("nickname", friend.getName());//需要传递的参数
        startActivity(intent);
    }
}
