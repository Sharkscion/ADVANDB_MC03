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
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

import model.Observer;
import model.ResultSets;
import model.Site;
import model.Tags;
import model.Transaction;
import controller.Controller;

public class ClientGUI extends JFrame implements ActionListener, Observer{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Controller c;
	private ResultSets rs;
	
	private JPanel topPanel, bottomPanel;
	private JPanel queryPanel, abortCommitPanel, mainPanel, isolationPanel;
	private JPanel customPanel, defaultPanel;
	private JPanel transactionsPanel, queryEditPanel;
	private JScrollPane readScroll, customScroll, pane;
	private JTabbedPane queryTabbedPane;
	private JTextArea readTextArea, customTextArea, transactionList;
	private JRadioButton rbAbort, rbCommit;
	private JButton btnRead, btnWrite;
	private JButton btnSubmitCustom;
	private JPanel settingsPanel;
	private JComboBox cbIsolationLevel, cbDefaultQueries;
	private JPanel areaPanel;
	private JCheckBox chckbxPalawan;
	private JCheckBox chckbxMarinduque;
	
	private boolean isPalawan;
	private boolean isMarinduque;
	private int isolationLevel;
	
	JScrollPane changeQueryScrollPane;
	JPanel changeCenterQueryPanel;
	JPanel changeQueryPanelArea;
	private HashMap<String, JTextField> queryComponents;
	private JButton btnSubmit;
	

	public ClientGUI(Controller con) {
		
		  this.c = con;
		  this.c.registerObserver(this);
		  
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
        String[] defaultQueryChoices = {"select id, aquani_vol, aquanitype from hpq_aquani",
        								"select id, age_yr, occup from hpq_mem"};
        cbDefaultQueries = new JComboBox(defaultQueryChoices);
        cbDefaultQueries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(cbDefaultQueries.getSelectedItem().equals("select id, aquani_vol, aquanitype from hpq_aquani")) {
					ArrayList<String> attributeList = new ArrayList<String>();
					attributeList.add("Household ID");
					attributeList.add("Fish Volume");
					attributeList.add("Fish Type");
				    createQueryEditPanel(attributeList);
				} else {
					ArrayList<String> attributeList = new ArrayList<String>();
					attributeList.add("Household ID");
					attributeList.add("Age");
					attributeList.add("Occupation");
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
        btnSubmitCustom = new JButton("Submit");
        btnSubmitCustom.addActionListener(this);
        customPanel.add(btnSubmitCustom);
        
        return queryPanel;
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
		
		String[] isolationLevelChoices = {"Read Uncommmitted", "Read Committed", "Read Repeatable", "Serialzable"};
		
		isolationPanel = new JPanel();
        isolationPanel.setLayout(new BorderLayout());
        isolationPanel.setBackground(Color.WHITE);
        isolationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Isolation Level", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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
		bottomPanel.setPreferredSize(new Dimension(590, 350));
		bottomPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder("Dataset");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		bottomPanel.setBorder(new CompoundBorder(border, margin));
		
		//bottomPanel.add(createTablePanel(), BorderLayout.CENTER);
	}
	
	public JPanel createTablePanel() {
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		
		JScrollPane pane = new JScrollPane(createJTable(rs));
		tablePanel.add(pane, BorderLayout.CENTER);
		
		return tablePanel;
	}
	
	public JTable createJTable(ResultSets rs) {
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
		
		if(e.getSource() == btnSubmitCustom){
			System.out.println("READ SUBMIT CUSTOM");
			//String schema = checkIfLocalOrGlobal();
			//String query = 
	
		//public Transaction(String protocolTag, String name, String schema, String tableName,String query, boolean isWrite){

			//Transaction t = new Transaction();
			String message = customTextArea.getText() + "%" + checkIfLocalOrGlobal();
			c.SEND_READ_REQUEST(message);		
		}else if (e.getSource() == rbAbort){
			isolationLevel = Transaction.ABORT;
		}else if (e.getSource() == rbCommit){
			isolationLevel = Transaction.COMMIT;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		rs = c.getResultSets();
		bottomPanel.removeAll();
		JTable table = createJTable(rs);
		JScrollPane pane = new JScrollPane(table);
		updateRowHeights(table);
		bottomPanel.add(pane, BorderLayout.CENTER);
		bottomPanel.revalidate();
		bottomPanel.repaint();
	}
	
}
