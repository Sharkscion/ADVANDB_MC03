//package model;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class ReadTransaction extends Transaction{
//	
//	
//	public ReadTransaction(String name, String schema, String tableName){
//		super(name, schema, tableName);
//	}
//
//	@Override
//	public void runTransaction(Object val, int id) {
//		try {
//			String query = "SELECT id, col FROM numbers;";
//			
//			preparedStatement = con.prepareStatement(query);
//			ResultSet rs = preparedStatement.executeQuery();
//		    
//			System.out.println("----------------------------------------");	
//			while(rs.next()) {
//				System.out.println("|"+name + " <" + rs.getInt(1) + ": " + rs.getInt(2) +">|");
//			}
//			System.out.println("----------------------------------------");	
//
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		 // lock.rl(this);
//		  setIsolation_level(Transaction.ISO_READ_UNCOMMITTED);
//		  beginTransaction(false);
//		  runTransaction(null, 0);
//		  endTransaction(Transaction.COMMIT, false);
//		 // lock.unlock(this);
//	}
//	
//	
//	
//
//
//}
