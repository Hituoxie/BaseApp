package com.lostliz.common.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import static java.net.Proxy.Type.HTTP;

/**
 * 网络状态类
 * 
 * @author yewei
 * 
 */
public class NetUtil
{

    public final static String NONETWORK = "NONETWORK";

    public final static String NET = "NET";

    public final static String WAP = "WAP";

    public final static String WIFI = "WIFI";

    public static final int NONETWORK_INT = -1;

    public static final int WAP_INT = 0;

    public static final int NET_INT = 1;

    public static final int WIFI_INT = 2;

    private static final String TAG = "NetUtil";
    
    private static final int SDK_22 = 8 ;
    
    private static final int SDK_50 = 21 ;

    /**
     * 获取网络连接方式
     * @param aContext
     * @return
     */
    public static String getNetMode(Context aContext)
    {
        String noteType = NONETWORK;

        ConnectivityManager connManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null)
        {
            if (info.getTypeName().toUpperCase().equals("WIFI"))
            {
                noteType = WIFI;
            }
            else
            {
                if (info.getExtraInfo() != null)
                {
                    if (info.getExtraInfo().toUpperCase().indexOf("WAP") != -1)
                    {
                        noteType = WAP;
                    }
                    else
                    {
                        noteType = NET;
                    }
                }
                else
                {
                    String apninfo = null;
                    Cursor cursor = getDefaultApnCursor(aContext);
                    if (cursor != null && cursor.moveToFirst())
                    {
                        apninfo = cursor
                                .getString(cursor.getColumnIndex("apn"));
                        cursor.close();
                    }
                    if (apninfo != null)
                    {
                        if (apninfo.toUpperCase().indexOf("WAP") != -1)
                        {
                            noteType = WAP;
                        }
                        else
                        {
                            noteType = NET;
                        }
                    }
                    else
                    {
                        noteType = NONETWORK;
                    }
                }
            }
        }
        else
        {
            noteType = NONETWORK;
        }
        return noteType;
    }
    
    /**
     * 获取网络连接方式
     * @param context
     * @return -1 无网络 0 wap网络 1 net网络 2 wifi网络
     */
    public static int getNetType(Context context)
    {
        String noteType = getNetMode(context);
        int notetype = NONETWORK_INT;

        if (noteType.equals(NONETWORK))
        {
            notetype = NONETWORK_INT;
        }
        else if (noteType.equals(WAP))
        {
            notetype = WAP_INT;
        }
        else if (noteType.equals(NET))
        {
            notetype = NET_INT;
        }
        else if (noteType.equals(WIFI))
        {
            notetype = WIFI_INT;
        }
//        DLog.e(TAG ,"判断2G/3G网络是否打开 = " + notetype );
        return notetype;
    }
    
    /**
     * @Author: kobe
     * @CreteDate: 2015-4-20 上午11:25:52
     * @Title: 
     * @Description:检测是否为移动数据网络打开类型
     * @ModifiedBy:
     * @param context
     * @return
     */
    public static boolean isDataNetAviable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean b = false ;
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null)
            {
                b = info.isConnected();
                return b ;
            }
        }
        return b;
    }

    /**
     * 当前网络是否可用
     * 
     * @return
     */
    public static boolean isNetworkActive(Context aContext)
    {
        return !getNetMode(aContext).equals(NONETWORK);
    }

    /**
     * 获取apn的cursor
     * @param aContext
     * @return
     */
    private static Cursor getDefaultApnCursor(Context aContext)
    {
        Cursor mCursor = null;

        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        mCursor = aContext.getContentResolver().query(uri, null, null, null,null);

        return mCursor;
    }


    /**
     * 检查网络是否可用，不分2/3G或者wifi
     * @param c
     * @return
     */
    public static boolean isNetworkAvailable(Context c)
    {
        ConnectivityManager connectivity = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 打开2g/3g/4g网络的开或关
     * @param context
     * @throws Exception
     */
    public static void openMobileDataEnabled(Context context) throws Exception
	{
		if (Build.VERSION.SDK_INT <= SDK_22)
		{
			setMobileDataEnableApi8(context, true);
		}
		else if (Build.VERSION.SDK_INT < SDK_50)
		{
			setMobileDataEnableApi9(context, true);
		}
		else
		{
			// TODO 适配5.0系统的方法
		}
	}
    
    /**
     * @Author: kobe
     * @CreteDate: 2015-4-20 下午1:17:23
     * @Title: 
     * @Description:跳转到系统设置页面
     * @ModifiedBy:
     */
    public static void goToSystemSettingPage(Context context)
    {
        try
        {
            Intent intent=new Intent("android.settings.SETTINGS");
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "跳转到设置界面异常 =" + e);
        }
    }

    /**
     * (普通手机专用)设置2g/3g/4g网络的开或关 需要<uses-permission
     * android:name="android.permission.CHANGE_NETWORK_STATE"/>；
     * http://stackoverflow
     * .com/questions/12535101/how-can-i-turn-off-3g-data-programmatically
     * -on-android
     * @param context
     * @param enabled
     * @throws Exception
     */
    public static void setMobileDataEnabled(Context context, boolean enabled)throws Exception
    {
        if (Build.VERSION.SDK_INT <= SDK_22)
        {
            setMobileDataEnableApi8(context, enabled);
        }
        else
        {
            setMobileDataEnableApi9(context, enabled);
        }
    }

    /**
     * 判断手机是否插有sim卡，只适用于普通手机，不适用于多卡手机
     * @param context
     * @return
     */
    public static boolean isSimInserted(Context context)
    {
        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
            if (tm.getSimState() == TelephonyManager.SIM_STATE_READY)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return false;
    }

    /**
     * (MTK机器专用)设置某sim卡的开启
     * @param context
     * @param simId 0代表关闭数据连接；非0代表启用某id的sim卡的数据连接
     * @throws Exception
     */
    private static void setMobileDataEnableMTK(Context context, long simId) throws Exception
    {
        Intent intent = new Intent();
        intent.putExtra("simid", simId);// MTK PhoneConstants.MULTI_SIM_ID_KEY
        intent.setAction("android.intent.action.DATA_DEFAULT_SIM");// Intent.ACTION_DATA_DEFAULT_SIM_CHANGED
        context.sendBroadcast(intent);
    }

    /**
     * MTK专用，取得当前机器里所有sim卡信息,如果抛出异常代表不是mtk机器。也可用是否抛异常来判断是否mtk手机
     * @param context
     * @return
     * @throws Exception
     */
    private static List<MTKSimInfo> getMTKAllSimId(Context context) throws Exception
    {
        List<MTKSimInfo> result = new ArrayList<MTKSimInfo>();
        Class<?> classTelephony = Class.forName("android.provider.Telephony");// .SIMInfo
        Class<?>[] classSub = classTelephony.getDeclaredClasses();
        boolean isHasSIMInfo = false;
        for (Class<?> siminfoClass : classSub)
        {
            if (siminfoClass.getName().equals("android.provider.Telephony$SIMInfo"))
            {
                isHasSIMInfo = true;
                Method getInsertedSIMList = siminfoClass.getDeclaredMethod("getInsertedSIMList", Context.class);
                getInsertedSIMList.setAccessible(true);
                List<Object> res = (List<Object>) getInsertedSIMList.invoke(context, context);
                Field fSimId = siminfoClass.getField("mSimId");
                Field fDisplayName = siminfoClass.getField("mDisplayName");
                for (Object o : res)
                {
                    long simId = fSimId.getLong(o);
                    String name = (String) fDisplayName.get(o);
                    result.add(new MTKSimInfo(simId, name));
                }
            }
        }
        if (!isHasSIMInfo)
        {
            throw new ClassNotFoundException();
        }
        return result;
    }

    /**
     * MTK平台上，取当前正在使用的sim卡的id
     * @param context
     * @return
     */
    private static long getMTKUsingSimId(Context context)
    {
        long res = 0;
        try
        {
            Class cls = Settings.System.class;
            Field field = cls.getDeclaredField("GPRS_CONNECTION_SIM_SETTING");
            String GPRS_CONNECTION_SIM = (String) field.get(cls);

            field = cls.getDeclaredField("DEFAULT_SIM_NOT_SET");
            long DEFAULT_SIM_NOT_SET = field.getLong(cls);

            res = Settings.System.getLong(context.getContentResolver(), GPRS_CONNECTION_SIM, DEFAULT_SIM_NOT_SET);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 设置移动网络的开关，android2.2及以下版本可用。
     * @param context
     * @param isEnabled
     * @throws Exception
     */
    private static void setMobileDataEnableApi8(Context context,boolean isEnabled) throws Exception
    {
        Method dataConnSwitchmethod;
        Class telephonyManagerClass;
        Object ITelephonyStub;
        Class ITelephonyClass;

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
        Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
        getITelephonyMethod.setAccessible(true);
        ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
        ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

        if (!isEnabled)
        {
            dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
        }
        else
        {
            dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
        }
        dataConnSwitchmethod.setAccessible(true);
        dataConnSwitchmethod.invoke(ITelephonyStub);
    }

    /**
     * 设置移动网络的开关。android2.3以上的使用
     * @param context
     * @param enabled
     * @throws Exception
     */
    private static void setMobileDataEnableApi9(Context context, boolean enabled)throws Exception
    {
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try
        {
            setMobileDataEnabl = cm.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.setAccessible(true);
            setMobileDataEnabl.invoke(cm, enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * 判断当前是否连上WIFI
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return netInfo.isConnected();
    }

    /**
     * @Author: kobe
     * @CreteDate: 2015-4-16 上午10:29:42
     * @Title: 判断移动网络是否开启(适配机型版)
     * @Description:判断移动网络是否开启(适配机型版)
     * @ModifiedBy:
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean  isMobileDataEnabled(Context context) 
    {
    	try
		{
    		if (Build.VERSION.SDK_INT <= SDK_22)
    		{
    			return isMobileDataEnabledApi8(context) ;
    		}
    		else if (Build.VERSION.SDK_INT < SDK_50)
    		{
    			return isMobileDataEnabledApi9(context) ;
    		}
    		else
    		{
    			return checkMobileEnable1(context);
    		}
		}
		catch (Exception e)
		{
			try
			{
				return checkMobileEnable1(context);
			}
			catch (Exception e2)
			{
				return true ;
			}
		}
    }

	public static boolean checkMobileEnable2(Context context)
	{
		return Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
	}

	/**检查数据网络是否可用**/
	public static boolean checkMobileEnable1(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);       
		NetworkInfo info = cm.getActiveNetworkInfo();  
		if(info != null)
		{
			return info.getType() == ConnectivityManager.TYPE_MOBILE ;
		}
		return false;
	}
    
    /**
     * @Author: kobe
     * @CreteDate: 2015-4-20 上午11:18:32
     * @Title: 
     * @Description:检测当前手机系统是否为5.0以上
     * @ModifiedBy:
     * @return
     */
    public static boolean isSDKMoreThan21()
    {
        boolean check = false ;
        try
        {
            if(Build.VERSION.SDK_INT >= SDK_50)
            {
                check = true ;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "===========isSDKMoreThan21()=======exception=====");
        }
        return check;
    }
    
    /**
     * 判断移动网络是否开启，2.3及以上使用此方法
     * @param context
     * @return
     * @throws Exception
     */
    private static boolean isMobileDataEnabledApi9(Context context)throws Exception
    {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method getMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
        getMobileDataEnabledMethod.setAccessible(true);

        Boolean res = (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager);
        return res;
    }

    /**
     * 判断移动网络是否打开，2.2及以下使用这个方法
     * @param context
     * @return
     * @throws Exception
     */
    private static boolean isMobileDataEnabledApi8(Context context)throws Exception
    {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isEnabled = false;
        if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
        {
            isEnabled = true;
        }
        else
        {
            isEnabled = false;
        }
        return isEnabled;
    }

    public static class MTKSimInfo
    {
        public long simId = 0;
        public String name = "";
        public MTKSimInfo(long simId, String name)
        {
            this.simId = simId;
            this.name = name;
        }
    }

    private static boolean isMTKDevice(Context context)
    {
        boolean isMutiSim = false;
        try
        {
            NetUtil.getMTKAllSimId(context);
            isMutiSim = true;
        }
        catch (Exception e)
        {
            isMutiSim = false;
        }
        return isMutiSim;
    }

    private static int getInsertSimCount(Context context)
    {
        int count = 0;
        try
        {
            count = NetUtil.getMTKAllSimId(context).size();
        }
        catch (Exception e)
        {
            if (NetUtil.isSimInserted(context))
            {
                count = 1;
            }
            else
            {
                count = 0;
            }
        }
        return count;
    }

    public static String getCurrentNetType(Context context) {       
        String type = "";       
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);       
        NetworkInfo info = cm.getActiveNetworkInfo();       
        if (info == null) 
        {           
            type = "";       
        } 
        else if (info.getType() == ConnectivityManager.TYPE_WIFI) 
        {           
            type = "WiFi";       
        } 
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE) 
        {           
        	type = "2G";
            int subType = info.getSubtype();           
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS || subType == TelephonyManager.NETWORK_TYPE_EDGE) 
            {               
                  type = "2G";           
            } 
            else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) 
            {               
                  type = "3G";           
            } 
            else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准               
                  type = "4G";           
            } 
        }       
        return type;
    }

    public static boolean isWifiOpen(Context context)
    {
    	ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);//获取状态
    	NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//判断wifi已连接的条件
    	if(wifi != null)
    		return true;
    	return false;
    }
    
    /**
     * @Author: kobe
     * @CreteDate: 2015-5-19 上午10:10:58
     * @Title: 判断app是否在前台还是后台运行
     * @Description:
     * @ModifiedBy:
     * @param context
     * @return
     */
    public static boolean isBackground(Context context)
    {  
        if(context == null ) return false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();  
        for (RunningAppProcessInfo appProcess : appProcesses) {  
            if (appProcess.processName.equals(context.getPackageName())) {  
                /* 
                BACKGROUND=400 EMPTY=500 FOREGROUND=100 
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200 
                 */  
                Log.i(context.getPackageName(), "此appimportace =" + appProcess.importance  + ",context.getClass().getName()="  + context.getClass().getName());  
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {  
                    Log.i(context.getPackageName(), "处于后台" + appProcess.processName);  
                    return true;  
                }
                else 
                {  
                    Log.i(context.getPackageName(), "处于前台" + appProcess.processName);  
                    return false;  
                }  
            }  
        }  
        return false;  
    } 
    
    /**
    * 检测某ActivityUpdate是否在当前Task的栈顶
    */
    public static boolean isTopActivy(Context context , String cmdName){
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
            String cmpNameTemp = null;

            if(null != runningTaskInfos)
            {
                cmpNameTemp=(runningTaskInfos.get(0).topActivity).getClassName();
                Log.e(TAG,"cmpname:"+cmpNameTemp);
            }
           if(null == cmpNameTemp)return false;
           return cmpNameTemp.equals(cmdName);
    }
    
    /**
     * @Author: qinyuanyuan
     * @CreteDate: 2015年7月22日 上午11:44:08
     * @Title: 
     * @Description: 获取运营商和2G/3G/4G类型
     * @ModifiedBy:
     * @param context
     * @return
     */
    public static String getSIMType(Context context) {
    	try {
    		TelephonyManager tm = (TelephonyManager) context
    				.getSystemService(Context.TELEPHONY_SERVICE);
    		return tm.getNetworkOperatorName() + getCurrentNetType(context);
		} catch (Exception e)
    	{
			e.printStackTrace();
		}
    	return "";
	}
    

    /**
	 * @Author: li.zhen
	 * @CreteDate: 2015年8月26日 下午6:06:02
	 * @Title: 
	 * @Description: 获取网络类型和运营商
	 * @ModifiedBy:
	 * @return wifi、2G中国电信等
	 */
	public static String getNetWorkType(){
		Context context = UIUtils.getContext();
		String netType = getCurrentNetType(context);
		if(!TextUtils.isEmpty(netType) && !"WiFi".equals(netType)){
			netType = netType+getMobileType(context);
		}
		return netType;
	}

	public static String getMobileType(Context context)
	{
		String type = "";
		TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String iNumeric = iPhoneManager.getSimOperator();
		if (iNumeric.length() > 0)
		{
			if (iNumeric.equals("46000") || iNumeric.equals("46002"))
			{
				// 中国移动
				type = "中国移动";
			}
			else if (iNumeric.equals("46001"))
			{
				// 中国联通
				type = "中国联通";
			}
			else if (iNumeric.equals("46003"))
			{
				// 中国电信
				type = "中国电信";
			}
		}
		return type;
	}
}
