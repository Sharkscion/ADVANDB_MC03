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
			MyClient = new Socket("192.168.56.1", 1234);
			
			Site client = new Site(MyClient);
			client.setName(Tags.CENTRAL);
			controller = new Controller(client);

			controller.add("10.2.181.70", Tags.PALAWAN);
			
			System.out.println("CLIENT LOCAL ADDRESS: "+ MyClient.getLocalAddress().getHostName());
			//clientGUI.setIPAdd(MyClient.getLocalAddress().getHostName());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}