package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import model.QueryFactory;
import model.Site;
import model.Tags;
import socket.ClientResponse;
import view.ClientGUI;

public class Controller {
	
	private Socket socket;
	private ClientGUI mainFrame;
	private QueryFactory queryFactory;
	private ClientResponse clientResponse;
	private Site client;
	
	public Controller(Site client) {
		
		queryFactory = new QueryFactory();
		this.client = client;	
		clientResponse = new ClientResponse(this);
		mainFrame = new ClientGUI(this, queryFactory.getQuery(), client, clientResponse);
	}

	public void getResult(){
		mainFrame.updateTable(queryFactory.getQuery());
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ClientGUI getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(ClientGUI mainFrame) {
		this.mainFrame = mainFrame;
	}

	public QueryFactory getQueryFactory() {
		return queryFactory;
	}

	public void setQueryFactory(QueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public ClientResponse getClientResponse() {
		return clientResponse;
	}

	public void setClientResponse(ClientResponse clientResponse) {
		this.clientResponse = clientResponse;
	}

	public Site getClient() {
		return client;
	}

	public void setClient(Site client) {
		this.client = client;
	}
	
	public void add(String ipAddress, String username){
		
		try {
			Site s = new Site(new Socket(ipAddress,Tags.PORT), username);
			this.client.addConnection(s);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void sendReadRequest(String readRequest){
		
			String message[] = readRequest.split("#", 2);
			String mail = "";
			
			Site receiver = null;
			Site receiver2 = null;
			
			boolean isBoth = false;
			
			if(!message[1].equals(Tags.NONE)){
				
				mail = Tags.READ_EXECUTE+"#"+message[0];
				
				switch(client.getName()){
					case Tags.PALAWAN :
							if(Tags.PALAWAN.equals(message[1]))
								receiver = client;
							else if(Tags.MARINDUQUE.equals(message[1])){
								receiver = client.searchForSiteUsername(Tags.CENTRAL); 
								if(receiver == null)
									receiver = client.searchForSiteUsername(Tags.MARINDUQUE);
							}else if (Tags.CENTRAL.equals(message[1])){
								receiver = client.searchForSiteUsername(Tags.CENTRAL); 
								if(receiver == null){
									receiver = client.searchForSiteUsername(Tags.MARINDUQUE);
									receiver2 = client;
									isBoth = true;
								}
							} break;
					
					case Tags.MARINDUQUE :
							if(Tags.MARINDUQUE.equals(message[1]))
								receiver = client;
							else if (Tags.PALAWAN.equals(message[1])){
								receiver = client.searchForSiteUsername(Tags.CENTRAL);
								if(receiver == null)
									receiver = client.searchForSiteUsername(Tags.PALAWAN);
							}else if (Tags.CENTRAL.equals(message[1])){
								receiver = client.searchForSiteUsername(Tags.CENTRAL);
								if(receiver == null){
									receiver = client.searchForSiteUsername(Tags.PALAWAN);
									receiver2 = client;
									isBoth = true;
								}
							}break;
					
					case Tags.CENTRAL : 
							if(Tags.CENTRAL.equals(message[1]))
								receiver = client;
							break;
					default: System.out.println("WHO YOU REQUESTING TO READ QUERY......");
				}
				
			}else{
				mail = Tags.READ_EXECUTE+"#"+message[0];
				receiver = client;
			}
			
			if(!isBoth && receiver != null){
				try {
					PrintWriter out = new PrintWriter(receiver.getSocket().getOutputStream());
					out.println(mail);
					out.flush();
				} catch (IOException e) { e.printStackTrace(); }
			}else if(isBoth && receiver!=null && receiver2!=null){
				/**query requesting for all information from 2 nodes since the central is down**/
				try {
					PrintWriter out1 = new PrintWriter(receiver.getSocket().getOutputStream());
					PrintWriter out2 = new PrintWriter(receiver2.getSocket().getOutputStream());
					
					out1.println(mail);
					out2.println(mail);
					
					out1.flush();
					out2.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
			System.out.println("MESSAGE OF CLIENT: " + readRequest);
		}
	
	
}
