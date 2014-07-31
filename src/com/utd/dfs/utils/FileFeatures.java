package com.utd.dfs.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

/**
 * FileAppend
 * This class writes to files
 * @author Anupam Gangotia
 * Profile::http://en.gravatar.com/gangotia
 * github::https://github.com/agangotia
*/
public class FileFeatures {

	public static synchronized void appendText(String fileName,String message){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
		    out.println(message);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Error in writing to file"+fileName);
		}
	}
	public static void copyFile(String sFile, String dFile) throws IOException {
		File sourceFile= new File(sFile);
		File destFile= new File(dFile);
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	public static void rename(String old_fname,String new_fname){
		File oldfile =new File(old_fname);
		File newfile =new File(new_fname);
 
		if(oldfile.renameTo(newfile)){
			System.out.println("Rename succesful");
		}else{
			System.out.println("Rename failed");
		}

	}
}