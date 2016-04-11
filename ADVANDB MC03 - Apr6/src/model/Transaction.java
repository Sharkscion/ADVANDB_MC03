package model;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sun.rowset.CachedRowSetImpl;

import controller.Controller;

public class Transaction implements Runnable, Subject, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ISO_READ_UNCOMMITTED = "READ UNCOMMITTED";
	public static final String ISO_READ_COMMITTED = "READ COMMITTED";
	public static final String ISO_REPEATABLE_READ = "REPEATABLE READ";
	public static final String ISO_SERIALIZABLE = "SERIALIZABLE";
	
	public static final int COMMIT = 10;
	public static final int ROLLBACK = 20;
	public static final int FAILED = 30;
	public static final int ABORT = 40;
	
	public static final String BEGIN_LOG = "START";
	public static final String COMMIT_LOG = "COMMIT";
	public static final String ABORT_LOG = "ABORT";
	
	
	private String name;
	private String isolation_level;
	private PreparedStatement preparedStatement;
	private DBConnection dbCon;
	private Connection con;
	private String schema;
	private String tableName;
	private boolean isWrite;
	private String query;
	private boolean goCommit;
	private boolean goAbort;

	private Site receiver;
	private Site sender;
	private int numUpdates;
	private int tran_action;
	private ArrayList<QueryObserver> obList;
	private CachedRowSetImpl cs;
	private ResultSet rs;

	
	public Transaction(String protocolTag, String name, String schema, String tableName,String query, boolean isWrite){
	
		dbCon = new DBConnection();
		con = dbCon.getConnection();
		preparedStatement = null;
		obList = new ArrayList<QueryObserver>();
		rs = null;
		cs =null;
		goCommit = false;
		this.query = query;
		this.schema = schema;
		this.isWrite = isWrite;
		this.tableName = tableName;
		this.name = name;
		isolation_level = ISO_SERIALIZABLE;
	}
	
	public Transaction(String query, Site receiver, String tranName){
		dbCon = new DBConnection();
		con = dbCon.getConnection();
		preparedStatement = null;
		obList = new ArrayList<QueryObserver>();
		rs = null;
		cs = null;
		this.receiver = receiver;
		this.query = query;
		this.name = tranName;
		this.goCommit = false;
		this.goAbort = false;
		isolation_level = ISO_SERIALIZABLE;
	}
	
	public boolean isGoAbort() {
		return goAbort;
	}

	public void setGoAbort(boolean goAbort) {
		this.goAbort = goAbort;
	}
	
	public Site getReceiver() {
		return receiver;
	}

	public void setReceiver(Site receiver) {
		this.receiver = receiver;
	}
	
	public boolean isWrite() {
		return isWrite;
	}
	
	public boolean isGoCommit() {
		return goCommit;
	}

	public void setGoCommit(boolean goCommit) {
		this.goCommit = goCommit;
	}

	public void setWrite(boolean isWrite) {
		this.isWrite = isWrite;
	}
	
	public int getTran_action() {
		return tran_action;
	}

	public void setTran_action(int tran_action) {
		this.tran_action = tran_action;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public Site getSender() {
		return sender;
	}

	public void setSender(Site sender) {
		this.sender = sender;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsolation_level() {
		return isolation_level;
	}
	
	public CachedRowSetImpl getCachedRowSetImpl(){
		return this.cs;
	}
	
	public void setCachedRowSetImpl(CachedRowSetImpl cs){
		this.cs = cs;
	}
	
	public void setIsolation_level(String isolation_level) {
		
		System.out.println("Setting isolation level for Transaction "+name+" to: " + isolation_level);
		this.isolation_level = isolation_level;
		
		try {
			con.setAutoCommit(false);
			
			switch(this.isolation_level) {
			
				case ISO_READ_UNCOMMITTED:
						con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					break;
				case ISO_READ_COMMITTED: 
						con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					break;
				case ISO_REPEATABLE_READ: 
						con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				 	break;
				case ISO_SERIALIZABLE: 
						con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					break;
				default: 
						con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					break;
			}		
		} catch (SQLException e) { e.printStackTrace();	}
	}
	public Connection getCon() {
		return con;
	}
	public void setCon(Connection con) {
		this.con = con;
	}

	public void beginTransaction(){
		
		String queryLock = "";
		System.out.println("\nBegin Transaction >>>" + name);
//		
//		try {
//			
//			if(isWrite){
//				queryLock = "LOCK TABLES "+tableName+" WRITE;";
//			}else
//				queryLock = "LOCK TABLES "+tableName+" READ;";
//		
//			preparedStatement = con.prepareStatement(queryLock);
//			preparedStatement.execute();
//			
//			preparedStatement = con.prepareStatement("START TRANSACTION;");
//			preparedStatement.executeQuery();
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("WAITING TO RECEIVE LOCK : "+name);
//		}
	}
	
	public void endTransaction(){
		
		System.out.println("END TRANSACTION: " + name + "\n");
		
		if(tran_action == COMMIT){
			try {
				con.commit();
				System.out.println("COMMITING TRANSACTION " + name + "....");
				if(!isWrite)
					notifyObservers(name);
				
				//if you have already committed and it is successful 
				// send the confirmation back to central
				if(goCommit && numUpdates != 0){
					sendCommitToReceiver();
				}
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(tran_action == ABORT){
			try {
				System.out.println("ABORTING TRANSACTION "+name+"...");
				if(goAbort){
					sendAbortToReceiver();
				}
				con.rollback();
				System.out.println("ROLLING BACK TRANSACTION "+name+"...");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				if(goAbort){
					sendAbortToReceiver();
				}
				con.rollback();
				System.out.println("ROLLING BACK TRANSACTION "+name+"...");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
//		try {
//			String queryLock = "UNLOCK TABLES;";
//			preparedStatement = con.prepareStatement(queryLock);
//			preparedStatement.execute();
//			System.out.println(">>TABLE " +tableName + " IS UNLOCKED<<");
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
	}
	
	public void setResultSet(ResultSet rs){
		this.rs = rs;
	}
	public ResultSet getResultSet(){
		return this.rs;
	}
	
	public void runTransaction(){
		
		numUpdates = 0;
		try{			
			if(!query.equals("")){
				
				System.out.println("QUERY FROM  TRANSACTION CLASS: "+ query);
				preparedStatement = con.prepareStatement(query);
	
				if(!isWrite){
					rs = preparedStatement.executeQuery();
					cs = new CachedRowSetImpl();
					cs.populate(rs);
					while(true){
						if(cs!=null)
							break;
					}
				}
				else
					numUpdates = preparedStatement.executeUpdate(query);
				
				// if magwriwrite muna siya and success siya <- central
				if(isWrite &&  !goCommit && tran_action == Transaction.COMMIT){
					System.out.println("SENDING PARTIAL COMMIT STATUS TO: "+sender.getName());
					sendPartialCommitStatusToSender(Tags.PARTIAL_COMMIT,name, sender);
					
				}// if magwriwrite muna siya and abort siya <- central
				else if(isWrite  && !goAbort && tran_action == Transaction.ABORT){
					
					System.out.println("SENDING PARTIAL ABORT STATUS TO: "+sender.getName());
					sendPartialCommitStatusToSender(Tags.PARTIAL_ABORT, name, sender);
					
				}// if magreread lng siya
				else if(!isWrite && cs != null){
					if(receiver.equals(sender) && cs != null){
//						
//						TableContents tc = Controller.rsList.get(name);
//						System.out.println("TABLE OF CONTENTS IN TC: "+ tc.getTranName());
						notifyQueryObservers(name, Controller.setTableContentsRow(name, cs)); 
					}
					else if(!receiver.equals(sender) && cs != null){
						System.out.println("SENDING RESULT SET TO : "+sender.getName());
						sendToSender(cs, sender);
					}
				}
				
			}
			
		}catch(Exception e){
			System.out.println("ERROR IN EXECUTING QUERY!: " );
			e.printStackTrace();
		}
	}
	
	public void sendAbortToReceiver(){
		
		try{
			System.out.println("SENDING ABORT TRANSACTION "+name+" TO: "+ receiver.getName());

			//send the protocol and the transaction name
			String mail = Tags.ABORT+ Tags.PROTOCOL + name;
			Mail m = new Mail(mail);			
			
			
			Socket SOCK = new Socket(receiver.getIpadd(),Tags.PORT);
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(m);
			ous.flush();
			ous.close();
		 	tempOut.flush();
		 	SOCK.close();
			
			System.out.println("FINISH SENDING ABORT TRANSACTION TO: "+ receiver.getName());
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND WRITE ABORT TO : "+ receiver.getName());
		}
	}
	public void sendCommitToReceiver(){
				
		try{
			
			System.out.println("SENDING COMMIT TRANSACTION "+name+" TO: "+ receiver.getName());

		
			//send the protocol and the transaction name
			String mail = Tags.COMMIT + Tags.PROTOCOL + name;
			
			Mail m = new Mail(mail);
			
			Socket SOCK = new Socket(receiver.getIpadd(), Tags.PORT);
			
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(m);
			ous.flush();
			ous.close();
		 	tempOut.flush();
		 	SOCK.close();
			
			System.out.println("FINISH SENDING SUCCESS COMMIT");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND WRITE SUCCESS TO : "+ receiver.getName());
		}
	}

	public void sendPartialCommitStatusToSender(String sp, String tranName, Site sender){
		try{
			System.out.println("PARTIAL COMMIT STATUS "+sp+" TO BE SENT TO : "+sender.getName());
			//send the protocol and the transaction name
			TransactionMail tm = new TransactionMail(query, receiver, name);
			tm.setSender(sender);
			tm.setISO_LEVEL(isolation_level);
			tm.setTableName(tableName);
			tm.setTranName(tranName);
			tm.setTranAction(tran_action);
			tm.setWrite(isWrite);
			
			Mail m = new Mail(sp);
			m.setTm(tm);
		
			Socket SOCK = new Socket(sender.getIpadd(), Tags.PORT);
			
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(m);
			ous.flush();
			ous.close();
		 	tempOut.flush();
		 	SOCK.close();
			
			System.out.println("FINISH SENDING PARTIAL COMMIT STATUS");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND WRITE SUCCESS TO : "+ sender.getName());
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		beginTransaction();
		runTransaction();
		
		if(!isWrite)
			endTransaction();
	}

	public void sendToSender(CachedRowSetImpl cs, Site sender){
		
		try{
			
			System.out.println("READ QUERY RESULT SET TO BE SENT TO: " + sender.getName() + " IP: "+ sender.getIpadd() + "#");
			Socket SOCK = new Socket(sender.getIpadd(),Tags.PORT);
			String sProtocol = Tags.RESULT_SET + Tags.PROTOCOL;
			
			Mail m = new Mail(sProtocol);
			TransactionMail tm = new TransactionMail(sProtocol, receiver, name);
			m.setTm(tm);
			m.setCs(cs);
			
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(m);
			ous.flush();
			ous.close();
		 	tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING READ QUERY RESULT SET");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND RESULT SET TO : "+ sender);
		}
	}
	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		obList.add((QueryObserver) o);
	}

	@Override
	public void unRegisterObserver(Observer o) {
		// TODO Auto-generated method stub
		obList.remove((QueryObserver)o);
	}

	@Override
	public void notifyObservers(String name) {
		// TODO Auto-generated method stub
		for(QueryObserver o: obList)
			o.update(name);
	}
	
	@Override
	public void notifyQueryObservers(String tranName, TableContents tc) {
		// TODO Auto-generated method stub
		for(QueryObserver o: obList){
			o.updateResultSet(tranName, tc);
		}
	}
}
