package socket;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import controller.Controller;
import model.Site;
import model.Tags;
import view.ClientGUI;

public class ClientResponse implements Runnable{

	//Globals
		Socket sock;
		Scanner INPUT;
		Scanner SEND = new Scanner(System.in);
		PrintWriter OUT;
		ClientGUI clientGUI;
		Controller c;

//-------------------------------------------------------------
		public ClientResponse(Controller c)
		{
			this.c = c;
			this.sock = c.getClient().getSocket();
			this.clientGUI = c.getMainFrame();
		}
//-------------------------------------------------------------
		public void run()
		{
			try
			{
				try
				{
					INPUT = new Scanner(sock.getInputStream());
					OUT = new PrintWriter(sock.getOutputStream());
					OUT.flush();
					CheckStream();
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
//-------------------------------------------------------------
	public void CheckStream()
	{
		while(true)
		{
			RECEIVE();
		}
	}
//-------------------------------------------------------------
	public void RECEIVE()
	{
		
		if(INPUT.hasNext())
		{
			String MESSAGE = INPUT.nextLine();
			
			String msgServer[] = MESSAGE.split("#", 2);
			
			
			switch(msgServer[0]){
			
				case Tags.READ_EXECUTE : executeReadQuery(msgServer[1]); break;
		
				
				default: System.out.println("INVALID COMMAND");
			}
		}
	}
	
	
	
	
	private void executeReadQuery(String query){
		
		
	}

	private void post(String substring) {
		String list[]=substring.split(" ");
		String msg="";
		for (int i = 1; i < list.length; i++) {
			msg+=list[i]+" ";
		}
		
		//chirp.addPost(list[0], msg);
	}
	
	public void filePost(String substring) 
	{
		OUT.println("0x009" + substring);
		OUT.flush();
		
	}
	
	private void following(String substring) {
		//chirp.addFollowing(substring);
		
	}
	private void follow(String substring) {
		//chirp.addFollower(substring);
	
	}
	private void message(String substring) {
		String[] list=substring.split(" ");
		String msg="";
		for (int i = 2; i < list.length; i++) {
			msg+=list[i]+" ";
		}
		//chirp.addMessage(msg, list[0], list[1]);
		
	}
	private void addUnfollowed(String substring) {
		//chirp.addNotification("Unfollowed "+substring);
		//chirp.removeFollowing(substring);
		
	}
	private void addUnfollowNotif(String substring) {
		//chirp.addNotification("Unfollowed by "+substring);
		//chirp.removeFollower(substring);
		
	}
	private void addRequested(String substring) {
		//chirp.addNotification("Sent follow request to "+substring);
	}
	private void addFollowerRequest(String substring) {
		//chirp.addApprovalRequest(substring);
	}
	//-------------------------------------------------------------
	public void setOnline(String text){
		String TEMP1 = text;
		TEMP1 = TEMP1.replace("[", "");
		TEMP1 = TEMP1.replace("]", "");

		String[] CurrentUsers = TEMP1.split(", ");
		System.out.println(CurrentUsers.length);
		//chirp.setOnline(CurrentUsers);
	}
	
	public void disconnect() throws IOException
	{
		OUT.println("0x002");
		OUT.flush();
		sock.close();
		JOptionPane.showMessageDialog(null, "You disconnected!");
		System.exit(0);
	}
		//-------------------------------------------------------------
	public void checkStatus(String text) {
		OUT.println("0x003"+text);
		OUT.flush();
	}
	
	public void selectUser(String text){
		boolean status;
		if(text.charAt(0)=='Y') status=true;
		else status=false;
		text=text.substring(1);
		Object[] options=new Object[3];
		if(!status){
			options[0] = "Follow";
			options[1] = "Message";
			options[2] = "Cancel";
		}
		else{
			options[0] = "Unfollow";
			options[1] = "Message";
			options[2] = "Cancel";
		}
		int n = JOptionPane.showOptionDialog(clientGUI,
			    "Meet your fellow fowl, "+text,
			    "The Nest",
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.INFORMATION_MESSAGE,
			    null,
			    options,
			    options[2]);
		if(n==JOptionPane.YES_OPTION){
			if(!status){
				OUT.println("0x004"+text);
				OUT.flush();
			}
			else{
				OUT.println("0x005"+text);
				OUT.flush();
			}
		}
		if(n==JOptionPane.NO_OPTION){
			String code = JOptionPane.showInputDialog(
			        clientGUI, 
			        "Enter your private message to "+text, 
			        "Message", 
			        JOptionPane.WARNING_MESSAGE
			    );
			if(code!=null){
				OUT.println("0x006"+text+" "+code);
				OUT.flush();
			}
		}
	}

}
