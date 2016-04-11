package controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;



import model.Mail;
import model.Observer;
import model.Query;
import model.QueryObserver;
import model.ResultSets;
import model.Site;
import model.Subject;
import model.TableContents;
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
	private static HashMap<String,TableContents> rsList;
	private HashMap<String, Transaction>partialList;
	private HashMap<String, Query> queryList;
	private ArrayList<TransactionMail> tranList;
	public Controller(Site owner)
	{
		this.owner = owner;
		SOCK = null;
		rs = null;
		t = null;
		obList = new ArrayList<Observer>();
		partialList = new HashMap<String, Transaction>();
		queryList = new HashMap<String, Query>();
		this.tranList = new ArrayList<TransactionMail>();
		this.queryList = new HashMap<String, Query>();
	
		// instantiate database manager
	}

	public void addTransactionMail(TransactionMail t){
		tranList.add(t);
	}
	
	public void addQueryList(String tranName,Query q){
		queryList.put(tranName,q);
	}
	
	public Site getOwner(){
		return owner;
	}
	public void setResultSets( HashMap<String, TableContents> rs){
		this.rsList = rs;
	}
	public HashMap<String, TableContents> getResultSets(){
		return this.rsList;
	}

	public void add(String ip, String name){
		Site newSite = new Site(ip,name);
		owner.addConnection(newSite);
	}
	
	public void COMMIT_TRANSACTION(String name){
		Transaction t =  partialList.get(name);
		t.setTran_action(Transaction.COMMIT);
		t.endTransaction();
		System.out.println("FINISH COMMITING TRANSACTION: "+name);
	}
	
	public void ABORT_TRANSACTION(String name){
		Transaction t =  partialList.get(name);
		t.setTran_action(Transaction.ABORT);
		t.endTransaction();
		System.out.println("FINISH ABORTING TRANSACTION: " + name);
		
	}
	
	public void PARTIAL_COMMIT_FROM_CENTRAL(Mail rMail){
		
		//receiver yung nag taga execute muna ng write-> which mostlikely si central
		//sender yung nag send ng request and mag rereceive ng PARTIAL commit status from central
		//check if central ay buhay habang nag wriwrite siya
			System.out.println("RECEIVED PARTIAL COMMIT STATUS FORM CENTRAL");
			
			TransactionMail tm = rMail.getTm();
			Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());
			t.setSender(tm.getSender());
			t.setIsolation_level(tm.getISO_LEVEL());
			t.setTableName(tm.getTableName());
			t.setTran_action(tm.getTranAction());
			if(tm.getTranAction() == Transaction.COMMIT)
				t.setGoCommit(true); // meaning go forth and commit
			else{
				t.setGoAbort(true);
			}
			t.setWrite(tm.isWrite());
			t.beginTransaction();
			t.runTransaction();
			t.endTransaction();
			System.out.println("FINISH COMMITING UPDATE TRANSACTION RECEIVED FROM CENTRAL");
	}
	
	public void ABORT_FROM_CENTRAL(Mail rMail){
		//receiver yung nag taga execute muna ng write-> which mostlikely si central
		//sender yung nag send ng request and mag rereceive ng PARTIAL commit status from central
		//check if central ay buhay habang nag wriwrite siya
			System.out.println("RECEIVED PARTIAL ABORT STATUS FROM CENTRAL");
			
			TransactionMail tm = rMail.getTm();
			Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());
			t.setSender(tm.getSender());
			t.setIsolation_level(tm.getISO_LEVEL());
			t.setTableName(tm.getTableName());
			t.setTran_action(Transaction.ABORT);
			t.setGoAbort(true);// meaning go forth and commit
			t.setWrite(tm.isWrite());
			t.beginTransaction();
			t.runTransaction();
			t.endTransaction();
			System.out.println("FINISH ABORTING UPDATE TRANSACTION RECEIVED FROM CENTRAL");		
	}
	
	public void EXECUTE_LOCAL_QUERY_REQUEST(TransactionMail tm){
		System.out.println("EXECUTING LOCAL QUERY REQUEST: "+ tm.getTranName());
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		t.registerObserver(this);
		
		if(tm.isWrite()){
			t.beginTransaction();
			t.runTransaction();
		}else{
			Thread tr = new Thread(t);
			tr.start();
		}
		
		if(tm.isWrite()){
			partialList.put(tm.getTranName(), t);
		}
		
		System.out.println("FINISH EXECUTING LOCAL QUERY REQUEST: "+tm.getTranName());
	
	}
	
	public synchronized void EXECUTE_LOCAL_QUERY_WRITE_REQUEST(TransactionMail tm){
		System.out.println("EXECUTING LOCAL QUERY REQUEST: "+ tm.getTranName());
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		t.registerObserver(this);
		
		if(tm.isWrite()){
			t.beginTransaction();
			t.runTransaction();
		}else{
			Thread tr = new Thread(t);
			tr.start();
		}
		
		if(tm.isWrite()){
			partialList.put(tm.getTranName(), t);
		}
		
		System.out.println("FINISH EXECUTING LOCAL QUERY REQUEST: "+tm.getTranName());
	
	}
	public void EXECUTE_QUERY_REQUEST(Mail rMail){
		System.out.println("EXECUTING QUERY REQUEST: "+rMail.getTm().getTranName());
	
		TransactionMail tm;
		tm= rMail.getTm();
		
		Transaction t = new Transaction(tm.getQuery(), tm.getReceiver(), tm.getTranName());		
		
		System.out.println("RECEIVER OF QUERY: "+ tm.getReceiver().getName());
		System.out.println("SENDER OF QUERY: "+tm.getSender().getName() + " IP:"+tm.getSender().getIpadd()+"#");
		
		t.setSender(tm.getSender());
		t.setIsolation_level(tm.getISO_LEVEL());
		t.setTableName(tm.getTableName());
		t.setTran_action(tm.getTranAction());
		t.setWrite(tm.isWrite());
		
		if(tm.isWrite()){
			t.beginTransaction();
			t.runTransaction();
		}else{
			Thread tr = new Thread(t);
			tr.start();
		}
	
		//t.registerObserver(this);
		
		if(tm.isWrite()){
			partialList.put(tm.getTranName(), t);
		}
		
		System.out.println("FINISH EXECUTING QUERY REQUEST: "+rMail.getTm().getTranName());
	}
	
	public void RECEIVE_RESULT_SET(Mail rMail){
	
		CachedRowSetImpl cas = rMail.getCs();
		String tranName = rMail.getTm().getTranName();
		if(cas == null)
			System.out.println("NULL SI CRS :(");
		else{
			updateResultSet(tranName,setTableContentsRow(tranName, cas));
			notifyObservers(rMail.getTm().getTranName());
		}	
	}
	
	public static TableContents setTableContentsRow(String tranName, CachedRowSetImpl cas){
		 
		TableContents tc = rsList.get(tranName);
		ResultSetMetaData metaData;
		try {
			metaData = cas.getMetaData();
			  Vector<String> columnNames = new Vector<String>();
	         int columnCount = metaData.getColumnCount();
	         for (int column = 1; column <= columnCount; column++) {
	            columnNames.add(metaData.getColumnName(column));
	         }
	         
	         tc.setColumnNames(columnNames);
	         // data of the table
	         while (cas.next()) {
	        	 Vector<Object> vector = new Vector<Object>();
	             //row of the table
	             for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	                 vector.add(cas.getObject(columnIndex));
	             }
	             
	             tc.getData().add(vector);
	         }
	         
	         return tc;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return null;
      
	}
	
