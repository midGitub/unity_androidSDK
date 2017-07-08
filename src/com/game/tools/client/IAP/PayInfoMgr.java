package com.game.tools.client.IAP;

import java.util.HashMap;


public class PayInfoMgr {

	//计费信息
	static HashMap<String, PayInfo> payInfos;
	
	static {
		payInfos=new HashMap<String, PayInfo>();
	}
	
	public static PayInfo getInfo(String myPayCode)
	{
		if(!payInfos.containsKey(myPayCode))
			return null;
		return payInfos.get(myPayCode);
	}
	
	
}
