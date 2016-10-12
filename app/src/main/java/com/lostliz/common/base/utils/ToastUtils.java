package com.lostliz.common.base.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author Li.z
 * toast管理类
 */
@SuppressLint("ShowToast")
public class ToastUtils {
	private static Toast mToast;
	
	public static void show(final String msg){
		if(TextUtils.isEmpty(msg)){
			return;
		}
		if(mToast == null){
			mToast = Toast.makeText(UIUtils.getContext(), msg, Toast.LENGTH_LONG);
		}
		
		mToast.setText(msg);
		
		UIUtils.post(new Runnable() {
			
			@Override
			public void run() {
				mToast.show();
			}
		});
	}
	
}
