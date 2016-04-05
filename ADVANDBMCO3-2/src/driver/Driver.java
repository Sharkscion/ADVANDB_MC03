package driver;

import controller.Controller;
import model.Site;
import model.Tags;
import network.Server;
import view.ClientGUI;
public class Driver
{
	final static int PORT = 1234;
	
	public static void main(String[] args){
		
		// Starting the Server
		Site owner = new Site("10.2.180.247", Tags.MARINDUQUE);
		Controller con = new Controller(owner);
		con.add("10.100.199.129", Tags.PALAWAN);
		con.add("10.150.199.129", Tags.CENTRAL);
		
		Server SER = new Server(con, 6969);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		ClientGUI mainGUI = new ClientGUI(con);
	}
}
