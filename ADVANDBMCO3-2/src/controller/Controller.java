package controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Observer;
import model.QueryObserver;
import model.ResultSets;
import model.Site;
import model.Subject;
import model.Tags;
import model.Transaction;

import com.sun.rowset.CachedRowSetImpl;

public class Controller implements Subject, QueryObserver
{
	// database manager?
	
	private ArrayList<Observer> obList;
	private static Site owner;
	private Socket SOCK;
	private PrintWriter OUT;
	private ResultSets rs;
	private Transaction t;
	private ArrayList<CachedRowSetImpl> rsList;
	
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


	public Site getOwner(){
		return owner;
	}
	public void setResultSets(ArrayList<CachedRowSetImpl> rs){
		this.rsList = rs;
	}
	public ArrayList<CachedRowSetImpl> getResultSets(){
		return this.rsList;
	}

	public void add(String ip, String name){
		Site newSite = new Site(ip,name);
		System.out.println("NEW SITE: "+ newSite.getName());
		owner.addConnection(newSite);
	}
	
	public void EXECUTE_QUERY_REQUEST(ArrayList<Transaction> tList){
		System.out.println("EXECUTING LOCAL QUERY READ REQUEST: "+ owner.getName());
		Thread T = new Thread(tList.get(0));
		T.start();
	}
	
	
	public void RETURN_READ_EXECUTE(byte[] receiveByte){
		System.out.println("EXECUTING RETURN QUERY");
	
		ArrayList<Transaction> tList;
		try {
			
			byte[] byteArr = new byte[65500];
			InputStream is = new ByteArrayInputStream(receiveByte);
			int bytesRead = is.read(byteArr, 0, byteArr.length);
			
			System.out.println("writeFile (bytes received: " + bytesRead + ")");
			
			tList = (ArrayList<Transaction>) deserialize(byteArr);
			System.out.println("STARTING THREAD");
			Thread T = new Thread(tList.get(0));
			T.start();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void RECEIVE_RESULT_SET(byte[] resultset){
		try {
			
			byte[] mybytearray = new byte[65500];							// Creates a byte array
			InputStream is = new ByteArrayInputStream(resultset);				// Creates an input stream from the given bytes
			int bytesRead = is.read(mybytearray, 0, mybytearray.length);	// Reads bytes from bytes to mybytearray and stores number of bytes read
			
			System.out.println("writeFile (bytes received: " + bytesRead + ")");
		
			CachedRowSetImpl cas = (CachedRowSetImpl) deserialize(mybytearray);
			
			if(cas == null)
				System.out.println("NULL SI CRS :(");
			else{
				updateResultSet(cas);
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
	
	

	
	public void SEND_QUERY_TO_RECEIVER(String mail, ArrayList<Transaction> tList, Site receiver) throws UnknownHostException, IOException{
	
		try{
			System.out.println("TO BE SENT TO: " + receiver.getName());
			byte[] mailQuery = null;
			byte[] mailByte = mail.getBytes();
			
			/**change the transaction receiver since hndi on si central**/
			for(Transaction t : tList)
				t.setReceiver(receiver);
			
			byte[] object = serialize(tList);
			mailQuery = byteConcat(mailByte, object);
			
			Socket SOCK = new Socket(receiver.getIpadd(),Tags.PORT);
			OutputStream tempOut = SOCK.getOutputStream();
		 	tempOut.write(mailQuery, 0, mailQuery.length);
		 	tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING TO: "+ receiver.getName());
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND RESULT SET TO : "+ receiver.getName());
		}
	}
	
	
	// Send READ REQUEST NOTIFICATION
	// first# -> query
	// second# -> area
	public void SEND_QUERY_REQUEST(ArrayList<Transaction> tList)
	{
		rsList = new ArrayList<CachedRowSetImpl>();
		
		System.out.println("==STARTING SENDING QUERY REQUEST==");
		Site receiver = null;
		String mail = Tags.RETURN_READ + Tags.PROTOCOL + owner.getName() + Tags.PROTOCOL;
		
		switch(owner.getName()){
			case Tags.CENTRAL: EXECUTE_QUERY_REQUEST(tList); break;
			case Tags.PALAWAN:
					  receiver = owner.searchConnection(Tags.CENTRAL);
					  try{
						  SEND_QUERY_TO_RECEIVER(mail, tList, receiver);
					  }catch(Exception e){
						  System.out.println(Tags.CENTRAL + " IS NOT CONNECTED!");
						  
						  EXECUTE_QUERY_REQUEST(tList);
						  receiver = owner.searchConnection(Tags.MARINDUQUE);
						  
						  try{
							  SEND_QUERY_TO_RECEIVER(mail, tList, receiver);
						  }catch(Exception e1){
							  System.out.println(Tags.MARINDUQUE+" IS NOT CONNECTED!");
						  }  
					  }break;
			case Tags.MARINDUQUE:
					  receiver = owner.searchConnection(Tags.CENTRAL);
					  try{
						  SEND_QUERY_TO_RECEIVER(mail, tList, receiver);
					  }catch(Exception e){
						  System.out.println(Tags.CENTRAL + " IS NOT CONNECTED!");
						  
						  EXECUTE_QUERY_REQUEST(tList);
						  receiver = owner.searchConnection(Tags.PALAWAN);
						  
						  try{
							  SEND_QUERY_TO_RECEIVER(mail, tList, receiver);
						  }catch(Exception e1){
							  System.out.println(Tags.PALAWAN+" IS NOT CONNECTED!");
						  } 
					  }break;
						  
			default: System.out.println("SITE NOT RECOGNIZED!");	  
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
		
		if(rsList != null){
			System.out.println("BAGO LIST :(");
			System.out.println("SIZE OF RES LIST: "+ rsList.size());
			for(Observer o: obList){
			    System.out.println("UPDATING TABLE");
	            o.update();
	        }
			rsList.clear();
		}
	  
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		notifyObservers();
	}

	@Override
	public void notifyQueryObservers(CachedRowSetImpl rs) {
		// TODO Auto-generated method stub		
		updateResultSet(rs);
	}

	@Override
	public void updateResultSet(CachedRowSetImpl rs) {
		System.out.println("FINISH EXECUTING QUERY READ REQUEST");
		try {
			System.out.println("ADDING RESULT SET : "+ rs.getFetchSize());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rsList.add(rs);
	}
	
	public static Site searchForSite(String username){
		return owner.searchConnection(username);
		
	}
}
