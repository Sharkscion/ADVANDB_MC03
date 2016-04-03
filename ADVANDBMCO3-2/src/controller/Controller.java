package controller;

import java.awt.FlowLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Site;
import model.Tags;
import model.Transaction;

public class Controller
{
	// database manager?
	
	private Site owner;
	private Socket SOCK;
	private PrintWriter OUT;
	
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		OUT = null;
		// instantiate database manager
	}

	public void add(String ip, String name)
	{
		Site newSite = new Site(ip,name);
		owner.addConnection(newSite);
	}
	
	public void EXECUTE_READ_REQUEST(String query){
		System.out.println("EXECUTING QUERY READ REQUEST: "+ query);
	}
	
	public void SEND_READ_TO_RECEIVER(String mail, Site receiver) throws UnknownHostException, IOException{
			SOCK = new Socket(receiver.getIpadd(), Tags.PORT);
			OUT = new PrintWriter(SOCK.getOutputStream());
			OUT.println(mail);
			OUT.flush();
	}
	
	// Send READ REQUEST NOTIFICATION
	// first# -> query
	// second# -> area
	public void SEND_READ_REQUEST(String readRequest)
	{
		System.out.println("==STARTING READ REQUEST==");
		Site receiver = null;
		String message[] = readRequest.split("#", 2);
		String mail = "";
		
		if(!Tags.NONE.equals(message[1])){
			
			mail = Tags.READ_EXECUTE+"#"+message[0];
			
			switch(owner.getName()){
				case Tags.CENTRAL: 
						if(Tags.CENTRAL.equals(message[1]))
							EXECUTE_READ_REQUEST(message[0]);
						 break;
				case Tags.PALAWAN:
						if(Tags.PALAWAN.equals(message[1]))
							EXECUTE_READ_REQUEST(message[0]);
						else if(Tags.MARINDUQUE.equals(message[1])){
							receiver = owner.searchConnection(Tags.CENTRAL);
							try {
								SEND_READ_TO_RECEIVER(mail, receiver);
							} catch (Exception e){
								System.out.println(receiver.getName()+ " NOT CONNECTED!");
								receiver = owner.searchConnection(Tags.MARINDUQUE);
								try {
									SEND_READ_TO_RECEIVER(mail, receiver);
								} catch (Exception e1){
									System.out.println(receiver.getName() + " NOT CONNECTED!");
								}
							}
						}else if(Tags.CENTRAL.equals(message[1])){
							receiver = owner.searchConnection(Tags.CENTRAL);
							try{
								SEND_READ_TO_RECEIVER(mail, receiver);
							}catch(Exception e){System.out.println(receiver.getName()+ " NOT CONNECTED!");}
						} break;
						
				default: System.out.println("UNRECOGNIZED USER");
			}
		}else{
			EXECUTE_READ_REQUEST(message[0]);
		}
		
		System.out.println("==READ REQUEST END==");
	 }
}
