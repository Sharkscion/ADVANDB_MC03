package model;

import java.io.BufferedWriter;
import java.sql.Connection;

public interface TransactionInterface{

	public static int SERIALIZABLE = 1;
	public static int REPEATABLE_READS = 2;
	public static int READ_COMMITTED = 3;
	public static int READ_UNCOMMITTED = 4;
	
	public static int COMMIT = 5;
	public static int ROLLBACK = 6;
	
	
	public int getTimeStamp();
	public void setTimeStamp(int timeStamp);
	public void setIsolationLevel(int iso_level);
	public int getIsolationLevel();
	public void beginTransaction();	
	public void endTransaction(int ending);
        public Connection getConnection();
	//public void run(int id, int number, int value, int ending);

}
