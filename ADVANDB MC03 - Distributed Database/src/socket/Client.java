package socket;

import java.io.IOException;
import java.net.Socket;

import model.Site;
import controller.Controller;
import view.ClientGUI;
public class Client {

	public static void main(String[] args) {
		Socket MyClient;
		Controller controller;
		
		try {
			MyClient = new Socket("192.168.56.1", 1234);
			
			Site client = new Site(MyClient);
			client.setName("Palawan");
			controller = new Controller(client);

			System.out.println("CLIENT LOCAL ADDRESS: "+ MyClient.getLocalAddress().getHostName());
			//clientGUI.setIPAdd(MyClient.getLocalAddress().getHostName());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}