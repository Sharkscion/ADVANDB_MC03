package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Site implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipadd;
	private String name;
	
	public ArrayList<Site> connectionList;


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
	
	public Site searchConnection(String username){
		
		Site s = null;
		for(int x = 0; x <connectionList.size(); x++){	
			if(connectionList.get(x).getName().equalsIgnoreCase(username)) {
				s = connectionList.get(x);
			}
		}
		return s;
	}
	

}
