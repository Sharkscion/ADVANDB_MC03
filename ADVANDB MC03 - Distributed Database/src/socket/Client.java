package socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import model.Site;
import model.Tags;
import controller.Controller;
import view.ClientGUI;
public class Client {

	private Controller controller;
	private Socket sock;
	private Scanner INPUT;
	private Scanner SEND = new Scanner(System.in);
	private PrintWriter OUT;
	
	public Client(Controller controller, Socket socket){
		this.controller = controller;
		this.sock = socket;
		
		RECEIVE();
	}
	
	public void RECEIVE(){
		
		try
		{
			try
			{
				INPUT = new Scanner(sock.getInputStream());
				OUT = new PrintWriter(sock.getOutputStream());
				OUT.flush();
				
				if(INPUT.hasNext())
				{
					String MESSAGE = INPUT.nextLine();
					System.out.println("MESSAGE FROM SERVER: "+ MESSAGE);
					String msgServer[] = MESSAGE.split("#", 2);
					
					
					switch(msgServer[0]){
					
						case Tags.READ_EXECUTE : executeReadQuery(msgServer[1]); break;
				
						
						default: System.out.println("INVALID COMMAND");
					}
				}
			}
			finally
			{
				sock.close();
			}
		}
		catch(Exception X)
		{
			X.printStackTrace();
		}		
	}
	
	private void executeReadQuery(String query){
		
		System.out.println("QUERY: "+ query);
	}


}