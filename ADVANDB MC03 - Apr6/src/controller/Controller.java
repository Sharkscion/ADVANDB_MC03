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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import model.Mail;
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
	private HashMap<String, Transaction>partialList;
	
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		OUT = null;
		rs = null;
		t = null;
		obList = new ArrayList<Observer>();
		partialList = new HashMap<String, Transaction>();
	
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
	
	public void COMMIT_TRANSACTION(String name){
		Transaction t =  partialList.get(name);
		t.setTran_action(Transaction.COMMIT);
		t.endTransaction();
	}
	
	public void ABORT_TRANSACTION(String name){
		Transaction t =  partialList.get(name);
		t.setTran_action(Transaction.ABORT);
		t.endTransaction();
	}
	
	public void PARTIAL_COMMIT_FROM_CENTRAL(Mail rMail){
		
		//receiver yung nag taga execute muna ng write-> which mostlikely si central
		//sender yung nag send ng request and mag rereceive ng PARTIAL commit status from central
		//check if central ay buhay habang nag wriwrite siya
			System.out.println("RECEIVEED PARTIAL COMMIT STATUS FORM CENTRAL");
			
			TransactionMail tm = rMail.getTm();
			Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());
			t.setSender(tm.getSender());
			t.setIsolation_level(tm.getISO_LEVEL());
			t.setTableName(tm.getTableName());
			t.setTran_action(Transaction.COMMIT);
			t.setGoCommit(true); // meaning go forth and commit
			t.setWrite(tm.isWrite());
		//	t.registerObserver(this);
		
