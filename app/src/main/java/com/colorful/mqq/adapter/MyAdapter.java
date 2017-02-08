package com.colorful.mqq.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.colorful.mqq.R;
import com.colorful.mqq.bean.Message;

import java.util.List;

/**
 * Created by colorful on 2016/12/30.
 */

public class MyAdapter extends BaseAdapter {
    private Activity context;
    private List<Message> list;
    //ViewHolder静态类
    public class ViewHolder extends BaseViewAdapter.AbsViewHolder
    {
        public TextView chatContentTime;
        public TextView chatContentText;
        public TextView chatUsername;
    }

    public MyAdapter(Activity context, List<Message> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Message message) {
        list.add(message);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       final Message message = list.get(position);
        System.out.println("position="+position+"convertView="+convertView+"isSend="+message.isSend());
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder =new ViewHolder();
            if (message.isSend()){
                System.out.println("右");
                convertView = View.inflate(context, R.layout.chat_message_item_right_layout,null);
            }else{
                System.out.println("左");
                convertView = View.inflate(context,R.layout.chat_message_item_left_layout,null);
            }
            viewHolder.chatUsername = (TextView) convertView.findViewById(R.id.tv_chat_msg_username);
            viewHolder.chatContentTime = (TextView) convertView.findViewById(R.id.tv_chat_msg_time);
            viewHolder.chatContentText = (TextView) convertView.findViewById(R.id.tv_chat_msg_content_text);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chatUsername.setText(message.getUsername());
        viewHolder.chatContentTime.setText(message.getDatetime());
        viewHolder.chatContentText.setText(message.getContent());
        return convertView;
    }
}
