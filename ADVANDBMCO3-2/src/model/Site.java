package model;

import java.util.ArrayList;

public class Site {
	private String ipadd;
	private String name;
	
	public static ArrayList<Site> connectionList;


	public Site(String ipadd, String name){
		this.ipadd = ipadd;
		this.name = name;
		connectionList = new ArrayList<Site>();
	}
	
	public ArrayList<Site> getConnectionList() {
		return connectionList;
	}


	public void setConnectionList(ArrayList<Site> connectionList) {
		this.connectionList = connectionList;
	}
	
	public String getIpadd() {
		return ipadd;
	}
	
	public void setIpadd(String ipadd) {
		this.ipadd = ipadd;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addConnection(Site newSite){
		connectionList.add(newSite);
	}
	
	public static Site searchConnection(String username){
		
		Site s = null;
		
		int x = 0;
		
		while(!connectionList.get(x).getName().equals(username)) x++;
		
		s = connectionList.get(x);
		
		return s;
	}
	

}
