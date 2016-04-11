package model;

import java.sql.ResultSet;
import java.util.Iterator;

import com.sun.rowset.CachedRowSetImpl;

public interface Subject {
	public void registerObserver(Observer o);
	public void unRegisterObserver(Observer o);
	public void notifyObservers(String tranName);
	public void notifyQueryObservers(String tranName,TableContents tc);
}
