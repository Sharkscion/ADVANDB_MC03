package network;

import java.net.ServerSocket;
import java.net.Socket;

import controller.Controller;

public class Server implements Runnable {

	int port;
	static Client c;
	Controller controller;
	
	public Server(Controller controller, int port)
	{
		this.controller = controller;
		this.port = port;
	}
	
	// Thread process
	 public void run(){
		 	System.out.println("==SERVER IS RUNNING==");
			try{

				ServerSocket SS = new ServerSocket(port); //Opens the server to connection
				System.out.println("==WAITING FOR CLIENTS TO CONNECT...==");
				while(true){	// Waits for connections
				//	SS.setSoTimeout(10000);
					Socket X = SS.accept(); 	// Accepts connections
					c = new Client(controller, X); 			// Creates a new client for each connection
					//X.close();
				}
			}
			catch(Exception ex){
				System.out.println(ex);
			}
			System.out.println("==SERVER SAID GOODBYE==");
	 }
}
