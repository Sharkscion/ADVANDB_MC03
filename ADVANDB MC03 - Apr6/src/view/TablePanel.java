/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.sun.rowset.CachedRowSetImpl;

import model.DBConnection;
import model.TableContents;
import controller.Controller;

/**
 *
 * @author Nikko
 */
public class TablePanel extends JPanel{
    
    private String title;
    private JTable tableData;
    private DefaultTableModel dtModel = null;
    private Vector<String> columnNames = new Vector<String>();
    private Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    private JScrollPane scrollTable;
    private DBConnection dbc;
    private ArrayList<CachedRowSetImpl> rsList;
    private ResultSetMetaData metaData = null;
    private String[] cols = {"Hello","Hi","O Elo", "Bonjour"}; 
    private Object[][] rows = {};
    private Controller c;
    private JButton btnClose;
    private int correspondingTabIndex;
    private ClientGUI cg;
    
    public TablePanel(String title){
        this.title = title;
        createTable();
    }
    
    public TablePanel(Controller c){
        this.c = c;
        createTable();
    }
    
    public TablePanel(){
        createTable();
    }
    
    public void createTable(){
        this.setLayout(null);
        this.setBackground(Color.decode("#8c9bab"));
        if(data!= null){
             // dtModel = new DefaultTableModel(data,columnNames);
            tableData = new JTable(dtModel); //tableData = new JTable(dtModel);
            System.out.println("SET NEW TABLE");
        }else{
            dtModel = new DefaultTableModel(rows,cols);
            tableData = new JTable(dtModel);
        }
        tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //cells will not resize
        tableData.setLayout(new BorderLayout());
        scrollTable = new JScrollPane(tableData,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollTable.setViewportView(tableData); //we use setViewportView to get the column names cause if we use ScrollPane, the headers will disappear.
        scrollTable.setBounds(2,2,550,550);
        this.add(scrollTable);
        
        btnClose = new JButton("Close Tab");
        btnClose.setBounds(2, 550, 120, 30);
        btnClose.addActionListener(new doActionListener());
        this.add(btnClose);
    }
    
    /*from http://stackoverflow.com/questions/10620448/most-simple-code-to-populate-jtable-from-resultset 
        This function puts all data of the query to the JTable
    */
    public DefaultTableModel buildTableModel(TableContents tc){
 
    	//System.out.println("SET: "+ tc.getTranName());
        columnNames = tc.getColumnNames();
        data = tc.getData();
        return new DefaultTableModel(data, columnNames);
    }

    public void setResultSet(TableContents tc) {
  //      this.rsList = rs;
//        dtModel = null;
//        createTable();
//        dtModel.fireTableStructureChanged();
        
        this.removeAll();   //Table will hang after populating table, so u need to refresh by removing all then repainting and revalidating
        dtModel = buildTableModel(tc);
        createTable();
        dtModel.fireTableStructureChanged();
        repaint();
        revalidate();
    }
    
    public class doActionListener implements ActionListener{
        
        public void actionPerformed(ActionEvent action){
            if(action.getSource() == btnClose){
                cg.closeTab(correspondingTabIndex);
            }
        }
    }
    
    public void setCorrespondingTab(ClientGUI cg, int index){
        this.cg = cg;
        this.correspondingTabIndex = index;
    }
    
    public void changeTabIndex(int index){
        this.correspondingTabIndex = index;
    }
    
    public int getTabIndex(){
        return correspondingTabIndex;
    }
}
