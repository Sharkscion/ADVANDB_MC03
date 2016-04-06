package model;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.URL;
import java.security.acl.Owner;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

import com.sun.rowset.CachedRowSetImpl;

import controller.Controller;

public class Transaction implements Runnable, Subject, Serializable{
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
	
	private Site receiver;
	private Site sender;

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
		this.query = query;
		this.schema = schema;
		this.isWrite = isWrite;
		this.tableName = tableName;
		this.name = name;
		isolation_level = ISO_SERIALIZABLE;
	}
	
	public Transaction(String query, Site receiver){
		dbCon = new DBConnection();
		con = dbCon.getConnection();
		preparedStatement = null;
		obList = new ArrayList<QueryObserver>();
		rs = null;
		cs = null;
		this.receiver = receiver;
		this.query = query;
		isolation_level = ISO_SERIALIZABLE;
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
		System.out.println("\nBegin Transaction ");
		
		try {
			
			if(isWrite){
				queryLock = "LOCK TABLES "+tableName+" WRITE;";
			}else
				queryLock = "LOCK TABLES "+tableName+" READ;";
		
			preparedStatement = con.prepareStatement(queryLock);
			preparedStatement.execute();
			
			preparedStatement = con.prepareStatement("START TRANSACTION;");
			preparedStatement.executeQuery();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void endTransaction(){
		
		System.out.println("End Transaction: " + name + "\n");
		
		if(tran_action == COMMIT){
			try {
				con.commit();
				notifyObservers();
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(tran_action == ABORT){
			try {
				System.out.println("Aborting Transaction "+name+"...");
				con.rollback();
				System.out.println("Rolling Back Transaction "+name+"...");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				con.rollback();
				System.out.println("Rolling Back Transaction "+name+"...");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try {
			String queryLock = "UNLOCK TABLES;";
			preparedStatement = con.prepareStatement(queryLock);
			preparedStatement.execute();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void setResultSet(ResultSet rs){
		this.rs = rs;
	}
	public ResultSet getResultSet(){
		return this.rs;
	}
	
	public void runTransaction(){
		
		try{			
			if(!query.equals("")){
				
				preparedStatement = con.prepareStatement(query);
				rs = preparedStatement.executeQuery();				
				cs = new CachedRowSetImpl();
				cs.populate(rs);
				while(true){
					if(cs!=null)
						break;
				}
				
				System.out.println("TRAN RECEIVER: "+ receiver.getName());
				System.out.println("TRAN SENDER: "+sender.getName());
				
<<<<<<< HEAD:ADVANDB MC03 v2/ADVANDBMCO3-2/src/model/Transaction.java
<<<<<<< HEAD:ADVANDB MC03 v2/ADVANDBMCO3-2/src/model/Transaction.java
				if(isWrite && cs != null){
					sendPartialCommitStatusToSender(Tags.PARTIAL_COMMIT,name, sender);
				}else if(!isWrite && cs != null){
					if(receiver.equals(sender) && cs != null)
						notifyQueryObservers(cs); 
					else if(cs != null)
						sendToSender(cs, sender);
				}else if(isWrite && cs == null){
					sendPartialCommitStatusToSender(Tags.ABORT, name, sender);
				}
=======
=======
>>>>>>> parent of 193d7a8... mergeing result sets:ADVANDBMCO3-2/src/model/Transaction.java
				if(receiver.equals(sender))// meaning central siya
					notifyQueryObservers(cs); 
				else
					sendToSender(cs, sender);	
>>>>>>> parent of 193d7a8... mergeing result sets:ADVANDBMCO3-2/src/model/Transaction.java
			}
			
		}catch(Exception e){
			System.out.println("ERROR IN EXECUTING QUERY!: " );
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		beginTransaction();
		runTransaction();
		if(!isWrite()){
			endTransaction();
		}
	}

	public void sendPartialCommitStatusToSender(String protocol, String name, Site sender){
		try{
			System.out.println("PARTIAL COMMIT SUCCESS TO BE SENT TO : "+sender.getName());
			//send the protocol and the transaction name
			String mail = protocol + Tags.PROTOCOL + name;
			Socket SOCK = new Socket(sender.getIpadd(), Tags.PORT);
			
			PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
			OUT.println(mail); 
			OUT.flush();
			
			System.out.println("FINISH SENDING");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND WRITE SUCCESS TO : "+ sender.getName());
		}
	}
	
	public void sendToSender(CachedRowSetImpl cs, Site sender){
		
		try{
			
			System.out.println("TO BE SENT TO: " + sender.getName() + " IP: "+ sender.getIpadd() + "#");
			Socket SOCK = new Socket("192.168.54.1",Tags.PORT);
			String sProtocol = Tags.RESULT_SET + Tags.PROTOCOL;
			
			OutputStream tempOut = SOCK.getOutputStream();
		 	byte[] protocol = sProtocol.getBytes();
		 	byte[] object = Controller.serialize(cs);
		 	byte[] mail = Controller.byteConcat(protocol, object);
		 	
		 	tempOut.write(mail, 0, mail.length);
		 	tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND RESULT SET TO : "+ sender.getName());
		}
	}
	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		if(obList == null)
			System.out.println("NUL DE TAE");
		obList.add((QueryObserver) o);
	}

	@Override
	public void unRegisterObserver(Observer o) {
		// TODO Auto-generated method stub
		obList.remove((QueryObserver)o);
	}

	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		for(QueryObserver o: obList)
			o.update();
	}
	
	@Override
	public void notifyQueryObservers(CachedRowSetImpl rs) {
		// TODO Auto-generated method stub
		for(QueryObserver o: obList){
			o.updateResultSet(rs);
		}
	}
}
