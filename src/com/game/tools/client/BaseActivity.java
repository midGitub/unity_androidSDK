package com.game.tools.client;


import android.os.Bundle;
import com.unity3d.player.UnityPlayerActivity;

public class BaseActivity extends UnityPlayerActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GameInterface.onCreateActivity(this);
	}
	
	
	public String getIMSI(){
		return GameInterface.getIMSI();
	}
	
	public String getIMEI()
	{
		return GameInterface.getIMEI();
	}
	
	public String getSimCard(){
		return GameInterface.getSimCard();
	}
	
	public String getMacAddr()
	{
		return GameInterface.getMacAddr();
	}
	
	public String getUmengChannel() {
		return GameInterface.getChannel();
	}
	
	public String getSmsPayPlatMap() {
		return GameInterface.getSmsPayPlatMap();
	}
	
	public String getThirdPayPlat() {
		return GameInterface.getThirdPayPlat();
	}
	
	public String getVersionName(){
		return GameInterface.getVersionName();
	}
	
	public String getPlatPayExtInfo(String json)//具体渠道具体实现
	{
		return GameInterface.getPlatPayExtInfo(json);
	}

	public void Pay(String json)
	{
		GameInterface.Pay(json);
	}
	
	public void Login()
	{
		GameInterface.Login();
	}
	
	public void LoginWithType(String type)
	{
		GameInterface.LoginWithType(type);
	}

	public void LoginOut()
	{
		GameInterface.Loginout();
	}
	
	public void StartLocation()
	{
		GameInterface.startLocation();
	}

}
