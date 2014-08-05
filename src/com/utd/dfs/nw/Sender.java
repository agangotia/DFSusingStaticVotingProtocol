package com.utd.dfs.nw;

import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.FileSystem;
import com.utd.dfs.msg.Message;
import com.utd.dfs.utils.ConnectionManager;
import com.utd.dfs.utils.FileFeatures;


public class Sender implements Runnable {
	String logFile;
	
	
	public Sender(){
		logFile=Constants.LOGFILERCVR+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(DFSMain.applicationRunning){
			
			 while(!DFSMain.sendQueue.isEmpty()){
				 Message objMessage= DFSMain.sendQueue.poll();
				 ConnectionManager.sendMessage(objMessage);
				 //FileFeatures.appendText(logFile, "1.."+(System.currentTimeMillis()-DFSMain.appStarted));
				//FileFeatures.appendText(logFile, "2.."+DFSMain.failureStartTime);
				 
				 boolean once=true;
				 
				 if(System.currentTimeMillis()-DFSMain.appStarted>=DFSMain.failureStartTime && System.currentTimeMillis()-DFSMain.appStarted<=DFSMain.failureStartTime+DFSMain.failureDuration){
					 
					 if(!once){
						 once=true;
						 FileSystem.releaseAllLocks();
					 }
					 
					 System.out.println("Failure Starts");
					 FileFeatures.appendText(logFile, "Failure Starts");
					 try {
						Thread.sleep(DFSMain.failureDuration);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 DFSMain.sendQueue.clear();
					 System.out.println("Failure Ends");
				 }
				 
			 }
			 if (checkExit())
		    		DFSMain.applicationRunning=false;
		}
		
	}
	
	public boolean checkExit(){
		if(!DFSMain.sendQueue.isEmpty())
			return false;
		for(int i:DFSMain.exitReplies){
			if(i==0)
				return false;
		}
		return true;
	}

}
