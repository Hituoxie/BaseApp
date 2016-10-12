package com.lostliz.common.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Util {
	private static final String TAG = "Util";

	/**
	 * MD5加密
	 * 
	 * @param content
	 * @return
	 */
	public static String getMD5(String content) {
		try {
			// 使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
			byte[] btInput = content.getBytes();
			// 获得指定摘要算法的 MessageDigest对象，此处为MD5
			// MessageDigest类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
			// 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// MD5 Message Digest from SUN, <initialized>
			// MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
			mdInst.update(btInput);
			// MD5 Message Digest from SUN, <in progress>
			// 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
			byte[] res = mdInst.digest();

			return String
					.format("%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
							res[0], res[1], res[2], res[3], res[4], res[5],
							res[6], res[7], res[8], res[9], res[10], res[11],
							res[12], res[13], res[14], res[15]);
		} catch (Exception e) {
			Log.e(TAG, "getMD5()#exception", e);
			return null;
		}
	}

	//是否有SD卡
	public static boolean existSDCard(){
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;  
		} else  
			return false;  
	}

	//写文件
	public static void writeFileSdcard(String fileName, String message){

		try {  

			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);  

			FileOutputStream fout = new FileOutputStream(fileName);  

			byte[] bytes = message.getBytes();  

			fout.write(bytes);  

			fout.close();  

		}  

		catch (Exception e) {  

			e.printStackTrace();  

		}  
	}

	// 读文件
	public static String readSDFile(File file) {
		String res = null;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int length = 0;
		try {
			length = fis.available();
			byte[] buffer = new byte[length];
			fis.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public static void deleteFile(File file) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				}
				// 如果它是一个目录
				else if (file.isDirectory()) {
					// 声明目录下所有的文件 files[];
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
						deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
					}
				}
				//file.delete();
			}
		}
	}

	public static String getAppVersion(Context context) {
		String version = "";
		try {
			version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	/** 去除引号 */
	public static String removeQuotationMark(String text) {
		if (!TextUtils.isEmpty(text))
			text = text.replace("\"", "");
		return text;
	}

	public static int calculateSignalLevel(int rssi, int numLevels) {

		int MIN_RSSI = -100;
		int MAX_RSSI = -55;
		int levels = 101;

		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return levels - 1;
		} else {
			float inputRange = (MAX_RSSI - MIN_RSSI);
			float outputRange = (levels - 1);
			return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
		}
	}

	/**
	 * 验证是否为手机号
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles)
	{
		if(TextUtils.isEmpty(mobiles))
			return false;
		Pattern p = Pattern.compile("^1[3,4,5,7,8][0-9]\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * @Author: qyy
	 * @CreteDate: 2015年1月8日 下午2:18:34
	 * @Title: 
	 * @Description: 验证手机号码是否为17号段的虚拟运营商号码
	 * @Param:
	 * @ModifiedBy:
	 * @param mobile
	 * @return
	 */
	public static boolean isVirtualMobileNo(String mobile)
	{
		if(TextUtils.isEmpty(mobile))
			return false;
		Pattern p = Pattern.compile("^17[0-9]\\d{8}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	/**
	 * @Author: will.gu
	 * @CreteDate: 2015-1-9 下午3:41:11
	 * @Title: 
	 * @Description: 验证字符是否为中文
	 * @Param:
	 * @ModifiedBy:
	 * @param name
	 * @return
	 */
	public static boolean isChineseNo(String name){

		Pattern pa = Pattern.compile( "^[\u4e00-\u9fa5]*$");
		Matcher m = pa.matcher(name);
		return m.find();
	}

	/**
	 * @Author: will.gu
	 * @CreteDate: 2015-1-9 下午3:57:00
	 * @Title: 
	 * @Description: 验证是否是email地址
	 * @Param:
	 * @ModifiedBy:
	 * @param email
	 * @return
	 */
	public static boolean isEmailNo(String email){

		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/** 
	 * Get the value of the data column for this Uri. This is useful for 
	 * MediaStore Uris, and other file-based ContentProviders. 
	 * 
	 * @param context The context. 
	 * @param uri The Uri to query. 
	 * @param selection (Optional) Filter used in the query. 
	 * @param selectionArgs (Optional) Selection arguments used in the query. 
	 * @return The value of the _data column, which is typically a file path. 
	 */ 
	public static String getDataColumn(Context context, Uri uri, String selection,  
			String[] selectionArgs) {  

		Cursor cursor = null;  
		final String column = "_data";  
		final String[] projection = {  
				column  
		};  

		try {  
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
					null);  
			if (cursor != null && cursor.moveToFirst()) {  
				final int index = cursor.getColumnIndexOrThrow(column);  
				return cursor.getString(index);  
			}  
		} finally {  
			if (cursor != null)  
				cursor.close();  
		}  
		return null;  
	}  

	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
		return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  

	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  

	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
		return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  

	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}

	//以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...  
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {  

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  

		// DocumentProvider  
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {  
			// ExternalStorageProvider  
			if (isExternalStorageDocument(uri)) {  
				final String docId = DocumentsContract.getDocumentId(uri);  
				final String[] split = docId.split(":");  
				final String type = split[0];  

				if ("primary".equalsIgnoreCase(type)) {  
					return Environment.getExternalStorageDirectory() + "/" + split[1];  
				}  

			}  
			// DownloadsProvider  
			else if (isDownloadsDocument(uri)) {  
				final String id = DocumentsContract.getDocumentId(uri);  
				final Uri contentUri = ContentUris.withAppendedId(  
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  

				return getDataColumn(context, contentUri, null, null);  
			}  
			// MediaProvider  
			else if (isMediaDocument(uri)) {  
				final String docId = DocumentsContract.getDocumentId(uri);  
				final String[] split = docId.split(":");  
				final String type = split[0];  

				Uri contentUri = null;  
				if ("image".equals(type)) {  
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
				} else if ("video".equals(type)) {  
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
				} else if ("audio".equals(type)) {  
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
				}  

				final String selection = "_id=?";  
				final String[] selectionArgs = new String[] {  
						split[1]  
				};  

				return getDataColumn(context, contentUri, selection, selectionArgs);  
			}  
		}  
		// MediaStore (and general)  
		else if ("content".equalsIgnoreCase(uri.getScheme())) {  
			// Return the remote address  
			if (isGooglePhotosUri(uri))  
				return uri.getLastPathSegment();  

			return getDataColumn(context, uri, null, null);  
		}  
		// File  
		else if ("file".equalsIgnoreCase(uri.getScheme())) {  
			return uri.getPath();  
		}  

		return null;  
	}  

	// 校验Tag Alias 只能是数字,英文字母和中文
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	/**
	 * @Author: qyy
	 * @CreteDate: 2015年1月13日 下午2:35:17
	 * @Title: 
	 * @Description: 去除“-”符号
	 * @Param:
	 * @ModifiedBy:
	 * @param s
	 * @return
	 */
	public static String removeMiddleLine(String s) {
		if (!TextUtils.isEmpty(s) && s.contains("-")) {
			return s.replace("-", "");
		}
		return null;
	}

	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {
				if (!isChinese(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * @Author: qyy
	 * @CreteDate: 2015年1月29日 下午5:17:38
	 * @Title: 
	 * @Description:
	 * @Param:
	 * @ModifiedBy:
	 * @param context
	 */
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * @Author: qyy
	 * @CreteDate: 2015年3月4日 下午6:46:34
	 * @Title: 
	 * @Description: 检测某个应用是否在栈顶
	 * @Param:
	 * @ModifiedBy:
	 * @param am
	 * @param packageName
	 * @return
	 */
	public static boolean isAppOnFor(ActivityManager am, String packageName) {
		boolean result = false;
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
				if (runningAppProcessInfo.processName.equals(packageName)) {
					int status = runningAppProcessInfo.importance;
					if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
							|| status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年4月11日 下午3:43:08
	 * @Title: 
	 * @Description: 添加list去除重复item
	 * @ModifiedBy:
	 * @param srcList
	 * @param addList 后添加的list
	 */
	public static <T> void listAddNoRepeat(List<T> srcList,List<T> addList){
		Set<T> set = new HashSet<T>(srcList);

		for(T t : addList){
			if(set.add(t)){
				srcList.add(t);
			}
		}
		set.clear();
		set = null;
	}

	/**
	 * @Author: qinyuanyuan
	 * @CreteDate: 2015年4月9日 下午4:16:58
	 * @Title: 
	 * @Description: 格式化距离(大于1KM显示单位为km，小于1km显示m)
	 * @ModifiedBy:
	 * @param distance
	 * @return
	 */
	public static String formatDistance(int distance) {
		DecimalFormat formatter = new DecimalFormat("####");
		if (distance >= 1000) {
			float di = (float) (distance / 1000.0);
			return formatter.format(di) + "km";
		}
		else
			return (distance <= 50 ? "<50" : distance) + "m";
	}

	/**
	 * @Author: qinyuanyuan
	 * @CreteDate: 2015年6月25日 下午4:39:19
	 * @Title: 
	 * @Description: 判断某个应用是否安装(根据包名判断)
	 * @ModifiedBy:
	 * @param context
	 * @param packageName
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean isInstalled(Context context, String packageName)
	{
		if (packageName == null || "".equals(packageName) || context == null)
			return false;
		try
		{
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * @Author: qinyuanyuan
	 * @CreteDate: 2015年8月29日 下午1:36:57
	 * @Title: 
	 * @Description: 根据包名判断apk目录下是否有此安装包
	 * @ModifiedBy:
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Object[] hasApkPackage(Context context, String packageName) {
		Object[] result = new Object[2];
		result[0] = false;
		result[1] = "s";
		try {
			String appPath = Environment.getExternalStorageDirectory() + File.separator
					+ "WiFiManager" + File.separator + "downloadApk" + File.separator;
			File file = new File(appPath);
			if (file.exists()) {
				File[] files = file.listFiles();
				PackageManager pm = context.getPackageManager(); 
				PackageInfo info2 = null;
				if (files.length > 0) {
					for (File info : files) {
						if (info.isFile() && info.getAbsolutePath().contains(".apk")) {
							info2 = pm.getPackageArchiveInfo(info.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
							if (info2 != null && info2.applicationInfo.packageName.contains(packageName)) {
								result[0] = true;
								result[1] = info.getAbsolutePath();
								break;
							}
						}
					}
				}
			} else
				file.mkdirs();
		} catch (Exception e) {
			Log.e(TAG, "hasBaiduAPK==Exception=" + e);
		}
		return result;
	}


	/**
	 * @Author: kobe
	 * @CreteDate: 2015-7-1 上午10:07:39
	 * @Title: 用来判断服务是否运行.
	 * @Description:用来判断服务是否运行.
	 * @ModifiedBy:
	 * @param context
	 * @param packageName 判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className)
	{
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(40);
		if (!(serviceList.size() > 0))
		{
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++)
		{
			String serviceName = serviceList.get(i).service.getClassName() ;
			//            DLog.i(TAG, "=serviceName=" + serviceName + " ,=classNamee= " + className);
			if (serviceName.equals(className) )
			{
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * @Author: kobe
	 * @CreteDate: 2015-7-1 上午10:07:39
	 * @Title: 判断应用是否正在运行
	 * @Description:判断应用是否正在运行
	 * @ModifiedBy:
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isRunning(Context context, String packageName)
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : list)
		{
			String processName = appProcess.processName;
			//            DLog.d(TAG, "=packageName=" + packageName + " ,=packageName= " + packageName);
			if (processName != null && processName.equals(packageName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年8月4日 下午2:02:12
	 * @Title: 
	 * @Description:应用是否在前台运行
	 * @ModifiedBy:
	 * @param context
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) { 

		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE); 
		String packageName = context.getApplicationContext().getPackageName(); 

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses(); 
		if (appProcesses == null) 
			return false; 

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.  
			if (appProcess.processName.equals(packageName) 
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true; 
			} 
		} 
		return false; 
	} 

	@TargetApi(19)
	public static void setTranslucentStatus(boolean on, Activity context) {
		Window win = context.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	
	public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int temp = 256 * divsum;
		int dv[] = new int[temp];
		for (i = 0; i < temp; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}
	
	public static Bitmap drawable2Bitmap(Drawable drawable) {  
        if (drawable instanceof BitmapDrawable) {  
            return ((BitmapDrawable) drawable).getBitmap();  
        } else if (drawable instanceof NinePatchDrawable) {  
            Bitmap bitmap = Bitmap  
                    .createBitmap(  
                            drawable.getIntrinsicWidth(),  
                            drawable.getIntrinsicHeight(),  
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                    : Bitmap.Config.RGB_565);  
            Canvas canvas = new Canvas(bitmap);  
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                    drawable.getIntrinsicHeight());  
            drawable.draw(canvas);  
            return bitmap;  
        } else {  
            return null;  
        }  
    } 
}
