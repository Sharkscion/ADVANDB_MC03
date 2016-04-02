package model;

import java.io.IOException;
import java.net.Socket;

import socket.Server;
import controller.Controller;

public class Driver{
	
	public static void main(String args[]){
		
		Site s;
		try {
			s = new Site(new Socket("",Tags.PORT), Tags.CENTRAL);
			Controller con = new Controller(s);
			Server server = new Server(con, Tags.PORT);
			Thread X = new Thread();
			X.start();
			
			con.add("10.2.181.70", Tags.PALAWAN);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
 	
}
