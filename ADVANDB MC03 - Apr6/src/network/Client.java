package network;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JOptionPane;

import model.Mail;
import model.Tags;
import controller.Controller;

public class Client {
	private Controller c;
	 public Client(Controller c, Socket X)
	 {
		  	this.c = c;
		  	RECEIVE(X); // pinasa na yung socket :)

	 }

	 void RECEIVE(Socket S){
			try{
				System.out.println("==CLIENT RECEIVING FROM SERVER START==");
				
				ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
				Mail rMail = (Mail) ois.readObject();
			   	ois.close();

			    System.out.println(">>Client received mail<<");
			    
			    String protocol[] = rMail.getMessage().split(Tags.PROTOCOL, 2);
			    System.out.println("MAIL SERVER PROTOCOL: "+protocol[0]);
			    
			    switch(protocol[0]){
			    	case Tags.RETURN_READ: 
			    			/** query-> index 0   sender-> index 1**/
			    			System.out.println("==REQUEST TO EXECUTE QUERY RECEIVED==");
			     			c.EXECUTE_QUERY_REQUEST(rMail);
			    		break;
			    	case Tags.RESULT_SET:
			    			System.out.println("==RESULT SET RECEIVED==");
			    			c.RECEIVE_RESULT_SET(rMail);
			    			break;
			    	case Tags.PARTIAL_COMMIT:
			    			System.out.println("==PARTIAL COMMIT STATUS RECEIVED==");
			    			c.PARTIAL_COMMIT_FROM_CENTRAL(rMail);
			    			break;
			    	case Tags.ABORT:
			    			System.out.println("==ABORT COMMIT STATUS RECEIVED==");
			    			c.ABORT_FROM_CENTRAL(rMail);break;
			    	case Tags.COMMIT:
			    			System.out.println("==COMMIT STATUS RECEIVED==");
			    			String mailServer[] = rMail.getMessage().split(Tags.PROTOCOL, 2);
			    			System.out.println("MAIL SVER 1: "+ mailServer[1]);
			    			c.COMMIT_TRANSACTION(mailServer[1].trim());break;
			    	default: System.out.println("PROTOCOL NOT RECOGNIZED!");
			    }
			   
			}
			catch(Exception x){
				x.printStackTrace();
				
			}
	 }	 
}
