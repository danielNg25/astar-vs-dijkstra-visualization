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
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class PathFindingFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private String[] tools = {"Start","Finish","Wall", "Eraser"};
	//mapAstar and mapDij instances
	Map mapAstar, mapDij;
	//border of control panel
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
		panel.setBounds(10, 10, 197, 600);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//add map to pane
		mapAstar = new Map();
		mapAstar.alg.setAStar(true);
		mapAstar.setBounds(234, 10, 601, 601);
		contentPane.add(mapAstar);
		
		mapDij = new Map();
		mapDij.alg.setAStar(false);
		mapDij.setBounds(858, 10, 601, 601);
		contentPane.add(mapDij);
		
		//search button
		JButton btnSearch = new JButton("Start Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				
					mapAstar.setSolving (true);
					mapDij.setSolving (true);
					
				
			}
		});
		btnSearch.setBounds(27, 90, 142, 23);
		panel.add(btnSearch);
		
		//clear button
		JButton btnClear = new JButton("Clear Map");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearMap();
				
			}
		});
		btnClear.setBounds(27, 141, 142, 23);
		panel.add(btnClear);
		
		//generate map button
		JButton btnGen = new JButton("Generate Map");
		btnGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapAstar.generateMap();
				mapDij.setMap(mapAstar);
			}
		});
		btnGen.setBounds(27, 192, 142, 23);
		panel.add(btnGen);
		
		//tool box to create elements on the grid
		JLabel lblNewLabel = new JLabel("Tool Box");
		lblNewLabel.setBounds(27, 249, 75, 14);
		panel.add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox(tools);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mapAstar.setTool(comboBox.getSelectedIndex());
			}
		});
		
		comboBox.setBounds(27, 275, 142, 22);
		panel.add(comboBox);
		
		//set button A Star map and Dijsktra map to be the same
		JButton btnSetMap = new JButton("Set Map");
		btnSetMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapDij.setMap(mapAstar);
			}
		});
		btnSetMap.setBounds(27, 42, 142, 23);
		panel.add(btnSetMap);
		
		//map size adjustment
		JLabel lblSize = new JLabel("20x20");
		lblSize.setBounds(143, 326, 45, 26);
		panel.add(lblSize);
		
		JSlider sliderSize = new JSlider(1,5,2);
		sliderSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Map.setCells(sliderSize.getValue()*10);
				clearMap();
				lblSize.setText(mapAstar.getCells() + "x" + mapAstar.getCells());		
			}
		});
		sliderSize.setMajorTickSpacing(10);
		sliderSize.setBounds(41, 326, 92, 26);
		panel.add(sliderSize);
		
		JLabel lblNewLabel_1_1 = new JLabel("Size");
		lblNewLabel_1_1.setBounds(10, 326, 34, 26);
		panel.add(lblNewLabel_1_1);
		
		//Obstacle density adjustment
		JLabel lblNewLabel_1_1_2 = new JLabel("Obs");
		lblNewLabel_1_1_2.setBounds(10, 363, 34, 26);
		panel.add(lblNewLabel_1_1_2);
		
		JLabel lblObs = new JLabel("50%");
		lblObs.setBounds(143, 363, 45, 26);
		panel.add(lblObs);
		
		JSlider sliderObs = new JSlider(1,100,50);
		sliderObs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Map.setDensity((double)sliderObs.getValue()/100*mapAstar.getCells() *mapAstar.getCells() );
				lblObs.setText(sliderObs.getValue() +"%");
			}
		});
		sliderObs.setMajorTickSpacing(5);
		sliderObs.setBounds(41, 363, 92, 26);
		panel.add(sliderObs);
		
		
		//delay time adjustment
		JLabel lblNewLabel_1_1_3 = new JLabel("Delay");
		lblNewLabel_1_1_3.setBounds(10, 400, 34, 26);
		panel.add(lblNewLabel_1_1_3);
		
		JLabel lblDelay = new JLabel("30ms");
		lblDelay.setBounds(143, 400, 45, 26);
		panel.add(lblDelay);
		
		JSlider sliderDelay = new JSlider(0,500,30);
		sliderDelay.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Map.setDelay(sliderDelay.getValue());
				lblDelay.setText(sliderDelay.getValue() + "ms");
			}
		});
		sliderDelay.setMajorTickSpacing(5);
		sliderDelay.setBounds(41, 400, 92, 26);
		panel.add(sliderDelay);
		
		
		//number of node checked and final path
		JLabel lblAStar = new JLabel("A Star:");
		lblAStar.setBounds(10, 437, 75, 14);
		panel.add(lblAStar);
		
		JLabel lblAStarChecked = mapAstar.lblChecked;
		lblAStarChecked.setBounds(39, 462, 117, 14);
		panel.add(lblAStarChecked);
		
		JLabel lblAStarPath = mapAstar.lblPath;
		lblAStarPath.setBounds(39, 491, 117, 14);
		panel.add(lblAStarPath);
		
		JLabel lblNewLabel_1_3 = new JLabel("Dijkstra ");
		lblNewLabel_1_3.setBounds(10, 521, 75, 14);
		panel.add(lblNewLabel_1_3);
		
		JLabel lblDijChecked = mapDij.lblChecked;
		lblDijChecked.setBounds(39, 546, 117, 14);
		panel.add(lblDijChecked);
		
		JLabel lblDijPath = mapDij.lblPath;
		lblDijPath.setBounds(39, 575, 117, 14);
		panel.add(lblDijPath);
		
		
		setVisible(true);
		try {
			startSearch();
		}catch(IllegalThreadStateException e) {	}
	}
	
	private void reset() {
		mapAstar.reset();
		mapDij.reset();
	}
	
	private void startSearch() {
		if (mapAstar.isSolving()||mapDij.isSolving()) {
			mapAstar.alg.start();
			mapDij.alg.start();
			
		}
		
		pause();	//pause state
	}
	
	private void pause() {	//pause state
		int i = 0;
		while(!mapAstar.isSolving()||!mapDij.isSolving()) {
			i++;
			if(i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch(Exception e) {}
		}
		startSearch();	//start state
	}
	private void clearMap() {
		mapAstar.clearMap();
		mapDij.clearMap();
	}
	
}