//	public static byte[] byteConcat(byte[] A, byte[] B) {
//        int aLen = A.length;
//        int bLen = B.length;
//        byte[] C = new byte[aLen + bLen];
//        System.arraycopy(A, 0, C, 0, aLen);
//        System.arraycopy(B, 0, C, aLen, bLen);
//        return C;
//    }
//	
//   public static byte[] serialize(Object obj) throws IOException {
//	    ByteArrayOutputStream out = new ByteArrayOutputStream();
//	    ObjectOutputStream os = new ObjectOutputStream(out);
//	    os.writeObject(obj);
//	    return out.toByteArray();
//	}
//	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
//	    ByteArrayInputStream in = new ByteArrayInputStream(data);
//	    ObjectInputStream is = new ObjectInputStream(in);
//	    return is.readObject();
//	}

	public void SEND_QUERY_TO_RECEIVER(Mail mail) throws UnknownHostException, IOException{
	
		try{
			
			Site receiver = mail.getTm().getReceiver();

			System.out.println("SENDING QUERY TO: " + receiver.getName());
			
			Socket SOCK = new Socket(receiver.getIpadd(),Tags.PORT);
			OutputStream tempOut = SOCK.getOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(tempOut);
			ous.writeObject(mail);
			ous.flush();
			ous.close();
			tempOut.flush();
		 	SOCK.close();
		 	
			System.out.println("FINISH SENDING QUERY TO: "+ receiver.getName());
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FAILED TO SEND RESULT SET TO : "+ mail.getTm().getReceiver().getName());
		}
	}
	
	public static boolean isNodeConnected(Site s){

			Socket SOCKET;
			try {
				SOCKET = new Socket();
				SOCKET.connect(new InetSocketAddress(s.getIpadd(), Tags.PORT),5000);
				//SOCKET.setSoTimeout(5000);
				SOCKET.close();
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				System.out.println(s.getName()+ " IS NOT CONNECTED!");
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println(s.getName()+ " IS NOT CONNECTED!");

				return false;
			}
			
			return true;
	}

