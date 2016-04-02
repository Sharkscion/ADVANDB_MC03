package model;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WriteTransaction  extends Transaction{
	
	private int value;
	private int id;

	public WriteTransaction(String name, String schema, String tableName){
		super(name, schema, tableName);
		value = 0;
		id = 0;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Override
	public void runTransaction(Object newVal, int id) {
		try {
			HashMap<Integer,Integer> idList = new HashMap<Integer, Integer>();
			
			String queryR = "SELECT col,id FROM numbers WHERE col BETWEEN 10 AND 65";
			int oldValue = 0;
			int rowID = 0;
			
			preparedStatement = con.prepareStatement(queryR);
			ResultSet res = preparedStatement.executeQuery();
			while(res.next()){
				oldValue = res.getInt(1);
				rowID = res.getInt(2);
				idList.put(rowID, oldValue);
			}
			
			Iterator<Entry<Integer, Integer>> it = idList.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		        
		    	String query = "UPDATE numbers SET col = ? WHERE id = ?;";
				preparedStatement = con.prepareStatement(query);
				preparedStatement.setInt(1, (Integer)newVal);
				preparedStatement.setInt(2, pair.getKey());
				preparedStatement.executeUpdate();
				
				//recordToLog(pair.getValue(), (Integer)newVal, pair.getKey(), "col");
		        it.remove(); // avoids a ConcurrentModificationException
		    }		
			
			System.out.println("Updating...");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 // lock.wl(this);
		  setIsolation_level(Transaction.ISO_READ_UNCOMMITTED);
		  beginTransaction(true);
		  runTransaction(value,id);
		  endTransaction(Transaction.COMMIT, true);
		 // lock.unlock(this);
	}

}
