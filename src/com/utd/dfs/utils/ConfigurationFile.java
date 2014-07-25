package com.utd.dfs.utils;

import java.util.Random;

public class ConfigurationFile {

	public void generate_cffile(int operations_count,int file_minindex, int file_maxindex){
		int remaining_ops=operations_count;
		int read_ops= (int) Math.floor(.7*operations_count);
		int write_ops= (int) Math.ceil(0.3*operations_count);
		Random file = new Random();//generates the index of file on which an operation has to be performed
		Random opr_select= new Random();//randomly select a number between 0 and 100. If the number falls between 0 and 90 then perform read else write
		while(remaining_ops>0){
			int file_index=file.nextInt(file_maxindex-file_minindex+1);//generate file index on which we need to perform some operation
			String file_name="file"+String.valueOf(file_index);
			int opr_index=opr_select.nextInt();
			if(opr_index<70){//perform read operation
				if(read_ops>0){//check if read operations left is greater than 0 perform read else do write
					FileAppend.appendText("config_file", file_name+" R");
					read_ops=read_ops-1;
				}
				else{
					FileAppend.appendText("config_file", file_name+" w");
					write_ops=write_ops-1;
				}
			}
			else{
				if(write_ops>0){
					FileAppend.appendText("config_file", file_name+" w");
					write_ops=write_ops-1;
				}
				else{
					FileAppend.appendText("config_file", file_name+" R");
					read_ops=read_ops-1;
				}
			}
			remaining_ops--;
		}
	}

	public static void main(String args[]){
		ConfigurationFile f= new ConfigurationFile();
		f.generate_cffile(10, 0, 3);
	}
}
