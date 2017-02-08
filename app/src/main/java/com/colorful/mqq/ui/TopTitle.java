package com.colorful.mqq.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.colorful.mqq.R;

/**
 * Created by colorful on 2017/1/5.
 * 每个Activity的头部自定义控件
 * 1.左边返回按钮，可以设置图片和文字
 * 2.当前Activity的标题文字
 *·
 */

public class TopTitle extends RelativeLayout {

    /**
     * 左边返回按钮的部分布局
     */
    private LinearLayout mLeftLayout;
    /**
     * 左边返回按钮的图片
     */
    private ImageView mLeftImageView;
    /**
     * 左边返回按钮的文字
     */
    private TextView mLeftTextView;


    /**
     * 左边返回按钮文字颜色，默认为白色
     */
    private int mLeftTextColor;
    /**
     * 左边返回按钮文字，默认为：返回
     */
    private String mLeftText;
    /**
     * 左边返回按钮文字大小，默认大小：14
     */
    private float mLeftTextSize;
    /**
     * 左边返回图标
     */
    private Drawable mLeftDrawale;
    /**
     * 左边返回部分布局是否显示，默认显示
     */
    private boolean mLeftVisible;
    /**
     * 左边返回部分的文字是否显示，默认显示，当mLeftVisible为false时，该变量不起作用
     */
    private boolean mLeftTextVisible;
    /**
     * 坐标返回图标是否显示，默认显示，当mLeftVisible为false时，该变量不起作用
     */
    private boolean mLeftImageVisible;
    /**
     * 中间的标题文字
     */
    private TextView mTitleView;
    /**
     * 标题文字
     */
    private String mTitleText;
    /**
     * 标题文字颜色，默认为白色
     */
    private int mTitleTextColor;
    /**
     * 标题文字大小，默认大小：14
     */
    private float mTitleTextSize;
    /**
     * 头部整个控件的背景颜色，默认为#18B5EA
     */
    private int mBackgroundColor;
    /**
     * 返回按钮点击事件
     */
    private LeftClickListener mLeftClickListener;
    /**
     * 默认字体大小
     */
    private final int DEFAULT_TEXT_SIZE = 15;
    /**
     * 头部边距
     */
    private final int VIEW_PADDING = 5;




    public TopTitle(Context context) {
        super(context);
    }

    public TopTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public TopTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        float scale = context.getResources().getDisplayMetrics().density;
        TypedArray attArray =  context.obtainStyledAttributes(attrs, R.styleable.TopTitle);

        mBackgroundColor = attArray.getColor(R.styleable.TopTitle_backgroundColor,getResources().getColor(R.color.topTitleBar_default_background));
        mLeftText = attArray.getString(R.styleable.TopTitle_leftText);
        mLeftTextColor = attArray.getColor(R.styleable.TopTitle_leftTextColor,getResources().getColor(R.color.white));
        mLeftTextSize = attArray.getDimension(R.styleable.TopTitle_leftTextSize,DEFAULT_TEXT_SIZE);
        mLeftDrawale = attArray.getDrawable(R.styleable.TopTitle_leftDrawable);
        mLeftVisible = attArray.getBoolean(R.styleable.TopTitle_leftVisible,true);
        mLeftTextVisible = attArray.getBoolean(R.styleable.TopTitle_leftTextVisible,true);
        mLeftImageVisible = attArray.getBoolean(R.styleable.TopTitle_leftImageVisible,true);

        mTitleText = attArray.getString(R.styleable.TopTitle_titleBarText);
        mTitleTextColor = attArray.getColor(R.styleable.TopTitle_titleBarTextColor,getResources().getColor(R.color.white));
        mTitleTextSize = attArray.getDimension(R.styleable.TopTitle_titltBarTextSize,DEFAULT_TEXT_SIZE);

        attArray.recycle();//调用避免重新创建的时候发生错误

        setBackgroundColor(mBackgroundColor);

        mLeftTextView = new TextView(context);
        mLeftImageView = new ImageView(context);
        mTitleView = new TextView(context);

        mLeftLayout  = new LinearLayout(context);
        mLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLeftLayout.setClickable(true);

        //给创建粗来的控件附上属性值
        if(mLeftDrawale!=null){
            mLeftImageView.setBackground(mLeftDrawale);
        }else {
            mLeftImageView.setBackgroundResource(R.drawable.tigerframe_icon_back_btn_selector);
        }

        if ((mLeftText ==null||mLeftText.length()==0)){
            mLeftText = "返回";
        }
        LinearLayout.LayoutParams leftChildParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
        mLeftLayout.addView(mLeftImageView,leftChildParams);
        if(!mLeftImageVisible){
            mLeftImageView.setVisibility(View.GONE);
        }

        mTitleView.setText(mTitleText);
        mTitleView.setTextColor(mTitleTextColor);
        mTitleView.setTextSize(mTitleTextSize);
        mTitleView.setGravity(Gravity.CENTER);

        LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
        //添加左边返回部分到reativelayout
        addView(mLeftLayout,leftParams);

        LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        //添加标题部分到ViewGroup
        addView(mTitleView,titleParams);

        int padding  = (int) (VIEW_PADDING * scale + 0.5f);
        setPadding(padding ,padding ,padding ,padding);
        setLeftVisible(mLeftVisible);

        mLeftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLeftClickListener !=null){
                    mLeftClickListener.onLeftClick();
                }
            }
        });


    }

    /**
     * 设置标题内容
     * @param title
     */
    public void setTitle(String title){
        mTitleView.setText(title);
    }

    /**
     * 设置左边文字
     * @param txt
     */
    public void setLeftText(String txt){
        mLeftTextView.setText(txt);
    }

    /**
     * 设置左边图片
     * @param resId
     */
    public void setLeftDrawable(int resId){
        mLeftImageView.setBackgroundResource(resId);
    }

    public void setLeftClickListener(LeftClickListener listener){
        mLeftClickListener = listener;
    }

    /**
     * 设置左边的返回部分是否显示
     * @param visible true 显示， false 不显示
     */
    public void setLeftVisible(boolean visible) {
        if(visible){
            mLeftLayout.setVisibility(VISIBLE);
        }else {
            mLeftLayout.setVisibility(GONE);
        }

    }


    /**
     * 左边按点击事件
     */
    public interface LeftClickListener {
        public void onLeftClick();
    }

    /**
     * 设置左边的返回图表是否显示，  默认显示。
     * @param visible
     * @return
     */
    public boolean setLeftImageVisible(boolean visible){
        if(visible){
            mLeftImageView.setVisibility(View.VISIBLE);
            return true;
        }else{
            mLeftImageView.setVisibility(View.GONE);
            mLeftTextView.setPadding(5,15,0,0);
            return false;
        }
    }

    /**
     * 设置左边的返回文字是否显示，  默认显示
     * @param visible
     * @return
     */
    public boolean setLeftTextVisible(boolean visible){
        if(visible){
            mLeftTextView.setVisibility(View.VISIBLE);
            return true;
        }else {
            mLeftTextView.setVisibility(View.GONE);
            return false;
        }
    }

}
