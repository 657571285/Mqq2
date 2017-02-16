package com.colorful.mqq.adapter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorful.mqq.R;
import com.colorful.mqq.bean.Message;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
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
		public ImageView chatUserAvatar;//用户头像
		public ImageView chatContentImage;//图片消息
		public ImageView chatContentVoice;//语音消息
		public ImageView chatContentLoading;//接受文件时的进度条
	}
	/**
	 * 上下文
	 */
	private Activity context;
	/**
	 * imageloader图片加载配置
	 */
	private DisplayImageOptions options;
	/**
	 * 聊天数据
	 */
	private List<Message> list;
	/**
	 * 音频播放器
	 */
	private MediaPlayer mediaPlayer;


	public ChatAdapter(Activity context, DisplayImageOptions options, List<Message> list) {
		super();
		this.context = context;
		this.options = options;
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
			viewHolder.chatUserAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_avatar);
			viewHolder.chatContentImage = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_image);
			viewHolder.chatContentVoice = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_voice);
			viewHolder.chatContentLoading = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_loading);
				convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChatViewHolder) convertView.getTag();
		}
		
		viewHolder.chatUsername.setText(message.getUsername());
		viewHolder.chatContentTime.setText(message.getDatetime());
		setMessageViewVisible(message.getType(),viewHolder);
		if(message.getType() == Message.MESSAGE_TYPE_TEXT){//获取的类型是文本消息
			viewHolder.chatContentText.setText(message.getContent());
		}else if (message.getType()==Message.MESSAGE_TYPE_IMAGE){//获取的类型是图片消息
			String url = "file://"+message.getFilePath();
			ImageLoader.getInstance().displayImage(url,viewHolder.chatContentImage,options,new SimpleImageLoadingListener());
			showLoading(viewHolder,message);
		}else if (message.getType()==Message.MESSAGE_TYPE_VOICE){//获取的类型是语音消息
			viewHolder.chatContentVoice.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					palyVoice(viewHolder.chatContentVoice,message);
				}
			});
		}

		return convertView;
	}

	private void palyVoice(final ImageView iv, final Message message) {
		if(message.isSend()){
			iv.setBackgroundResource(R.drawable.anim_chat_voice_right);
		}else {
			iv.setBackgroundResource(R.drawable.anim_chat_voice_left);
		}
		final AnimationDrawable animationDrable = (AnimationDrawable) iv.getBackground();
		iv.post(new Runnable() {
			@Override
			public void run() {
				animationDrable.start();
			}
		});
		if (mediaPlayer == null || !mediaPlayer.isPlaying()){//状态：未播放。 点击播放，再次点击停止播放
			//开始播放录音
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					animationDrable.stop();
					//恢复语音标志信息图标
					if (message.isSend()){
						iv.setBackgroundResource(R.drawable.gxu);
					}else {
						iv.setBackgroundResource(R.drawable.gxx);
					}
				}
			});

			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(message.getFilePath());
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			animationDrable.stop();
			//恢复语音标志信息图标
			if (message.isSend()){
				iv.setBackgroundResource(R.drawable.gxu);
			}else {
				iv.setBackgroundResource(R.drawable.gxx);
			}
			if(mediaPlayer!=null){
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	private void showLoading(ChatViewHolder viewHolder, Message message) {
		switch (message.getLoadState()){
			case 0://加载开始
				viewHolder.chatContentLoading.setBackgroundResource(R.drawable.chat_file_content_loading_anim);
				final AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.chatContentLoading.getBackground();
				viewHolder.chatContentLoading.post(new Runnable() {
					@Override
					public void run() {
						animationDrawable.start();
					}
				});
				viewHolder.chatContentLoading.setVisibility(View.VISIBLE);
				break;
			case 1:
				viewHolder.chatContentLoading.setVisibility(View.GONE);
				break;
			case -1:
				viewHolder.chatContentLoading.setBackgroundResource(R.drawable.load_fail);
				break;

		}
	}

	/**
	 * 根据消息类型展示对应的消息
	 * @param type
	 * @param viewHolder
     */
	private void setMessageViewVisible(int type,ChatViewHolder viewHolder) {
		if(type == Message.MESSAGE_TYPE_TEXT){
			//文本消息
			viewHolder.chatContentText.setVisibility(View.VISIBLE);
			viewHolder.chatContentImage.setVisibility(View.GONE);
			viewHolder.chatContentVoice.setVisibility(View.GONE);
		}else if(type == Message.MESSAGE_TYPE_IMAGE){
			viewHolder.chatContentText.setVisibility(View.GONE);
			viewHolder.chatContentImage.setVisibility(View.VISIBLE);
			viewHolder.chatContentVoice.setVisibility(View.GONE);
		}else if (type == Message.MESSAGE_TYPE_VOICE){
			viewHolder.chatContentText.setVisibility(View.GONE);
			viewHolder.chatContentImage.setVisibility(View.GONE);
			viewHolder.chatContentVoice.setVisibility(View.VISIBLE);
		}
	}

}
