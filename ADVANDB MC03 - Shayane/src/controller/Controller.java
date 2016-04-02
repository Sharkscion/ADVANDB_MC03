package controller;

import java.net.Socket;
import java.util.ArrayList;

import model.QueryFactory;
import socket.ClientResponse;
import view.ClientGUI;

public class Controller {
	
	private Socket socket;
	private ClientGUI mainFrame;
	private QueryFactory queryFactory;
	private ClientResponse clientResponse;
	public Controller(Socket socket) {
		
		queryFactory = new QueryFactory();
		this.socket = socket;
		clientResponse = new ClientResponse(socket, mainFrame);
		mainFrame = new ClientGUI(this, queryFactory.getQuery(), this.socket, clientResponse);
	}
	
	public void getResult(){
		mainFrame.updateTable(queryFactory.getQuery());
	}
}
