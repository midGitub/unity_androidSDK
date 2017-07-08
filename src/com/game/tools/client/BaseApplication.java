package com.game.tools.client;

import com.unicom.shield.UnicomApplicationWrapper;

public class BaseApplication extends UnicomApplicationWrapper {

	@Override
	public void onCreate() {
		super.onCreate();
		GameInterface.onCreatApp(this);
	}	
	
}
