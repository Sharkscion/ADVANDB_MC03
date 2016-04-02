package model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryFactory {
	public ResultSet query(String query) {
		Connection conn = (Connection) new DBConnection().getConnection();
		PreparedStatement stmt;
		ResultSet rs = null;
		try {
			stmt = (PreparedStatement) conn.prepareStatement(query);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet getQuery() {
		String query = "select * from numbers";
		return this.query(query);
	}
}
