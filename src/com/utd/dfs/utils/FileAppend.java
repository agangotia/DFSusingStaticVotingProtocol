package com.utd.dfs.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FileAppend
 * This class writes to files
 * @author Anupam Gangotia
 * Profile::http://en.gravatar.com/gangotia
 * github::https://github.com/agangotia
*/
public class FileAppend {

	public static void appendText(String fileName,String message){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
		    out.println(message);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Error in writing to file"+fileName);
		}
	}
}