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
		this.sock=sock;
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
					
					String result[] = MESSAGE.split("#", 2);
					//actions based on command
					switch(result[0]){
						case Tags.READ_REQUEST : sendReadRequest(result[1]); break;
						case Tags.ADD_SITE : addSite(result[1]); break;

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
	
	public Site searchForSite(Socket socket){
		
		Site s = null;
		int x = 0;
		while(server.getClientList().get(x).getSocket()!=sock) x++;
			server.getClientList().get(x);
		return s;
	}
	public void addSite(String username){
		Site s = searchForSite(sock);
		System.out.println(s.getName()+ "is connected!");
	}
	
	private void sendReadRequest(String query){
	
		Site s = searchForSite(sock);
		if("Central".equals(s.getName())){
//			Socket tempSock=server.getClientList().get(i).getSocket();
//			PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//			tempOut.println("0x008"+server.getClientList().get(a).getUsername()+" "+substring);
//			tempOut.flush();
		}
		
		System.out.println("MESSAGE OF CLIENT: " + query);
	}
//	private void post(String substring) throws IOException {
//		int a=0;
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		OUT.println("0x008"+server.getClientList().get(a).getUsername()+" "+substring);
//		OUT.flush();
//		for (int i = 0; i < server.getClientList().size(); i++) {
//			if(i!=a){
//				if(server.getClientList().get(i).isFollowing(server.getClientList().get(a).getUsername())){
//					Socket tempSock=server.getClientList().get(i).getSocket();
//					PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//					tempOut.println("0x008"+server.getClientList().get(a).getUsername()+" "+substring);
//					tempOut.flush();
//				}
//			}
//		}
//		
//	}
//	
//	//changed
//	private void filePost(String substring) throws IOException
//	{
//		int a = 0;
//		FileOutputStream fos = null;
//		System.out.println("substirng inside serverpresposne: " + substring);
//		File newFile = new File(substring);
//		
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		OUT.println("0x009"+server.getClientList().get(a).getUsername()+" "+substring);
//		OUT.flush();
//		for (int i = 0; i < server.getClientList().size(); i++) {
//			if(i!=a){
//				if(server.getClientList().get(i).isFollowing(server.getClientList().get(a).getUsername()))
//				{
//					Socket tempSock = server.getClientList().get(i).getSocket();
//					
//					int count;
//					int packetSize = 65536;
//					byte[] buffer = new byte[packetSize];
//					
//					OutputStream Output = sock.getOutputStream();
//					BufferedInputStream BuffInput = new BufferedInputStream(new FileInputStream(newFile));
//					
//					while ((count = BuffInput.read(buffer)) >= 0) {
//						Output.write(buffer, 0, count);
//					    Output.flush();
//					}
//					
//					ObjectOutputStream tempOut = new ObjectOutputStream(tempSock.getOutputStream());
//					tempOut.writeObject("0x009"+server.getClientList().get(a).getUsername()+" "+fos);
//					tempOut.flush();
//				}
//			}
//		}
//		
//	}
//
//	private void follow(String substring) throws IOException {
//		int i=0;
//		while(!server.getClientList().get(i).getUsername().equals(substring)) i++;
//		
//		int a=0;
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		server.getClientList().get(a).addFollower(substring);
//		server.getClientList().get(i).addFollowing(server.getClientList().get(a).getUsername());
//		
//		OUT.println("0x007"+substring);
//		OUT.flush();
//		
//		Socket tempSock=server.getClientList().get(i).getSocket();
//		PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//		tempOut.println("1x007"+server.getClientList().get(a).getUsername());
//		tempOut.flush();
//		
//		
//	}
//
//	private void message(String substring) throws IOException {
//		String[] list=substring.split(" ");
//		String msg="";
//		for (int j = 1; j < list.length; j++) {
//			msg+=list[j]+" ";
//		}
//		
//		int i=0;
//		while(!server.getClientList().get(i).getUsername().equals(list[0])) i++;
//		
//		int a=0;
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		OUT.println("0x006"+server.getClientList().get(a).getUsername()+" "+list[0]+" "+msg);
//		OUT.flush();
//		
//		Socket tempSock=server.getClientList().get(i).getSocket();
//		PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//		tempOut.println("0x006"+server.getClientList().get(a).getUsername()+" "+list[0]+" "+msg);
//		tempOut.flush();
//	}
//
//	private void unfollow(String substring) throws IOException {
//		int i=0;
//		while(!server.getClientList().get(i).getUsername().equals(substring)) i++;
//		
//		
//		OUT.println("1x005"+substring);
//		OUT.flush();
//		
//		int a=0;
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		Socket tempSock=server.getClientList().get(i).getSocket();
//		PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//		tempOut.println("0x005"+server.getClientList().get(a).getUsername());
//		tempOut.flush();
//		
//		server.getClientList().get(i).remFollower(server.getClientList().get(a).getUsername());
//		server.getClientList().get(a).remFollowing(substring);
//	}
//
//	private void followPending(String substring) throws IOException {
//		int i=0;
//		while(!server.getClientList().get(i).getUsername().equals(substring)) i++;
//		
//		OUT.println("1x004"+substring);
//		OUT.flush();
//		
//		int a=0;
//		while(server.getClientList().get(a).getSocket()!=sock) a++;
//		
//		Socket tempSock=server.getClientList().get(i).getSocket();
//		PrintWriter tempOut=new PrintWriter(tempSock.getOutputStream());
//		tempOut.println("0x004"+server.getClientList().get(a).getUsername());
//		tempOut.flush();
//		
//		
//	}
//
//	private void checkStatus(String substring) {
//		int i=0;
//		while(server.getClientList().get(i).getSocket()!=sock) i++;
//		
//		if(server.getClientList().get(i).isFollowing(substring)){
//			OUT.println("0x003Y"+substring);
//			OUT.flush();
//		}
//		else{
//			OUT.println("0x003N"+substring);
//			OUT.flush();
//		}
//		
//	}
//
//	//removes a person from the online list and sends the username of the person to all sockets
//	public void disconnect(){
//		String remUser;
//		int x=0;
//		while(server.getClientList().get(x).getSocket()!=sock) x++;
//		
//		remUser=server.getClientList().get(x).getUsername();
//		//server.getClientList().remove(x);
//
//		
//		for(int i=1; i<=server.getClientList().size(); i++){
//			try{
//				if(server.getClientList().get(i-1).getSocket().isConnected()){
//					PrintWriter tempOut=new PrintWriter(server.getClientList().get(i-1).getSocket().getOutputStream());
//					tempOut.println("0x002"+remUser+" has disconnected!");
//					tempOut.flush();
//				}
//				
//			}
//			catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		updateOnline();
//	}
//	
//	//sends the new list of online people to all sockets
//	public void updateOnline(){
//		
//		for(int i=1; i <= server.getClientList().size(); i++){
//			try{
//				if(server.getClientList().get(i-1).getSocket().isConnected()){
//					PrintWriter tempOut=new PrintWriter(server.getClientList().get(i-1).getSocket().getOutputStream());
//					tempOut.println("0x001"+server.getClientList());
//					tempOut.flush();
//				}
//			}
//			catch(IOException ex){
//				ex.printStackTrace();
//			}
//			
//		}
//	}
//	
//	//sets the username(at object Person) of the newly logged in person
//	public void addUser(String username){
//		int x=0;
//		while(server.getClientList().get(x).getSocket()!=sock) x++;
//		
//		server.getClientList().get(x).setUsername(username);
//
//		updateOnline();
//	}
	
//	public void changeProfPic(){
//
//		FileOutputStream fos=null;
//		File newFile= new File("C:/Users/weeza/Pictures/chirp/bird.jpg");
//		try {
//			fos = new FileOutputStream(newFile);
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
//		if(!newFile.exists()){
//			try {
//				newFile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		try{
//			OutputStream out = sock.getOutputStream();
//			InputStream in = sock.getInputStream();
//			IOUtils.copy(in, fos);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}

}

