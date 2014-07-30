package com.utd.dfs;

public interface Constants {

	String TOPOLOGYFILE="data//topology.txt";
	String FILECONFIG="data//initialFileconfig.txt";
	
	boolean TESTSENDERRECEIVER=false;
	int SIZESENDQ=100;
	int SIZESRECVQ=100;
	Long timeOut=20000L;
	
	
	//for File System
	int FILEMININDEX=1;
	int FILEMAXINDEX=3;
}