/**************************
 * CENTRAL SENDING WRITE QUERY
 **************************/
	
	public void sendWriteFromCentral(TransactionMail tm, Query q){
		
		String area ="";
		Site sender = null;
		int removeWhere = 0;
		for(int i = 0; i<q.getWHERE().size(); i++)
			if(q.getWHERE().get(i).contains(Tags.AREA)){
				area = q.getWHERE().get(i);
				removeWhere = i;
			}
		
		String parse[] = area.split("= ", 2);
		System.out.println("AREA: "+area);
		
		int areaID = Integer.parseInt(parse[1].trim());
		if(areaID == 1){
			System.out.println("PASOK PALAWAN");
			sender = owner.searchConnection(Tags.PALAWAN);
		}
		else if(areaID == 2)
			sender = owner.searchConnection(Tags.MARINDUQUE);
		else
			System.out.println("AREA NOT RECOGNIZED!");
	
		if(isNodeConnected(sender)){
			  q.getWHERE().remove(removeWhere);
			  tm.setQuery(writeQueryContructor(q));
			  tm.setSender(sender);
			  EXECUTE_LOCAL_QUERY_WRITE_REQUEST(tm);
		  }else{
			  System.out.println("UNABLE TO WRITE FROM"+sender.getName()+ "IS DOWN!");
		  }
		
	}

