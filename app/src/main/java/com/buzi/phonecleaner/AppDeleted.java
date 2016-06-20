package com.buzi.phonecleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppDeleted extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		 String pack = intent.getDataString();
		 
		 for(int i=0; i < AppActivity.apps.size(); i++){
			 if(AppActivity.apps.get(i).getPname().equals(pack.replace("package:", ""))){
				 AppActivity.apps.get(i).setDelete(true);
			 }
		 }
		
	     Intent broadcast = new Intent();
		 broadcast.setAction("QUITAR");
		 context.sendBroadcast(broadcast);
	}

}
