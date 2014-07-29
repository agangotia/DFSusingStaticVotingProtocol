package com.utd.dfs.utils;

import com.utd.dfs.fs.FileSystem;

public class ReadWrite extends Thread{
	FileMessage mess;
	FileSystem fs= new FileSystem();
	FileOperationsCount foc;
	public ReadWrite(FileMessage mess, FileOperationsCount foc) {
		super();
		this.mess = mess;
		this.foc=foc;
	}
	
	public void run(){
		if(mess.operation.equals("R")){//read operation
			if(fs.getStatus(mess.file)){// check for lock
				fs.lock(mess.file, "R");// if not lock acquire lock
				//call the broadcast class
				foc.setTimeStarted(System.currentTimeMillis());// set the tiem out for broadcast
				synchronized(foc){
					try {
						foc.wait();
						if(foc.checkMajority()){//once has the majority
							
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			else{
				System.out.println("file is locked. Unable to do the read operation "+mess.file);
				return;
			}
		
		}
		else{
			
		}
	}
	
}
