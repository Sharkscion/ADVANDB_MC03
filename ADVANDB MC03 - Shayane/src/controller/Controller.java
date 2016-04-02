package controller;

import java.net.Socket;
import java.util.ArrayList;

import model.QueryFactory;
import model.Site;
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
		clientResponse = new ClientResponse(client, mainFrame);
		mainFrame = new ClientGUI(this, queryFactory.getQuery(), client, clientResponse);
	}
	
	public void getResult(){
		mainFrame.updateTable(queryFactory.getQuery());
	}
}
