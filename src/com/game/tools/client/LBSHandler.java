package com.game.tools.client;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;

public class LBSHandler {

	private LocationClient mLocationClient;
	private BDLocationListener mLocationListener=new MyLocationListener();
	
	//定位成功的回调
	public interface OnLocationReceive
	{
		public void onReceive(int code,String result);
	}
	OnLocationReceive receiver;
	
	public void Init(Context context,OnLocationReceive receiver)
	{
		mLocationClient=new LocationClient(context);
		mLocationClient.registerLocationListener(mLocationListener);
		this.receiver=receiver;
		initLocation();
	}

	void initLocation()
	{
		LocationClientOption option=new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		

		option.setCoorType("bd09ll");
		//可选，默认gcj02，设置返回的定位结果坐标系
//		1. gcj02：国测局坐标；
//		2. bd09：百度墨卡托坐标；
//		3. bd09ll：百度经纬度坐标；
		
		option.setScanSpan(0);
		//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		
		option.setIsNeedAddress(true);
		//可选，设置是否需要地址信息，默认不需要
		
		option.setOpenGps(true);
		//可选，默认false,设置是否使用gps
		
		option.setLocationNotify(false);
		//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		
		option.setIsNeedLocationDescribe(false);
		//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		
		option.setIsNeedLocationPoiList(true);
		//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		
		option.setIgnoreKillProcess(true);
		//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死 
		
		option.SetIgnoreCacheException(true);
		//可选，默认false，设置是否收集CRASH信息，默认收集
		
		option.setEnableSimulateGps(false);
		//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		
		mLocationClient.setLocOption(option);
	}
	
	public void StartLocation()
	{
		if(mLocationClient==null)
		{
			Log.e("LBSHandler", "LocationClient have not init");
			return;
		}
		mLocationClient.start();
	}
	
	void onLocationReceived(int type,String result)
	{
		Log.e("LBSHandler", "LocationResult: "+type+", "+result);
		mLocationClient.stop();
		if(receiver!=null)
		{
			receiver.onReceive(type, result);
		}
	}
	
	
public class MyLocationListener implements BDLocationListener {

		
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			JSONObject json = new JSONObject();
			int error=0;
			try {
				//定位错误码 见http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/getloc
				json.put("code",location.getLocType());
				
				if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
						 
					error=0;
		            json.put("CityCode", location.getCityCode());
		            json.put("City", location.getCity());
		            json.put("Province", location.getProvince());
		            json.put("Type","GPS");
		            
				}else if(location.getLocType() == BDLocation.TypeNetWorkLocation)// 网络定位结果
				{
					error=0;
					json.put("CityCode", location.getCityCode());
		            json.put("City", location.getCity());
		            json.put("Province", location.getProvince());
		            json.put("Type","NetWork");
		            
				}else if(location.getLocType() == BDLocation.TypeOffLineLocation)//离线定位
				{
					error=0;
					json.put("CityCode", location.getCityCode());
		            json.put("City", location.getCity());
		            json.put("Province", location.getProvince());
		            json.put("Type","OffLine");
		            
				}else if (location.getLocType() == BDLocation.TypeServerError) {//定位失败
					
					error=-1;
		            json.put("desc","服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

		        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {//定位失败
		        	
		        	error=-1;
		        	json.put("desc","网络不同导致定位失败，请检查网络是否通畅");
		 
		        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {//定位失败
		        	
		        	error=-1;
		        	json.put("desc","无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
		        }
				
				json.put("error",error);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Log.e("LBS", "Location:"+json.toString());
	        onLocationReceived(location.getLocType(), json.toString());
	    }
		

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			
			
		}
	}

	
	
}





