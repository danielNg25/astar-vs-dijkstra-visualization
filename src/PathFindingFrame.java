import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class PathFindingFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private String[] tools = {"Start","Finish","Wall", "Eraser"};
	Map mapAstar, mapDij;
	//BORDER
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new PathFindingFrame();
				

	}

	/**
	 * Create the frame.
	 */
	public PathFindingFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 1500, 670);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(loweredetched,"Controls"));
		panel.setBounds(10, 10, 198, 600);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JButton btnNewButton = new JButton("Start Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				
					mapAstar.setSolving (true);
					mapDij.setSolving (true);
					
				
			}
		});
		btnNewButton.setBounds(46, 89, 107, 23);
		panel.add(btnNewButton);
		
		JButton btnNewButton_1_1 = new JButton("Clear Map");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapAstar.clearMap();
				mapDij.clearMap();
			}
		});
		btnNewButton_1_1.setBounds(46, 140, 107, 23);
		panel.add(btnNewButton_1_1);
		
		JButton btnNewButton_1_2 = new JButton("Generate Map");
		btnNewButton_1_2.setBounds(46, 191, 107, 23);
		panel.add(btnNewButton_1_2);
		
		JLabel lblNewLabel = new JLabel("Tool Box");
		lblNewLabel.setBounds(46, 237, 75, 14);
		panel.add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox(tools);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mapAstar.setTool(comboBox.getSelectedIndex());
			}
		});
		
		comboBox.setBounds(46, 274, 107, 22);
		panel.add(comboBox);
		
		JButton btnSetMap = new JButton("Set Map");
		btnSetMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapDij.setMap(mapAstar);
			}
		});
		btnSetMap.setBounds(46, 41, 107, 23);
		panel.add(btnSetMap);
		
		
		mapAstar = new Map();
		mapAstar.alg.setAStar(true);
		mapAstar.setBounds(234, 10, 601, 601);
		contentPane.add(mapAstar);
		
		mapDij = new Map();
		mapDij.alg.setAStar(false);
		mapDij.setBounds(858, 10, 601, 601);
		contentPane.add(mapDij);
		setVisible(true);
		startSearch();
	}
	
	private void reset() {
		mapAstar.reset();
		mapDij.reset();
	}
	
	private void startSearch() {
		if (mapAstar.isSolving()||mapDij.isSolving()) {
			Thread aStarThread = new Thread(mapAstar.alg);
			Thread dijThread = new Thread(mapDij.alg);
			aStarThread.run();
			dijThread.run();
			
		}
		
		pause();	//PAUSE STATE
	}
	
	public void pause() {	//PAUSE STATE
		int i = 0;
		while(!mapAstar.isSolving()||!mapDij.isSolving()) {
			i++;
			if(i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch(Exception e) {}
		}
		startSearch();	//START STATE
	}
	
}
