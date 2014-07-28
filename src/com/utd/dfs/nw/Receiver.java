package com.utd.dfs.nw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.sun.nio.sctp.SctpChannel;
import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;
import com.utd.dfs.utils.ConnectionManager;

public class Receiver implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 ByteBuffer byteBuffer= ByteBuffer.allocate(10000);
		while(DFSMain.applicationRunning){
			
			try {

                for (int id : DFSMain.connectionSocket.keySet()) {
                    SctpChannel schnl = DFSMain.connectionSocket.get(id);

                    byteBuffer.clear();
                    schnl.configureBlocking(false);
                    schnl.receive(byteBuffer, null,null);
                    byteBuffer.flip();

                    if (byteBuffer.remaining() > 0) {
                        Message receivedMsg = (Message) deserialize(byteBuffer
                                .array());
               
                        String msgPrint="*********************************************";
        	            msgPrint+="\nRecieved  Time-"+System.currentTimeMillis()+"\n"
        		                +receivedMsg.printMessage();
        	           
        	            msgPrint+="\n*********************************************";
                        System.out.println(msgPrint);
                        
                        if(Constants.TESTSENDERRECEIVER==true && receivedMsg.getMsgType()==30)//for check
                        	DFSMain.applicationRunning=false;

                    byteBuffer.clear();
                }
                }// end for
                } catch (IOException e) {
	                e.printStackTrace();
	            } catch (ArrayIndexOutOfBoundsException e) {
	            } catch (NullPointerException e) {
	                e.printStackTrace();
	            }finally {
	                
	            }


		}
	}	
			public static Object deserialize(byte[] obj) {
				
				ByteArrayInputStream bos = new ByteArrayInputStream(obj);
				
				try {
					ObjectInputStream in = new ObjectInputStream(bos);
					return in.readObject();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.print("Error in deserialization");
				}
				return null;
			}
}