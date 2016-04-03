/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import static model.TransactionInterface.READ_COMMITTED;
import static model.TransactionInterface.READ_UNCOMMITTED;
import static model.TransactionInterface.REPEATABLE_READS;
import static model.TransactionInterface.SERIALIZABLE;
import model.action.TransAction;

/**
 *
 * @author gelomatias
 */
public class Transaction implements TransactionInterface, Runnable 
{
    private int timeStamp;
    private Connection conn;
    private DBConnection dbConnect;
    private Statement stmt;
    private int isolationLvl;
    private BufferedReader br;
    private BufferedWriter bw;
    private CyclicBarrier cb;
    private int id, number, value, end;
    private ArrayList<TransAction> actions;
    private int currentAction;
    
    public Transaction(CyclicBarrier cb,int isolationLvl, int end)
    {
    	this.cb = cb;
        this.isolationLvl = isolationLvl;
        this.end = end;
        this.actions = new ArrayList<TransAction>();
        this.currentAction = -1;
        
        dbConnect = new DBConnection();
        conn = dbConnect.getConnection();
    }
    
    public Connection getConnection()
    {
        return conn;
    }

    @Override
    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public void setIsolationLevel(int iso_level) 
    {
        System.out.println("Setting isolation level for write transaction as: " + iso_level);	
        isolationLvl = iso_level;

        try 
        {
            switch(isolationLvl) 
            {
                    case READ_UNCOMMITTED:
                                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                            break;
                    case READ_COMMITTED:
                                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                            break;
                    case REPEATABLE_READS:
                                    conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                            break;
                    case SERIALIZABLE:
                                    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                            break;
                    default:
                                    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                            break;
            }
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }

    @Override
    public int getIsolationLevel() {
        return isolationLvl;
    }

    @Override
    public void beginTransaction() 
    {
        System.out.println("Writing Transaction begins");	
        try {
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement("START TRANSACTION;");
                ps.executeQuery();
        } catch (SQLException e) {

                e.printStackTrace();
        }
    }

    public void executeAction()
    {
        if(currentAction == -1)
        {
            beginTransaction();
            currentAction++;
            System.out.println("Begin Transaction !");
        }
        else if(currentAction < actions.size())
        {
            actions.get(currentAction).execute();
            currentAction++;
            if(currentAction < actions.size())
            System.out.println("Do " + actions.get(currentAction).toString() + " !");
        }
        else if(currentAction == actions.size())
        {
            endTransaction(end);
            System.out.println("Transactions Ended !");
        }
        
    }

    @Override
    public void endTransaction(int ending) {
        System.out.println("Writing has ended.");	
        try 
        {
            switch(ending) 
            {
                    case COMMIT: conn.commit();
                            break;
                    case ROLLBACK: conn.rollback();
                            break;
                    default: conn.rollback();
                            break;
            }
        } catch(SQLException e) {
                e.printStackTrace();
        }
    }
    
    public void addAction(TransAction action)
    {
        actions.add(action);
    }

	@Override
	public void run() {
		try {
			cb.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BrokenBarrierException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Running Writer...");
        for(int i = 0; i <= actions.size()+1; i++)
        {
        	if(i <actions.size())
            System.out.println(actions.get(i).toString());
            System.out.println("Current Action: " + (currentAction));
            executeAction();
                
        }
	}

    
    /*@Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            System.out.println("Transaction 1");
            System.out.println("BEGIN TRANSACTION");
            for(int i = 0; i < actions.size(); i++)
            {
                System.out.println(actions.get(i).toString());
            }
            System.out.println("Current Action: " + (currentAction+1));
            System.out.println("Press Any Key to execute an action");
            sc.nextLine();
            executeAction();
            
        }
    }*/

}
