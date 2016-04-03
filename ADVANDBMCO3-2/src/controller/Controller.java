package controller;

import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.CustomResultSet;
import model.Observer;
import model.QueryObserver;
import model.ResultSets;
import model.Site;
import model.Subject;
import model.Tags;
import model.Transaction;

public class Controller implements Subject, QueryObserver
{
	// database manager?
	
	private ArrayList<Observer> obList;
	private Site owner;
	private Socket SOCK;
	private PrintWriter OUT;
	private ResultSets rs;
	private Transaction t;
	private ArrayList<ResultSet> rsList;
	
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		OUT = null;
		rs = null;
		t = null;
		obList = new ArrayList<Observer>();
		// instantiate database manager
	}
	
	public void setResultSets(ResultSets rs){
		this.rs =rs;
	}
	public ResultSets getResultSets(){
		return this.rs;
	}

	public void add(String ip, String name)
	{
		Site newSite = new Site(ip,name);
		owner.addConnection(newSite);
	}
	
	public void EXECUTE_READ_REQUEST(String query){
		System.out.println("EXECUTING QUERY READ REQUEST: "+ query);
		
		t = new Transaction(query, Tags.NONE);
		t.registerObserver(this);
		t.setTableName("numbers");
		t.setIsolation_level(Transaction.ISO_SERIALIZABLE);
		Thread T = new Thread(t);
		T.start();
	}
	
	public static byte[] byteConcat(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }
	
   public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
		
	public void RETURN_READ_EXECUTE(String query , String sender){
		
		System.out.println("RECEIVED READ REQUEST FROM : " +sender);
		t = new Transaction(query, sender);
		t.registerObserver(this);
		t.setTableName("numbers");
		t.setIsolation_level(Transaction.ISO_SERIALIZABLE);
		Thread T = new Thread(t);
		T.start();
		
	}
	
	public void RECEIVE_RESULT_SET(byte[] resultset){
		try {
			CustomResultSet crs = (CustomResultSet) deserialize(resultset);
			if(crs == null)
				System.out.println("NULL SI CRS :(");
			else{
				updateResultSet(crs.getRs());
				notifyObservers();
			}
				
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void SEND_READ_TO_RECEIVER(String mail, Site receiver) throws UnknownHostException, IOException{
			SOCK = new Socket(receiver.getIpadd(), Tags.PORT);
			OUT = new PrintWriter(SOCK.getOutputStream());
			OUT.println(mail);
			OUT.flush();
	}
	
	
	// Send READ REQUEST NOTIFICATION
	// first# -> query
	// second# -> area
	public void SEND_READ_REQUEST(String readRequest)
	{
		rsList = new ArrayList<ResultSet>();
		
		System.out.println("==STARTING READ REQUEST==");
		Site receiver = null;
		String message[] = readRequest.split("#", 2);
		String mail = "";
		
		if(!Tags.NONE.equals(message[1])){
			
			//mailExecute = Tags.EXECUTE_READ+"#"+message[0]+"#"+
			mail = Tags.RETURN_READ+"#"+message[0]+"#" + owner.getName();
			
			switch(owner.getName()){
				case Tags.CENTRAL: 
						if(Tags.CENTRAL.equals(message[1]))
							EXECUTE_READ_REQUEST(message[0]);
						 break;
				case Tags.PALAWAN:
						if(Tags.PALAWAN.equals(message[1]))
							EXECUTE_READ_REQUEST(message[0]);
						else if(Tags.MARINDUQUE.equals(message[1])){
							receiver = owner.searchConnection(Tags.CENTRAL);
							try {
								SEND_READ_TO_RECEIVER(mail, receiver);
							} catch (Exception e){
								System.out.println(receiver.getName()+ " NOT CONNECTED!");
								receiver = owner.searchConnection(Tags.MARINDUQUE);
								try {
									SEND_READ_TO_RECEIVER(mail, receiver);
								} catch (Exception e1){
									System.out.println(receiver.getName() + " NOT CONNECTED!");
								}
							}
						}else if(Tags.CENTRAL.equals(message[1])){
							receiver = owner.searchConnection(Tags.CENTRAL);
							try{
								SEND_READ_TO_RECEIVER(mail, receiver);
							}catch(Exception e){System.out.println(receiver.getName()+ " NOT CONNECTED!");}
						} break;
						
				default: System.out.println("UNRECOGNIZED USER");
			}
		}else{
			EXECUTE_READ_REQUEST(message[0]);
		}
		
		System.out.println("==READ REQUEST END==");
	 }

	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		obList.add(o);
	}

	@Override
	public void unRegisterObserver(Observer o) {
		// TODO Auto-generated method stub
		obList.remove(o);
	}
	
	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		
	   rs = new ResultSets(rsList);
	   for(Observer o: obList){
		    System.out.println("UPDATING TABLE");
            o.update();
        }
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		notifyObservers();
		//setResultSet();
	}

	@Override
	public void notifyQueryObservers(ResultSet rs) {
		// TODO Auto-generated method stub		
		updateResultSet(rs);
	}

	@Override
	public void updateResultSet(ResultSet rs) {
		System.out.println("FINISH EXECUTING QUERY READ REQUEST");
		System.out.println("SETTING RESULT SET");
		rsList.add(t.getResultSet());
	}
}
