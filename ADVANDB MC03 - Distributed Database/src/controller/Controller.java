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
			Site s = new Site(new Socket(ipAddress,Tags.PORT));
			this.client.addConnection(username);
			
			PrintWriter OUT = new PrintWriter(this.client.getSocket().getOutputStream());
			OUT.println(Tags.ADD_SITE + "#" + ipAddress + "#" + username); 
			OUT.flush();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
