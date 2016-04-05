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
import model.TransactionMail;

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
	private Transaction partialTransaction;
	
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		OUT = null;
		rs = null;
		t = null;
		partialTransaction = null;
		obList = new ArrayList<Observer>();
		rsList = new ArrayList<CachedRowSetImpl>();
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
	
	public void RECEIVED_PARTIAL_COMMIT(String name){
		System.out.println("PARTIAL COMMIT IN CONTROLLER!");
	}
	
	public void EXECUTE_QUERY_REQUEST(TransactionMail tm){
		System.out.println("EXECUTING LOCAL QUERY READ REQUEST: "+ owner.getName());
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver());		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		t.registerObserver(this);
		
		Thread T = new Thread(t);
		T.start();
	}
	
	
	public void RETURN_READ_EXECUTE(byte[] receiveByte){
		System.out.println("EXECUTING RETURN QUERY");
	
		TransactionMail tm;
		try {
			
			byte[] byteArr = new byte[65500];
			InputStream is = new ByteArrayInputStream(receiveByte);
			int bytesRead = is.read(byteArr, 0, byteArr.length);
			
			System.out.println("writeFile (bytes received: " + bytesRead + ")");
			
			tm = (TransactionMail) deserialize(byteArr);
			System.out.println("STARTING THREAD");
			
			Transaction t = new Transaction(tm.getQuery(), tm.getReceiver());		
			
			System.out.println("ReCEIVER: "+ tm.getReceiver().getName());
			System.out.println("SENDER: "+tm.getSender().getName() + " IP:"+tm.getSender().getIpadd()+"#");
			
			t.setSender(tm.getSender());
			t.setIsolation_level(tm.getISO_LEVEL());
			t.setTableName(tm.getTableName());
			t.setTran_action(tm.getTranAction());
			t.setWrite(tm.isWrite());
			t.registerObserver(this);
			
			Thread T = new Thread(t);
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
			
			System.out.println("RESULT SET RECEIVED");
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

	public void SEND_QUERY_TO_RECEIVER(String mail, TransactionMail tm) throws UnknownHostException, IOException{
				
			Site receiver = tm.getReceiver();
			byte[] mailQuery = null;
			byte[] mailByte = mail.getBytes();
			byte[] object = serialize(tm);
			mailQuery = byteConcat(mailByte, object);
			
			System.out.println("TO BE SENT TO: " + receiver.getName());
			
			Socket SOCK = new Socket(receiver.getIpadd(),Tags.PORT);
			OutputStream tempOut = SOCK.getOutputStream();
		 	tempOut.write(mailQuery, 0, mailQuery.length);
		 	tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING TO: "+ receiver.getName());
	
	}
	
	
	// Send READ REQUEST NOTIFICATION
	// first# -> query
	// second# -> area
	public void SEND_QUERY_REQUEST(ArrayList<TransactionMail> tList) throws UnknownHostException, IOException
	{
	
		
		System.out.println("==STARTING SENDING QUERY REQUEST==");
		Site receiver = null;
		String mail = Tags.RETURN_READ + Tags.PROTOCOL;
		
		for(TransactionMail tm : tList){
			rsList.clear();
			switch(owner.getName()){
				case Tags.CENTRAL: sendFromCentral(mail, tm); break;
				case Tags.PALAWAN: sendFromPalawan(mail, tm); break;
				case Tags.MARINDUQUE: sendFromMarinduque( mail, tm); break;
				default: System.out.println("SITE NOT RECOGNIZED!");	  
			}
		}
		
		
		System.out.println("==FINISH SENDING QUERY REQUEST ==");
	 }

	public boolean isNodeConnected(Site s){
	
			Socket SOCKET;
			try {
				SOCKET = new Socket(s.getIpadd(), Tags.PORT);
				return SOCKET.getInetAddress().isReachable(2000);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				return false;
			}
	}

	public void sendFromCentral(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		if(tm.isWrite()){
			switch(tm.getReceiver().getName()){
				case Tags.PALAWAN: 
					if(isNodeConnected(tm.getReceiver())){
						SEND_QUERY_TO_RECEIVER(mail, tm);
						tm.setReceiver(owner);
						EXECUTE_QUERY_REQUEST(tm);
					}else{
						System.out.println("UNABLE TO WRITE PALAWAN NOT CONNECTED");
					}break;
				case Tags.MARINDUQUE:
					if(isNodeConnected(tm.getReceiver())){
						SEND_QUERY_TO_RECEIVER(mail, tm);
						tm.setReceiver(owner);
						EXECUTE_QUERY_REQUEST(tm);
					}else{
						System.out.println("UNABLE TO WRITE MARINDUQUE NOT CONNECTED");
					}break;
				case Tags.CENTRAL: System.out.println("NOT YET IMPLEMENTED! :( "); break;
			}
			
		}else{
			EXECUTE_QUERY_REQUEST(tm); //implement special query before sending to the controller
		}
	}
	
	public void sendFromPalawan(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		Site receiver = null;
		Site receiver2 = null;
		if(tm.isWrite()){
			switch(tm.getReceiver().getName()){
				case Tags.CENTRAL:System.out.println("NOT YET IMPLEMENTED");break;
				case Tags.MARINDUQUE:
					receiver = owner.searchConnection(Tags.CENTRAL);
					receiver2 = tm.getReceiver();
					if(isNodeConnected(receiver)&& isNodeConnected(receiver2)){
							// if central is not down and marin is not down
							// then execute the write query
							tm.setReceiver(receiver);
							SEND_QUERY_TO_RECEIVER(mail, tm);
							
							tm.setReceiver(receiver2);
							SEND_QUERY_TO_RECEIVER(mail, tm);
						
					}else{
						System.out.println("UNABLE TO WRITE :( CENTRAL AND MARIN IS DOWN");
					}break;
				case Tags.PALAWAN:
					receiver = owner.searchConnection(Tags.CENTRAL);
					if(isNodeConnected(tm.getReceiver())){
						SEND_QUERY_TO_RECEIVER(mail, tm);
						EXECUTE_QUERY_REQUEST(tm);
					}else{
						System.out.println("UNABLE TO WRITE :( CENTRAL IS DOWN");
					}break;
			}
			
		}else{
			
			switch(tm.getReceiver().getName()){
				case Tags.PALAWAN: EXECUTE_QUERY_REQUEST(tm);break;
				
				case Tags.MARINDUQUE:
					 //if Central is online
					 receiver = owner.searchConnection(Tags.CENTRAL);
					 if(isNodeConnected(receiver)){
						 tm.setReceiver(receiver);
						 SEND_QUERY_TO_RECEIVER(mail, tm);
					 }else{
						 //if Central is not online send it to Marinduque
						 System.out.println("CENTRAL IS NOT ONLINE");
						 if(isNodeConnected(tm.getReceiver())){
							 SEND_QUERY_TO_RECEIVER(mail, tm); 
						 }else{
							 System.out.println("MARINDUQUE NOT CONNECTED :(");
						 }
						 
					 }break;
					 
				case Tags.CENTRAL:
					 // if Central is connected
					 if(isNodeConnected(tm.getReceiver())){
						 SEND_QUERY_TO_RECEIVER(mail, tm);
					 }else{
						 receiver = owner.searchConnection(Tags.MARINDUQUE);
						 if(isNodeConnected(receiver)){
							 //if Marinduque is connected send to Marinduque
							 tm.setReceiver(receiver);
							 SEND_QUERY_TO_RECEIVER(mail, tm);
							 
							 //execute to self and merge result set from marinduque
							 tm.setReceiver(owner);
							 EXECUTE_QUERY_REQUEST(tm);
						 }
					 }break;
				default: System.out.println("UNABLE TO READ QUERY: "+ tm.getQuery());
			}
		}
		
	}
	
	public void sendFromMarinduque(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		
		Site receiver = null;
		Site receiver2 = null;
		
		if(tm.isWrite()){
			switch(tm.getReceiver().getName()){
				case Tags.CENTRAL:System.out.println("NOT YET IMPLEMENTED");break;
				case Tags.PALAWAN:
					receiver = owner.searchConnection(Tags.CENTRAL);
					receiver2 = tm.getReceiver();
					if(isNodeConnected(receiver)&& isNodeConnected(receiver2)){
							// if central is not down and palawan is not down
							// then execute the write query
							tm.setReceiver(receiver);
							SEND_QUERY_TO_RECEIVER(mail, tm);
							
							tm.setReceiver(receiver2);
							SEND_QUERY_TO_RECEIVER(mail, tm);
						
					}else{
						System.out.println("UNABLE TO WRITE :( CENTRAL AND PALAWAN IS DOWN");
					}break;
				case Tags.MARINDUQUE:
					//if central is not down
					receiver = owner.searchConnection(Tags.CENTRAL);
					if(isNodeConnected(tm.getReceiver())){
						SEND_QUERY_TO_RECEIVER(mail, tm);
						EXECUTE_QUERY_REQUEST(tm);
					}else{
						System.out.println("UNABLE TO WRITE :( CENTRAL IS DOWN");
					}break;
			}
			
		}else{
			
			switch(tm.getReceiver().getName()){
				case Tags.MARINDUQUE: EXECUTE_QUERY_REQUEST(tm); break;
				
				case Tags.PALAWAN:
					//if central is online
					receiver = owner.searchConnection(Tags.CENTRAL);
					if(isNodeConnected(tm.getReceiver())){
						tm.setReceiver(receiver);
						SEND_QUERY_TO_RECEIVER(mail, tm);						
					}else{
						// if central is not connected then send the request to palawan
						System.out.println("CENTRAL IS DOWN :(");
						if(isNodeConnected(tm.getReceiver())){
							SEND_QUERY_TO_RECEIVER(mail, tm);
						}else{
							System.out.println("PALAWAN IS NOT CONNECTED!");
						}
					}break;
				
				case Tags.CENTRAL: 
					if(isNodeConnected(tm.getReceiver())){
						SEND_QUERY_TO_RECEIVER(mail, tm);
					}else{
						System.out.println("CENTRAL NOT CONNECTED!");
						receiver = owner.searchConnection(Tags.PALAWAN);						
						if(isNodeConnected(receiver)){
							//send the query request to palawan
							tm.setReceiver(receiver);
							SEND_QUERY_TO_RECEIVER(mail, tm);
							
							//execute the query yourself and merge it with palawan
							tm.setReceiver(owner);
							EXECUTE_QUERY_REQUEST(tm);
						}else{
							System.out.println("PALAWAN NOT CONNECTED!");
						}
						
					}break;
				default: System.out.println("UNABLE TO READ QUERY: "+ tm.getQuery());
			}
		}
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
			System.out.println("SIZE OF RES LIST: "+ rsList.size());
			for(Observer o: obList){
			  
	            o.update();
	            System.out.println("UPDATING TABLE");
	        }
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
		rsList.add(rs);
	}
	
	public static Site searchForSite(String username){
		return owner.searchConnection(username);
		
	}
}
