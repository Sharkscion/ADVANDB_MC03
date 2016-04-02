package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Transaction implements Runnable{
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
	
	public static final String STATUS_LOG = "status=";
	public static final String TRAN_ID = "t=";
	public static final String SCHEMA = "schema=";
	public static final String OLD_VAL = "old=";
	public static final String NEW_VAL = "new=";
	public static final String TABLE_DET = "table_det=";
	
	
	protected String name;
	protected int isolation_level;
	protected PreparedStatement preparedStatement;
	protected DBConnection dbCon;
	protected Connection con;
	protected BufferedWriter fWriter;
	protected int timeStamp;
	protected String schema;
	protected String tableName;

	public Transaction(String name, String schema, String tableName){
		try {
			dbCon = new DBConnection();
			con = dbCon.getConnection();
			preparedStatement = null;
			timeStamp = 0;
			this.schema = schema;
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
	
//	public void recordToLog(int oldValue, int newValue,int id, String colName){
//		
//		try {
//			fWriter.write("<"+ TRAN_ID + name + ","
//							 + SCHEMA + schema + ","
//					         + TABLE_DET + tableName + ":" + colName + ":" + id + ","
//							 + OLD_VAL + oldValue + ","
//					         + NEW_VAL + newValue + ">");
//			fWriter.newLine();
//			System.out.println("Recording to Log...");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//	}
	
//	public void setTimeStamp(int timeStamp){
//		this.timeStamp = timeStamp;
//	}
//	
//	public int getTimeStamp(){
//		return this.timeStamp;
//	}
	
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
//	public BufferedWriter getFwriter() {
//		return fWriter;
//	}
//	public void setFwriter(BufferedWriter fwriter) {
//		this.fWriter = fwriter;
//	}
//	
	public void beginTransaction(boolean isWrite){
		
		String queryLock = "";
		System.out.println("\nBegin Transaction: " + name);
		
		try {
			con.setAutoCommit(false);
			
			if(isWrite){
				queryLock = "LOCK TABLES "+tableName+" WRITE;";
			}else
				queryLock = "LOCK TABLES "+tableName+" READ;";
		
			preparedStatement = con.prepareStatement(queryLock);
			preparedStatement.execute();
			
			preparedStatement = con.prepareStatement("START TRANSACTION;");
			preparedStatement.executeQuery();
			
//			if(isWrite){
//				fWriter.write("<"+ TRAN_ID + name + ","
//								 + SCHEMA + schema + ","
//								 + STATUS_LOG + BEGIN_LOG +">");
//				fWriter.newLine();
//				System.out.println("Recording to Log Begin Write Transaction...");
//			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void endTransaction(int action, boolean isWrite){
		
		System.out.println("End Transaction: " + name + "\n");
		
		if(action == COMMIT){
			try {
				con.commit();
//				if(isWrite){
//					try {
//						fWriter.write("<"+ TRAN_ID + name + ","
//					                     + SCHEMA + schema + ","
//								         + STATUS_LOG + COMMIT_LOG + ">");
//						fWriter.newLine();
//						fWriter.close();
//						System.out.println("Recording to Log Commit Write Transaction...\n");
//					
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
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
	
	public abstract void runTransaction(Object val, int id);
}