//			if(tm.isWrite()){
//				System.out.println("SET PARTIAL COMMIT TO TRANSACTION RECEIVED TRANSACTITON");
//				partialCommit = t;
//			}
			
			Thread T = new Thread(t);
			T.start();	
	}
	
	public void ABORT_FROM_CENTRAL(Mail rMail){
		
	}
	public void EXECUTE_LOCAL_QUERY_REQUEST(TransactionMail tm){
		System.out.println("EXECUTING LOCAL QUERY REQUEST: "+ owner.getName());
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		t.registerObserver(this);
		
		if(tm.isWrite()){
			System.out.println("SET PARTIAL COMMIT TO TRANSACTION");
			partialList.put(tm.getTranName(), t);
		}
		
		Thread T = new Thread(t);
		T.start();
	}
	
	
	public void EXECUTE_QUERY_REQUEST(Mail rMail){
		System.out.println("EXECUTING RETURN QUERY");
	
		TransactionMail tm;
		
		tm= rMail.getTm();
		System.out.println("STARTING THREAD");
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());		
		
		System.out.println("ReCEIVER: "+ tm.getReceiver().getName());
		System.out.println("SENDER: "+tm.getSender().getName() + " IP:"+tm.getSender().getIpadd()+"#");
		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		t.registerObserver(this);
		
		if(tm.isWrite()){
			System.out.println("SET PARTIAL COMMIT TO TRANSACTION");
			partialList.put(tm.getTranName(), t);
		}
		
		Thread T = new Thread(t);
		T.start();		
	
	}
	
	public void RECEIVE_RESULT_SET(Mail rMail){
	
		CachedRowSetImpl cas = rMail.getCs();
		
		if(cas == null)
			System.out.println("NULL SI CRS :(");
		else{
			System.out.println("RECEIVED RESULT");
			updateResultSet(cas);
			notifyObservers();
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

	public void SEND_QUERY_TO_RECEIVER(Mail mail) throws UnknownHostException, IOException{
	
		try{
			
			Site receiver = mail.getTm().getReceiver();
//			byte[] mailQuery = serialize(mail);
			
			System.out.println("TO BE SENT TO: " + receiver.getName());
			
			Socket SOCK = new Socket(receiver.getIpadd(),Tags.PORT);
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(mail);
			ous.flush();
			ous.close();
			tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING TO: "+ receiver.getName());
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND RESULT SET TO : "+ mail.getTm().getReceiver().getName());
		}
	}
	
	public static boolean isNodeConnected(Site s){

			
			Socket SOCKET;
			try {
				SOCKET = new Socket(s.getIpadd(), Tags.PORT);
				//SOCKET.setSoTimeout(5000);
				SOCKET.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			return true;
	}
	
	public void sendWriteFromPalawan(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		
		Site receiver = null;
		Site receiver2 = null;
		Mail m = new Mail(mail);
		
		
		//means palawan requesting to update data from own
		if(tm.getReceiver().equals(tm.getSender())){
			receiver = owner.searchConnection(Tags.CENTRAL);
			if(isNodeConnected(receiver)){
				System.out.println("EXECUTING WRITE DATA");
				//set receiver of the qrite request to central
				tm.setReceiver(receiver);
				m.setTm(tm);
				SEND_QUERY_TO_RECEIVER(m);
			}else{
				System.out.println("UNABLE TO WIRTE-> CENTRAL IS DOWN");
			}
		}else if(tm.getReceiver().getName().equals(Tags.MARINDUQUE)){
			receiver = owner.searchConnection(Tags.CENTRAL);
			receiver2 = owner.searchConnection(Tags.MARINDUQUE);
			if(isNodeConnected(receiver) && isNodeConnected(receiver2)){
				System.out.println("SENDING WRITE MARIN DATA REQUEST TO CENTRAL");
				
				tm.setReceiver(receiver); // set the receiver of the write request to central
				tm.setSender(receiver2);  // set the sender to marin so the notif could be send to it
				m.setTm(tm);
				SEND_QUERY_TO_RECEIVER(m);
			}
			
		}
	}

	public void sendReadFromPalawan(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		Site receiver = null;
		Mail m = new Mail(mail);
		m.setTm(tm);
		
		System.out.println("HELLO: "+ tm.getReceiver().getName());
		//If local yung query
		  if(tm.getReceiver().equals(tm.getSender()))
			  EXECUTE_LOCAL_QUERY_REQUEST(tm);
		  else if(tm.getReceiver().getName().equals(Tags.MARINDUQUE)){
			  //if Central is not down then send the query to central
			  //execute special query to get marinduque data only
			  receiver = owner.searchConnection(Tags.CENTRAL);
			  
			  if(isNodeConnected(receiver)){//check if central is connected
				  tm.setReceiver(receiver);
				  SEND_QUERY_TO_RECEIVER(m);
			  }else{
				  receiver = owner.searchConnection(Tags.MARINDUQUE);
				  if(isNodeConnected(receiver)){					  
					  tm.setReceiver(receiver);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND MARINDUQUE IS DOWN CANT GET MARINDUQUE");
				  }
			  }
		  }else if(tm.getReceiver().getName().equals(Tags.CENTRAL)){
			  System.out.println("SENDING TO CENTRAL");
			  if(isNodeConnected(tm.getReceiver())){
				  SEND_QUERY_TO_RECEIVER(m); //send to central
			  }else{
				  
				  receiver = owner.searchConnection(Tags.MARINDUQUE);
				  if(isNodeConnected(receiver)){
					  tm.setReceiver(owner);
					  EXECUTE_LOCAL_QUERY_REQUEST(tm);
					  
					  tm.setReceiver(receiver);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND MARINDUQUE IS DOWN :( CANNT GET MERGE");
				  }
				  
			  }
		  }
	}
	public void SEND_QUERY_REQUEST(ArrayList<TransactionMail> tList) throws UnknownHostException, IOException
	{
		System.out.println("==STARTING SENDING QUERY REQUEST==");
		Site receiver = null;
		String mail = Tags.RETURN_READ + Tags.PROTOCOL;
		rsList = new ArrayList<CachedRowSetImpl>();

		for(TransactionMail tm : tList){
			rsList.clear();
			switch(owner.getName()){
				case Tags.CENTRAL: EXECUTE_LOCAL_QUERY_REQUEST(tm); break;
				case Tags.PALAWAN:
						 if(!tm.isWrite())
							 sendReadFromPalawan(mail, tm);
						 else{
							 System.out.println("WRITE");
							 sendWriteFromPalawan(mail, tm);
						 }
						break;
				case Tags.MARINDUQUE:
						//If local yung query
						if(tm.getReceiver().equals(tm.getSender()))
							 EXECUTE_LOCAL_QUERY_REQUEST(tm);
						else{
							//If central is not down then send the query to central
							  receiver = owner.searchConnection(Tags.CENTRAL);
							  try{
								  tm.setReceiver(receiver);
								//  SEND_QUERY_TO_RECEIVER(mail, tm);
							  }catch(Exception e){
								  System.out.println(Tags.CENTRAL + " IS NOT CONNECTED!");
								  
								  tm.setReceiver(owner);
								  EXECUTE_LOCAL_QUERY_REQUEST(tm);
								  
								  try{
									  receiver = owner.searchConnection(Tags.PALAWAN);  
									  tm.setReceiver(receiver);
									//  SEND_QUERY_TO_RECEIVER(mail, tm);
								  }catch(Exception e1){
									  System.out.println(Tags.PALAWAN+" IS NOT CONNECTED!");
								  } 
							  }
						}break;
							  
				default: System.out.println("SITE NOT RECOGNIZED!");	  
			}
		}
		
		
		System.out.println("==SENDING QUERY REQUEST END==");
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
			    System.out.println("UPDATING TABLE");
	            o.update();
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

//		try {
//			ResultSetMetaData mdata = rs.getMetaData();
//			int colCount = mdata.getColumnCount();
//			while (rs.next()) {
//				//String[] rowData = new String[colCount];
//				for (int i = 1; i <= colCount; i++) {
//					//rowData[i - 1] = rs.getString(i);
//					System.out.println("DATA: "+ rs.getString(i));
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		rsList.add(rs);
	}
	
	public static Site searchForSite(String username){
		return owner.searchConnection(username);
		
	}
}
