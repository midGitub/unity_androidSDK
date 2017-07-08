package com.game.tools.client.IAP;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.game.tools.client.GameInterface;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class IapMgr {

	static String m_simCard;
	static String m_channel;
	static String m_thirdPayPlat;
	static String m_smsPayPlatMap;
	static String m_loginPlat;
	
	static Activity m_act;
	static Application m_app;
	
	static I_BaseIAP iap;
	static I_BaseIAPAuth iapAuth;
	static I_BaseIAP smsIap;
	
	static HashMap<String, String>smsMap;
	
	public static void onCreateApp(Application app)
	{
		m_app=app;
		m_simCard=GameInterface.getSimCard();
		getChannel();
		getSmsPayPlatMap();
		getThirdPayPlat();
		
		DeCodeSMSPlatMap();
		CreateIapInstance();
		
		if(iap!=null)
		{
			iap.onCreatApp();
			iap.setAttatchIapItem(iapAuth);
		}

		if(iapAuth!=null)
		{
			iapAuth.onCreateActivity();
			iapAuth.setAttatchIapItem(iap);
		}
			
		if(smsIap!=null)
			smsIap.onCreatApp();
	}
	
	public static void onCreateActivity()
	{
		m_act=GameInterface.getMainActivity();
		if(iap!=null)
			iap.onCreateActivity();
		if(iapAuth!=null)
			iapAuth.onCreateActivity();
		if(smsIap!=null)
			smsIap.onCreateActivity();
	}
	
	static void DeCodeSMSPlatMap()
	{
		smsMap=new HashMap<String, String>();
		String[]plats=m_smsPayPlatMap.split(";");
		for(int i=0,Imax=plats.length;i<Imax;i++)
		{
			String[] kv=plats[i].split("-");
			smsMap.put(kv[0], kv[1]);
		}
	}
	
	static void CreateIapInstance()
	{
		String thirdIap="";
		String smsIap="";
		String loginPlat="";
		if(m_thirdPayPlat==null||m_thirdPayPlat.isEmpty()||m_thirdPayPlat.equals("auto"))
			thirdIap=m_channel;
		else 
			thirdIap=m_thirdPayPlat;
		
		if(m_loginPlat==null||m_loginPlat.isEmpty()||m_loginPlat.equals("auto"))
			loginPlat=m_channel;
		else 
			loginPlat=m_loginPlat;
		
		smsIap=smsMap.get(m_simCard);
		
		if(smsIap!=null&&!smsIap.isEmpty()&&!smsIap.equals("empty"))
			IapMgr.smsIap=CreateIapInstance(smsIap);
		
		if(thirdIap!=null&&!thirdIap.isEmpty()&&!thirdIap.equals("empty"))
			iap=CreateIapInstance(thirdIap);
			
		if(loginPlat!=null&&!loginPlat.isEmpty()&&!loginPlat.equals("empty"))
			iapAuth=CreateIAPAuthInstance(loginPlat);
	}
	
	static I_BaseIAP CreateIapInstance(String pkname)
	{
		I_BaseIAP iap=null;
		String className="com.game.tools.client.IAP.cn"+pkname+".Iap";
		try
		{
			Class cls=Class.forName(className);
			if(cls!=null)
				iap=(I_BaseIAP)cls.newInstance();
		}catch(ClassNotFoundException e)
		{
			
		}catch (InstantiationException e) {
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return iap;
	}
	
	static I_BaseIAPAuth CreateIAPAuthInstance(String pkname)
	{
		I_BaseIAPAuth auth=null;
		String className="com.game.tools.client.IAP.cn"+pkname+".IapAuth";
		try
		{
			Class cls=Class.forName(className);
			if(cls!=null)
				auth=(I_BaseIAPAuth)cls.newInstance();
		}catch(ClassNotFoundException e)
		{
			
		}catch (InstantiationException e) {
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return auth;
	}
	
	
	public static String getPlatPayExtInfo(String json)
	{
		JSONObject jsonobj;
		String skyPayPoint="";
		Boolean useSMS=false;	
		
		try {
			jsonobj = new JSONObject(json);
			skyPayPoint=jsonobj.getString("skyPayPoint");
			useSMS=jsonobj.getBoolean("useSMS");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(useSMS&&smsIap!=null)
			return smsIap.getPlatPayExtInfo(skyPayPoint);
		else if(!useSMS&&iap!=null)
			return iap.getPlatPayExtInfo(skyPayPoint);
		
		Log.e("IapMgr ","GetExtInfo");
		return "";
	}
	
	public static void Pay(String json)
	{
		Boolean useSMS=false;
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			useSMS=jsonObj.getBoolean("useSMS");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(useSMS&&smsIap!=null)
			smsIap.pay(json);
		else if(!useSMS&&iap!=null)
			iap.pay(json);
	}
	
	public static void Login()
	{
		if(iapAuth!=null)
			iapAuth.login();
	}
	
	public static void Login(String type)
	{
		if(iapAuth!=null)
			iapAuth.login(type);
	}
	
	public static void Loginout()
	{
		if(iapAuth!=null)
			iapAuth.loginout();
	}
	
	
	public static String getChannel()
	{
		if(m_channel==null||m_channel.isEmpty())
		{
			String ret = "";
			ApplicationInfo appInfo;
			try {
				appInfo =m_app.getPackageManager().getApplicationInfo(m_app.getPackageName(),PackageManager.GET_META_DATA);
				ret = appInfo.metaData.get("UMENG_CHANNEL").toString();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_channel = ret;
		}
		return m_channel; 
	}
	
	//获取短信支付平台的映射
	public static String getSmsPayPlatMap()
	{
		try{
			if(m_smsPayPlatMap==null||m_smsPayPlatMap.isEmpty())
			{
				String ret = "";
				ApplicationInfo appInfo;
				appInfo = m_app.getPackageManager().getApplicationInfo(m_app.getPackageName(),PackageManager.GET_META_DATA);
				ret = appInfo.metaData.get("sms_payPlatMap").toString();
				m_smsPayPlatMap=ret;
			}
		}catch(NameNotFoundException e){e.printStackTrace();}
		
		return m_smsPayPlatMap;
	}
	
	public static String getThirdPayPlat() 
	{
		try{
			if(m_thirdPayPlat==null||m_thirdPayPlat.isEmpty())
			{
				String ret = "";
				ApplicationInfo appInfo;
				appInfo =m_app.getPackageManager().getApplicationInfo(m_app.getPackageName(),PackageManager.GET_META_DATA);
				ret = appInfo.metaData.get("third_payplat").toString();
				m_thirdPayPlat=ret;
			}
		}catch(NameNotFoundException e){e.printStackTrace();}
		
		return m_thirdPayPlat;
	}
	
	public static String getThirdLoginPlat() 
	{
		try{
			if(m_loginPlat==null||m_loginPlat.isEmpty())
			{
				String ret = "";
				ApplicationInfo appInfo;
				appInfo =m_app.getPackageManager().getApplicationInfo(m_app.getPackageName(),PackageManager.GET_META_DATA);
				ret = appInfo.metaData.get("loginPlat").toString();
				m_loginPlat=ret;
			}
		}catch(NameNotFoundException e){e.printStackTrace();}
		
		return m_loginPlat;
	}
	
}
