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
		if(mess.operation.equals("R")){
			if(fs.getStatus(mess.file)){
				fs.lock(mess.file, "R");
				//call the broadcast class
				foc.setTimeStarted(System.currentTimeMillis());
				synchronized(foc){
					try {
						foc.wait();
						if(foc.checkMajority()){
							
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
