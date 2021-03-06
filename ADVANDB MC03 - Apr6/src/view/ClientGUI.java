package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.sun.rowset.CachedRowSetImpl;

import model.Observer;
import model.Query;
import model.ResultSets;
import model.Site;
import model.TableContents;
import model.Tags;
import model.Transaction;
import model.TransactionMail;
import controller.Controller;

public class ClientGUI extends JFrame implements ActionListener, Observer{

	private Site client;
	private Controller c;
	private TableContents rsList;
	private String ISO_LEVEL;
	private int TRAN_ACTION;
	
	private JPanel topPanel, EastPanel;
	private JPanel queryPanel, abortCommitPanel, mainPanel, isolationPanel;
	private JPanel defaultPanel;
	private JPanel transactionsPanel, queryEditPanel;
	private JScrollPane readScroll, pane;
	private JTabbedPane queryTabbedPane, resultsTabbedPane;
	private JTextArea readTextArea, transactionList;
	private JRadioButton rbAbort, rbCommit;
	private JButton btnRead, btnWrite;
	private JPanel settingsPanel;
	private JPanel areaPanel;
	private JCheckBox chckbxPalawan;
	private JCheckBox chckbxMarinduque;
	private JComboBox cbIsolationLevel;

	private boolean isPalawan;
	private boolean isMarinduque;

	private HashMap<String, JTextField> queryComponents;
	private JButton btnSubmit;
	private HashMap<String, JPanel> tableReadList;

	private ArrayList<String> readTranList;

	private HashMap<Integer,TablePanel> tablePanelList = new HashMap<Integer, TablePanel>();
	private ArrayList<String> transactionQueries = new ArrayList<String>();
	private HashMap<String, String> readWriteComponents = new HashMap<String, String>();
	private int transactionCounter = 1;
	private JPanel namePanel;
	private JLabel nameLabel;
	private JTextField textField;

	public ClientGUI(Controller con) {

		this.c = con;
		this.c.registerObserver(this);
		this.ISO_LEVEL = "";
		this.TRAN_ACTION = Transaction.COMMIT;
		this.tableReadList = new HashMap<String, JPanel>();
		this.readTranList = new ArrayList<String>();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch (Exception e) {}

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel);

		createTopPanel();
		createBottomPanel();
		mainPanel.add(topPanel, BorderLayout.WEST);  
		mainPanel.add(EastPanel, BorderLayout.EAST);

