package socket;

import java.io.IOException;
import java.net.Socket;

import model.Site;
import model.Tags;
import controller.Controller;
import view.ClientGUI;
public class Client {

	public static void main(String[] args) {
		Socket MyClient;
		Controller controller;
		
		try {
			MyClient = new Socket("10.2.181.70", 1234);
			
			Site client = new Site(MyClient);
			client.setName(Tags.CENTRAL);
			controller = new Controller(client);

			System.out.println("CLIENT LOCAL ADDRESS: "+ MyClient.getLocalAddress().getHostName());
			//clientGUI.setIPAdd(MyClient.getLocalAddress().getHostName());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}