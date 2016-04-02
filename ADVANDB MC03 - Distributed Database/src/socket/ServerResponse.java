package socket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import model.Site;
import model.Tags;

public class ServerResponse implements Runnable{
	private Socket sock;
	private Scanner INPUT;
	private PrintWriter OUT;
	private Server server;
	private ObjectOutputStream obOut;
	private ObjectInputStream obIn;
	String MESSAGE = "";
	
	public ServerResponse(Socket sock, Server server) {
		System.out.println("SOCKET: ");
		this.sock = sock;
		this.server=server;
	}

	//checks socket connection
	public void CheckConnection() throws IOException
	{
		if(!sock.isConnected())
		{
			//removes socket
			for(int i=1; i<=server.getClientList().size(); i++){
				if(server.getClientList().get(i).getSocket()==sock)
				{
					server.getClientList().remove(i);
				}
			}
			//notifies all sockets about the disconnected socket
			for(int i=1; i<=server.getClientList().size(); i++){
				Socket tempSock=server.getClientList().get(i-1).getSocket();
				PrintWriter tempOut = new PrintWriter(tempSock.getOutputStream());
				tempOut.println(tempSock.getLocalAddress().getHostName() + " disconnected!");
				tempOut.flush();
				//Show disconnection at SERVER
				System.out.println(tempSock.getLocalAddress().getHostName() + " disconnected!");
				System.out.println(server.getClientList().get(i-1).getSocket().getLocalAddress().getHostName() + " disconnected!");
			}
		}
	}
	
	@Override
	public void run() {
		
		try
		{
			try
			{
				//I/O stream
				INPUT = new Scanner(sock.getInputStream());
				OUT = new PrintWriter(sock.getOutputStream());
				
				while(true)
				{
					CheckConnection();

					//skip if no more input from sockets
					if(!INPUT.hasNext())
					{ return;}

					MESSAGE = INPUT.nextLine();
					System.out.println("Client said: " + MESSAGE);
					
					String msgClient[] = MESSAGE.split("#", 2);
					//actions based on command
					switch(msgClient[0]){
						case Tags.READ_REQUEST : sendReadRequest(msgClient[1]); break;
						case Tags.ADD_SITE : addSite(msgClient[1]); break;
						case Tags.EDIT_SITE : editSite(msgClient[1]);break;
						default: System.out.println("INVALID COMMAND");
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		catch(Exception X)
		{
			System.out.print(X);
		}
		
	}
	
	public Site searchForSiteSocket(Socket socket){
		
		Site s = null;
		int x = 0;
		while(server.getClientList().get(x).getSocket()!=sock) x++;
			s=server.getClientList().get(x);
		return s;
	}
	
	public Site searchForSiteUsername(String username){
		Site s = null;
		int x = 0;
		while(!server.getClientList().get(x).getName().equals(username)) x++;
			s=server.getClientList().get(x);
		return s;
	}
	
	public void editSite(String username){
		Site s = searchForSiteSocket(sock);
		s.setName(username);
		//System.out.println(username+ "is connected!");
	}
	public void addSite(String clientDetails){
		
		String details[] = clientDetails.split("#");
		
		try {
			Site s = new Site(new Socket(details[0], Tags.PORT));
			s.setName(details[1]);
			server.addClient(s);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void sendReadRequest(String readRequest){
	
		String message[] = readRequest.split("#", 2);
		String mail = "";
		
		Site s = searchForSiteSocket(sock);
		Site receiver = null;
		Site receiver2 = null;
		
		boolean isBoth = false;
		
		if(!message[1].equals(Tags.NONE)){
			
			mail = Tags.READ_EXECUTE+"#"+message[0];
			
			switch(s.getName()){
				case Tags.PALAWAN :
						if(Tags.PALAWAN.equals(message[1]))
							receiver = s;
						else if(Tags.MARINDUQUE.equals(message[1])){
							receiver = searchForSiteUsername(Tags.CENTRAL); 
							if(receiver == null)
								receiver = searchForSiteUsername(Tags.MARINDUQUE);
						}else if (Tags.CENTRAL.equals(message[1])){
							receiver = searchForSiteUsername(Tags.CENTRAL); 
							if(receiver == null){
								receiver = searchForSiteUsername(Tags.MARINDUQUE);
								receiver2 = s;
								isBoth = true;
							}
						} break;
				
				case Tags.MARINDUQUE :
						if(Tags.MARINDUQUE.equals(message[1]))
							receiver = s;
						else if (Tags.PALAWAN.equals(message[1])){
							receiver = searchForSiteUsername(Tags.CENTRAL);
							if(receiver == null)
								receiver = searchForSiteUsername(Tags.PALAWAN);
						}else if (Tags.CENTRAL.equals(message[1])){
							receiver = searchForSiteUsername(Tags.CENTRAL);
							if(receiver == null){
								receiver = searchForSiteUsername(Tags.PALAWAN);
								receiver2 = s;
								isBoth = true;
							}
						}break;
				
				case Tags.CENTRAL : 
						if(Tags.CENTRAL.equals(message[1]))
							receiver = s;
						break;
				default: System.out.println("WHO YOU REQUESTING TO READ QUERY......");
			}
			
		}else{
			mail = Tags.READ_EXECUTE+"#"+message[0];
			receiver = s;
		}
		
		if(!isBoth && receiver != null){
			try {
				PrintWriter out = new PrintWriter(receiver.getSocket().getOutputStream());
				out.println(mail);
				out.flush();
			} catch (IOException e) { e.printStackTrace(); }
		}else if(isBoth && receiver!=null && receiver2!=null){
			/**query requesting for all information from 2 nodes since the central is down**/
			try {
				PrintWriter out1 = new PrintWriter(receiver.getSocket().getOutputStream());
				PrintWriter out2 = new PrintWriter(receiver2.getSocket().getOutputStream());
				
				out1.println(mail);
				out2.println(mail);
				
				out1.flush();
				out2.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		System.out.println("MESSAGE OF CLIENT: " + readRequest);
	}

}

