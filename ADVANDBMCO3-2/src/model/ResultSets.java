package model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sun.rowset.CachedRowSetImpl;

public class ResultSets implements Serializable {
	
	private ArrayList<CachedRowSetImpl> rsList;
	private CachedRowSetImpl current;
	
	 public ResultSets(ArrayList<CachedRowSetImpl> rsList) {
         this.rsList = new ArrayList<CachedRowSetImpl>(rsList);
         if(rsList.size()> 1)
        	 current = rsList.remove(0);
         else
        	 current = this.rsList.get(0);
     }
	 
	 public boolean next() {
       try {
    	   if(!rsList.isEmpty()){
    		   if (current.next()) {
    		       return true;
    		   }else if (!rsList.isEmpty()) {
    		       current = rsList.remove(0);
    		       return next();
    		   }
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
