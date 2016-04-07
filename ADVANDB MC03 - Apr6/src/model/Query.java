package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Query {
	private ArrayList<String> SELECT;
	private ArrayList<String> WHERE;
	private ArrayList<String> SET;
	
	
	public Query(){
		SELECT = new ArrayList<String>();
		WHERE  = new ArrayList<String>();
		SET = new ArrayList<String>();
	}
	

	public ArrayList<String> getSELECT() {
		return SELECT;
	}


	public void setSELECT(ArrayList<String> sELECT) {
		SELECT = sELECT;
	}


	public ArrayList<String> getWHERE() {
		return WHERE;
	}


	public void setWHERE(ArrayList<String> wHERE) {
		WHERE = wHERE;
	}


	public ArrayList<String> getSET() {
		return SET;
	}


	public void setSET(ArrayList<String> sET) {
		SET = sET;
	}

	
	public void addSELECT(String value){
		SELECT.add(value);
	}
	
	public void addWHERE(String value){
		WHERE.add(value);
	}
	
	public void addSET(String value){
		SET.add(value);
	}

}
