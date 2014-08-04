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

import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSFile;
import com.utd.dfs.fs.FileSystem;

public class ConfigurationFile {
	String path = "fs" + Constants.FILESEPARATOR
			+ DFSMain.currentNode.getNodeID() + Constants.FILESEPARATOR
			+ "config_file";
	public int nodeId = DFSMain.currentNode.getNodeID();
	public Set<String> filesContentsList = new HashSet<String>();

	public void generate_cffile(int operations_count, int file_minindex,
			int file_maxindex) {
		System.out.println("inside generating config file");
		int remaining_ops = operations_count;
		int line_index = 1;
		int read_ops = (int) Math.floor(.5 * operations_count);
		int write_ops = (int) Math.ceil(0.5 * operations_count);
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random_string = new Random();// generate random strings for write
											// operations
		Random file = new Random();// generates the index of file on which an
									// operation has to be performed
		Random opr_select = new Random();// randomly select a number between 0
											// and 100. If the number falls
											// between 0 and 90 then perform
											// read else write
		System.out.println("path for config file--------" + path);
		while (remaining_ops > 0) {
			int file_index = file.nextInt(file_maxindex - file_minindex + 1);// generate
																				// file
																				// index
																				// on
																				// which
																				// we
																				// need
																				// to
																				// perform
																				// some
																				// operation
			String file_name = "file" + String.valueOf(file_index) + ".txt";
			int opr_index = opr_select.nextInt();
			if (opr_index < 70) {// perform read operation
				if (read_ops > 0) {// check if read operations left is greater
									// than 0 perform read else do write
					FileFeatures.appendText(path, line_index + " " + nodeId
							+ " " + file_name + " R");
					read_ops = read_ops - 1;
				} else {// generate random text to write
					for (int i = 0; i < 10; i++) {
						char c = chars[random_string.nextInt(chars.length)];
						sb.append(c);
					}
					FileFeatures.appendText(path, line_index + " " + nodeId
							+ " " + file_name + " W " + sb.toString());
					sb.delete(0, sb.length());
					write_ops = write_ops - 1;
				}
			} else {
				if (write_ops > 0) {
					for (int i = 0; i < 10; i++) {
						char c = chars[random_string.nextInt(chars.length)];
						sb.append(c);
					}
					FileFeatures.appendText(path, line_index + " " + nodeId
							+ " " + file_name + " W " + sb.toString());
					sb.delete(0, sb.length());
					write_ops = write_ops - 1;
				} else {
					FileFeatures.appendText(path, line_index + " " + nodeId
							+ " " + file_name + " R");
					read_ops = read_ops - 1;
				}
			}
			remaining_ops--;
			line_index++;
		}
	}

	public void read_configuration(Queue<FileMessage>[] file_queue) {
		File f = new File(path);
		FileMessage message = null;
		try {
			@SuppressWarnings("resource")
			Scanner fread = new Scanner(f);
			while (fread.hasNextLine()) {
				String line = fread.nextLine();
				String[] linesplit = line.split(" ");
				Integer line_index = Integer.parseInt(linesplit[0]);
				int nodeIdLocal = Integer.parseInt(linesplit[1]);
				Integer queue_index = Integer.parseInt(linesplit[2].substring(
						4, 5));
				filesContentsList.add(linesplit[2]);
				if (linesplit.length > 4) {
					message = new FileMessage(line_index, nodeIdLocal,
							linesplit[2], linesplit[3], linesplit[4]);
					FileSystem.fsobject.put(linesplit[2], new DFSFile(
							linesplit[2], 0, linesplit[4]));
				} else {
					message = new FileMessage(line_index, nodeIdLocal,
							linesplit[2], linesplit[3], null);
				}
				System.out.println("3" + message);
				System.out.println("4" + file_queue);
				if (nodeIdLocal == nodeId) {
					file_queue[queue_index].add(message);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}