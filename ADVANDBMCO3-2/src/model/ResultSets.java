package model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResultSets implements Serializable {
	
	private ArrayList<ResultSet> rsList;
	private ResultSet current;
	
	 public ResultSets(ArrayList<ResultSet> rsList) {
         this.rsList = new ArrayList<ResultSet>(rsList);
         if(rsList.size()> 1)
        	 current = rsList.remove(0);
         else
        	 current = this.rsList.get(0);
     }
	 
	 public boolean next() {
       try {
			if (current.next()) {
		       return true;
		   }else if (!rsList.isEmpty()) {
		       current = rsList.remove(0);
		       return next();
		   }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return false;
	 }
	 
	 public ResultSetMetaData getMetaData(){
		 
		 try {
			return current.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	 }
	 
	 public int getInt(int pos){
         try {
			return current.getInt(pos);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        return -1;
     }

	 public String getString(int field){
         try {
			return current.getString(field);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         return null;
     }

}