/*************************
 * MARIN SENDING QUERY START
 * **********************/
	public void sendWriteFromMarinduque(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		Site receiver = null;
		Site receiver2 = null;
		Mail m = new Mail(mail);
		
		//means marin requesting to update data from own
		if(tm.getReceiver().equals(tm.getSender())){
			//send the updating request to central
			receiver = owner.searchConnection(Tags.CENTRAL);
			if(isNodeConnected(receiver)){
				System.out.println("SENDING WRITE REQUEST FROM MARIN");
				tm.setReceiver(receiver);
				m.setTm(tm);
				SEND_QUERY_TO_RECEIVER(m);
			}else{
				System.out.println("UNABLE TO WRITE-> CENTRAL IS DOWN!");
			}
		}else if(tm.getReceiver().getName().equals(Tags.PALAWAN)){
			//redirecting the receiver to central and the sender to palawan
			receiver = owner.searchConnection(Tags.CENTRAL);
			receiver2 = owner.searchConnection(Tags.PALAWAN);
			
			if(isNodeConnected(receiver) && isNodeConnected(receiver2)){
				System.out.println("SENDING WRITE REQUEST TO PALAWAN AND CENTRAL FROM MARIN");
				tm.setReceiver(receiver);
				tm.setSender(receiver2);
				m.setTm(tm);
				SEND_QUERY_TO_RECEIVER(m);
				System.out.println("FINISH SENDING WRITE REQUEST TO PALAWAN AND CENTRAL FROM MARIN");

			}
			
		}
	}
	
	public void sendReadFromMarinduque(String mail, TransactionMail tm, Query q) throws UnknownHostException, IOException{
		Site receiver = null;
		Mail m = new Mail(mail);
		
		//If local yung query
		  if(tm.getReceiver().equals(tm.getSender()))
			  EXECUTE_LOCAL_QUERY_REQUEST(tm);
		  else if(tm.getReceiver().getName().equals(Tags.PALAWAN)){
			  //if Central is not down then send the query to central
			  //execute special query to get palawan data only
			  receiver = owner.searchConnection(Tags.CENTRAL);
			  
			  if(isNodeConnected(receiver)){//check if central is connected
				  q.addWHERE(Tags.AREA+"="+1);
				  tm.setQuery(readQueryContructor(q));
				  tm.setReceiver(receiver);
				  m.setTm(tm);
				  SEND_QUERY_TO_RECEIVER(m);
			  }else{
				  receiver = owner.searchConnection(Tags.PALAWAN);
				  if(isNodeConnected(receiver)){					  
					  tm.setReceiver(receiver);
					  m.setTm(tm);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND PALAWAN IS DOWN CANT GET MARINDUQUE");
				  }
			  }
		  }else if(tm.getReceiver().getName().equals(Tags.CENTRAL)){
			  System.out.println("SENDING QUERY TO CENTRAL");
			  if(isNodeConnected(tm.getReceiver())){
				  m.setTm(tm);
				  SEND_QUERY_TO_RECEIVER(m); //send to central
			  }else{
				  
				  System.out.println("CENTRAL IS NOT CONNECTED!");
				  receiver = owner.searchConnection(Tags.PALAWAN);
				  if(isNodeConnected(receiver)){
					 
					  tm.setReceiver(owner);
					  EXECUTE_LOCAL_QUERY_REQUEST(tm);
					  
					 
					  tm.setReceiver(receiver);
					  m.setTm(tm);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND PALAWAN IS DOWN :( CANNT GET MERGE");
				  }
				  
			  }
		  }
	}
/*************************
 * MARIN SENDING QUERY START
 * **********************/
/*************************
 * PALAWAN SENDING QUERY START
 * **********************/
	public void sendWriteFromPalawan(String mail, TransactionMail tm) throws UnknownHostException, IOException{
		
		Site receiver = null;
		Site receiver2 = null;
		Mail m = new Mail(mail);
		
		//means palawan requesting to update data from own
		if(tm.getReceiver().equals(tm.getSender())){
			receiver = owner.searchConnection(Tags.CENTRAL);
			if(isNodeConnected(receiver)){
				System.out.println("SENDING WRITE REQUEST FROM PALAWAN");
				//set receiver of the qrite request to central
				tm.setReceiver(receiver);
				m.setTm(tm);
				SEND_QUERY_TO_RECEIVER(m);
			}else{
				System.out.println("UNABLE TO WIRTE-> CENTRAL IS DOWN !");
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
				System.out.println("FINISH SENDING WRITE MARIN DATA REQUEST TO CENTRAL");

			}
		}
	}

	public void sendReadFromPalawan(String mail, TransactionMail tm, Query q) throws UnknownHostException, IOException{
		Site receiver = null;
		Mail m = new Mail(mail);
		
		//If local yung query
		  if(tm.getReceiver().equals(tm.getSender()))
			  EXECUTE_LOCAL_QUERY_REQUEST(tm);
		  else if(tm.getReceiver().getName().equals(Tags.MARINDUQUE)){
			  //if Central is not down then send the query to central
			  //execute special query to get marinduque data only
			  receiver = owner.searchConnection(Tags.CENTRAL);
			  if(isNodeConnected(receiver)){//check if central is connected
				  //special query
				  q.addWHERE(Tags.AREA +"="+2);
				  tm.setQuery(readQueryContructor(q));
				  tm.setReceiver(receiver);
				  m.setTm(tm);
				  SEND_QUERY_TO_RECEIVER(m);
			  }else{
				  receiver = owner.searchConnection(Tags.MARINDUQUE);
				  if(isNodeConnected(receiver)){					  
					  tm.setReceiver(receiver);
					  m.setTm(tm);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND MARINDUQUE IS DOWN CANT GET MARINDUQUE");
				  }
			  }
		  }else if(tm.getReceiver().getName().equals(Tags.CENTRAL)){
			  System.out.println("SENDING TO CENTRAL");
			  if(isNodeConnected(tm.getReceiver())){
				  m.setTm(tm);
				  SEND_QUERY_TO_RECEIVER(m); //send to central
			  }else{
				  
				  receiver = owner.searchConnection(Tags.MARINDUQUE);
				  if(isNodeConnected(receiver)){
					  tm.setReceiver(owner);
					  EXECUTE_LOCAL_QUERY_REQUEST(tm);
					  
					  tm.setReceiver(receiver);
					  m.setTm(tm);
					  SEND_QUERY_TO_RECEIVER(m);
				  }else{
					  System.out.println("CENTRAL AND MARINDUQUE IS DOWN :( CANNT GET MERGE");
				  }
			  }
		  }
	}
/*************************
 * PALAWAN SENDING QUERY END
 * **********************/
	
	public void SEND_QUERY_REQUEST() throws UnknownHostException, IOException
	{
		System.out.println("==STARTING SENDING QUERY REQUEST==");
		Site receiver = null;
		String mail = "";
		rsList = new HashMap<String, TableContents>();
		
		for(TransactionMail tm : tranList){
			
			Query q = queryList.get(tm.getTranName());
			
			if(!tm.isWrite()){
				System.out.println("NAME OF TRANSACTION TO BE READ: "+tm.getTranName());
				rsList.put(tm.getTranName(), new TableContents(tm.getTranName()));	
				mail = Tags.RETURN_READ + Tags.PROTOCOL;
			}else{
				mail = Tags.EXECUTE_WRITE + Tags.PROTOCOL;
			}
			System.out.println("RS LIST SIZE: "+ rsList.size());
			if(q == null)
				System.out.println("WAANG LAMN");
			switch(owner.getName()){
				case Tags.CENTRAL: 
						  if(!tm.isWrite())
								  EXECUTE_LOCAL_QUERY_REQUEST(tm);
						  else{
							  sendWriteFromCentral(tm, q);
						  }
						  break;
				case Tags.PALAWAN:
						 if(!tm.isWrite()){
							 System.out.println("QUERY IS READ FROM PALAWAN");
							 sendReadFromPalawan(mail, tm, q);
						 }
						 else{
							 System.out.println("QUERY IS WRITE FROM PALAWAN");
							 sendWriteFromPalawan(mail, tm);
						 }
						break;
				case Tags.MARINDUQUE:
						 if(!tm.isWrite()){
							 System.out.println("QUERY IS READ FROM MARINDUQUE");
							 sendReadFromMarinduque(mail, tm, q);
						 }
						 else{
							 System.out.println("QUERY IS WRITE FROM MARINDUQUE");
							 sendWriteFromMarinduque(mail, tm);
						 }
						break;
							  
				default: System.out.println("SITE NOT RECOGNIZED!");	  
			}
		}
		
		queryList.clear();
		tranList.clear();
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
	public void notifyObservers(String tranName) {
		// TODO Auto-generated method stub
		
		if(rsList != null){
			//System.out.println("SIZE OF RES LIST: "+ rsList.size());
			for(Observer o: obList){
			    System.out.println("UPDATING TABLE");
	            o.update(tranName);
	        }
		}
	}

	@Override
	public void update(String tranName) {
		// TODO Auto-generated method stub
		notifyObservers(tranName);
	}

	@Override
	public void notifyQueryObservers(String tranName, TableContents tc) {
		// TODO Auto-generated method stub		
		updateResultSet(tranName, tc);
	}

	@Override
	public void updateResultSet(String name, TableContents tc) {
		rsList.replace(name, tc);
	}
	
	public static Site searchForSite(String username){
		return owner.searchConnection(username);
		
	}
	
	public String readQueryContructor(Query q){
		String query = "SELECT * FROM " + Tags.TABLE;
				
		if(!q.getWHERE().isEmpty()){
			query += " WHERE ";
			for(int index = 0; index<q.getWHERE().size(); index++)
				if(index==0)
					query += q.getWHERE().get(index);
				else
					query += " AND " + q.getWHERE().get(index);
		}
		
		
		return query;
	}
	
	public String writeQueryContructor(Query q){
		String query = "UPDATE "+Tags.TABLE+ " SET ";
				
		for(int index = 0; index<q.getSET().size(); index++)
			if(index==0)
				query += q.getSET().get(index);
			else
				query += "," + q.getSET().get(index);
		
		
		if(!q.getWHERE().isEmpty()){
			query += " WHERE ";
			for(int index = 0; index<q.getWHERE().size(); index++)
				if(index==0)
					query += q.getWHERE().get(index);
				else
					query += " AND " + q.getWHERE().get(index);
		}
		
		return query;
	}
	
	
}
