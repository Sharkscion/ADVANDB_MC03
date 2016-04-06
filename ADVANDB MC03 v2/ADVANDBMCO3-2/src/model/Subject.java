package model;

import java.sql.ResultSet;
import java.util.Iterator;

import com.sun.rowset.CachedRowSetImpl;

public interface Subject {
	public void registerObserver(Observer o);
	public void unRegisterObserver(Observer o);
	public void notifyObservers(/*Iterator Object*/);
	public void notifyQueryObservers(CachedRowSetImpl rs);
}
