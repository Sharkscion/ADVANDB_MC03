package model;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import model.action.readAction;
import model.action.writeAction;

public class Main {

	public static void main(String[] args)
	{
		final CyclicBarrier cb = new CyclicBarrier(3);
		
		
		//WriteTransaction w = new WriteTransaction(cb, TransactionInterface.READ_UNCOMMITTED, 2,2,2,TransactionInterface.COMMIT);
                Transaction t = new Transaction(cb,TransactionInterface.READ_UNCOMMITTED, TransactionInterface.COMMIT);
                t.addAction(new writeAction(t, 2, 2, 2));
                t.addAction(new readAction(t, 2, 2));
		//w.run(2, 1, 1, Transaction.COMMIT);
		Thread thread1 = new Thread(t);
		//WriteTransaction w2 = new WriteTransaction(cb, TransactionInterface.READ_UNCOMMITTED, 2,1,1,TransactionInterface.COMMIT);
		//Thread thread2 = new Thread(w2);
		
		thread1.start();
		
		try {
			cb.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BrokenBarrierException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/*
	 	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Lock lock = new Lock();
		Transaction1 transaction1_1 = new Transaction1();
		Transaction1 transaction1_2 = new Transaction1();
		Transaction1 transaction1_3 = new Transaction1();
		Transaction2 transaction2_1 = new Transaction2();
		Transaction2 transaction2_2 = new Transaction2();
		
	
		
		Thread thread1 = new Thread () {
		  public void run () {
			  
			  lock.rl(transaction1_1);
			  transaction1_1.setIsolationLevel(Transaction.ISO_READ_UNCOMMITTED);
			  transaction1_1.beginTransaction();
			  transaction1_1.transactionBody(1);
			  transaction1_1.endTransaction(0);
			  lock.unlock(transaction1_1);
		    
		  }
		};
		Thread thread2 = new Thread () {
		  public void run () {
			  lock.rl(transaction1_2);
			  transaction1_2.setIsolationLevel(Transaction.ISO_READ_UNCOMMITTED);
			  transaction1_2.beginTransaction();
			  transaction1_2.transactionBody(2);
			  transaction1_2.endTransaction(0);
			  lock.unlock(transaction1_2);
		  }
		};
		Thread thread3 = new Thread () {
		  public void run () {
			  lock.rl(transaction1_3);
			  transaction1_3.beginTransaction();
			  transaction1_3.transactionBody(3);
			  transaction1_3.endTransaction(0);
			  lock.unlock(transaction1_3);
		  }
		};
		Thread thread4 = new Thread () {
		  public void run () {
			  lock.wl(transaction2_1);
			  transaction2_1.setIsolationLevel(Transaction.ISO_READ_UNCOMMITTED);
			  transaction2_1.beginTransaction();
			  transaction2_1.transactionBody(4);
			  try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  transaction2_1.endTransaction(Transaction.COMMIT);
			  lock.unlock(transaction2_1);
		  }
		};
		Thread thread5 = new Thread () {
		  public void run () {
			  lock.wl(transaction2_2);
			  transaction2_2.beginTransaction();
			  transaction2_2.transactionBody(5);
			  transaction2_2.endTransaction(0);
			  lock.unlock(transaction2_2);
		  }
		};

		
		thread4.start();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread1.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread2.start();

	}
	*/
}
