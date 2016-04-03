package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import controller.Controller;
import model.Site;

public class Server implements Runnable{
	
	private ArrayList<Site> clients;
	private Controller controller;
	private int port;
	private static Client client;
	
	public Server(Controller controller, int port ){
		this.controller = controller;
		this.port = port;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("SERVER Running");
		try{
			ServerSocket serverSock = new ServerSocket(port);
			System.out.println("Waiting for clients..");
			while(true){
				Socket clientSock = serverSock.accept();
				client = new Client(controller, clientSock);
				System.out.println("New Client Connected: "+ clientSock.getLocalAddress().getHostName());
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
			System.out.println("Server error");
		}
	}

}
