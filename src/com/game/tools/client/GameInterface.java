package com.game.tools.client;

import com.game.tools.client.IAP.IapMgr;
import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class GameInterface {

	final static String mUnityReciver="Native";
	
	protected static Application m_application;
	protected static Activity m_activity;
	protected static LBSHandler lbs;
	
	static SharedPreferences sp;
	
	public static void onCreatApp(Application app)
	{
		m_application=app;
		IapMgr.onCreateApp(app);
	}
	
	public static void onCreateActivity(Activity act)
	{
		m_activity=act;
		IapMgr.onCreateActivity();
		lbs=new LBSHandler();
		lbs.Init(m_activity, new LBSHandler.OnLocationReceive() {
			@Override
			public void onReceive(int code, String result) {
				SendToUnity("onLocationReceive", result);
			}
		});
		sp =m_activity.getSharedPreferences("com.game.go.sharedpreferences", 0);
	}
	
	public static Application getMainApplication()
	{
		return m_application;
	}

	public static Activity getMainActivity()
	{
		return m_activity;
	}
	
	public static void SendToUnity(String method,String json)
	{
		if(!method.isEmpty())
			UnityPlayer.UnitySendMessage(mUnityReciver, method, json);
	}

	//用于获取SDK透传参数
	public static String getPlatPayExtInfo(String json)
	{
		return IapMgr.getPlatPayExtInfo(json);
	}
	
	//调起SDK充值
	public static void Pay(String json)
	{
		IapMgr.Pay(json);
	}
	
	//回调充值结果
	public static void PayResult(String json)
	{
		SendToUnity("PayResult", json);
	}
	
	//登录
	public static void Login()
	{
		IapMgr.Login();
	}
	
	//带一个参数的登入接口  例如腾讯的SDK会有QQ登入和微信登入  这时就需要一个标记来确定是微信登录还是QQ登入了
	public static void LoginWithType(String type)
	{
		IapMgr.Login(type);
	}
	
	//回调登录结果
	public static void LoginResult(String json)
	{
		SendToUnity("LoginResult", json);
	}
	
	//登出
	public static void Loginout()
	{
		IapMgr.Loginout();
	}
	
	//回调登出结果
	public static void LoginOutResult(String json)
	{
		SendToUnity("LoginOutResult", json);
	}

	//lbs
	public static void startLocation()
	{
		lbs.StartLocation();
	}

	public static String getIMSI(){
		String imsi="";
		TelephonyManager tm = (TelephonyManager)m_activity.getApplicationContext().getSystemService(m_activity.TELEPHONY_SERVICE);
		imsi=tm.getSubscriberId();
		return imsi;
	}
	
	public static String getIMEI()
	{
		String deviceId = sp.getString("deviceId", "");
		if (TextUtils.isEmpty(deviceId)) {
			TelephonyManager tm = (TelephonyManager) m_activity.getApplicationContext().getSystemService(m_activity.TELEPHONY_SERVICE);
			SharedPreferences.Editor ed = sp.edit();
			String data = "";
			if (tm != null) {
				data = "IMEI_" + tm.getDeviceId();
				if (TextUtils.isEmpty(data))
					data = "IMSI_" + tm.getSubscriberId();
				if (TextUtils.isEmpty(data))
					data = "SIMNUM_" + tm.getSimSerialNumber();
			}
			if (TextUtils.isEmpty(data))
				data = "UUID_" + java.util.UUID.randomUUID();
			ed.putString("deviceId", data);
			ed.commit();
			Log.d("yy", "Saved Device id : " + data);
			return data;
		}
		Log.d("yy", "Read saved Device id : " + deviceId);
		return deviceId;
	}
	
	

	public static String getSimCard(){
		try {
			TelephonyManager TManager = (TelephonyManager) m_application.getSystemService(m_application.TELEPHONY_SERVICE);
			String imsi = TManager.getSubscriberId();
			if(imsi.startsWith("46003") 
					|| imsi.startsWith("46005")
					|| imsi.startsWith("460011")){
				return "ct";//电信
			}else if(imsi.startsWith("46001") 
					|| imsi.startsWith("46006")){
				return "cu";//联通
			}else if(imsi.startsWith("46000") 
					|| imsi.startsWith("46002") 
					|| imsi.startsWith("46007") 
					|| imsi.startsWith("460020")){
				return "cm";//移动
			}
		}catch(Exception e){}
		return "empty";//无卡
	}
	
	static String int2ip(long ipInt) {  
        StringBuilder sb = new StringBuilder();  
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();  
	} 
	
	public static String getMacAddr()
	{
		Log.e("JXTZ","GetMacAddr");
		String macAddress = "";//, ip = "";  
		WifiManager wifiMgr = (WifiManager)m_activity.getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());  
		if (null != info) {  
		    macAddress = info.getMacAddress();  
		  //  ip = int2ip(info.getIpAddress());  
		}  
		return macAddress;
	}
	
	//获取渠道号
	public static String getChannel() {
		return IapMgr.getChannel();
	}
	
	//获取配在manifest中短信支付的平台映射
	public static String getSmsPayPlatMap() {
		return IapMgr.getSmsPayPlatMap();
	}
	
	//获取第三方支付平台
	public static String getThirdPayPlat() {
		return IapMgr.getThirdPayPlat();
	}
	
	//获取版本号
	public static String getVersionName(){
		try {
			PackageInfo pi=m_activity.getPackageManager().getPackageInfo(m_activity.getPackageName(), 0);
			String vn=pi.versionName;
			if(vn!=null && vn.length()>0)
				return vn;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}
	
	
}
