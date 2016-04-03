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

public class Transaction implements Runnable, Subject{
	public static final int ISO_READ_UNCOMMITTED = 1;
	public static final int ISO_READ_COMMITTED = 2;
	public static final int ISO_REPEATABLE_READ = 3;
	public static final int ISO_SERIALIZABLE = 4;
	
	public static final int COMMIT = 10;
	public static final int ROLLBACK = 20;
	public static final int FAILED = 30;
	public static final int ABORT = 40;
	
	public static final String BEGIN_LOG = "START";
	public static final String COMMIT_LOG = "COMMIT";
	public static final String ABORT_LOG = "ABORT";
	
	
	private String name;
	private int isolation_level;
	private PreparedStatement preparedStatement;
	private DBConnection dbCon;
	private Connection con;
	private BufferedWriter fWriter;
	private String schema;
	private String tableName;
	private boolean isWrite;
	private String protocolTag;
	private String query;
	private String sender;
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
		this.protocolTag = protocolTag;
		this.schema = schema;
		this.isWrite = isWrite;
		this.tableName = tableName;
		this.name = name;
		isolation_level = ISO_SERIALIZABLE;
	}
	
	public Transaction(String query, String sender){
		dbCon = new DBConnection();
		con = dbCon.getConnection();
		preparedStatement = null;
		obList = new ArrayList<QueryObserver>();
		rs = null;
		cs = null;
		this.sender = sender;
		this.query = query;
		isolation_level = ISO_SERIALIZABLE;
	}
	
	public String getTableName() {
		return tableName;
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
	public int getIsolation_level() {
		return isolation_level;
	}
	
	public CachedRowSetImpl getCachedRowSetImpl(){
		return this.cs;
	}
	
	public void setCachedRowSetImpl(CachedRowSetImpl cs){
		this.cs = cs;
	}
	
	public void setIsolation_level(int isolation_level) {
		
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
	
	public void endTransaction(int action){
		
		System.out.println("End Transaction: " + name + "\n");
		
		if(action == COMMIT){
			try {
				con.commit();
				notifyObservers();
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(action == ABORT){
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
				
				System.out.println("SENDER: "+sender);
				if(!Tags.NONE.equals(sender)){
					Site s = Controller.searchForSite(sender);
					System.out.println("SNAME: "+s.getName());
					try{
						Socket SOCK = new Socket(s.getIpadd(),Tags.PORT);
						String sProtocol = Tags.RESULT_SET.getBytes() + Tags.PROTOCOL;
						
						OutputStream tempOut = SOCK.getOutputStream();
					 	byte[] protocol = sProtocol.getBytes();
					 	byte[] object = Controller.serialize(cs);
					 	byte[] mail = Controller.byteConcat(protocol, object);
					 	
					 	InputStream is = new ByteArrayInputStream(mail);
					 	tempOut.write(mail, 0, mail.length);
					 	tempOut.flush();
					 	SOCK.close();
					 	
						System.out.println("FINISH SENDING");
					}catch(Exception e){
						e.printStackTrace();
						System.out.println("FAILED TO SEND RESULT SET TO : "+ sender);
					}
					
				}else				
					notifyQueryObservers(cs);
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
		endTransaction(Transaction.COMMIT);
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
		for(QueryObserver o: obList)
			o.updateResultSet(rs);
	}
}
