package com.lostliz.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author tuoxie
 * 基础Activity
 */
public abstract class BaseActivity extends AppCompatActivity{
	protected BaseActivity mContext; 
	
	protected ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		initView();
		initActionBar();
		initListener();
		
		loadData();
	}
	
	/**
	 * 开始加载数据
	 */
	protected void loadData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 初始化控件
	 */
	protected abstract void initView();
	
	/**
	 * 初始化actionbar
	 */
	protected abstract void initActionBar();
	
	/**
	 * 添加View的listener
	 */
	protected abstract void initListener();
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void showProgressDialog(String msg){
		if(mDialog==null){
			mDialog = new ProgressDialog(mContext);
			mDialog.setCanceledOnTouchOutside(false);
		}
		if(!mDialog.isShowing()){
			mDialog.setMessage(msg);
			mDialog.show();
		}
	}

	public void dismissProgressDialog(){
		if(mDialog==null){
			return;
		}
		if(mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

}
