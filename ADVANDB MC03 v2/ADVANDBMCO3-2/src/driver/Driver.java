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
<<<<<<< HEAD:ADVANDB MC03 v2/ADVANDBMCO3-2/src/driver/Driver.java
		Site owner = new Site("10.2.180.27", Tags.CENTRAL);
		Controller con = new Controller(owner);
		con.add("10.2.181.65", Tags.PALAWAN);
		con.add("10.150.199.129", Tags.MARINDUQUE);
		
=======
		Site owner = new Site("10.100.201.78", Tags.PALAWAN);
		Controller con = new Controller(owner);
		con.add("10.2.181.65", Tags.CENTRAL);

>>>>>>> parent of 193d7a8... mergeing result sets:ADVANDBMCO3-2/src/driver/Driver.java
		Server SER = new Server(con, Tags.PORT);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		ClientGUI mainGUI = new ClientGUI(con);
	}
}
