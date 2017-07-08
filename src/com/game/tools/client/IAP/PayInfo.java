package com.game.tools.client.IAP;

public class PayInfo {

	public String myPayCode;
	public String ProductName;
	public String fee;
	public String payCode_cm;
	public String payCode_mm;
	public String payCode_cu;
	public String payCode_ct;
	public String payCode_third;
	public String count;
	public String desc;
	
	public PayInfo(String myPayCode,String ProductName,String fee,String payCode_cm,
			String payCode_cu,String payCode_ct,String payCode_third,String count,String desc)
	{
		this.myPayCode=myPayCode;
		this.ProductName=ProductName;
		this.fee=fee;
		this.payCode_cm=payCode_cm;
		this.payCode_cu=payCode_cu;
		this.payCode_ct=payCode_ct;
		this.payCode_third=payCode_third;
		this.count=count;
		this.desc=desc;
	}
	
}
