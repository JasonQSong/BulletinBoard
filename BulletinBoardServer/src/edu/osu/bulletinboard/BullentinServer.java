package edu.osu.bulletinboard;

import java.net.ServerSocket;
import java.net.Socket;

import javax.print.attribute.standard.PrinterLocation;

public class BullentinServer {
	public static void main(String argv[]) throws Exception{
		
		// Set the port number.
		int port =7001;
		// Establish the listen socket.
		ServerSocket socket = new ServerSocket(port);
		
		Initial();
					
		// Process  service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			System.out.println("waiting");
		    Socket ClientSocket = socket.accept();
		    DataList.ClientSocketList.add(ClientSocket);
		    System.out.println(DataList.ClientSocketList.size());
		    
		    // Construct an object to process the HTTP request message.
		    ConnectRequest request = new ConnectRequest(ClientSocket);
		    
		    // Create a new thread to process the request.
		    Thread thread = new Thread(request);
		    
		    // Start the thread.
		    thread.start();
		    
		}
		
	    }
	private static void Initial(){
		DataList.GroupList.add("Rental");
		DataList.GroupList.add("ComputerScience");
		DataList.GroupList.add("Math");
		DataList.GroupList.add("Food");
		DataList.GroupIDList.add(0);
		DataList.GroupIDList.add(1);
		DataList.GroupIDList.add(2);
		DataList.GroupIDList.add(3);
		
		
		DataList.CheckCommandType.add("%grouppost");
		DataList.CheckCommandType.add("%groupjoin");
		DataList.CheckCommandType.add("%groupusers");
		DataList.CheckCommandType.add("%groupleave");
		DataList.CheckCommandType.add("%groupmessage");
		DataList.CheckCommandType.add("%exit");
		DataList.CheckCommandType.add("%groups");
		DataList.CheckCommandType.add("%username");
		
		DataList.CheckCommandType.add("%GROUPPOST");
		DataList.CheckCommandType.add("%GROUPJOIN");
		DataList.CheckCommandType.add("%GROUPUSERS");
		DataList.CheckCommandType.add("%GROUPLEAVE");
		DataList.CheckCommandType.add("%GROUPMESSAGE");
		DataList.CheckCommandType.add("%EXIT");
		DataList.CheckCommandType.add("%GROUPS");
		DataList.CheckCommandType.add("%USERNAME");
		
	}
}
