package network;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class cDriver extends JFrame implements ActionListener {

	static String 			username; 	// My own username
	static String			profpic;	// Filename of profile picture
	static PrintWriter 		pw;		  	// General PrintWriter for sending out the syntax
	static JTextField 		chatip; 	// Post text field
	
	// Main option buttons
	static JButton			changepic;	// Main Window's change profile picture button
	static JButton 			send;		// Main Window's send button
	static JButton 			exit;		// Main Window's exit button
	static JButton 			pm;			// Main Windows's PM Button
	static JButton 			follow; 	// Main Window's follow button
	static JButton 			unfollow;	// Main Window's unfollow Button
	static JButton			sendwfile;	// Main Window's send (post) with file button
	static JButton 			file;		// Main Window's send file button
	
	static Socket  			chatusers; 	// Your socket
	
	// Panels
	static JPanel			mainPanel;	
	static JPanel			profPicPanel;	// Updating profile picture
	static JPanel			messagePanel;	// Displays messages and notifications
	static JScrollPane		messagePane;
	static JPanel			followerPanel;	// Displays users following you
	static JScrollPane		followerPane;
	static JPanel			followingPanel;	// Displays users you are following
	static JScrollPane		followingPane;
	
	// Lists of users and ips
	static ArrayList<String> followers = new ArrayList<String>();		// List of Followers' IPs
	static ArrayList<String> followersName = new ArrayList<String>(); 	// List of Followers' Names
	static ArrayList<String> following = new ArrayList<String>(); 		// List of Following's IPs
	static ArrayList<String> followingName = new ArrayList<String>(); 	// List of Following's Names
	static ArrayList<String> OnlineIPs = new ArrayList<String>(); 		// List of Online Users' IPs
	static ArrayList<String> OnlineNames = new ArrayList<String>(); 	// List of Online Users' Names
	
	// Login Window Components
	private static JFrame		loginWindow;
	private static JTextField 	usernamefield;
	private static JTextField 	profpicfield;
	private static JButton		login;
	
	// Change Profile Picture Window Components
	private static JFrame		profilePictureWindow;
	private static JTextField	profilepicfield;
	private static JButton		choosepic;
	
	// PM Window Components
	public static JFrame PMWindow = new JFrame();
	public static JTextField TF_UserNamePMBox = new JTextField(20);
	public static JTextField TF_PMBox = new JTextField(20);
	private static JButton B_ENTERPM = new JButton("Enter w/o img");
	private static JButton B_ENTERPMIMG = new JButton("Enter w/ img");
	private static JLabel L_EnterUserNameToPM = new JLabel("Username to PM: ");
	private static JLabel L_EnterPM = new JLabel("Enter message: ");
	public static JTextField TF_PMIMGBox = new JTextField(20);	
	private static JLabel L_EnterIMGPM = new JLabel("Enter image name: ");
	private static JPanel P_PM = new JPanel();
	
	// Approve Window Components
	public static JFrame ApproveWindow = new JFrame();
	private static JLabel L_Approve = new JLabel();
	private static JButton B_YESApprove= new JButton("ACCEPT");
	private static JButton B_NOApprove = new JButton("REJECT");
	private static JPanel P_Approve = new JPanel();

	// Follow Window Components
	public static JFrame FollowWindow = new JFrame();
	public static JTextField TF_UserNameFollowBox = new JTextField(20);
	private static JButton B_ENTERFollow = new JButton("Enter");
	private static JLabel L_EnterUserNameToFollow = new JLabel("Username to Follow: ");
	private static JPanel P_Follow = new JPanel();
	
	// UnFollow Window Components
	public static JFrame UnFollowWindow = new JFrame();
	public static JTextField TF_UserNameUnFollowBox = new JTextField(20);
	private static JButton B_ENTERUnFollow = new JButton("Enter");
	private static JLabel L_EnterUserNameToUnFollow = new JLabel("Username to UnFollow: ");
	private static JPanel P_UnFollow = new JPanel();
	
	
	// File Sending Window Components
	public static JFrame FileSendWindow = new JFrame();
	public static JTextField TF_FileNameSendBox = new JTextField(20);
	private static JButton B_ENTERFileSend = new JButton("Enter");
	private static JLabel L_EnterFileSend = new JLabel("Enter Filename to send: ");
	private static JPanel P_FileSend = new JPanel();
	
	// Post Image Window Components
	private static JFrame SENDWFILEWindow = new JFrame();
	private static JTextField TF_SENDWFILENAMEBox = new JTextField(20);
	private static JTextField TF_SENDWFILEMSGBox = new JTextField(20);
	private static JButton B_ENTERSENDFILE = new JButton("Enter w/ img");
	private static JLabel L_EnterSENDFILENAME = new JLabel("Image name to PM: ");
	private static JLabel L_EnterMSGSENDFILE = new JLabel("Enter message: ");
	private static JPanel P_SENDFILE = new JPanel();
	
	// Temporary Variables
	private static String tempIP = null, tempName = null;
	private static byte [] tempBytes = null;
	
	// Displaying images and playing wavs
	private static ArrayList<String> WavFile;
	private static ArrayList<JButton> btnWav;
	private static ArrayList<ActionListener> wavListeners;
	private static ArrayList<JButton> stopWav;
	private static ArrayList<Clip> clip;
	private static ArrayList<String> displayImage;
	private static ArrayList<JButton> btnImage;
	private static ArrayList<ActionListener> imageListeners;
	
	// Constant Variables
	final static int PORT = 1234;
	
	// cDriver Constructor
	// Main Window
	public cDriver(String theusername, String theprofpic)
	{
		
		super(theusername + "'s Chatbox");
		username = theusername;
		profpic = theprofpic;
		
		WavFile = new ArrayList<String>();
		btnWav = new ArrayList<JButton>();
		wavListeners = new ArrayList<ActionListener>();
		stopWav = new ArrayList<JButton>();
		clip = new ArrayList<Clip>();
		displayImage = new ArrayList<String>();
		btnImage = new ArrayList<JButton>();
		imageListeners = new ArrayList<ActionListener>();
		
		// Adding online users
		//OnlineIPs.add("192.168.43.210");
		//OnlineNames.add("Avril");
		//OnlineIPs.add("192.168.43.103");
		//OnlineNames.add("XGB");
		//OnlineIPs.add("192.168.43.100");
		//OnlineNames.add("Shark");
		//OnlineIPs.add("192.168.43.232");
		//OnlineNames.add("Bry");
		
		
		//OnlineIPs.add("10.100.215.53");
		//OnlineNames.add("Kerrbie");
				
		OnlineIPs.add("192.168.1.115");
		OnlineNames.add("Gelo");
		
		// Option buttons
		send 	= new JButton("Send");
		sendwfile = new JButton("S w/pic");
		exit 	= new JButton("Exit");
		pm 	 	= new JButton("PM");
		follow	= new JButton("Follow");
		unfollow = new JButton("Unfollow");
		file = new JButton("File");
		changepic = new JButton("Change Pic");
		
		// Adding action listeners to option buttons
		send.addActionListener(this);
		sendwfile.addActionListener(this);
		exit.addActionListener(this);
		pm.addActionListener(this);
		follow.addActionListener(this);
		unfollow.addActionListener(this);
		file.addActionListener(this);
		changepic.addActionListener(this);
		
		// Message text field
		chatip = new JTextField(35);
		
		// Adding action listeners to approve button commands
		B_YESApprove.addActionListener(this);
		B_NOApprove.addActionListener(this);
		
	// LAYOUT
		setSize(600,500);
		setLocationRelativeTo(null);
		
		// Main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBounds(0,0,600,500);
		add(mainPanel);
		setVisible(true);
		
		// Panel for profile picture
		buildProfPicPanel();
		
		// Panel for message posting
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(BorderFactory.createTitledBorder("Post to followers"));
		chatPanel.setBounds(120,15,450,105);
		chatPanel.add(chatip);
		chatPanel.add(send);
		chatPanel.add(sendwfile);
		chatPanel.add(pm);
		chatPanel.add(follow);
		chatPanel.add(unfollow);
		chatPanel.add(file);
		mainPanel.add(chatPanel);
		
		// Panel for incoming messages
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePane = new JScrollPane(messagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messagePane.setBorder(BorderFactory.createTitledBorder("Feed"));
		messagePane.setBounds(15,140,400,300);
		mainPanel.add(messagePane);
		
		// Panel listing followers
		followerPanel = new JPanel();
		buildFollowerPanel();
		
		// Panel listing following
		followingPanel = new JPanel();
		buildFollowingPanel();
		
	// WINDOWS
		
		// Change profile picture
		buildProfilePictureWindow();
		
		// PM
		PMWindow.setTitle("Private Message");
		PMWindow.setSize(400, 170);
		PMWindow.setLocation(250, 200);
		PMWindow.setResizable(false);
		P_PM.add(L_EnterUserNameToPM);
		P_PM.add(TF_UserNamePMBox);
		P_PM.add(L_EnterPM);
		P_PM.add(TF_PMBox);
		P_PM.add(L_EnterIMGPM);
		P_PM.add(TF_PMIMGBox);	
		P_PM.add(B_ENTERPMIMG);
		P_PM.add(B_ENTERPM);
		B_ENTERPMIMG.addActionListener(this);
		B_ENTERPM.addActionListener(this);
		PMWindow.add(P_PM);
		PMWindow.setVisible(false);
		
		// Post (with image)
		SENDWFILEWindow.setTitle("Send Image");
		SENDWFILEWindow.setSize(400, 120);
		SENDWFILEWindow.setLocation(250, 200);
		SENDWFILEWindow.setResizable(false);
		P_SENDFILE.add(L_EnterMSGSENDFILE);
		P_SENDFILE.add(TF_SENDWFILEMSGBox);
		P_SENDFILE.add(L_EnterSENDFILENAME);
		P_SENDFILE.add(TF_SENDWFILENAMEBox);
		P_SENDFILE.add(B_ENTERSENDFILE);
		B_ENTERSENDFILE.addActionListener(this);
		SENDWFILEWindow.add(P_SENDFILE);
		
		// Approve window
		buildApproveWindow("");
		
		// Follow window
		FollowWindow.setTitle("Follow User");
		FollowWindow.setSize(400, 120);
		FollowWindow.setLocation(250, 200);
		FollowWindow.setResizable(false);
		P_Follow.add(L_EnterUserNameToFollow);
		P_Follow.add(TF_UserNameFollowBox);
		P_Follow.add(B_ENTERFollow);
		FollowWindow.add(P_Follow);
		B_ENTERFollow.addActionListener(this);
		FollowWindow.setVisible(false);
		
		// Unfollow window
		UnFollowWindow.setTitle("UnFollow User");
		UnFollowWindow.setSize(400, 120);
		UnFollowWindow.setLocation(250, 200);
		UnFollowWindow.setResizable(false);
		P_UnFollow.add(L_EnterUserNameToUnFollow);
		P_UnFollow.add(TF_UserNameUnFollowBox);
		P_UnFollow.add(B_ENTERUnFollow);
		UnFollowWindow.add(P_UnFollow);
		B_ENTERUnFollow.addActionListener(this);
		UnFollowWindow.setVisible(false);
		
		// File send
		FileSendWindow.setTitle("Send Files");
		FileSendWindow.setSize(400, 120);
		FileSendWindow.setLocation(250, 120);
		FileSendWindow.setResizable(false);
		P_FileSend.add(L_EnterFileSend);
		P_FileSend.add(TF_FileNameSendBox);
		P_FileSend.add(B_ENTERFileSend);
		B_ENTERFileSend.addActionListener(this);
		FileSendWindow.add(P_FileSend);
		FileSendWindow.setVisible(false);
	}
	
	/*public static void main(String[] args){
		
		// Starting the Server
		Server SER = new Server(PORT);
		Thread X = new Thread(SER);
		X.start();	// Runs the server process
		
		// Login asking for username and profile picture
		loginWindow();
	}*/
	
// WINDOWS
		
	// Login window
	public static void loginWindow(){
		loginWindow = new JFrame("Login to Chat");
		loginWindow.setSize(400,120);
		loginWindow.setVisible(true);

		JPanel mPanel = new JPanel();
		JPanel containerPanel = new JPanel();
		mPanel.setLayout(new GridLayout(2,2));
		containerPanel.add(mPanel);
		loginWindow.add(containerPanel, BorderLayout.CENTER);
		
		usernamefield = new JTextField(15);
		profpicfield = new JTextField(15);
		login = new JButton("Login");
		
		mPanel.add(new JLabel("Username: "));
		mPanel.add(usernamefield);
		mPanel.add(new JLabel("Profile Picture File Name: "));
		mPanel.add(profpicfield);
		containerPanel.add(login, BorderLayout.SOUTH);
		
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a) {
				if(a.getSource() == login){
					String theusername = usernamefield.getText();
					String theprofpic = profpicfield.getText();
					
					usernamefield.setText("");
					profpicfield.setText("");
					
					loginWindow.setVisible(false);
					
					new cDriver(theusername, theprofpic);
					
					System.out.println("Login (complete)");
				}
				
			}
		});	
		
		loginWindow.setLocationRelativeTo(null);
	}
	
	// Build change profile picture window
	public static void buildProfilePictureWindow(){
		profilePictureWindow = new JFrame("Change Profile Picture");
		profilePictureWindow.setSize(420, 80);
		profilePictureWindow.setVisible(false);
		
		JPanel profilepicPanel = new JPanel();
		profilepicfield = new JTextField(20);
		choosepic = new JButton("Change Profile Picture");
		
		choosepic.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				profpic = profilepicfield.getText();
				for(int i = 0; i < followers.size(); i++){
					IMG(followers.get(i), PORT);
				}
				profilepicfield.setText("");
				profilePictureWindow.setVisible(false);
				
				mainPanel.remove(profPicPanel);
				buildProfPicPanel();
			}
			
		});
		
		profilepicPanel.add(profilepicfield);
		profilepicPanel.add(choosepic);
		
		profilePictureWindow.add(profilepicPanel);
		profilePictureWindow.setLocationRelativeTo(null);
	}
	
	// Builds approve window
	public static void buildApproveWindow(String name)
	{
		ApproveWindow.setTitle(name);
		ApproveWindow.setSize(460, 80);
		ApproveWindow.setLocation(250, 200);
		ApproveWindow.setResizable(false);
		P_Approve.add(L_Approve);
		L_Approve.setText("Will you allow " + name + "to follow your posts?");
		P_Approve.add(B_YESApprove);
		P_Approve.add(B_NOApprove);
		ApproveWindow.add(P_Approve);
		ApproveWindow.setVisible(false);
	}
	
