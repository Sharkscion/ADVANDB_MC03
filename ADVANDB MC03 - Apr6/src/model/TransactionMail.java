package model;

import java.io.Serializable;


public class TransactionMail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String query;
	private Site sender;
	private Site receiver;
	private String ISO_LEVEL;
	private String tableName;
	private int tranAction;
	private boolean isWrite;
	private String tranName;
	
	public TransactionMail(String query, Site receiver, String tranName){
		
		this.query = query;
		this.receiver = receiver;
		this.tranName = tranName;
	}
	
	public String getQuery() {
		return query;
	}

	public String getTranName() {
		return tranName;
	}

	public void setTranName(String tranName) {
		this.tranName = tranName;
	}

	public void setQuery(String query) {
		this.query = query;
	}


	public Site getSender() {
		return sender;
	}


	public void setSender(Site sender) {
		this.sender = sender;
	}


	public Site getReceiver() {
		return receiver;
	}


	public void setReceiver(Site receiver) {
		this.receiver = receiver;
	}


	public String getISO_LEVEL() {
		return ISO_LEVEL;
	}


	public void setISO_LEVEL(String iSO_LEVEL) {
		ISO_LEVEL = iSO_LEVEL;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public int getTranAction() {
		return tranAction;
	}


	public void setTranAction(int tranAction) {
		this.tranAction = tranAction;
	}


	public boolean isWrite() {
		return isWrite;
	}


	public void setWrite(boolean isWrite) {
		this.isWrite = isWrite;
	}


}
