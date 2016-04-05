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
import java.util.Map.Entry;

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
import model.ResultSets;
import model.Site;
import model.Tags;
import model.Transaction;
import model.TransactionMail;
import controller.Controller;

public class ClientGUI extends JFrame implements ActionListener, Observer{

	private Site client;
	private Controller c;
	private ArrayList<CachedRowSetImpl> rsList;
	private ArrayList<TransactionMail> tranList;
	private String ISO_LEVEL;
	private int TRAN_ACTION;
	
	private JPanel topPanel, bottomPanel;
	private JPanel queryPanel, abortCommitPanel, mainPanel, isolationPanel;
	private JPanel customPanel, defaultPanel;
	private JPanel transactionsPanel, queryEditPanel;
	private JScrollPane readScroll, customScroll, pane;
	private JTabbedPane queryTabbedPane, resultsTabbedPane;
	private JTextArea readTextArea, customTextArea, transactionList;
	private JRadioButton rbAbort, rbCommit;
	private JButton btnRead, btnWrite;
	private JButton btnSubmitButton;
	private JPanel settingsPanel;
	private JPanel areaPanel;
	private JCheckBox chckbxPalawan;
	private JCheckBox chckbxMarinduque;
	private JComboBox cbIsolationLevel, cbDefaultQueries;

	private boolean isPalawan;
	private boolean isMarinduque;

	private HashMap<String, JTextField> queryComponents;
	private JButton btnSubmit;

	private ArrayList<TablePanel> tablePanelList = new ArrayList<TablePanel>();
	private ArrayList<String> transactionQueries = new ArrayList<String>();
	private HashMap<String, String> readWriteComponents = new HashMap<String, String>();
	private int transactionCounter = 1;

	public ClientGUI(Controller con) {

		this.c = con;
		this.c.registerObserver(this);
		this.tranList = new ArrayList<TransactionMail>();
		this.ISO_LEVEL = "";
		this.TRAN_ACTION = Transaction.COMMIT;
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch (Exception e) {}

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel);

		createTopPanel();
		createBottomPanel();
		createButtonsPanel();
		mainPanel.add(topPanel, BorderLayout.WEST);  
		mainPanel.add(bottomPanel, BorderLayout.EAST);

