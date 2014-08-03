package com.utd.dfs.fs;
import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DFSFile {


	private String fname;
	private String path;
	
	private int file_version;
	public int file_version_old;
	
	private String data;
	public String cacheddata;
	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public ReentrantReadWriteLock rwl;
	public int readLockCount;//only unlock readlock when this is 0.
	
	public DFSFile(String fname, int file_version, String data) {
		super();
		this.fname = fname;
		this.file_version = file_version;
		this.data = data;
		this.rwl= new ReentrantReadWriteLock();
		path="fs"+Constants.FILESEPARATOR+DFSMain.currentNode.getNodeID()+Constants.FILESEPARATOR+fname;
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
			FileFeatures.copyFile(path,path+"_bk");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void restorePreviousVersion(){
		try {
			FileFeatures.copyFile(path+"_bk",path);
			//Need to get the data  from file.
			@SuppressWarnings("resource")
			String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
			data=content;
			file_version=file_version_old;
			
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
		FileFeatures.appendText(path, data);
		this.data+="\n"+data;
		
	}
	
	public synchronized void write(String data2){
		//	rwl.writeLock().lock();
			FileFeatures.writeText(path, data2);
			this.data=data;
			
		}
	
	public synchronized String read(){
	//	rwl.readLock().lock();
		//just read the local copy
		//rwl.readLock().unlock();
		return this.data;
	}
	public  synchronized void releaseWrite(){
		if(rwl.writeLock().isHeldByCurrentThread())
			rwl.writeLock().unlock();
		else{
			System.out.println("**********Error************");
			System.out.println("**********Write Lock is not yet acquired************");
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
