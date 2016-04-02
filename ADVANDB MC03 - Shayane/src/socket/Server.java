package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Site;

public class Server {
	
	private ArrayList<Site> clients;
	
	public Server(){
		clients = new ArrayList<Site>();
	}
	
	public ArrayList<Site> getClientList(){
		return clients;
	}
	
	public void addClient(Site client){
		clients.add(client);
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		ArrayList<Socket> clientList = new ArrayList<Socket>();
		ServerSocket serverSock;
		Socket clientSock = null;
		
		try{
			serverSock = new ServerSocket(1234);
			System.out.println("Waiting for clients..");
			while(true){
				clientSock = serverSock.accept();
				clientList.add(clientSock);
				server.addClient(new Site(clientSock));
				
				Thread X=new Thread(new ServerResponse(clientSock, server));
				X.start();
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
			System.out.println("Server");
		}
	}

}
