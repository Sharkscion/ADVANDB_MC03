package socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import model.Site;

public class Server {

	public static ArrayList<Site> clientList;
	
	public Server(){
		clientList = new ArrayList<Site>();
	}
	
	public ArrayList<Site>  getClientList(){
		return clientList;
	}
	
	public void addClient(Site client){
		clientList.add(client);		
	}
	
	public static void main(String[] args) {
        try{
        	Server server = new Server();
            final int PORT = 444;
            ServerSocket SERVER = new ServerSocket(PORT);
            System.out.println("Waiting for clients");
            
            while(true){
                Socket SOCK = SERVER.accept();                
                System.out.println("Client connected from: "+SOCK.getLocalAddress().getHostName());
                AddUserName(SOCK);
                
                ServerResponse serverResponse = new ServerResponse(SOCK, server );
                Thread X = new Thread(serverResponse);
                X.start();
                UpdateUsers();
            }
        }
        catch(Exception X){
                System.out.println(X);
        }
        
    }
    
    public static void AddUserName(Socket X) throws IOException {
        Scanner INPUT = new Scanner(X.getInputStream());
        String siteName = INPUT.nextLine();
        
        Site s = new Site(X);
        s.setName(siteName);
        
        clientList.add(s);
        
        for(int i=1; i<= Server.clientList.size(); i++){
            Socket TEMP_SOCK = (Socket) Server.clientList.get(i-1).getSocket();
            PrintWriter OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
            OUT.println(clientList);
            OUT.flush();
        }   
    }
    
    public static void UpdateUsers() throws IOException {
        for(int i=0;i<Server.clientList.size();i++){
            Socket TEMPSOCK = (Socket) Server.clientList.get(i).getSocket();
            PrintWriter OUT = new PrintWriter(TEMPSOCK.getOutputStream());
            OUT.println("User \""+Server.clientList.get(Server.clientList.size()-1)+"\" has joined"+"\nNew set of users:");
            for(int j=0;j<Server.clientList.size();j++){
                OUT.println("["+j+"]"+Server.clientList.get(j).getName());
            }
            OUT.flush();
        }
    }
}
