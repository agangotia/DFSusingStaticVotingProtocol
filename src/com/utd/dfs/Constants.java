package com.utd.dfs;

public interface Constants {
	
	String FILESEPARATOR="//";//Change this to \\ for windows 
	
	String TOPOLOGYFILE="data"+FILESEPARATOR+"topology.txt";
	String FILECONFIG="data"+FILESEPARATOR+"initialFileconfig.txt";
	
	boolean TESTSENDERRECEIVER=false;
	int SIZESENDQ=100;
	int SIZESRECVQ=100;
	Long timeOut=20000L;
	
	
	//for File System
	int FILEMININDEX=0;
	int FILEMAXINDEX=2;
	
	
	//For Logger
	String LOGFILE="log//logNode";//Change this to \\ for windows
	String LOGFILEEND=".log";
}