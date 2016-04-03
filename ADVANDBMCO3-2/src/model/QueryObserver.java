package model;

import java.sql.ResultSet;

public interface QueryObserver extends Observer {
	public void updateResultSet(ResultSet rs);
}
