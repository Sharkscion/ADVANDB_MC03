package model;

import java.net.Socket;
import java.util.ArrayList;

public class Site {
	private String name;
	private transient Socket socket;
	private String ipAddress;
	private ArrayList<Site> connectionList;
	
	public Site(Socket socket, String username){
		this.name = username;
		this.ipAddress = "";
		this.socket = socket;
		connectionList = new ArrayList<Site>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void addConnection(Site s){
		this.connectionList.add(s);
	}
	public ArrayList<Site> getConnectionList() {
		return connectionList;
	}

	public void setConnectionList(ArrayList<Site> connectionList) {
		this.connectionList = connectionList;
	}
	
	public Site searchForSiteUsername(String username){
		Site s = null;
		int x = 0;
		while(!connectionList.get(x).getName().equals(username)) x++;
			s= connectionList.get(x);
		return s;
	}

	
}
