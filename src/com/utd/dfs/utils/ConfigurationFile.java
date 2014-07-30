package com.utd.dfs.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;



import com.utd.dfs.fs.DFSFile;
import com.utd.dfs.fs.FileSystem;

public class ConfigurationFile {

	public static Set<String> filesContentsList= new HashSet<String>();
	public static void generate_cffile(int operations_count,int file_minindex, int file_maxindex){
		int remaining_ops=operations_count;
		int read_ops= (int) Math.floor(.7*operations_count);
		int write_ops= (int) Math.ceil(0.3*operations_count);
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random_string = new Random();//generate random strings for write operations
		Random file = new Random();//generates the index of file on which an operation has to be performed
		Random opr_select= new Random();//randomly select a number between 0 and 100. If the number falls between 0 and 90 then perform read else write
		while(remaining_ops>0){
			int file_index=file.nextInt(file_maxindex-file_minindex+1);//generate file index on which we need to perform some operation
			String file_name="file"+String.valueOf(file_index);
			int opr_index=opr_select.nextInt();
			if(opr_index<70){//perform read operation
				if(read_ops>0){//check if read operations left is greater than 0 perform read else do write
					FileFeatures.appendText("config_file", file_name+" R");
					read_ops=read_ops-1;
				}
				else{//generate random text to write
					for (int i = 0; i < 10; i++) {
						char c = chars[random_string.nextInt(chars.length)];
						sb.append(c);
					}
					FileFeatures.appendText("config_file", file_name+" W "+ sb.toString());
					sb.delete(0, sb.length());
					write_ops=write_ops-1;
				}
			}
			else{
				if(write_ops>0){
					for (int i = 0; i < 10; i++) {
						char c = chars[random_string.nextInt(chars.length)];
						sb.append(c);
					}
					FileFeatures.appendText("config_file", file_name+" W "+ sb.toString());
					sb.delete(0, sb.length());
					write_ops=write_ops-1;
				}
				else{
					FileFeatures.appendText("config_file", file_name+" R");
					read_ops=read_ops-1;
				}
			}
			remaining_ops--;
		}
	}

	public static void read_configuration(String config_file, Queue<FileMessage> file_queue[]){//maintaining separate queues for individual operations on file
		File f = new File(config_file);
		FileMessage message=null;
		try {
			@SuppressWarnings("resource")
			Scanner fread= new Scanner(f);
			while(fread.hasNextLine()){
				String line= fread.nextLine();
				System.out.println("1"+line);
				String[] linesplit= line.split(" ");
				Integer queue_index= Integer.parseInt(linesplit[0].substring(4,5));
				System.out.println("2"+queue_index);
				filesContentsList.add(linesplit[0]);
				if(linesplit.length>2){
					message=new FileMessage(linesplit[0],linesplit[1],linesplit[2]);
					FileSystem.fsobject.put(linesplit[0], new DFSFile(linesplit[0],0,linesplit[2]));
				}
				else{

					message= new FileMessage(linesplit[0],linesplit[1],null);	
				}
				System.out.println("3"+message);
				System.out.println("4"+file_queue);
				file_queue[queue_index].add(message);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*public static void main(String args[]){
		ConfigurationFile f= new ConfigurationFile();
		f.generate_cffile(10, 0, 3);
		@SuppressWarnings("unchecked")
		Queue<FileMessage> q[]= new Queue[4];
		q[0]= new LinkedList<FileMessage>();
		q[1]=new LinkedList<FileMessage>();
		q[2]=new LinkedList<FileMessage>();
		q[3]=new LinkedList<FileMessage>();
		f.read_configuration("config_file", q);
		for(int i=0; i<4; i++){
			System.out.println(q[i].toString());
		}
		try {
			FileFeatures.copyFile("config_file", "data\\config_bkup");
			FileFeatures.rename("data\\config_bkup", "data\\config_backup");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/}
