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
		Site owner = new Site("192.168.1.140", Tags.CENTRAL);
		Controller con = new Controller(owner);
		Server SER = new Server(con, PORT);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		
		con.add("192.168.1.138", "Palawan");
		//con.add("10.100.203.93", "Marinduque");
		ClientGUI mainGUI = new ClientGUI(con);
	}
}
