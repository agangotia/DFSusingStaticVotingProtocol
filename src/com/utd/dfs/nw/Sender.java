package com.utd.dfs.nw;

import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;
import com.utd.dfs.utils.ConnectionManager;


public class Sender implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(DFSMain.applicationRunning){
			
			 while(!DFSMain.sendQueue.isEmpty()){
				 Message objMessage= DFSMain.sendQueue.poll();
				 ConnectionManager.sendMessage(objMessage);
			 }	 
		}
		
	}

}
