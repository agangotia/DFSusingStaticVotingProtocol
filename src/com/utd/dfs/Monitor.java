package com.utd.dfs;

import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.statustrackers.Status;

public class Monitor implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(DFSMain.applicationRunning){
			for(String s:DFSCommunicator.mapFileStatus.keySet()){
				if(s!=null){
					Status o=DFSCommunicator.mapFileStatus.get(s);
					if(o!=null){
						Object lock=o.getO();
						if(lock!=null){
							synchronized (lock) {
								if(System.currentTimeMillis()-o.getWaitStart()>Constants.timeOut)
									lock.notify();
							}
						}else{
							System.out.println("Unfortunately LOCK is null");
						}
						
					}else{
						System.out.println("Unfortunately Status object is null");
					}
				}
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
