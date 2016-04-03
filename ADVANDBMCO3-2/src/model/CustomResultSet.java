package model;

import java.io.Serializable;
import java.sql.ResultSet;

public class CustomResultSet implements Serializable{
	
	private ResultSet rs;

	public CustomResultSet(ResultSet rs){
		this.rs = rs;
	}
	
	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}


}