		setBackground(Color.gray);
		setSize(1000, 700);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}

	public void createButtonsPanel(){
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
		String[] defaultQueryChoices = {"select hpq_hh_id, aquani_vol, aquanitype_o from hpq_aquani",
		"select id, age_yr, occup from hpq_mem"};
		cbDefaultQueries = new JComboBox(defaultQueryChoices);
		cbDefaultQueries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(cbDefaultQueries.getSelectedItem().equals("select hpq_hh_id, aquani_vol, aquanitype_o from hpq_aquani")) {
					ArrayList<String> attributeList = new ArrayList<String>();
					attributeList.add("Household ID: ");
					attributeList.add("Aquani Volume: ");
					attributeList.add("Aquani Type: ");
					createQueryEditPanel(attributeList);
				} else {
					ArrayList<String> attributeList = new ArrayList<String>();
					attributeList.add("Household ID: ");
					attributeList.add("Age: ");
					attributeList.add("Occupation: ");
					createQueryEditPanel(attributeList);
				}
				queryEditPanel.repaint();
				queryEditPanel.revalidate();
				pane.repaint();
				pane.revalidate();
				defaultPanel.repaint();
				defaultPanel.revalidate();
			}
		});

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

		defaultPanel.add(cbDefaultQueries, BorderLayout.NORTH);
		defaultPanel.add(pane, BorderLayout.CENTER);
		defaultPanel.add(btnsPanel, BorderLayout.SOUTH);

		customPanel = new JPanel();
		customPanel.setBackground(Color.WHITE);
		queryTabbedPane.addTab("Custom", null, customPanel, null);
		customPanel.setLayout(new GridLayout(0, 1, 0, 0));
		customTextArea = new JTextArea(customPanel.getWidth(), customPanel.getHeight());
		customScroll = new JScrollPane(customTextArea);
		customScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		customPanel.add(customScroll);
		btnSubmitButton = new JButton("Submit");
		btnSubmitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				transactionQueries.clear();
				transactionCounter = 1;
				transactionList.removeAll();
				transactionList.repaint();
				transactionList.revalidate();
				//generate table
			}
		});
		customPanel.add(btnSubmitButton);

		return queryPanel;
	}

	//DOES NOT TAKE INTO ACCOUNT STRING TYPE (lacking: " ")
	public String getWriteQuery() {
		String query = "";
		String table = "";
		String set = "set ";
		String where = "where ";
		String[] attributeTitles = new String[3];

		if(cbDefaultQueries.getSelectedIndex() == 0) {
			table = "hpq_aquani";
		} else {
			table = "hpq_mem";
		}

		if(cbDefaultQueries.getSelectedIndex() == 0) {
			attributeTitles[0] = "hpq_hh_id";
			attributeTitles[1] = "aquani_vol";
			attributeTitles[2] = "aquanitype_o";
		} else {
			attributeTitles[0] = "id";
			attributeTitles[1] = "age_yr";
			attributeTitles[2] = "occup";
		}

		Component[] children = queryEditPanel.getComponents();
		for(int i = 0; i < children.length; i++) {
			JPanel panel = (JPanel) children[i];
			Component[] panelChildren = panel.getComponents();
			for(int j = 0; j < panelChildren.length; j++) {
				if(panelChildren[j] instanceof JTextField) {
					String text = ((JTextField)panelChildren[j]).getText();
					if(!text.isEmpty()) {
						readWriteComponents.put(attributeTitles[i], text);
					}
				}
			}
		}

		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
			if(entry.getKey().equals("id") || entry.getKey().equals("hpq_hh_id")) {
				where = where + "id = " + entry.getValue() + ";";
				readWriteComponents.remove(entry.getKey());
			} 
		}

		int i = 1;
		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
			if (i < readWriteComponents.size()) {
				set = set + entry.getKey() + " = " + entry.getValue() + ", ";
			} else {
				set = set + entry.getKey() + " = " + entry.getValue();
			}
			i++;
		}

		if(!where.isEmpty() && !set.isEmpty()) {
			query = "UPDATE " + table + " " + set + " " + where;
			System.out.println(query);
		}

		transactionQueries.add(query);
		readWriteComponents.clear();

		return query;
	}

	public String getReadQuery() {
		String query = cbDefaultQueries.getSelectedItem().toString();
		String where = "where ";
		String[] attributeTitles = new String[3];

		if(cbDefaultQueries.getSelectedIndex() == 0) {
			attributeTitles[0] = "hpq_hh_id";
			attributeTitles[1] = "aquani_vol";
			attributeTitles[2] = "aquanitype_o";
		} else {
			attributeTitles[0] = "id";
			attributeTitles[1] = "age_yr";
			attributeTitles[2] = "occup";
		}

		Component[] children = queryEditPanel.getComponents();
		for(int i = 0; i < children.length; i++) {
			JPanel panel = (JPanel) children[i];
			Component[] panelChildren = panel.getComponents();
			for(int j = 0; j < panelChildren.length; j++) {
				if(panelChildren[j] instanceof JTextField) {
					String text = ((JTextField)panelChildren[j]).getText();
					if(!text.isEmpty()) {
						readWriteComponents.put(attributeTitles[i], text);
					}
				}
			}
		}

		int i = 1;
		for(Entry<String, String> entry : readWriteComponents.entrySet()) {
			if(i < readWriteComponents.size()) {
				where = where + entry.getKey() + " = " + entry.getValue() + " AND ";
			} else {
				where = where + entry.getKey() + " = " + entry.getValue() + ";";
			}
			i++;
		}

		if(!where.equals("where ")) {
			query = query + " " + where;
			System.out.println(query);
		} else {
			query = query + ";";
		}

		transactionQueries.add(query);
		readWriteComponents.clear();

		return query;
	}

	public void createQueryEditPanel(ArrayList<String> attributeList) {
		queryEditPanel.removeAll();
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
		bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(600, 350));
		bottomPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder("Dataset");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		bottomPanel.setBorder(new CompoundBorder(border, margin));

		resultsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabbedPane.setBackground(Color.RED);
		bottomPanel.add(resultsTabbedPane, BorderLayout.CENTER);
		//bottomPanel.add(createTablePanel(), BorderLayout.CENTER);
	}

	public JPanel createTablePanel() {
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());

		JScrollPane pane = new JScrollPane(createJTable());
		tablePanel.add(pane, BorderLayout.CENTER);

		return tablePanel;
	}

	public JTable createJTable() {
		JTable table = new JTable();
		DefaultTableModel dataModel = new DefaultTableModel();
		table.setModel(dataModel);

		try {
			System.out.println("SIZE OF RS LIST IN GUI: "+rsList.size());
			for(int i = 0; i<rsList.size(); i++){
				CachedRowSetImpl rs = rsList.get(i);
				System.out.println("RES #"+i);
				ResultSetMetaData mdata = rs.getMetaData();
				int colCount = mdata.getColumnCount();		
				String[] colNames = getColumnNames(colCount, mdata);
				dataModel.setColumnIdentifiers(colNames);
				while (rs.next()) {
					System.out.println("PRINT");
					String[] rowData = new String[colCount];
					for (int j = 1; j <= colCount; j++) {
						rowData[j - 1] = rs.getString(j);
						System.out.println("DATA: "+rs.getString(j));
					}
					dataModel.addRow(rowData);
				}
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

	public void updateTable() {
		bottomPanel.removeAll();
		JTable table = createJTable();
		JScrollPane pane = new JScrollPane(table);
		updateRowHeights(table);
		bottomPanel.add(pane, BorderLayout.CENTER);
		bottomPanel.revalidate();
		bottomPanel.repaint();
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

		}else if(e.getSource() == btnSubmit){
			
			
			try {
				c.SEND_QUERY_REQUEST(tranList);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			resultsTabbedPane.removeAll();
			tablePanelList.clear();
			
			for(int i = 0; i<transactionList.getLineCount()-1; i++){
				TablePanel tablePanel= new TablePanel("");
				tablePanel.setBackground(Color.WHITE);
				resultsTabbedPane.addTab("Query", null, tablePanel, null);
				resultsTabbedPane.setSelectedIndex(resultsTabbedPane.getTabCount()-1);
				tablePanel.setCorrespondingTab(this,resultsTabbedPane.getSelectedIndex()); //select last one
				tablePanelList.add(resultsTabbedPane.getSelectedIndex(),tablePanel);
			}

		}else if(e.getSource() == btnWrite){
			String que = getWriteQuery();
			//addTransaction(que, true);
			addTransaction("UPDATE numbers SET col = 999 WHERE id = 1;", true);
			transactionList.append(transactionCounter + ". " + "Write " + que + "\n"); 
			transactionCounter++;
		}else if (e.getSource() == btnRead){
			String que = getReadQuery();
			//addTransaction(que,false);
			addTransaction("SELECT * FROM numbers;", false);
			transactionList.append(transactionCounter + ". " + "Read " + que + "\n"); 
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
			receiver = c.searchForSite(Tags.CENTRAL);
		else 
			receiver = c.searchForSite(siteChosen);
		
		System.out.println("SITE CHOSEN: "+siteChosen);
		TransactionMail t = new TransactionMail(query, receiver);
		t.setSender(c.getOwner());
		
		if(ISO_LEVEL.equals(""))
			ISO_LEVEL = cbIsolationLevel.getSelectedItem().toString();
		
		t.setISO_LEVEL(ISO_LEVEL);
		t.setTableName("numbers");
		t.setTranAction(TRAN_ACTION);
		t.setWrite(isWrite);
		
		tranList.add(t);
	}
	
	public void closeTab(int index){

		if(index!=resultsTabbedPane.getTabCount()-1){
			for(int i = index+1;i< tablePanelList.size(); i++){
				System.out.println("ITERATE: "+ (i)+"OLD: " + tablePanelList.get(i).getTabIndex()+ "NEW: " + (tablePanelList.get(i).getTabIndex()-1));
				tablePanelList.get(i).changeTabIndex(tablePanelList.get(i).getTabIndex()-1);
			}
		}

		resultsTabbedPane.remove(index);
		tablePanelList.remove(index);
		System.out.println("Removed: " + (index+1));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		rsList = c.getResultSets();
		updateTable();
		System.out.println("FINISH UPDATING GUI");
	}

}