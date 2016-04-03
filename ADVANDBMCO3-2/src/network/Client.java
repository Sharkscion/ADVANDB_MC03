package network;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JOptionPane;

import model.Tags;
import controller.Controller;

public class Client {
	private Controller c;
	 public Client(Controller c, Socket X)
	 {
		  	this.c = c;
		  	RECEIVE(X); // pinasa na yung socket :)

	 }
	 
	 public static void POST(String ip, int port, String message)
		{
			System.out.println("POST (start)");
			Socket SOCK;
			try {
				SOCK = new Socket(ip, port);								// Open socket
				PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
				OUT.println("\"POST\" " + message + "\0"); 					// Send message
				OUT.flush();		
				SOCK.close(); 												// Close socket	

			}
			catch (Exception e) {
				e.printStackTrace();
			} 
			System.out.println("POST (end)");
		 }


	 void RECEIVE(Socket S){
			try{
				System.out.println("==CLIENT RECEIVING FROM SERVER START==");
				
				InputStream input = S.getInputStream();			
				byte [] scannedbytes = new byte [65500];
			    int bytesRead = input.read(scannedbytes,0,scannedbytes.length);
			    int current = bytesRead;
			    
			    ObjectInputStream ois = new ObjectInputStream(input);
			    System.out.println(ois.readObject());
			    
			    System.out.println("Client receive (continue to read bytes)");
			    String InputCommand = new String(scannedbytes, "UTF-8");
			    
			    System.out.println("Client receive (input string start: " + InputCommand.substring(0,5) + ")");
				
			    String mailServer[] = InputCommand.split("#", 2);
			    switch(mailServer[0]){
			    	case Tags.RETURN_READ: 
			    			/** query-> index 0   sender-> index 1**/
			    			String mail[] = mailServer[1].split("#",2);
			    			System.out.println("RECEIVED MAIL FROM--" + mail[1]);
			    			c.RETURN_READ_EXECUTE(mail[0], mail[1]);
			    		break;
			    	default: System.out.println("PROTOCOL NOT RECOGNIZED!");
			    }
			   
			}
			catch(Exception x){
				x.printStackTrace();
				
			}
	 }	 
}
