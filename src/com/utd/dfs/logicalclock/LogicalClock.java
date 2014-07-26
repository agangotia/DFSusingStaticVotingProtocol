package com.utd.dfs.logicalclock;
/**
 * Logical CLock Implementation
 * @author Anupam Gangotia
 * Profile::http://en.gravatar.com/gangotia
 * github::https://github.com/agangotia
*/
public class LogicalClock {
	int logicalClockValue;
	public LogicalClock(){
		logicalClockValue=0;
	}
	
	public synchronized void incrementValue(){
		logicalClockValue++;
	}
	public synchronized void incrementValue(int valRecvd){
		if(valRecvd>logicalClockValue)
			logicalClockValue=valRecvd+1;
		else
			logicalClockValue++;
	}
	public synchronized int getValue(){
		return logicalClockValue;
	}
}
