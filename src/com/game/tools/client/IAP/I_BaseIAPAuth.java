package com.game.tools.client.IAP;

import com.game.tools.client.GameInterface;

public abstract class I_BaseIAPAuth {

	protected I_BaseIAP m_iap;

	public abstract void onCreateApplication();
	public abstract void onCreateActivity();

	public abstract void login();
	public abstract void login(String type);
	
	public void loginCallBack(String json)
	{
		GameInterface.LoginResult(json);
	}
	
	public abstract void loginout();
	public void loginoutCallBack(String json)
	{
		GameInterface.LoginOutResult(json);
	}
	  
	public void setAttatchIapItem(I_BaseIAP iap)
	{
	  this.m_iap = iap;
	}

}
