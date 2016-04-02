package socket;

import java.io.IOException;
import java.net.Socket;

import controller.Controller;
import view.ClientGUI;
public class Client {

	public static void main(String[] args) {
		Socket MyClient;
		Controller controller;
		
		try {
			MyClient = new Socket("192.168.1.148", 444);
			controller = new Controller(MyClient);
			System.out.println("CLIENT LOCAL ADDRESS: "+ MyClient.getLocalAddress());
			//clientGUI.setIPAdd(MyClient.getLocalAddress().getHostName());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}