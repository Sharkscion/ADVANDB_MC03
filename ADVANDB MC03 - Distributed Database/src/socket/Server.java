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
		clients = new ArrayList<Site>();
		this.port = port;
	}
	
	public ArrayList<Site> getClientList(){
		return clients;
	}
	
	public void addClient(Site client){
		clients.add(client);
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("SERVER");
		try{
			ServerSocket serverSock = new ServerSocket(port);
			System.out.println("Waiting for clients..");
			while(true){
				Socket clientSock = serverSock.accept();
				client = new Client(controller, clientSock);

			}
		}
		catch(IOException e)
		{
			System.out.println(e);
			System.out.println("Server");
		}
	}

}