		setBackground(Color.gray);
		setSize(1000, 700);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}

	public HashMap<String, JPanel> getTableReadList() {
		return tableReadList;
	}

	public void setTableReadList(HashMap<String, JPanel> tableReadList) {
		this.tableReadList = tableReadList;
	}

	public JPanel createQueryPanel(){
		queryPanel = new JPanel();
		queryPanel.setBackground(Color.WHITE);
		queryPanel.setLayout(new BorderLayout(0, 0));

		queryTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		queryTabbedPane.setBackground(Color.WHITE);
		queryPanel.add(queryTabbedPane, BorderLayout.CENTER);

		defaultPanel = new JPanel();
		defaultPanel.setBackground(Color.WHITE);
		queryTabbedPane.addTab("Default", null, defaultPanel, null);
		defaultPanel.setLayout(new BorderLayout());

		queryEditPanel = new JPanel();
		queryEditPanel.setLayout(new BoxLayout(queryEditPanel, BoxLayout.PAGE_AXIS));
		pane = new JScrollPane(queryEditPanel);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	
		ArrayList<String> attributeList = new ArrayList<String>();
		attributeList.add("Household ID ");
		attributeList.add("Mem ID ");
		attributeList.add("Aquani Volume ");
		attributeList.add("Aquani Type ");
		attributeList.add("Aquani Type Others ");
		createQueryEditPanel(attributeList);
		
		queryEditPanel.repaint();
		queryEditPanel.revalidate();
		pane.repaint();
		pane.revalidate();
		defaultPanel.repaint();
		defaultPanel.revalidate();

		/*************
		 * button panel for submitting read or write queries
		 *************/
		JPanel btnsPanel = new JPanel();
		btnsPanel.setBackground(Color.WHITE);
		btnsPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

		btnRead = new JButton("Read");
		btnRead.addActionListener(this);

		btnWrite = new JButton("Write");
		btnWrite.addActionListener(this);

		btnsPanel.add(btnRead);
		btnsPanel.add(btnWrite);

		defaultPanel.add(pane, BorderLayout.CENTER);
		defaultPanel.add(btnsPanel, BorderLayout.SOUTH);

		return queryPanel;
	}

	//DOES NOT TAKE INTO ACCOUNT STRING TYPE (lacking: " ")
	public Query getWriteQuery() {
		String query = "";
		String table = "";
		String set = "set ";
		String where = "where ";
		
		table = Tags.TABLE;
		
		Query q = new Query();

		ArrayList<String>attributeTitles = new ArrayList<String>();

		attributeTitles.add(Tags.HPQ_HH_ID);
		attributeTitles.add(Tags.MEM_ID);
		attributeTitles.add(Tags.AQUANI_VOL);
		attributeTitles.add(Tags.AQUANI_TYPE);
		attributeTitles.add(Tags.AQUANI_TYPE_O);
		

		Component[] children = queryEditPanel.getComponents();
		for(int i = 0; i < children.length; i++) {
			JPanel panel = (JPanel) children[i];
			Component[] panelChildren = panel.getComponents();
			for(int j = 0; j < panelChildren.length; j++) {
				if(panelChildren[j] instanceof JTextField) {
					String text = ((JTextField)panelChildren[j]).getText();
					if(!text.isEmpty()) {
						readWriteComponents.put(attributeTitles.get(i), text);
					}
				}
			}
		}
		
		HashMap<String, String> readWriteComponents2 = (HashMap<String, String>) readWriteComponents.clone();
		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
			if(entry.getKey().equals(Tags.HPQ_HH_ID)){
				q.addWHERE(Tags.HPQ_HH_ID +"="+entry.getValue() );
				readWriteComponents2.remove(entry.getKey());
			}
			if(entry.getKey().equals("id")){
				q.addWHERE(Tags.MEM_ID +"="+entry.getValue() );
				readWriteComponents2.remove(entry.getKey());
			} 
		}

		if(c.getOwner().getName().equals(Tags.CENTRAL) && checkIfLocalOrGlobal().equals(Tags.PALAWAN)){
			q.addWHERE(Tags.AREA + "= 1 ");
		}
		else if(c.getOwner().getName().equals(Tags.CENTRAL) && checkIfLocalOrGlobal().equals(Tags.MARINDUQUE)){
			q.addWHERE(Tags.AREA + " = 2 ");
		}
		int i = 1;
		
		readWriteComponents.clear();
		readWriteComponents = readWriteComponents2;
		
		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
				q.addSET(entry.getKey() + " = " + entry.getValue() );
				//set = set + entry.getKey() + " = " + entry.getValue() + ", ";
		}

		transactionQueries.add(c.writeQueryContructor(q));
		readWriteComponents.clear();

		return q;
	}

	public Query getReadQuery() {
		String query = "select hpq_hh_id, id, aquani_vol, aquanitype_o from hpq_aquani";
		ArrayList<String>attributeTitles = new ArrayList<String>();

		attributeTitles.add(Tags.HPQ_HH_ID);
		attributeTitles.add(Tags.MEM_ID);
		attributeTitles.add(Tags.AQUANI_VOL);
		attributeTitles.add(Tags.AQUANI_TYPE);
		attributeTitles.add(Tags.AQUANI_TYPE_O);
		
		//if hndi siya local or central siya -> add the column area 
		if(!c.getOwner().getName().equals(checkIfLocalOrGlobal()) || 
			c.getOwner().getName().equals(Tags.CENTRAL))
			attributeTitles.add(Tags.AREA);
		
		Query q = new Query();
		
		Component[] children = queryEditPanel.getComponents();
		for(int i = 0; i < children.length; i++) {
			JPanel panel = (JPanel) children[i];
			Component[] panelChildren = panel.getComponents();
			for(int j = 0; j < panelChildren.length; j++) {
				if(panelChildren[j] instanceof JTextField) {
					String text = ((JTextField)panelChildren[j]).getText();
					if(!text.isEmpty()) {
						readWriteComponents.put(attributeTitles.get(i), text);
						
					}
				}
			}
		}

		int i = 1;
		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
			if(i < readWriteComponents.size()) {
				q.addWHERE(entry.getKey()+"="+entry.getValue() + " ");
				
			} else {
				q.addWHERE(entry.getKey()+"="+entry.getValue() + " ");
			}
			i++;
		}
	
		if(c.getOwner().getName().equals(Tags.CENTRAL) && checkIfLocalOrGlobal().equals(Tags.PALAWAN)){
			q.addWHERE(Tags.AREA + "= 1 ");
		}
		else if(c.getOwner().getName().equals(Tags.CENTRAL) && checkIfLocalOrGlobal().equals(Tags.MARINDUQUE)){
			q.addWHERE(Tags.AREA + " = 2 ");
		}

		query = c.readQueryContructor(q);
		transactionQueries.add(query);
		readWriteComponents.clear();

		return q;
	}

	public void createQueryEditPanel(ArrayList<String> attributeList) {
		//queryEditPanel.removeAll();
		for(int i = 0; i < attributeList.size(); i++) {
			JPanel tempPanel = new JPanel();
			tempPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

			JLabel tempLabel = new JLabel(attributeList.get(i) + ": ");
			JTextField tempField = new JTextField();
			tempField.setPreferredSize(new Dimension(100, 25));

			tempPanel.add(tempLabel);
			tempPanel.add(tempField);

			queryEditPanel.add(tempPanel);
		}
	}

	public void createTopPanel() {
		topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(400, 260));
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Color.LIGHT_GRAY);
		Border border = BorderFactory.createTitledBorder("Controls");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		topPanel.setBorder(new CompoundBorder(border, margin));
		topPanel.add(createTransactionsPanel(), BorderLayout.SOUTH);

		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		transactionsPanel.add(btnSubmit, BorderLayout.SOUTH);
		topPanel.add(createQueryPanel(), BorderLayout.CENTER);
		
		namePanel = new JPanel();
		namePanel.setBackground(new Color(102, 205, 170));
		namePanel.setPreferredSize(new Dimension(10, 30));
		queryPanel.add(namePanel, BorderLayout.NORTH);
		namePanel.setLayout(new BorderLayout(0, 0));
		
		nameLabel = new JLabel("Transaction Name :");
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		textField = new JTextField();
		namePanel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		topPanel.add(createSettingsPanel(), BorderLayout.NORTH);
	}

	public JPanel createTransactionsPanel() {
		transactionsPanel = new JPanel();
		transactionsPanel.setBackground(Color.WHITE);
		transactionsPanel.setLayout(new BorderLayout());
		transactionsPanel.setPreferredSize(new Dimension(250, 200));
		transactionsPanel.setBorder(new TitledBorder(null, "Transcation List", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		transactionList = new JTextArea();
		transactionList.setEditable(false);
		JScrollPane transcationPane = new JScrollPane(transactionList);
		transcationPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		transactionsPanel.add(transcationPane, BorderLayout.CENTER);

		return transactionsPanel;
	}

	public JPanel createSettingsPanel(){
		settingsPanel = new JPanel();
		settingsPanel.setBackground(Color.WHITE);
		settingsPanel.setLayout(new BorderLayout(0, 0));

		settingsPanel.add(createIsolationPanel(), BorderLayout.NORTH);
		settingsPanel.add(createAreaPanel(), BorderLayout.CENTER);
		settingsPanel.add(createAbortCommitPanel(), BorderLayout.SOUTH);
		
		return settingsPanel;
	}

	public JPanel createAreaPanel(){			
			areaPanel = new JPanel();
			areaPanel.setBackground(Color.WHITE);
			areaPanel.setBorder(new TitledBorder(null, "Area", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			areaPanel.setLayout(new BorderLayout(0, 0));
			
			chckbxPalawan = new JCheckBox("Palawan");
			chckbxPalawan.setBackground(Color.WHITE);
			chckbxPalawan.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
				   if(e.getStateChange() == ItemEvent.SELECTED) {
			            isPalawan = true;
			        } else {
			        	isPalawan = false;
			        };
				}
			});
			areaPanel.add(chckbxPalawan);
			
			chckbxMarinduque = new JCheckBox("Marinduque");
			chckbxMarinduque.setBackground(Color.WHITE);
			chckbxMarinduque.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					if(e.getStateChange() == ItemEvent.SELECTED) {
			            isMarinduque = true;
			        } else {
			        	isMarinduque = false;
			        };
				}
			});
			areaPanel.add(chckbxMarinduque, BorderLayout.NORTH);
			return areaPanel;
	}
		
	public JPanel createIsolationPanel() {
		Border border = BorderFactory.createTitledBorder("Isolation Level");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);

		String[] isolationLevelChoices = {Transaction.ISO_READ_UNCOMMITTED, Transaction.ISO_READ_COMMITTED, 
										  Transaction.ISO_REPEATABLE_READ, Transaction.ISO_SERIALIZABLE};

		isolationPanel = new JPanel();
		isolationPanel.setLayout(new BorderLayout());
		isolationPanel.setBackground(Color.WHITE);
		isolationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Isolation Level", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		cbIsolationLevel = new JComboBox(isolationLevelChoices);
		cbIsolationLevel.addActionListener(this);
		isolationPanel.add(cbIsolationLevel, BorderLayout.NORTH);
		return isolationPanel;
	}

	public JPanel createAbortCommitPanel() {

		Border border = BorderFactory.createTitledBorder("Abort or Commit");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);

		abortCommitPanel = new JPanel();
		abortCommitPanel.setLayout(new BorderLayout());
		abortCommitPanel.setBackground(Color.WHITE);
		abortCommitPanel.setBorder(new TitledBorder(null, "Abort or Commit", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		ButtonGroup buttonGroup = new ButtonGroup();
		rbAbort = new JRadioButton("Abort");
		rbAbort.setBackground(Color.WHITE);
		rbAbort.addActionListener(this);
		
		rbCommit = new JRadioButton("Commit");
		rbCommit.setBackground(Color.WHITE);
		rbCommit.addActionListener(this);
		
		buttonGroup.add(rbAbort);
		buttonGroup.add(rbCommit);

		abortCommitPanel.add(rbAbort, BorderLayout.NORTH);
		abortCommitPanel.add(rbCommit, BorderLayout.SOUTH);

		return abortCommitPanel;
	}


	public void createBottomPanel() {
		EastPanel = new JPanel();
		EastPanel.setPreferredSize(new Dimension(600, 350));
		EastPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder("Dataset");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		EastPanel.setBorder(new CompoundBorder(border, margin));

		resultsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabbedPane.setBackground(Color.RED);
		EastPanel.add(resultsTabbedPane, BorderLayout.CENTER);
		//bottomPanel.add(createTablePanel(), BorderLayout.CENTER);
	}

//	public JPanel createTablePanel() {
//		JPanel tablePanel = new JPanel();
//		tablePanel.setLayout(new BorderLayout());
//
//		JScrollPane pane = new JScrollPane(createJTable(rsList));
//		tablePanel.add(pane, BorderLayout.CENTER);
//
//		return tablePanel;
//	}

//	public JTable createJTable(TableContents rsList){
//		JTable table = new JTable();
//		//DefaultTableModel dataModel = new DefaultTableModel();
//		 Vector<Vector<Object>> data = null;
//		 Vector<String> columnNames = null;
//		 
//		for(CachedRowSetImpl rs : rsList){
//				ResultSetMetaData metaData;
//			
//				try {
//					metaData = rs.getMetaData();
//					 // names of columns
//				    columnNames = new Vector<String>();
//				    int columnCount = metaData.getColumnCount();
//				    for (int column = 1; column <= columnCount; column++) {
//				        columnNames.add(metaData.getColumnName(column));
//				    }
//		
//				    // data of the table
//				    data = new Vector<Vector<Object>>();
//				    while (rs.next()) {
//				        Vector<Object> vector = new Vector<Object>();
//				        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
//				            vector.add(rs.getObject(columnIndex));
//				        }
//				        data.add(vector);
//				    } 
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			   
//			}
//	    
//	    DefaultTableModel dataModel = new DefaultTableModel(data, columnNames);
////
////		try {
////			for(CachedRowSetImpl rs : rsList){
////				System.out.println("CACHED SIED: "+ rs.getRow());
////				ResultSetMetaData mdata = rs.getMetaData();
////				int colCount = mdata.getColumnCount();		
////				String[] colNames = getColumnNames(colCount, mdata);
////				dataModel.setColumnIdentifiers(colNames);
////				while (rs.next()) {
////					String[] rowData = new String[colCount];
////					for (int i = 1; i <= colCount; i++) {
////						rowData[i - 1] = rs.getString(i);
////					}
////					dataModel.addRow(rowData);
////				}
////			}
////		} catch (SQLException e) {}
//
//		table.setModel(dataModel);
//		return table;
//	}

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

	public void updateTable(String tranName) {
		//EastPanel.removeAll();
		
		TablePanel tpanel = (TablePanel) tableReadList.get(tranName);
		int tabCount = resultsTabbedPane.getTabCount();
		System.out.println("TAB COUNT: "+tabCount);
		for(int i=0; i<tabCount; i++){
			if(tablePanelList.get(i).equals(tpanel))
			{
				System.out.println("PASOK");
				 tpanel.setBackground(Color.WHITE);
				 //resultsTabbedPane.remove(i);
				 tpanel.setResultSet(rsList);
				 tpanel.setCorrespondingTab(this,resultsTabbedPane.getSelectedIndex()); //select last one
				 tablePanelList.replace(i, tpanel);
				 resultsTabbedPane.addTab(tranName, null, tpanel, null);
				 resultsTabbedPane.setSelectedIndex(resultsTabbedPane.getTabCount()-1);
				 
			}
		}
		
		
//		System.out.println("TAB COUNT: "+ tabCount);
//		for(int i=0; i<tabCount;i++){
//			if(resultsTabbedPane.getComponentAt(i).equals(tpanel)){
//				resultsTabbedPane.remove(i);
//				tpanel.setResultSet(rsList);
//				resultsTabbedPane.addTab(tranName, null, tpanel, null);
//				resultsTabbedPane.setSelectedIndex(resultsTabbedPane.getTabCount()-1);
//		        tpanel.setCorrespondingTab(this,resultsTabbedPane.getSelectedIndex()); //select last one
//			}
//		}
		
//		JTable table = createJTable(rsList);
//		JScrollPane pane = new JScrollPane(table);
//		updateRowHeights(table);
//		EastPanel.add(pane, BorderLayout.CENTER);
		EastPanel.revalidate();
		EastPanel.repaint();
	}

	public String checkIfLocalOrGlobal(){

		String result = Tags.NONE;

		if(isPalawan)
			result = Tags.PALAWAN;
		if (isMarinduque)
			result = Tags.MARINDUQUE;

		if (isPalawan && isMarinduque)
			result = Tags.CENTRAL;

		return result;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() ==  cbIsolationLevel){
			
			switch(cbIsolationLevel.getSelectedItem().toString()){
				case Transaction.ISO_READ_UNCOMMITTED:
					 ISO_LEVEL = Transaction.ISO_READ_UNCOMMITTED; break;
				case Transaction.ISO_READ_COMMITTED:
					 ISO_LEVEL = Transaction.ISO_READ_COMMITTED; break;
				case Transaction.ISO_REPEATABLE_READ:
					 ISO_LEVEL = Transaction.ISO_REPEATABLE_READ; break;
				case Transaction.ISO_SERIALIZABLE:
					 ISO_LEVEL = Transaction.ISO_SERIALIZABLE; break;
				default: ISO_LEVEL =  Transaction.ISO_SERIALIZABLE;
					
			}

		}else if(e.getSource() == btnSubmit && !textField.getText().isEmpty()){
			
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			try {
				//yung list of tables na i agdidisplay ng result set 
				tablePanelList.clear();
				//yung name ng transaction and yung table panel
	    		tableReadList.clear();
	    		resultsTabbedPane.removeAll();
	    		
	    		 for(int i = 0; i < readTranList.size(); i++){
		                TablePanel tablePanel= new TablePanel("");
		                tablePanel.setBackground(Color.WHITE);
		                resultsTabbedPane.addTab(readTranList.get(i).toString(), null, tablePanel, null);
		                resultsTabbedPane.setSelectedIndex(resultsTabbedPane.getTabCount()-1);
		                tablePanel.setCorrespondingTab(this,resultsTabbedPane.getSelectedIndex()); //select last one
		                tablePanelList.put(resultsTabbedPane.getSelectedIndex(),tablePanel);
		               // System.out.println("SELECTED INDEX: "+resultsTabbedPane.getSelectedIndex());
		                tableReadList.put(readTranList.get(i), tablePanel);
		            }
			           
	    		
				c.SEND_QUERY_REQUEST();
		        //tablePanelList.clear();
	            
	            //CLEAR TRANSACTION LIST ??NOT SURE WHEN TO RESET
	            transactionQueries.clear();
	    		transactionCounter = 1;
				transactionList.setText("");
	    		transactionList.repaint();
	    		transactionList.revalidate();
	    		readTranList.clear();
	    		textField.setText("");
			
	    		System.out.println("TAB COUNT AFTER SENDING: "+ resultsTabbedPane.getTabCount());
				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			} catch (Exception e1){
				e1.printStackTrace();
				System.out.println("UNABLE TO SEND QUERY REQUEST GUI");
			}	

		}else if(e.getSource() == btnWrite && !textField.getText().isEmpty()){
			Query que = getWriteQuery();
			String sQuery = c.writeQueryContructor(que);
			addTransaction(sQuery, true);
			transactionList.append(transactionCounter + ". " + "Write " + sQuery + "\n"); 
			c.addQueryList(textField.getText().toString(), que);
			//transactionCounter++;
			
		}else if (e.getSource() == btnRead && !textField.getText().isEmpty()){
			Query que = getReadQuery();
			String sQuery = c.readQueryContructor(que);
			addTransaction(sQuery,false);
			readTranList.add(textField.getText().toString());
			transactionList.append(transactionCounter + ". " + "Read " + sQuery + "\n"); 
			c.addQueryList(textField.getText().toString(), que);
			transactionCounter++;
			
		}
		else if(e.getSource() == rbAbort){
			TRAN_ACTION = Transaction.ABORT;
			
		}else if (e.getSource() == rbCommit){
			
			TRAN_ACTION = Transaction.COMMIT;
		}

	}

	public void addTransaction(String query, boolean isWrite){
		
		Site receiver = null;
		
		String siteChosen = checkIfLocalOrGlobal();
		
		if(c.getOwner().getName().equals(siteChosen))
			receiver = c.getOwner();
		else if(c.getOwner().getName().equals(Tags.CENTRAL))
			receiver = c.getOwner();
		else 
			receiver = c.searchForSite(siteChosen);
		
		
		TransactionMail t = new TransactionMail(query, receiver,textField.getText().toString());
		t.setSender(c.getOwner());
		
		if(ISO_LEVEL.equals(""))
			ISO_LEVEL = cbIsolationLevel.getSelectedItem().toString();
		
		t.setISO_LEVEL(ISO_LEVEL);
		t.setTableName(Tags.TABLE);
		t.setTranAction(TRAN_ACTION);
		t.setWrite(isWrite);
		c.addTransactionMail(t);
	}
	
	public void closeTab(int index){

		if(index!=resultsTabbedPane.getTabCount()-1){
			for(int i = index+1;i< readTranList.size(); i++){
				System.out.println("ITERATE: "+ (i)+"OLD: " + tablePanelList.get(i).getTabIndex()+ "NEW: " + (tablePanelList.get(i).getTabIndex()-1));
				tablePanelList.get(i).changeTabIndex(tablePanelList.get(i).getTabIndex()-1);
			}
			resultsTabbedPane.remove(index);
			//tablePanelList.remove(index);
			System.out.println("Removed: " + (index+1));
		}

	
	}
	


	@Override
	public void update(String tranName) {
		// TODO Auto-generated method stub
		
		
		rsList = c.getResultSets().get(tranName);
		System.out.println("rsList: "+ rsList.getTranName());
		//System.out.println("RS LIST: "+ rsList.get());
		updateTable(tranName);
	}

}