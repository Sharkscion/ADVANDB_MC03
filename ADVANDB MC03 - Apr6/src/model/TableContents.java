package model;

import java.util.ArrayList;
import java.util.Vector;

public class TableContents {
	
	private String tranName;
	private Vector<String> columnNames; //column anmes
    private Vector<Vector<Object>> data;
	private Vector<Object> vector; //row
	

	public TableContents(String tranName){
		columnNames = new Vector<String>();
		data = new Vector<Vector<Object>>();
		vector = new Vector<Object>();
		this.tranName = tranName;
	}
	
	public String getTranName() {
		return tranName;
	}
	public void setTranName(String tranName) {
		this.tranName = tranName;
	}
	
	public Vector<Vector<Object>> getData() {
		return data;
	}
	public void setData(Vector<Vector<Object>> data) {
		this.data = data;
	}
	   public Vector<String> getColumnNames() {
			return columnNames;
		}
		public void setColumnNames(Vector<String> columnNames) {
			this.columnNames = columnNames;
		}
		public Vector<Object> getVector() {
			return vector;
		}
		public void setVector(Vector<Object> vector) {
			this.vector = vector;
		}
}
