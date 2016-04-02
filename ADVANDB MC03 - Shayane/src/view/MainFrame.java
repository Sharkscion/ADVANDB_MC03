package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	public MainFrame() {
		JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);
        
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        setBackground(Color.gray);
        setSize(350, 150);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	public JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setPreferredSize(new Dimension(350, 40));
		
		JLabel projectTitle = new JLabel("ADVANDB MCO3");
		projectTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
		
		topPanel.add(projectTitle, BorderLayout.CENTER);
		
		return topPanel;
	}
	
	public JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setPreferredSize(new Dimension(350, 90));
		
		JButton btnCentralOffice = new JButton("Central Office");
        JButton btnPalawanBranch = new JButton("Palawan Branch");
        JButton btnMarinduqueBranch = new JButton("Marinduque Branch");
        
        bottomPanel.add(btnCentralOffice, BorderLayout.NORTH);
        bottomPanel.add(btnPalawanBranch, BorderLayout.CENTER);
        bottomPanel.add(btnMarinduqueBranch, BorderLayout.SOUTH);
        
        return bottomPanel;
	}
}
