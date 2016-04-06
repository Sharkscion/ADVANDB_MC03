package model;

import java.sql.ResultSet;

import com.sun.rowset.CachedRowSetImpl;

public interface QueryObserver extends Observer {
	public void updateResultSet(CachedRowSetImpl rs);
}
