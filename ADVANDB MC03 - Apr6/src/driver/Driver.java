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
		Site owner = new Site("172.20.10.4", Tags.CENTRAL);
		Controller con = new Controller(owner);
		con.add("172.20.10.3", Tags.PALAWAN); //camille
		con.add("172.20.10.5", Tags.MARINDUQUE); //borja

		Server SER = new Server(con, Tags.PORT);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		ClientGUI mainGUI = new ClientGUI(con);
	}
}
