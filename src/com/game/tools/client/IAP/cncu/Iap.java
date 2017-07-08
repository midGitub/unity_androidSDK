package com.game.tools.client.IAP.cncu;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.game.tools.client.GameInterface;
import com.game.tools.client.IAP.I_BaseIAP;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

public class Iap extends I_BaseIAP {

	Activity m_activity;
	
	Handler handler=new Handler(Looper.getMainLooper())
	{
		public void handleMessage(Message msg) {
			Toast.makeText(GameInterface.getMainActivity(), msg.obj.toString(), 0).show();
		}
	};
	
	@Override
	public void onCreatApp() {

	}
	
	@Override
	public void onCreateActivity() {
		m_activity=GameInterface.getMainActivity();
		Utils.getInstances().initPayContext(m_activity, new UnipayPayResultListener() {
			public void PayResult(String arg0, int arg1, int arg2, String arg3) {
			}
		});
	}
	
	@Override
	public void pay(String orderInfo) {
		try {
			Log.e("UniPay", "PayInAndroid");
			JSONObject json=new JSONObject(orderInfo);
			final String orderId=json.getString("orderId");
			final String payCode="001";//json.getString("payCode");
			final String monthType="0";//"0"道具关卡  "1"按次代缴订购 "2"按次代缴退订
			
			handler.post(new Runnable() {
				@Override
				public void run() {
					
					Utils.getInstances().payOnline(m_activity, payCode, monthType, orderId, new UnipayPayResultListener() {
						
						@Override
						public void PayResult(String paycode, int result, int callBackType, String desp) {
							Message msg=Message.obtain();
							switch(result)
							{
							case 1://支付成功
								Log.e("UniPay","result "+result+" 支付成功");
								msg.obj="支付结果正在验证...请稍候...";
								handler.sendMessage(msg);
								break;
							case 2://支付失败
								Log.e("UniPay","result "+result+" 支付失败 "+desp);
								msg.obj="支付失败";
								handler.sendMessage(msg);
								break;
							case 3://取消支付
								Log.e("UniPay","result "+result+" 用户取消支付 "+desp);
								msg.obj="取消支付";
								handler.sendMessage(msg);
								break;
							default:
								Log.e("UniPay","位置错误 "+result+"："+desp);
								msg.obj="未知错误"+result+":"+desp;
								handler.sendMessage(msg);
								break;
							}
							JSONObject json=new JSONObject();
							try {
								json.put("orderId",orderId);
								json.put("payCode", payCode);
								json.put("result", result);
								json.put("desp",desp);
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							payResult(json.toString());
						}
					});
				}
			});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exitGame() {

	}

	@Override
	public String getPlatPayExtInfo(String skyPayPoint) {
		return skyPayPoint;
		
	}
	
}
