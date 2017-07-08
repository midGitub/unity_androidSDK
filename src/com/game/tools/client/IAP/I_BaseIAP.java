package com.game.tools.client.IAP;

import com.game.tools.client.GameInterface;

public abstract class I_BaseIAP {

	protected I_BaseIAPAuth m_auth;
	protected final String paySuccess="1";
	protected final String payFailed="2";
	protected final String payCancel="3";
	
	public abstract void onCreatApp();
	public abstract void onCreateActivity();
	
	public abstract void moreGame();
	
	public abstract void exitGame();
	
	public abstract String getPlatPayExtInfo(String skyPayPoint);
	
	public abstract void pay(String json);
	
	//payResult的json中应包含一个支付结果码，payResult,1为成功，2为失败，3为取消
	public void payResult(String json)
	{
		GameInterface.PayResult(json);
	}

	public void setAttatchIapItem(I_BaseIAPAuth auth)
	{
		this.m_auth = auth;
	}
}
