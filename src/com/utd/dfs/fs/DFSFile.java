package com.utd.dfs.fs;
import com.utd.dfs.utils.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DFSFile {


	private String fname;
	
	private int file_version;
	
	private String data;
	public ReentrantReadWriteLock rwl;
	public int readLockCount;//only unlock readlock when this is 0.
	
	public DFSFile(String fname, int file_version, String data) {
		super();
		this.fname = fname;
		this.file_version = file_version;
		this.data = data;
		this.rwl= new ReentrantReadWriteLock();
	}
	/**
	 * this is a readwritelock
	 */
	
	
	/**
	 * sets the fname to the name in FileOperationsCount object
	 */
	 /** Backsup original copy so that the file can be rolledback in case of failure
	 * @param file_details
	 */
	public synchronized void backup_original(){
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
	public synchronized void append(String data){
	//	rwl.writeLock().lock();
		this.data+=data;
		FileFeatures.appendText(fname, data);
	}
	public synchronized String read(){
	//	rwl.readLock().lock();
		//just read the local copy
		rwl.readLock().unlock();
		return this.data;
	}
	public synchronized void releaseWrite(int status){
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
	public synchronized void relaseRead(){
		readLockCount--;
		if(readLockCount==0)
			rwl.readLock().unlock();
	}
	
	public int getFile_version() {
		return file_version;
	}


	public void setFile_version(int file_version) {
		this.file_version = file_version;
	}
}
