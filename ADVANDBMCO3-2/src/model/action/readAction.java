/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Transaction;

/**
 *
 * @author gelomatias
 */
public class readAction implements TransAction
{
    private int id, number;
    private Connection conn;
    private Transaction t;
    
    public readAction(Transaction t, int id, int number)
    {
        this.t = t;
        this.id = id;
        this. number = number;
        this.conn = t.getConnection();
    }

    @Override
    public void execute() {
        System.out.println("Running the Query for the Transaction: ");
        try 
        {
            String lock = "LOCK table tbl READ";
            PreparedStatement ls = conn.prepareStatement(lock);
            ls.execute();
            String query = null;

            switch(number)
            {
                    case 1: query = "SELECT id,num1 FROM tbl WHERE id = ?";
                                    break;
                    case 2: query = "SELECT id,num2 FROM tbl WHERE id = ?";
                                    break;
                    default: query = "SELECT * FROM tbl";
                                    break;
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeQuery();

            String unlock = "UNLOCK tables;";
            PreparedStatement us = conn.prepareStatement(unlock);
            us.execute();

            ls.close();
            ps.close();
            us.close();
            System.out.println("Done Reading!");

        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    
    public String toString()
    {
        return "read(tbl)";
    }
    
}
