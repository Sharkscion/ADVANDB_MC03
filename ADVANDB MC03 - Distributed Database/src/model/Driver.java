package model;

import java.io.IOException;
import java.net.Socket;

import socket.Server;
import controller.Controller;

public class Driver{
	
	public static void main(String args[]){
		
		Site s;
		
			s = new Site("192.168.1.140", Tags.CENTRAL);
			Controller con = new Controller(s);
			con.add("192.168.1.138", Tags.PALAWAN);
			Server server = new Server(con, Tags.PORT);
			Thread X = new Thread();
			X.start();			
	}

	
 	
}
