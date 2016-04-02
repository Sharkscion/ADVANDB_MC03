package model;

import java.net.Socket;
import java.util.ArrayList;

public class Site {
	private String name;
	private transient Socket socket;
	private String ipAddress;
	private ArrayList<String> connectionList;
	
	public Site(Socket socket){
		this.name = "";
		this.ipAddress = "";
		this.socket = socket;
		connectionList = new ArrayList<String>();
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

	public void addConnection(String username){
		this.connectionList.add(username);
	}
	public ArrayList<String> getConnectionList() {
		return connectionList;
	}

	public void setConnectionList(ArrayList<String> connectionList) {
		this.connectionList = connectionList;
	}

	
}
