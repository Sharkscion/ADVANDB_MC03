/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.TransactionInterface;

/**
 *
 * @author gelomatias
 */
public class writeAction implements TransAction
{
    private Connection conn;
    private TransactionInterface transact;
    private String query;
    private int id, number, value;
    
    public writeAction(TransactionInterface transact, int id, int number, int value)
    {
        conn = transact.getConnection();
        //this.query = query;
        this.id = id;
        this.number = number;
        this.value = value;
    }
    
    public void execute()
    {
        System.out.println("Running the Query for the Transaction: ");
        try {
                String lock = "LOCK table tbl WRITE";
                if(conn == null)
                    System.out.println("how come conn is null !");
                PreparedStatement ls = conn.prepareStatement(lock);
                ls.execute();
                String query = null;

                switch(number)
                {
                        case 1: query = "UPDATE tbl SET num1 = ? WHERE id = ?";
                                        break;
                        case 2: query = "UPDATE tbl SET num2 = ? WHERE id = ?";
                                        break;
                        default: query = "UPDATE tbl SET num1 = ? WHERE id = ?";
                                        break;
                }

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, value);
                ps.setInt(2, id);
                ps.executeUpdate();

                String unlock = "UNLOCK tables;";
                PreparedStatement us = conn.prepareStatement(unlock);
                us.execute();

                ls.close();
                ps.close();
                us.close();
                System.out.println("Done Writing!");

        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    
    public String toString()
    {
        return "write(tbl)";
    }
    
}
