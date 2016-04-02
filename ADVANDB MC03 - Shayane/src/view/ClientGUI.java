package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;

import controller.Controller;

import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import socket.ClientResponse;

public class ClientGUI extends JFrame implements ActionListener{
	
	private Socket socket;
	private ResultSet rs;
	private Controller c;
	
	private JPanel topPanel, bottomPanel;
	private JPanel queryPanel, abortCommitPanel, mainPanel, isolationPanel;
	private JPanel writePanel, readPanel;
	private JScrollPane readScroll, writeScroll;
	private JTabbedPane queryTabbedPane;
	private JTextArea readTextArea, writeTextArea;
	private JRadioButton rbAbort, rbCommit;
	private JButton btnReadSubmit;
	private JButton btnWriteButton;
	private JButton btnDisconnect;
	private ClientResponse clientResponse;
	private JPanel settingsPanel;
	private JComboBox cbIsolationLevel;
	
	public ClientGUI(Controller c, ResultSet rs, Socket socket, ClientResponse clientResponse) {
		this.c = c;
		this.rs = rs;
		this.socket = socket;
		this.clientResponse = clientResponse;
		
		  try {
              UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		  }catch (Exception e) {}
		  
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);
        
        createTopPanel();
        createBottomPanel();
   
        mainPanel.add(topPanel, BorderLayout.NORTH);  
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setBackground(Color.gray);
        setSize(800, 600);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        
    }
	
	public JPanel createQueryPanel(){
	    queryPanel = new JPanel();
	    queryPanel.setBackground(Color.WHITE);
        queryPanel.setLayout(new BorderLayout(0, 0));
        
        queryTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        queryTabbedPane.setBackground(Color.WHITE);
        queryPanel.add(queryTabbedPane, BorderLayout.CENTER);
        
        readPanel = new JPanel();
        queryTabbedPane.addTab("Read Query", null, readPanel, null);
        readPanel.setLayout(new GridLayout(0, 1, 0, 0));
        readTextArea = new JTextArea(readPanel.getWidth(), readPanel.getHeight());
        readScroll = new JScrollPane(readTextArea);
        readScroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        readPanel.add(readScroll);
        
        btnReadSubmit = new JButton("SUBMIT");
        btnReadSubmit.addActionListener(this);
        readPanel.add(btnReadSubmit);
             
        writePanel = new JPanel();
        writePanel.setBackground(Color.WHITE);
        queryTabbedPane.addTab("Write Query", null, writePanel, null);
        writePanel.setLayout(new GridLayout(0, 1, 0, 0));
        writeTextArea = new JTextArea(writePanel.getWidth(), writePanel.getHeight());
        writeScroll = new JScrollPane(writeTextArea);
        writeScroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        writePanel.add(writeScroll);
        
        btnWriteButton = new JButton("SUBMIT");
        btnWriteButton.addActionListener(this);
        writePanel.add(btnWriteButton);
        
        return queryPanel;
	}
	
	public void createTopPanel() {
		topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(800, 220));
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		Border border = BorderFactory.createTitledBorder("Controls");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		topPanel.setBorder(new CompoundBorder(border, margin));
		
		
		topPanel.add(createButtonsPanel(), BorderLayout.EAST);
		topPanel.add(createQueryPanel(), BorderLayout.CENTER);
		topPanel.add(createSettingsPanel(), BorderLayout.WEST);

	}
	
	public JPanel createSettingsPanel(){
		 settingsPanel = new JPanel();
	     settingsPanel.setLayout(new BorderLayout(0, 0));
		
	     settingsPanel.add(createIsolationPanel(), BorderLayout.NORTH);
	     settingsPanel.add(createAbortCommitPanel(), BorderLayout.SOUTH);
		 return settingsPanel;
	}
	
	public JPanel createIsolationPanel() {
		Border border = BorderFactory.createTitledBorder("Isolation Level");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		
		String[] isolationLevelChoices = {"read uncommmitted", "read committed", "read repeatable"};
		
		isolationPanel = new JPanel();
        isolationPanel.setLayout(new BorderLayout());
        isolationPanel.setBackground(Color.WHITE);
        isolationPanel.setBorder(new CompoundBorder(border, margin));
        cbIsolationLevel = new JComboBox(isolationLevelChoices);
        isolationPanel.add(cbIsolationLevel, BorderLayout.NORTH);
        return isolationPanel;
	}
	
	public JPanel createAbortCommitPanel() {
		
		Border border = BorderFactory.createTitledBorder("Abort or Commit");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		
		abortCommitPanel = new JPanel();
		abortCommitPanel.setLayout(new BorderLayout());
        abortCommitPanel.setBackground(Color.WHITE);
        abortCommitPanel.setBorder(new CompoundBorder(border, margin));
        
        rbAbort = new JRadioButton("Abort");
        rbAbort.setBackground(Color.WHITE);
        rbCommit = new JRadioButton("Commit");
        rbCommit.setBackground(Color.WHITE);
        
        abortCommitPanel.add(rbAbort, BorderLayout.NORTH);
        abortCommitPanel.add(rbCommit, BorderLayout.SOUTH);
       
        return abortCommitPanel;
	}
	
	public JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.WHITE);
		
		btnDisconnect = new JButton("DISCONNECT");
		btnDisconnect.addActionListener(this);
		buttonsPanel.setLayout(new BorderLayout(0, 0));
		buttonsPanel.add(btnDisconnect, BorderLayout.NORTH);
		
		return buttonsPanel;
	}
	
	public void createBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(800, 370));
		bottomPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder("Dataset");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		bottomPanel.setBorder(new CompoundBorder(border, margin));
		
		bottomPanel.add(createTablePanel(), BorderLayout.CENTER);
	}
	
	public JPanel createTablePanel() {
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		
		JScrollPane pane = new JScrollPane(createJTable(rs));
		tablePanel.add(pane, BorderLayout.CENTER);
		
		return tablePanel;
	}
	
	public JTable createJTable(ResultSet rs) {
		JTable table = new JTable();
		DefaultTableModel dataModel = new DefaultTableModel();
		table.setModel(dataModel);
		
		try {
			ResultSetMetaData mdata = rs.getMetaData();
			int colCount = mdata.getColumnCount();		
			String[] colNames = getColumnNames(colCount, mdata);
			dataModel.setColumnIdentifiers(colNames);
			while (rs.next()) {
				String[] rowData = new String[colCount];
				for (int i = 1; i <= colCount; i++) {
					rowData[i - 1] = rs.getString(i);
				}
				dataModel.addRow(rowData);
			}
		} catch (SQLException e) {}
		
		return table;
	}
	
	public String[] getColumnNames(int colCount, ResultSetMetaData mdata) throws SQLException {
		String[] colNames = new String[colCount];
		for (int i = 1; i <= colCount; i++) {
			String col = mdata.getColumnName(i);
			colNames[i-1] = col;
		}
		return colNames;
	}
	
	private void updateRowHeights(JTable table) {
		try {
			for (int row = 0; row < table.getRowCount(); row++) {
				int rowHeight = table.getRowHeight();

				for (int column = 0; column < table.getColumnCount(); column++) {
					Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
					rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
				}

				table.setRowHeight(row, rowHeight);
			}
		} catch (ClassCastException e) {
		}
	}
	
	public void updateTable(ResultSet rs) {
		bottomPanel.removeAll();
		JTable table = createJTable(rs);
		JScrollPane pane = new JScrollPane(table);
		updateRowHeights(table);
		bottomPanel.add(pane, BorderLayout.CENTER);
		bottomPanel.revalidate();
		bottomPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnReadSubmit){
			System.out.println("HELLO :/");
			PrintWriter OUT;
			try {
				OUT = new PrintWriter(socket.getOutputStream());
				OUT.println("<read>"+readTextArea.getText());
				OUT.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	}
	
}
