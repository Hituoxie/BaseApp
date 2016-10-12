package com.lostliz.common.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author li.zhen
 * @version 
 * @create 2015年3月25日 下午2:18:08
 * @類說明  
 **/
public class ImageUtils {
	public static String TAG = "ImageUtils";

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月25日 下午2:18:59
	 * @Title: 
	 * @Description:
	 * @ModifiedBy:
	 * @param imagePath 
	 * @return
	 */
	public static String image2Base64Str(String imagePath){
		FileInputStream fis =null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(imagePath);
			baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buf = new byte[1024*2];
			while((len=fis.read(buf))!=-1){
				baos.write(buf, 0, len);
			}
			return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(fis);
			closeStream(baos);
		}
		return null;
	}


	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月26日 上午11:19:32
	 * @Title: 
	 * @Description: 精确压缩到指定大小
	 * @ModifiedBy:
	 * @param srcBmp
	 * @param pixelW
	 * @param pixelH
	 * @return
	 */
	public static Bitmap compressImageBySize(Bitmap srcBmp,int pixelW,int pixelH){
		Bitmap dstBmp = Bitmap.createScaledBitmap(srcBmp, pixelW, pixelH, false);
		if(srcBmp!=dstBmp){
			srcBmp.recycle();
		}
		Log.i(TAG, "dstBmp-->"+dstBmp.getHeight()+":"+dstBmp.getWidth());
		return dstBmp;
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月26日 上午11:32:52
	 * @Title: 
	 * @Description: 将目标图片按指定大小压缩后再转Base64 str
	 * @ModifiedBy:
	 * @param imagePath
	 * @param pixelW
	 * @param pixelH
	 * @return
	 */
	public static String image2Base64Str(String imagePath,int pixelW,int pixelH){
		return image2Base64Str(compressImageBySize(BitmapFactory.decodeFile(imagePath), pixelW, pixelH));
	}

	public static String image2Base64Str(Bitmap bitmap){
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null)
			{

				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] data = baos.toByteArray();
				return Base64.encodeToString(data, Base64.DEFAULT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
			if(bitmap!=null&&!bitmap.isRecycled()){
				bitmap.recycle();
			}
		}
		return null;
	}

	public static void closeStream(Closeable io){
		if(io!=null){
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月26日 上午10:58:26
	 * @Title: 
	 * @Description: 等比压缩图片到指定比例大小
	 * @ModifiedBy:
	 * @param imagePath
	 * @param pixelW
	 * @param pixelH
	 * @return
	 */
	public static Bitmap compressImageBySize(String imagePath,int pixelW,int pixelH){
		BitmapFactory.Options newOpts = new BitmapFactory.Options();  
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了  
		newOpts.inJustDecodeBounds = true;  
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath,newOpts);//此时返回bm为空  

		newOpts.inJustDecodeBounds = false;  
		int w = newOpts.outWidth;  
		int h = newOpts.outHeight;   
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
		int be = 1;//be=1表示不缩放  
		if (w >= h && w > pixelW) {
			//如果宽度大的话根据宽度固定大小缩放  
			be = (int) (newOpts.outWidth / pixelW);  
		} else if (w < h && h > pixelH) {
			//如果高度高的话根据宽度固定大小缩放  
			be = (int) (newOpts.outHeight / pixelH);  
		}  
		if (be <= 0)  
			be = 1;  
		newOpts.inSampleSize = be;//设置缩放比例  
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
		bitmap = BitmapFactory.decodeFile(imagePath, newOpts);  

		return bitmap;
	}

	/**
	 * 等比压缩图片到指定比例大小
	 * @param imagePath
	 * @param pixelW
	 * @param pixelH
	 * @return 
	 */
	public static void compressImageAndSave(String imagePath,int pixelW,int pixelH){
		Bitmap bitmap = compressImageBySize(imagePath, pixelW, pixelH);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			saveByteToFile(baos.toByteArray(), imagePath);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
		}
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月31日 上午9:47:42
	 * @Title: 
	 * @Description: 压缩图片
	 * @ModifiedBy:
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image,int maxSize) {  
		if(image == null){
			return null;
		}
		Bitmap bitmap = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		try {

			baos = new ByteArrayOutputStream();  
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
			int options = 100;  
			while ( baos.toByteArray().length > maxSize) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
				baos.reset();//重置baos即清空baos  
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
				options -= 10;//每次都减少10  
			}  
			bais = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
			bitmap = BitmapFactory.decodeStream(bais, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
			closeStream(bais);
		}
		return bitmap;  
	} 

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月31日 上午9:54:57
	 * @Title: 
	 * @Description: 将指定地址的图片压缩并覆盖原图
	 * @ModifiedBy:
	 * @param imagePath 图片地址
	 * @param maxSize 图片最大大小（单位/kb）
	 * @return 是否压缩了
	 */
	public static boolean compressImage(String imagePath,int maxSize){
		if(imagePath == null){
			return false;
		}
		Bitmap bitmap = null;
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;

		try {
			bitmap = BitmapFactory.decodeFile(imagePath);
			baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
			int options = 100;  
			while ( baos.toByteArray().length > (maxSize*1024)) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
				baos.reset();//重置baos即清空baos  
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
				options -= 10;//每次都减少10  
			}  

			fos = new FileOutputStream(imagePath);
			fos.write(baos.toByteArray());
			fos.flush();
			if(bitmap!=null){
				bitmap.recycle();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
			closeStream(fos);
		}

		return false;
	}

	/**
	 * @Author: will.gu
	 * @CreteDate: 2015年8月26日 下午3:43:57
	 * @Title: 
	 * @Description:压缩精确
	 * @ModifiedBy:
	 * @param imagePath
	 * @param maxSize
	 * @return
	 */
	public static boolean compressImagePrecision(String imagePath,int maxSize){
		if(imagePath == null){
			return false;
		}
		Bitmap bitmap = null;
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;

		try {
			bitmap = BitmapFactory.decodeFile(imagePath);
			baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
			int options = 80;  
			System.out.println(TAG+" ="+baos.toByteArray().length);
			while ( baos.toByteArray().length > (maxSize*1024)) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
				baos.reset();//重置baos即清空baos  
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
				System.out.println(TAG+options+"="+baos.toByteArray().length);
				if(options <= 1){
					break;
				}
				options -= 1;

			}  

			fos = new FileOutputStream(imagePath);
			fos.write(baos.toByteArray());
			fos.flush();
			if(bitmap!=null){
				bitmap.recycle();
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
			closeStream(fos);
		}

		return false;
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年3月31日 下午3:15:10
	 * @Title: 
	 * @Description:  讲图片按最大size保存到文件
	 * @ModifiedBy:
	 * @param bitmap
	 * @param maxSize
	 * @param desPath
	 */
	public static void saveBitmapAsMaxSize(Bitmap bitmap,int maxSize,String desPath){
		if(bitmap == null){
			return ;
		}

		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;

		try {
			baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
			int options = 100;  
			while ( baos.toByteArray().length > (maxSize*1024)) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
				baos.reset();//重置baos即清空baos  
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
				options -= 10;//每次都减少10  
			}  

			File file = new File(desPath);
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(baos);
			closeStream(fos);
		}

	}

	public static byte[] getByteFile(File file){
		if(file == null || !file.exists()){
			return null;
		}
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		byte[] buf = new byte[1024*3];
		int len = 0;
		try {
			baos = new ByteArrayOutputStream();
			fis = new FileInputStream(file);
			while((len = fis.read(buf))!=-1){
				baos.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(fis);
		}
		return baos.toByteArray();
	}

	public static byte[] getGzipByteFile(File file){
		if(file == null || !file.exists()){
			return null;
		}
		GZIPOutputStream gos = null;
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		byte[] buf = new byte[1024*3];
		int len = 0;
		try {
			baos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(baos);
			fis = new FileInputStream(file);
			while((len = fis.read(buf))!=-1){
				gos.write(buf, 0, len);
			}
			//gos.finish();   
			gos.flush();
			gos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(fis);
			closeStream(gos);
		}
		return null;
	}

	public static byte[] getGzipByte(byte[] inByte){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(baos);
			gos.write(inByte);
			gos.finish();   
			gos.flush();
			gos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeStream(gos);
		}
		return null;
	}

	public static byte[] decodeGzipByte(byte[] inByte){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = null;
		GZIPInputStream gis = null;
		try {
			bais = new ByteArrayInputStream(inByte);
			gis = new GZIPInputStream(bais);
			byte[] buf = new byte[1024*3];
			int len = 0;
			while ((len = gis.read(buf))!=-1) {
				baos.write(buf, 0, len);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeStream(gis);
		}
		return null;
	}

	public static void saveByteToFile(byte[] bytes , String filePath){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(bytes);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeStream(fos);
		}
	}

	/** 
	 * 以最省内存的方式读取本地资源的图片 
	 *  
	 * @param context 
	 * @param resId 
	 * @return 
	 */  
	@SuppressWarnings("deprecation")
	public static Bitmap readBitmap(Context context, int resId) {  
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = Bitmap.Config.RGB_565;  
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片  
		InputStream is = context.getResources().openRawResource(resId);  
		return BitmapFactory.decodeStream(is, null, opt);  
	}
}
