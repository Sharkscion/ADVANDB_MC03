package model;

public class Driver{
	
	public static void main(String args[]){
		//Lock lock = new Lock();
		
		ReadTransaction readT = new ReadTransaction("T1", "Central", "numbers");
		//readT.setLock(lock);
		WriteTransaction writeT = new WriteTransaction("T2", "Central", "numbers");
		//writeT.setLock(lock);
		writeT.setValue(70);
//		WriteTransaction writeT2 = new WriteTransaction("T3", "Central", "numbers");
//		writeT.setLock(lock);
//		writeT.setValue(897);
		ReadTransaction readT2 = new ReadTransaction("T3", "Central", "numbers");
		//readT2.setLock(lock);
		
		Thread readThread = new Thread (readT);
			
		Thread writeThread = new Thread (writeT);
		
		Thread readThread2 = new Thread (readT2);
		
		
		try {
			
			writeThread.start();
			Thread.sleep(200);
			readThread.start();
			Thread.sleep(1000);
			readThread2.start();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
 	
}
