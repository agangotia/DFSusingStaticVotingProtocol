package com.utd.dfs;

import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.statustrackers.Status;

public class Monitor implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(DFSMain.applicationRunning){
			for(String s:DFSCommunicator.mapFileStatus.keySet()){
				Status o=DFSCommunicator.mapFileStatus.get(s);
				if(System.currentTimeMillis()-o.getWaitStart()>Constants.timeOut)
					o.getO().notify();
			}
			try {
				Thread.sleep((1l/3)*Constants.timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	
	}

}
