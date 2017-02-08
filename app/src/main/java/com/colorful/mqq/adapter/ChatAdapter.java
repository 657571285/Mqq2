package com.colorful.mqq.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.colorful.mqq.R;
import com.colorful.mqq.bean.Message;
import java.util.List;

/**
 * 聊天内容展示适配器
 * @time: 2015-10-29上午9:50:57
 */
public class ChatAdapter extends BaseAdapter {
	public class ChatViewHolder {
		public TextView chatUsername;//消息来源人昵称
		public TextView chatContentTime;//消息时间
		public TextView chatContentText;//文本消息
	}
	/**
	 * 上下文
	 */
	private Activity context;
	/**
	 * 聊天数据
	 */
	private List<Message> list;


	public ChatAdapter(Activity context, List<Message> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Message getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
    public int getItemViewType(int position) {
        return list.get(position).isSend() ? 1 : 0;
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    public void update(Message message) {
    	int idx = list.indexOf(message);
    	if(idx < 0) {
    		list.add(message);
    	} else {
    		list.set(idx, message);
    	}
    	notifyDataSetChanged();
    }
	
	public void add(Message message) {
		list.add(message);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message message = list.get(position);
			final ChatViewHolder viewHolder;
			if(convertView == null) {
			viewHolder = new ChatViewHolder();
			if(message.isSend()) {//发送出去的消息，自己发送的消息展示在界面右边
				convertView = View.inflate(context, R.layout.chat_message_item_right_layout, null);
			} else {
				convertView = View.inflate(context, R.layout.chat_message_item_left_layout, null);
			}
			viewHolder.chatUsername = (TextView) convertView.findViewById(R.id.tv_chat_msg_username);
			viewHolder.chatContentTime = (TextView) convertView.findViewById(R.id.tv_chat_msg_time);
			viewHolder.chatContentText = (TextView) convertView.findViewById(R.id.tv_chat_msg_content_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChatViewHolder) convertView.getTag();
		}
		
		viewHolder.chatUsername.setText(message.getUsername());
		viewHolder.chatContentTime.setText(message.getDatetime());
		viewHolder.chatContentText.setText(message.getContent());

		return convertView;
	}

}
