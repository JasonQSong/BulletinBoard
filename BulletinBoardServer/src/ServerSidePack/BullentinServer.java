package ServerSidePack;

import ServerSidePack.ConnectRequest;

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
		DataList.GroupList.add("Computer Science");
		DataList.GroupList.add("Math");
		DataList.GroupList.add("Food");
		
		DataList.CheckCommandType.add("%LEAVE");
		DataList.CheckCommandType.add("%GROUPPOST");
		DataList.CheckCommandType.add("%GET");
		DataList.CheckCommandType.add("%GROUPJOIN");
		DataList.CheckCommandType.add("%GROUPUSERS");
		DataList.CheckCommandType.add("%GROUPLEAVE");
		DataList.CheckCommandType.add("%GROUPMESSAGE");
		DataList.CheckCommandType.add("%EXIT");
		DataList.CheckCommandType.add("%GROUPS");
		
	}
}
