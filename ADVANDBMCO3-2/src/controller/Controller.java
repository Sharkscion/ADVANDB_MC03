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
import model.TransactionInterface;
import model.action.readAction;
import model.action.writeAction;

public class Controller
{
	// database manager?
	
	Site owner;
	String type;
	Site central;
	Site marin;
	Site palawan;
	final static int Port = 1234;
	
	private Socket SOCK;
	private PrintWriter OUT;
	
	public Site getCentral() {
		return central;
	}
	
	public Site getMarin() {
		return marin;
	}
	
	public Site getPalawan() {
		return palawan;
	}
	
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		OUT = null;
		// instantiate database manager
	}
	
	public String getType()
	{
		return type;
	}
	
	public void add(String ip, String name)
	{
		Site newSite = new Site(ip,name);
		owner.addConnection(newSite);
	}
	
	
//	public void CHECK_CONNECTION() throws IOException
//	{
//		for(int i = 0; i<owner.getConnectionList().size(); i++){
//			Socket s = new Socket();
//			
//		}
//		if(!sock.isConnected())
//		{
//			//removes socket
//			for(int i=1; i<=server.getClientList().size(); i++){
//				if(server.getClientList().get(i).getSocket()==sock)
//				{
//					server.getClientList().remove(i);
//				}
//			}
//			//notifies all sockets about the disconnected socket
//			for(int i=1; i<=server.getClientList().size(); i++){
//				Socket tempSock=server.getClientList().get(i-1).getSocket();
//				PrintWriter tempOut = new PrintWriter(tempSock.getOutputStream());
//				tempOut.println(tempSock.getLocalAddress().getHostName() + " disconnected!");
//				tempOut.flush();
//				//Show disconnection at SERVER
//				System.out.println(tempSock.getLocalAddress().getHostName() + " disconnected!");
//				System.out.println(server.getClientList().get(i-1).getSocket().getLocalAddress().getHostName() + " disconnected!");
//			}
//		}
//	}
	
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
						}else{
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

	// Receive POST notification
		public void ReadingAction(String ip, byte [] bytes, String senderip)
		{
			System.out.println("ReadAction (start)");
			
			// Convert bytes to string
			String string = "";
			try {
				string = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.out.println("ERROR READING ACTION");
			}
			
			// Get message (until NULL or EOF)
			String message = getUntilSpaceOrNull(string, 'e');
			
			
			// If character after message is null, display regular message
			System.out.println(ip + " " +  message);
			
			final CyclicBarrier cb = new CyclicBarrier(2);				
            Transaction t = new Transaction(cb, TransactionInterface.READ_UNCOMMITTED, TransactionInterface.COMMIT);
            t.addAction(new writeAction(t, 1, 1, 5));
            //t.addAction(new writeAction(t, 2, 1, 5));
            //t.addAction(new readAction(t, 2, 2));
			Thread thread1 = new Thread(t);
            Transaction t2 = new Transaction(cb, TransactionInterface.READ_UNCOMMITTED, TransactionInterface.COMMIT);
            t2.addAction(new writeAction(t, 2, 1, 5));
            Thread thread2 = new Thread(t2);
            
			System.out.println(ip + " " +  message);
			thread1.start();
			thread2.start();
			
			try {
				cb.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (BrokenBarrierException e1) {
				e1.printStackTrace();
			}
			
			System.out.println("ReadAction (end)");
		}
		

		// Returns string cut off at either space, null or eof, depending on c
		public String getUntilSpaceOrNull(String bytesinstring, char c){
			System.out.println("getUntilSpaceOrNull");
			
			int i = 0;											// Character index
			char [] bytesinchar = bytesinstring.toCharArray();	// String converted to char array
			 
			// Space or null
			if(c == 'b')
				while(bytesinchar[i] != ' ' && bytesinchar[i] != '\0')
					i++;
			  
			// Null
			else if(c == 'n')
				while(bytesinchar[i] != '\0')
					i++;
			  
			// Space
			else if(c == 's')
				while(bytesinchar[i] != ' ')
					i++;
			  
			// Null or EOF
			else if(c == 'e')
				while(bytesinchar[i] != '\0' && bytesinchar[i] != '\u001a')
					i++;
			  
			// Return cut up string
			return bytesinstring.substring(0, i);
		}
}
