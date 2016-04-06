package model;

import java.io.Serializable;
import java.util.HashMap;

import com.sun.rowset.CachedRowSetImpl;

public class Mail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String message;
	public CachedRowSetImpl cs;
	public HashMap<String, String> detList;
	public TransactionMail tm;
	
	public Mail(String message){
		this.message = message;
		detList = new HashMap<String, String>();
		cs = null;
		tm = null;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public CachedRowSetImpl getCs() {
		return cs;
	}
	public void setCs(CachedRowSetImpl cs) {
		this.cs = cs;
	}
	public HashMap<String, String> getDetList() {
		return detList;
	}
	public void setDetList(HashMap<String, String> detList) {
		this.detList = detList;
	}
	public TransactionMail getTm() {
		return tm;
	}
	public void setTm(TransactionMail tm) {
		this.tm = tm;
	}
	
}
