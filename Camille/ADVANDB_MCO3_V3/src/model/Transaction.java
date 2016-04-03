package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transaction implements Runnable{
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

	public Transaction(String protocolTag, String name, String schema, String tableName,String query, boolean isWrite){
		try {
			dbCon = new DBConnection();
			con = dbCon.getConnection();
			preparedStatement = null;
			this.query = query;
			this.protocolTag = protocolTag;
			this.schema = schema;
			this.isWrite = isWrite;
			this.tableName = tableName;
			this.name = name;
			isolation_level = ISO_SERIALIZABLE;
			fWriter = new BufferedWriter(new FileWriter("log.txt", true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void setIsolation_level(int isolation_level) {
		
		System.out.println("Setting isolation level for Transaction "+name+" to: " + isolation_level);
		this.isolation_level = isolation_level;
		
		try {
			con.setAutoCommit(false);
			
			switch(isolation_level) {
			
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
		System.out.println("\nBegin Transaction: " + name);
		
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
	
	public void runTransaction(){
		System.out.println("HELLO");
	}
	
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		beginTransaction();
		runTransaction();
	}
}
