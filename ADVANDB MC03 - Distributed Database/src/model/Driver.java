package model;

import java.io.IOException;
import java.net.Socket;

import socket.Server;
import controller.Controller;

public class Driver{
	
	public static void main(String args[]){
		
		Site s;
		
			s = new Site(null, Tags.CENTRAL);
			Controller con = new Controller(s);
			Server server = new Server(con, Tags.PORT);
			Thread X = new Thread();
			X.start();
			
			con.add("10.2.181.70", Tags.PALAWAN);
			
	}

	
 	
}
