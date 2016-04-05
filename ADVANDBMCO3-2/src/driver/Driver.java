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
		Site owner = new Site("192.168.1.118", Tags.CENTRAL);
		Controller con = new Controller(owner);
		con.add("192.168.1.125", Tags.PALAWAN);

		Server SER = new Server(con, Tags.PORT);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		ClientGUI mainGUI = new ClientGUI(con);
	}
}