// PANELS
	
	// Creates the profile picture panel
	public static void buildProfPicPanel(){
		profPicPanel = new JPanel();
		profPicPanel.setBorder(BorderFactory.createTitledBorder(username));
		profPicPanel.setBounds(15,15,100,105);
		profPicPanel.add(new JLabel(getResizedImage(profpic,70,70)));
		profPicPanel.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				profilePictureWindow.setVisible(true);
			}
			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}
		});
		mainPanel.add(profPicPanel);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	// Creates the follower panel
	public static void buildFollowerPanel(){
		followerPanel = new JPanel();
		followerPanel.setLayout(new BoxLayout(followerPanel, BoxLayout.Y_AXIS));
		
		followerPane = new JScrollPane(followerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		followerPane.setBorder(BorderFactory.createTitledBorder("Followers"));
		followerPane.setBounds(430,140,140,142);		
		
		for(int i = 0; i < followersName.size(); i++){
			followerPanel.add(new JLabel(followersName.get(i)));
		}
		
		mainPanel.add(followerPane);
		followerPane.revalidate();
		followerPane.repaint();
	}
	
	// Creates following panel
	public static void buildFollowingPanel(){
		followingPanel = new JPanel();
		followingPanel.setLayout(new BoxLayout(followingPanel, BoxLayout.Y_AXIS));
		
		followingPane = new JScrollPane(followingPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		followingPane.setBorder(BorderFactory.createTitledBorder("Following"));
		followingPane.setBounds(430,290,140,142);		
		
		for(int i = 0; i < followingName.size(); i++){
			followingPanel.add(new JLabel(followingName.get(i)));
		}
		
		mainPanel.add(followingPane);
		followingPane.revalidate();
		followingPane.repaint();
	}
	
// SENDING
	
	// Send FOLLOW request
	public static void FOLLOW(String ip, int port)
	{
		System.out.println("FOLLOW (start)");
		
		// If user has a profile picture, send the profile picture
		if(!profpic.equals("")){
			System.out.println("FOLLOW (profpic != \"\")");
			sendFile(ip, port, profpic, "\"FOLLOW\" " + username + "\0");
		}
		
		// Otherwise send a follow request without a profile picture
		else{
			Socket SOCK;
			try
			{
				SOCK = new Socket(ip, port);								// Open socket
				PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());	
				OUT.println("\"FOLLOW\" " + username + "\0"); 				// Send follow request
				OUT.flush();												
				SOCK.close(); 												// Close socket
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		System.out.println("FOLLOW (end)");
	}

	// Send UNFOLLOW notification
	public static void UNFOLLOW(String ip, int port)
	{
		System.out.println("UNFOLLOW (start)");
		Socket SOCK;
		try
		{
			SOCK = new Socket(ip, port);								// Open socket
			PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());	
			OUT.println("\"UNFOLLOW\""); 								// Send unfollow notification
			OUT.flush();												
			SOCK.close(); 												// Close socket
			
			String theName;			// Name of user being unfollowed
			int index = 0;			// Index of user in list of following
			boolean found = false;	// Boolean of whether user has already been found in the list or not
			
			// Looks for the user list of following and removes him/her
			while(found == false && index < following.size())
			{	if(following.get(index).equals(ip))
				{	
					following.remove(index);						// Remove user ip from list
					theName = followingName.get(index).toString();	// Get name of user
					followingName.remove(index);					// Remove user name from list
					found = true;									// Set as found
					addGenericToChat(ip, " is no longer being followed by you!");	// Display notification in chat
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		refreshFollowingFollowers();	// Refresh display of followers and following
		System.out.println("UNFOLLOW (end)");
	}
	
	// Send APPROVE notification
	public static void APPROVE(String ip, int port)
	{
		System.out.println("APPROVE (start)");
		Socket SOCK;
		try
		{
			
			
			// If user has a profile picture
			if(!profpic.equals(""))
				// Send approve notification with profile picture
				sendFile(ip, port, profpic, "\"APPROVE\" " + username + " "); 
			
			// If user doesn't have a profile picture
			else{
				// Send regular approve notification
				SOCK = new Socket(ip, port);									// Open socket
				PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
				OUT.println("\"APPROVE\"" + username + "\0");
				OUT.flush();			
				SOCK.close(); 													// Close socket
			}													
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		addGenericToChat(ip, " is now following you!");	// Display new follower notification in chat
		refreshFollowingFollowers();					// Refresh display of followers and following
		System.out.println("APPROVE (end)");
	}
	
	// Send POST notification
	public static void POST(String ip, int port, String message)
	{
		System.out.println("POST (start)");
		Socket SOCK;
		try {
			SOCK = new Socket(ip, port);								// Open socket
			PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
			OUT.println("\"POST\" " + message + "\0"); 					// Send message
			OUT.flush();		
			SOCK.close(); 												// Close socket	

		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("POST (end)");
	 }
	
	 // Send POST notification with image
	 public static void POSTPIC(String ip, int port, String message, String filename){
		 System.out.println("POSTPIC (start)");
		 sendFile(ip, port, filename, "\"POST\" " + message + '\u001a');	// Send message with image
		 addOwnPicToChat(filename, message, 'm');							// Echo post in chat
		 System.out.println("POSTPIC (end)");
	 }
	 

	 // Send PM notification
	 public static void PM(String ip, int port, String message)
	 {
		 System.out.println("PM (start)");
		 Socket SOCK;
		 try {
			 SOCK = new Socket(ip, port);								// Open socket
			 PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
			 OUT.println("\"PM\" " + message + "\0"); 					// Send private message
			 OUT.flush();			
			 SOCK.close(); 												// Close socket
		 } catch (Exception e) {
			 e.printStackTrace();
		 } 
			
		 addGenericToChat("", " (PM): " + message);	// Echo private message in chat display
		 System.out.println("PM (end)");
	 }
	 
	  // Send IMG profile picture update
	  public static void IMG(String ip, int port){
		  System.out.println("IMG (start)");
		  sendFile(ip, port, profpic, "\"IMG\" ");	// Send profile picture file to specified ip
		  System.out.println("IMG (end)");
	  }
	  
	  // Send FILE
	  public static void FILESEND(String ip, int port, String filename)
	  {
		  System.out.println("FILESEND (start)");
		  sendFile(ip, port, filename, "\"FILE\" " + filename + " ");	// Send file to specified ip
		  System.out.println("FILESEND (end)");
	  }

// RECEIVING
	
	// Receive FOLLOW request
	public static void FollowAction(String ip, byte [] bytes)
	{		
		System.out.println("FollowAction (start)");
		// Convert bytes to string
		String string = null;
		try {
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Get username of user		
		String name = getUntilSpaceOrNull(string, 'n');
		
		// If username was not included in the FOLLOW command
		if(name.equals("")){
			name = getUsername(ip);	// Get name from list of ips
		}
		
		// If username is included in the follow command
		else{
			// Places the ip, name and bytes in temporary variables for storage
			tempIP = ip;
			tempName = name;
			name = name + " ";
			tempBytes = Arrays.copyOfRange(bytes, name.getBytes().length, bytes.length);
		}
		
		// Creates the approve window
		ApproveWindow.setTitle(name);
		L_Approve.setText("Will you allow " + name + " to follow your posts?");
		ApproveWindow.setVisible(true);

		System.out.println("FollowAction (end)");
	}
	
	// Receive UNFOLLOW notification
	public static void UnfollowAction(String ip)
	{
		System.out.println("UnfollowAction (start)");
		
		String theName = null;	// Name of user who unfollowed
		int index = 0;			// Index of user in list of followers
		boolean found = false;	// Boolean of whether user has already been found in the list or not
		
		// Looks for the user list of followers and removes him/her
		while(found == false && index < followers.size())
		{	if(followers.get(index).equals(ip))
			{	
				followers.remove(index);						// Removes user's ip from list
				theName = followersName.get(index).toString();	// Gets the name of the user
				followersName.remove(index);					// Remove's user's name as list
				found = true;									// Set user as found
				addGenericToChat(ip, " is no longer following you!");	// Display unfollow notification
			}
		}
		
		refreshFollowingFollowers();	// Refresh displayed lists of following and followers
		
		System.out.println("UnfollowAction (end)");
	}
	
	// Receive APPROVE notification
	public static void ApproveAction(String ip, byte [] bytes)
	{
		System.out.println("ApproveAction (start)");
		
		// Convert bytes to string
		String string = "";
		try {
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		// Get name of followed user
		String name = getUntilSpaceOrNull(string, 'b');
		String nameAndSpace = name + " ";	// Create a temporary variable containing the name and a space
		
		// If the character following the username is a space, then download the user's profile picture
		if(string.substring(name.length(), name.length() + 1).equals(" "))
			writeFile(Arrays.copyOfRange(bytes, nameAndSpace.getBytes().length, bytes.length), name + ".jpg");
		
		following.add(ip);			// Add user's ip to following
		followingName.add(name);	// Add user's name to following
		
		addGenericToChat(ip, " is now being followed by you!");	// Display new following notification
		refreshFollowingFollowers();							// Refresh display of following and follower lists
		
		System.out.println("ApproveAction (end)");
	}	
	
	// Receive POST notification
	public static void PostAction(String ip, byte [] bytes, String senderip)
	{
		System.out.println("PostAction (start)");
		
		// Convert bytes to string
		String string = "";
		try {
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Get message (until NULL or EOF)
		String message = getUntilSpaceOrNull(string, 'e');
		
		// If character after message is null, display regular message
		if(string.substring(message.length(),message.length()+1).equals("\0"))
			addToChat(ip, message);
		
		// If character after message is eof, display message with image
		else{
			String temp = message + " ";	// Temporary variable for message with a space
			addPicToChat(ip, message, Arrays.copyOfRange(bytes, temp.getBytes().length, bytes.length), 'm');	// Display message with image in chat
		}
		
		System.out.println("PostAction (end)");
	}
	
	// Receiving PM notification
	public static void PMAction(String ip, byte [] bytes)
	{
		System.out.println("PMAction (start)");
		
		String name = null;
		int WhereItIs = getOnlineIPIndex(ip);

		// If ip is in the list of online IPs
		if(WhereItIs != -1){
			name = OnlineNames.get(WhereItIs);	// Get corresponding username
			
			// Convert bytes to string
			String string = "";
			try {
				string = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			  
			// Get message
			String message = getUntilSpaceOrNull(string, 'e');
				
			// If character after message is null, display regular message in chat
			if(string.substring(message.length(),message.length()+1).equals("\0"))
				addGenericToChat(OnlineIPs.get(WhereItIs), " (PM): " + message);

			// If character after message is EOF, display message with image in chat
			else{
				String temp = message + " ";	// Temporary variable containing message with a space
				addPicToChat(ip, message, Arrays.copyOfRange(bytes, temp.getBytes().length, bytes.length), 'p');	// Display message with image in chat
			}
		}
		  
		// If ip is not list of online IPs, display notification
		else
			JOptionPane.showMessageDialog(null, "That user is not online!");	
		
		System.out.println("PMAction (end)");
	  }
	  
	  // Receive IMG profile picture update
	  public static void ProfPicAction(String ip, byte [] bytes) throws IOException{ 
		  System.out.println("ProfPicAction (start)");
		  
		  String filename = getUsername(ip) + ".jpg";	// Filename for profile picture (username.jpg)
		  
		  writeFile(Arrays.copyOfRange(bytes, 0, bytes.length), filename); 	// Download new profile picture
		  addGenericToChat(ip, " has changed profile picture!");			// Display new profile picture notification in chat
		  
		  System.out.println("ProfPicAction (end)");
	  }
	  
	  // Receive FILE
	  public static void FileAction(String ip, byte [] bytes) throws IOException{
		  System.out.println("FileAction (start)");
		  
		  String filename = getUntilSpaceOrNull(new String(bytes, "UTF-8"), 'b');	// Filename
		  String tempString = filename + " ";										// Temporary variable for filename with space
		  
		  writeFile(Arrays.copyOfRange(bytes, tempString.getBytes().length, bytes.length), filename);	// Download file
		  
		  addFileToChat(ip, filename);	// Display file in chat
		  
		  System.out.println("FileAction (end)");
	  }  

// ACTION LISTENER
	  
	public void actionPerformed(ActionEvent a)
	{
		
	// DISPLAYING WINDOWS
		
		// PM: PM button
		if(a.getSource() == pm)
		{
			PMWindow.setVisible(true);
		}
		
		// POST (with image): Send W/ File button
		if(a.getSource() == sendwfile)
		{
			SENDWFILEWindow.setVisible(true);
		}
		
		// FOLLOW: Follow button
		if(a.getSource() == follow)
		{
			FollowWindow.setVisible(true);
		}
		
		// UNFOLLOW: Unfollow button
		if(a.getSource() == unfollow)
		{
			UnFollowWindow.setVisible(true);
		}
		
		// FILE: File button
		if(a.getSource() == file)
		{
			// send file window
			FileSendWindow.setVisible(true);
		}
		
	// SENDING
		
		// POST: Send button
		if(a.getSource() == send)
		{
			System.out.println("Send Listener");
			// Send message to all followers
			for(int i = 0; i < followers.size(); i++)
				POST(followers.get(i), PORT, chatip.getText());
			
			// Echo message to own chat display
			addToChat("", chatip.getText());
			
			// Clear message text field
			chatip.setText("");
		}
		
		// PM (with image): Enter PM button
		if(a.getSource() == B_ENTERPMIMG)
		{
			System.out.println("PM with Image Listener");
			
			int WhereItIs = getOnlineNameIndex(TF_UserNamePMBox.getText().toString());
			
			// If user is in the list of online users
			if(WhereItIs != -1)
			{
				// Send image file
				sendFile(OnlineIPs.get(WhereItIs), PORT, TF_PMIMGBox.getText(), "\"PM\" " + TF_PMBox.getText() + "\u001a");
				// Echo sent file in own chat display
				addOwnPicToChat(TF_PMIMGBox.getText(), TF_PMBox.getText(), 'p');
			}
			
			// If user is not in the list of online users display notification
			else
			{
				JOptionPane.showMessageDialog(null, "That user is not online!");
			}
			
			// Close window and clear fields
			PMWindow.setVisible(false);
			TF_UserNamePMBox.setText("");
			TF_PMIMGBox.setText("");
			TF_PMBox.setText("");
		}
		
		// POST (with image): Enter send file button
		if(a.getSource() == B_ENTERSENDFILE)
		{
			System.out.println("Post with Image Listener");
			
			// Send image file and notification to all followers
			for(int i = 0; i < followers.size(); i++)
				sendFile(followers.get(i), PORT, TF_SENDWFILENAMEBox.getText(), "\"POST\" " + TF_SENDWFILEMSGBox.getText() + "\u001a");
			
			// Echo message and image file to own chat display
			addOwnPicToChat(TF_SENDWFILENAMEBox.getText(), TF_SENDWFILEMSGBox.getText(), 'm');
			
			// Close window and clear fields
			SENDWFILEWindow.setVisible(false);
			TF_SENDWFILENAMEBox.setText("");
			TF_SENDWFILEMSGBox.setText("");
		}
		
		// PM: PM button
		if(a.getSource() == B_ENTERPM)
		{
			System.out.println("PM Listener");
			
			int WhereItIs = getOnlineNameIndex(TF_UserNamePMBox.getText().toString());
			
			// If user is in the list of online names
			if(WhereItIs != -1)
			{
				// Send PM to user
				PM(OnlineIPs.get(WhereItIs), PORT, TF_PMBox.getText());
			}
			// If user isn't in the list of online names
			else
			{
				// Display notification
				JOptionPane.showMessageDialog(null, "That user is not online!");
			}
			
			// Close window and clear fields
			PMWindow.setVisible(false);
			TF_UserNamePMBox.setText("");
			TF_PMBox.setText("");
			TF_PMIMGBox.setText("");
		}
		
		// FOLLOW: Enter follow button
		if(a.getSource() == B_ENTERFollow)
		{
			System.out.println("Follow Listener");
			
			int WhereItIs = getOnlineNameIndex(TF_UserNameFollowBox.getText());
			
			// If user is in list of online users
			if(WhereItIs != -1)
			{
				// Follow user
				FOLLOW(OnlineIPs.get(WhereItIs), PORT);
			}
			
			// If user isn't in list of online users
			else
			{
				// Display notification
				JOptionPane.showMessageDialog(null, "That user is not online!");
			}
			
			// Close window and clear fields
			FollowWindow.setVisible(false);
			TF_UserNameFollowBox.setText("");
		}
		
		// UNFOLLOW: Enter unfollow button
		if(a.getSource() == B_ENTERUnFollow) // WINONA this whole thing
		{
			System.out.println("Unfollow Listener");
			
			int WhereItIs = -1;	// Index in list of followers
			
			// Get index of user in list of followers
			for(int index = 0; index < followingName.size(); index++)
			{
				if(followingName.get(index).equals(TF_UserNameUnFollowBox.getText().toString()))
				{	WhereItIs = index;
				}
			}
			
			// If user is in list of followers
			if(WhereItIs != -1)
			{
				UNFOLLOW(following.get(WhereItIs), PORT);	// Send that user an unfollow notification
				refreshFollowingFollowers();				// Refresh displayed lists of followers and following
			}
			
			// If user isn't in list of followers
			else
			{
				// Display notification
				JOptionPane.showMessageDialog(null, "You are not following that user!");
			}
			
			// Close window and clear fields
			UnFollowWindow.setVisible(false);
			TF_UserNameUnFollowBox.setText("");
		}
		
		// FILE: Enter file send button
		if(a.getSource() == B_ENTERFileSend)
		{
			System.out.println("File Send Listener");
			
			// Send file to all followers
			for(int i = 0; i < followers.size(); i++){
				FILESEND(followers.get(i), PORT, TF_FileNameSendBox.getText());
			}
			
			// Echo file send in own chat display
			addFileToChat("", TF_FileNameSendBox.getText());
			
			// Close window and clear fields
			FileSendWindow.setVisible(false);
			TF_FileNameSendBox.setText("");
		}
		
		// APPROVE: Approve button
		if(a.getSource() == B_YESApprove)
		{
			System.out.println("Approve Listener");
			
			// Add to followers
			followers.add(tempIP);
			followersName.add(tempName);
			
			// Download profile picture if it exists
			if(tempBytes.length > 2)
				writeFile(tempBytes, tempName + ".jpg");
			
			// Close window
			ApproveWindow.setVisible(false);
			
			// Send notification of approval to new follower
			APPROVE(tempIP, PORT);	
		}
		
		// APPROVE: Reject button
		if(a.getSource() == B_NOApprove)
		{
			System.out.println("Reject Listener");
			
			// Close window
			ApproveWindow.setVisible(false);
		}
		
	}
	
// FILE METHODS
	
	// Download a file
	public static void writeFile(byte [] bytes, String filename){
		System.out.println("writeFile (start)");
		
		try{
			byte[] mybytearray = new byte[65500];							// Creates a byte array
			InputStream is = new ByteArrayInputStream(bytes);				// Creates an input stream from the given bytes
			FileOutputStream fos = new FileOutputStream(filename);			// Creates an output stream for writing to the file
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = is.read(mybytearray, 0, mybytearray.length);	// Reads bytes from bytes to mybytearray and stores number of bytes read
			  
			bos.write(mybytearray, 0, bytesRead);							// Writes bytesRead number of bytes from mybytearray to file
			bos.close();													// Closes output stream
			
			System.out.println("writeFile (bytes received: " + bytesRead + ")");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// Upload a file
	public static void sendFile(String ip, int port, String filename, String message){
		System.out.println("sendFile (start)");
		try {
			  File myFile = new File(filename);	// File to be sent
			  Socket SOCK;						// Open socket
			  SOCK = new Socket(ip, port);
			
			  byte[] mybytearray = new byte[(int) myFile.length()];								// Create byte array from file
			  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));	// Create input stream
			  int bytesRead = bis.read(mybytearray, 0, mybytearray.length);						// Read bytes from file input stream into mybytearray and store number of bytes read
			  OutputStream os = SOCK.getOutputStream();											// Get socket output stream to send file through											// 
			  byte [] stringbytes = message.getBytes();											// Convert message (usually including syntax/commands) string to bytes
			  os.write(stringbytes, 0, stringbytes.length);										// Send string bytes through socket
			  os.write(mybytearray, 0, mybytearray.length);										// Send file bytes through socket
			  os.flush();				
			  SOCK.close();																		// Close socket
			  
			  System.out.println("sendFile (bytes sent to " + ip + ": " + bytesRead + ")");
		  } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
	}
	
// CHAT DISPLAY METHODS
	
	// Adds regular message to chat
	public static void addToChat(String ip, String message){
		System.out.println("addToChat (start)");
		
		String user;
		
		// If ip is blank, use own username
		if(ip.equals(""))
			user = username; 

		// Otherwise, get the username given the ip
		else
			user = getUsername(ip);
		
		// Create new panel to add to chat display
		JPanel newChatPanel = new JPanel();
		newChatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// Profile picture display
		if(user.equals(username))
			newChatPanel.add(new JLabel(getResizedImage(profpic, 30, 30)));			// Own profile picture
		else
			newChatPanel.add(new JLabel(getResizedImage(user + ".jpg", 30, 30)));	// Other user's profile picture
		
		// Label and message
		newChatPanel.add(new JLabel(user + ": " + message));
		
		messagePanel.add(newChatPanel);
		messagePanel.revalidate();
		messagePanel.repaint();
		
		System.out.println("addToChat(end)");
	}
	
	// Adds user's sent image to chat
	public static void addOwnPicToChat(String filename, String message, char c){	
		System.out.println("addOwnPicToChat (start)");
		
		// Create new panel to add to chat display
		JPanel newChatPanel = new JPanel();
		newChatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// Display own profile picture
		newChatPanel.add(new JLabel(getResizedImage(profpic, 30, 30)));
		
		// If it is a message/post
		if(c == 'm'){
			newChatPanel.add(new JLabel(username + ": " + message));
		}
		
		// If it is a PM
		else{
			newChatPanel.add(new JLabel(username + " (PM): " + message));
		}
			
		// Add image to panel
		newChatPanel.add(new JLabel(getResizedImage(filename, 100, 100)));
		
		messagePanel.add(newChatPanel);
		messagePanel.revalidate();
		messagePanel.repaint();
		
		System.out.println("addOwnPicToChat (end)");
	}
	
	// Adds received picture to chat
	public static void addPicToChat(String ip, String message, byte [] bytes, char c){
		System.out.println("addPicToChat (start)");
		
		String user;
		
		writeFile(bytes, "thepostedpicture.jpg");
		
		// If ip is blank, use own username
		if(ip.equals(""))
			user = username; 
		
		// Otherwise, get the username given the ip
		else
			user = getUsername(ip);
			
		// Create new chat panel to add to chat display
		JPanel newChatPanel = new JPanel();	
		newChatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// Display profile picture
		if(user.equals(username))
			newChatPanel.add(new JLabel(getResizedImage(profpic, 30, 30)));			// Own profile picture
		else
			newChatPanel.add(new JLabel(getResizedImage(user + ".jpg", 30, 30)));	// Other user's profile picture
		
		// If it is a message/post
		if(c == 'm'){
			newChatPanel.add(new JLabel(user + ": " + message));
		}
		
		// If it is a PM
		else{
			newChatPanel.add(new JLabel(user + " (PM): " + message));
		}
			
		// Add image to chat panel
		newChatPanel.add(new JLabel(getResizedImage("thepostedpicture.jpg", 100, 100)));
		
		messagePanel.add(newChatPanel);
		messagePanel.revalidate();
		messagePanel.repaint();
		
		System.out.println("addPicToChat (end)");
	}
	
	// Adds file to chat
	public static void addFileToChat(String ip, String filename){
		System.out.println("addFileToChat (start)");
		
		String user;
		
		// If ip is blank, use own username
		if(ip.equals(""))
			user = username; 

		// Otherwise, find the username given the ip
		else
			user = getUsername(ip);
			
		// Create new chat panel to add to chat display
		JPanel newChatPanel = new JPanel();
		newChatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// Display profile picture
		if(user.equals(username))
			newChatPanel.add(new JLabel(getResizedImage(profpic, 30, 30)));			// Own profile picture
		else
			newChatPanel.add(new JLabel(getResizedImage(user + ".jpg", 30, 30)));	// Other user's profile picture
		
		// Message with filename
		newChatPanel.add(new JLabel(user + " sent " + filename));
		
		// If file is an image, display in chat panel
		if(getFileExtension(filename).equals("jpg") ||
		   getFileExtension(filename).equals("png") ||
		   getFileExtension(filename).equals("gif")){
			displayImage.add(filename);
			btnImage.add(new JButton("View " + filename));
			
			int index = displayImage.size()-1;
			
			imageListeners.add(new ActionListener() {
				public void actionPerformed(ActionEvent e)
		        {
					createImageWindow(displayImage.get(imageListeners.indexOf(this)));
		        }
		    });
			
			btnImage.get(btnImage.size()-1).addActionListener(imageListeners.get(index));
			
			newChatPanel.add(btnImage.get(index));
		}
		
		// If file is a wav file, add button for playing wav file in chat panel
		else if(getFileExtension(filename).equals("wav"))
		{
			WavFile.add(filename);
			btnWav.add(new JButton("Play"));
			stopWav.add(new JButton("Stop"));
			
			int index = WavFile.size()-1;
			
			wavListeners.add(new ActionListener() {
				public void actionPerformed(ActionEvent e)
		        {
					if(e.getSource() == btnWav.get(wavListeners.indexOf(this)))
						playSound(wavListeners.indexOf(this));
					
					else if(e.getSource() == stopWav.get(wavListeners.indexOf(this)))
						clip.get(wavListeners.indexOf(this)).close();
		        }
		    });
			
			btnWav.get(index).addActionListener(wavListeners.get(index));
			stopWav.get(index).addActionListener(wavListeners.get(index));
			
			newChatPanel.add(btnWav.get(index));
			newChatPanel.add(stopWav.get(index));
		}
		
		messagePanel.add(newChatPanel);
		messagePanel.revalidate();
		messagePanel.repaint();
		
		System.out.println("addFileToChat (end)");
	}
	
	// Adds PMed message to chat
	public static void addGenericToChat(String ip, String message){
		System.out.println("addGenericToChat (start)");
		
		String user;
		
		// If ip is blank, use own username
		if(ip.equals(""))
			user = username; 

		// Otherwise, find the username given the ip
		else
			user = getUsername(ip);
		
		// Create new chat panel to add to chat display
		JPanel newChatPanel = new JPanel();
		newChatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// Display profile picture
		if(user.equals(username))
			newChatPanel.add(new JLabel(getResizedImage(profpic, 30, 30)));			// Own profile picture
		else
			newChatPanel.add(new JLabel(getResizedImage(user + ".jpg", 30, 30)));	// Other user's profile picture
		
		// Add label with message to chat panel
		newChatPanel.add(new JLabel(user + message));
		
		messagePanel.add(newChatPanel);
		messagePanel.revalidate();
		messagePanel.repaint();
		
		System.out.println("addGenericToChat (end)");
	}
	
// MISC DISPLAY
	
	// Refreshes display of the list of followers and following
	public static void refreshFollowingFollowers(){
		mainPanel.remove(followerPane);
		buildFollowerPanel();
		mainPanel.remove(followingPane);
		buildFollowingPanel();
	}
	
// "FINDER" METHODS
	
	// Gets username given the ip
	public static String getUsername(String ip){
		System.out.println("getUsername (start)");
		
		int i = 0;
		
		// Search through followers
		
		while(i < followers.size() && !followers.get(i).equals(ip))
			i++;
		
		if(i < followers.size())
			return followersName.get(i);
		
		else
			i = 0;
		
		// If not there, search through following

		while(i < following.size() && !following.get(i).equals(ip))
			i++;
		
		if(i < following.size())
			return followingName.get(i);
		
		else
			i = 0;
		
		// If not there, search through online ips
		
		while(i < OnlineIPs.size() && !OnlineIPs.get(i).equals(ip))
			i++;
		
		System.out.println("getUsername (end)");
		
		if(i < OnlineIPs.size())
			return OnlineNames.get(i);
		
		// If still not there, return a blank string
		
		else
			return "";
	}
	
	// Searches for ip's index in list of online IPs
	public static int getOnlineIPIndex(String theip){
		System.out.println("getOnlineIPIndex");
		
		int ipindex = -1;
		
		// Find user by searching through the list of online IPs for a match
		for(int index = 0; index < OnlineIPs.size(); index++)
		{
			if(OnlineIPs.get(index).equals(theip)){	
				ipindex = index;
			}
		}
		
		return ipindex;
	}
	
	// Searches for name's index in list of online names
	public static int getOnlineNameIndex(String thename){
		System.out.println("getOnlineNameIndex");
		
		int userindex = -1;
		
		// Find user by searching through the list of online names for a match
		for(int index = 0; index < OnlineNames.size(); index++)
		{
			if(OnlineNames.get(index).equals(thename))
			{	userindex = index;
			}
		}
		
		return userindex;
	}
	
// MISC METHODS

	// Returns the file extension given the file name
	public static String getFileExtension(String filename){
		System.out.println("getFileExtension");
		
		char [] charArray = filename.toCharArray();
		
		int i = 0;
		
		// Look for index of '.'
		while(i < filename.length() && charArray[i] != '.')
			i++;
		
		// If '.' exists, return substring of characters after '.'
		if(i < filename.length())
			return filename.substring(i+1);
		
		else
			return "";
	}
	
	// Returns string cut off at either space, null or eof, depending on c
	public static String getUntilSpaceOrNull(String bytesinstring, char c){
		System.out.println("getUntilSpaceOrNull");
		
		int i = 0;											// Character index
		char [] bytesinchar = bytesinstring.toCharArray();	// String converted to char array
		 
		// Space or null
		if(c == 'b')
			while(bytesinchar[i] != ' ' && bytesinchar[i] != '\0')
				i++;
		  
		// Null
		else if(c == 'n')
			while(bytesinchar[i] != '\0')
				i++;
		  
		// Space
		else if(c == 's')
			while(bytesinchar[i] != ' ')
				i++;
		  
		// Null or EOF
		else if(c == 'e')
			while(bytesinchar[i] != '\0' && bytesinchar[i] != '\u001a')
				i++;
		  
		// Return cut up string
		return bytesinstring.substring(0, i);
	}
	
	// Gets a filename and returns the resized image icon
	public static ImageIcon getResizedImage(String filename, int width, int height){
		System.out.println("getResizedImage");
		
		ImageIcon imageIcon = new ImageIcon(filename); // load the image to a imageIcon
		Image image = imageIcon.getImage(); // transform it 
		
		double scalex = (double) width / image.getWidth(null);
		double scaley = (double) height / image.getHeight(null);
		double scale = Math.min(scalex, scaley);
		
		Image newimg = image.getScaledInstance((int)(image.getWidth(null) * scale), (int)(image.getHeight(null) * scale),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		
		return imageIcon;
	}
	
// OPENING/USING RECEIVED FILES
	
	// Open window with image
	public static void createImageWindow(String filename){
		System.out.println("imageWindow (" + filename + ")");
			
		JFrame image = new JFrame(filename);
		image.setSize(500,500);
		image.setVisible(true);
			
		JPanel imagePanel = new JPanel();
		image.add(imagePanel);
		
		imagePanel.add(new JLabel(getResizedImage(filename, 500, 500)));
		
		image.pack();
	}
	
	// Play wav file
	public static void playSound(int index)
    {
		String soundName = WavFile.get(index);
		
		System.out.println("playSound (" + soundName + ")");
		
		try 
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
			if(clip.size() <= index){
				clip.add(AudioSystem.getClip());
				System.out.println("playSound (add clip at index " + index + ")");
				clip.get(index).open(audioInputStream);
			}
			else{
				clip.set(index, AudioSystem.getClip());
				clip.get(index).open(audioInputStream);
			}
			clip.get(index).start();
		}
		catch(Exception ex)
		{
			System.out.println("Error with playing sound.");
			ex.printStackTrace( );
		}
    }
}

