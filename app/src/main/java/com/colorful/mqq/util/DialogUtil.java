package com.colorful.mqq.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * 对话框工具类
 * Created by colorful on 2016/12/19.
 */

public class DialogUtil {
    /**
     * 等待进度条
     */
    private static ProgressDialog mProgressDialog;

    /**
     * 显示等待进度条
     * @param ctx  上下文
     * @param message 显示等待进度条的等待信息
     */
    public static void  showProgressDialog(Context ctx ,String message){
        if(mProgressDialog==null){
            mProgressDialog  = new ProgressDialog(ctx);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }else {
            mProgressDialog.show();
        }
    }

    /**
     * showEditTextDialog中按钮的监听器
     *
     */
    public static interface DialogButtonClickListener{
        /**
         * 确定按钮响应时该值为修改后的值，取消按钮响应时该值为修改前的默认值
         * @param value
         */
        public void onClickListener(String value);
    }

    /**
     * 隐藏等待进度条
     */
    public static void hideProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 显示一个带EditText的对话框，同事包含确定与取消按钮
     * @param ctx               上下文
     * @param title             标题
     * @param defaultValue      默认值
     * @param okListener        确定按钮监听
     * @param cancelListener    取消按钮监听
     * @param inputType         EditText的输入类型
     */

    public static void showEditTextDialog(Context ctx ,String title,final String defaultValue,
                                          final DialogButtonClickListener okListener, final DialogButtonClickListener cancelListener, int inputType){
        final EditText edt = new EditText(ctx);
        edt.setText(defaultValue);
        edt.setInputType(inputType);
        edt.requestFocus();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ctx).setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(okListener != null){
                            okListener.onClickListener(edt.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(cancelListener != null) {
                                    cancelListener.onClickListener(defaultValue);
                                }
                            }
                        }
                );
        mBuilder.setView(edt);
        mBuilder.show();

    }
    /**
     * 显示一个单选列表的对话框
     * @param ctx
     * @param items					待选的列表数据
     * @param checkedItem			默认选择项的索引
     * @param btnText				按钮文字
     * @param onClickListener		当某个选项被选择后的响应监听，此时对话框不会隐藏，需要点击按钮才会隐藏
     */
    public static void showSingleChoiceDialog(Context ctx, String[] items,int checkedItem,String btnText, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ctx).setTitle("请选择");
        if(!ValueUtil.isEmpty(btnText)) {
            mBuilder.setNegativeButton(btnText, null);
        } else {
            mBuilder.setNegativeButton("确定", null);
        }

        mBuilder.setSingleChoiceItems(items, checkedItem, onClickListener);
        mBuilder.show();
    }

    /**
     * 显示一个单选列表的对话框
     * @param ctx
     * @param items					待选的列表数据
     * @param checkedItems			如果为null则列表默认都不选中，如果该参数不为null则该参数的长度必须与items长度一样
     * @param btnText				按钮文字
     * @param listener				当某个选项被选择后的响应监听，此时对话框不会隐藏，需要点击按钮才会隐藏
     */
    public static void showMultiChoiceDialog(Context ctx, String[] items,boolean[] checkedItems,String btnText, DialogInterface.OnMultiChoiceClickListener listener){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ctx).setTitle("请选择");
        if(!ValueUtil.isEmpty(btnText)) {
            mBuilder.setNegativeButton(btnText, null);
        } else {
            mBuilder.setNegativeButton("确定", null);
        }

        mBuilder.setMultiChoiceItems(items, checkedItems, listener);
        mBuilder.show();
    }

    /**
     * 包含"确定"与"取消"两个按钮的选择对话框
     * @param ctx
     * @param message
     * @param okListener
     */
    public static void showDialog(Context ctx, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("系统提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定", okListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 包含"确定"与"取消"两个按钮的选择对话框
     * @param ctx
     * @param title				对话框标题
     * @param message			对话框提示信息，为空默认为："系统提示"
     * @param positiveTxt		确定按钮对应的文字，为空默认为："确定"
     * @param cancelTxt			取消按钮对象的文字，为空默认为："取消"
     * @param okListener		确定按钮点击事件
     * @param cancelListener	取消按钮点击事件
     */
    public static void showDialog(Context ctx, String title, String message,
                                  String positiveTxt, String cancelTxt, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(ValueUtil.isEmpty(title)) {
            title = "系统提示";
        }
        if(ValueUtil.isEmpty(positiveTxt)) {
            positiveTxt = "确定";
        }
        if(ValueUtil.isEmpty(cancelTxt)) {
            cancelTxt = "取消";
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveTxt, okListener);
        builder.setNegativeButton(cancelTxt, cancelListener);
        builder.create().show();
    }
}
