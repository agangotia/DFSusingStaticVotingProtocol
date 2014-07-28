package com.utd.dfs.fs;
import com.utd.dfs.utils.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class File {


	private String fname;
	private int file_version;
	/**
	 * this is a readwritelock
	 */
	public ReentrantReadWriteLock rwl= new ReentrantReadWriteLock();
	
	/**
	 * sets the fname to the name in FileOperationsCount object
	 */
	 /** Backsup original copy so that the file can be rolledback in case of failure
	 * @param file_details
	 */
	public void backup_original(FileOperationsCount file_details){
		try {
			FileFeatures.copyFile(fname,"data\\"+fname+"_bk");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Adds new Content needed to add
	 * @param file_details
	 * @param data
	 */
	public void append(FileOperationsCount foc,String data){
	//	rwl.writeLock().lock();
		String fnameNoversion=foc.getFileWithoutVersion();
		FileFeatures.appendText(fname, data);
		file_version=foc.incrementVersion();
		FileFeatures.rename(fname, fnameNoversion+"_v"+String.valueOf(file_version));
	}
	public void read(){
	//	rwl.readLock().lock();
		//get the content of from latest version using sctp channel......to be completed 
		rwl.readLock().unlock();
	}
	public void releaseWrite(int status){
		if(status==1){//indicates that operation is a success
			rwl.writeLock().unlock();
		}
		else{
			try {
				FileFeatures.copyFile("data\\"+fname+"_bk", fname);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void relaseRead(){
		rwl.readLock().unlock();
	}
}
