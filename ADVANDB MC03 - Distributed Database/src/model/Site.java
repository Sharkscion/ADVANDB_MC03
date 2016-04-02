package model;

import java.net.Socket;

public class Site {
	private String name;
	private transient Socket socket;
	private String ipAddress;
	
	public Site(Socket socket){
		this.name = "";
		this.ipAddress = "";
		this.socket = socket;
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
	
}
