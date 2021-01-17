import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class PathFinding {
	
	//FRAME
	JFrame frame;
	//GENERAL VARIABLES
	private int cells = 250;
	private int delay = 30;

	private int startx = -1;
	private int starty = -1;
	private int finishx = -1;
	private int finishy = -1;
	private double finishAngle = Math.PI;
	private int tool = 0;
	private int checks = 0;
	private int length = 0;
	private int curAlg = 0;
	private int WIDTH = 1440;
	private final int HEIGHT = 1080;
	private final int MSIZE = 1000;
	private int CSIZE = MSIZE/cells;
	//UTIL ARRAYS
	private String[] algorithms = {"Dijkstra","A*"};
	private String[] tools = {"Start","Finish","Wall", "Eraser"};
	//BOOLEANS
	private boolean solving = false;
	//UTIL
	Node[][] map;
	Algorithm Alg = new Algorithm();
	Random r = new Random();
	ArrayList<Node> path;
	Car car = new Car();
	//IMAGE
	
	BufferedImage destination;
	
	//SLIDERS

	JSlider speed = new JSlider(0,500,delay);
	JSlider obstacles = new JSlider(1,100,50);
	//LABELS

	JLabel toolL = new JLabel("Toolbox");
	JLabel delayL = new JLabel("Delay:");
	JLabel msL = new JLabel(delay+"ms");
	JLabel obstacleL = new JLabel("Dens:");
	JLabel densityL = new JLabel(obstacles.getValue()+"%");
	JLabel checkL = new JLabel("Checks: "+checks);
	JLabel lengthL = new JLabel("Path Length: "+length);
	//BUTTONS
	JButton searchB = new JButton("Start Search");
	JButton resetB = new JButton("Reset");

	JButton clearMapB = new JButton("Clear Map");

	//DROP DOWN
	
	JComboBox toolBx = new JComboBox(tools);
	//PANELS
	JPanel toolP = new JPanel();
	//CANVAS
	Map canvas;
	//BORDER
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public static void main(String[] args) {	//MAIN METHOD
		new PathFinding();
	}

	public PathFinding() {	//CONSTRUCTOR
		clearMap();
		initialize();
	}
	
	
	
	public void clearMap() {	//CLEAR MAP
		finishx = -1;	//RESET THE START AND FINISH
		finishy = -1;
		startx = -1;
		starty = -1;
		map = new Node[cells][cells];	//CREATE NEW MAP OF NODES
		for(int x = 0; x < cells; x++) {
			for(int y = 0; y < cells; y++) {
				map[x][y] = new Node(3,x,y);	//SET ALL NODES TO EMPTY
			}
		}
		reset();	//RESET SOME VARIABLES
	}
	
	public void resetMap() {	//RESET MAP
		for(int x = 0; x < cells; x++) {
			for(int y = 0; y < cells; y++) {
				Node current = map[x][y];
				if(current.getType() == 4 || current.getType() == 5)	//CHECK TO SEE IF CURRENT NODE IS EITHER CHECKED OR FINAL PATH
					map[x][y] = new Node(3,x,y);	//RESET IT TO AN EMPTY NODE
			}
		}
		if(startx > -1 && starty > -1) {	//RESET THE START AND FINISH
			map[startx][starty] = new Node(0,startx,starty);
			map[startx][starty].setHops(0);
		}
		if(finishx > -1 && finishy > -1)
			map[finishx][finishy] = new Node(1,finishx,finishy);
		reset();	//RESET SOME VARIABLES
	}

	private void initialize() {	//INITIALIZE THE GUI ELEMENTS
		frame = new JFrame();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(WIDTH,HEIGHT);
		frame.setTitle("Path Finding");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		toolP.setBorder(BorderFactory.createTitledBorder(loweredetched,"Controls"));
		int space = 50;
		int buff = 90;
		
		toolP.setLayout(null);
		toolP.setBounds(40,11,250,1000);
		
		searchB.setBounds(40,space, 150, 25);
		toolP.add(searchB);
		space+=buff;
		
		resetB.setBounds(40,space,150,25);
		toolP.add(resetB);
		space+=buff;
		
		
		
		clearMapB.setBounds(40,space, 150, 25);
		toolP.add(clearMapB);
		space+=buff;
		
	
		
		toolL.setBounds(40,space,150,25);
		toolP.add(toolL);
		space+=40;
		
		toolBx.setBounds(40,space,150,25);
		toolP.add(toolBx);
		space+=buff;
		

		
		delayL.setBounds(15,space,50,25);
		toolP.add(delayL);
		speed.setMajorTickSpacing(5);
		speed.setBounds(50,space,120,25);
		toolP.add(speed);
		msL.setBounds(170,space,90,25);
		toolP.add(msL);
		space+=buff;
		
		obstacleL.setBounds(15,space,120,25);
		toolP.add(obstacleL);
		obstacles.setMajorTickSpacing(5);
		obstacles.setBounds(50,space,120,25);
		toolP.add(obstacles);
		densityL.setBounds(170,space,120,25);
		toolP.add(densityL);
		space+=buff;
		
		checkL.setBounds(50,space,100,25);
		toolP.add(checkL);
		space+=buff;
		
		lengthL.setBounds(50,space,100,25);
		toolP.add(lengthL);
		space+=buff;
		

		
		frame.getContentPane().add(toolP);
		
		canvas = new Map();
		canvas.setBounds(350, 15, MSIZE+1, MSIZE+1);
		frame.getContentPane().add(canvas);
		
		searchB.addActionListener(new ActionListener() {		//ACTION LISTENERS
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				if((startx > -1 && starty > -1) && (finishx > -1 && finishy > -1))
					solving = true;
			}
		});
		resetB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMap();
				Update();
			}
		});
		
		clearMapB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearMap();
				Update();
			}
		});
		
		toolBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				tool = toolBx.getSelectedIndex();
			}
		});
		
		speed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				delay = speed.getValue();
				Update();
			}
		});
		

		
		startSearch();	//START STATE
	}
	
	public void startSearch() {	//START STATE
		if(solving) {
			Alg.AStar();
				
		}
		pause();	//PAUSE STATE
	}
	
	public void pause() {	//PAUSE STATE
		int i = 0;
		while(!solving) {
			i++;
			if(i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch(Exception e) {}
		}
		startSearch();	//START STATE
	}
	
	public void Update() {	//UPDATE ELEMENTS OF THE GUI
		

		canvas.repaint();

		msL.setText(delay+"ms");
		lengthL.setText("Path Length: "+length);
		densityL.setText(obstacles.getValue()+"%");
		checkL.setText("Checks: "+checks);
	}
	
	public void reset() {	//RESET METHOD
		
		solving = false;
		length = 0;
		checks = 0;
	}
	
	public void delay(int delay) {	//DELAY METHOD
		try {
			Thread.sleep(delay);
		} catch(Exception e) {}
	}
	

	
	class Map extends JPanel implements MouseListener, MouseMotionListener{	//MAP CLASS
		
		public Map() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			try {
				destination = ImageIO.read(getClass().getResourceAsStream("/destination.png"));
			}catch(Exception e) {
				e.printStackTrace();
			}
		
			AffineTransform carat = AffineTransform.getTranslateInstance(0, 0);
			AffineTransform desat = AffineTransform.getTranslateInstance(0, 0);
			boolean cared = false;
			boolean desed = false;
			boolean solved = false;
			for(int x = 0; x < cells; x++) {	//PAINT EACH NODE IN THE GRID
				for(int y = 0; y < cells; y++) {
					switch(map[x][y].getType()) {
						case 0:
							carat.translate(x*CSIZE-car.getCar().getWidth()/2, y*CSIZE-car.getCar().getHeight()/2);
							cared = true;
							g.setColor(Color.GREEN);
							break;
						case 1:
							desat.translate(x*CSIZE-destination.getWidth()/2, y*CSIZE-destination.getHeight()/2);
							desed = true;
							g.setColor(Color.RED);
							break;
						case 2:
							g.setColor(Color.BLACK);
							break;
						case 3:
							g.setColor(Color.WHITE);
							break;
						case 4:
							g.setColor(Color.CYAN);
							break;
						case 5:
							cared = false;
							solved = true;
							carat = car.transForm(carat,x,y);
							break;
						case 6:
							g.setColor(Color.CYAN);
							break;
						case 7:
							g.setColor(Color.WHITE);
					}
					g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
					g.setColor(Color.BLACK);
				}
			}
			if (desed == true) {
				g2d.drawImage(destination, desat, null);
				desed = false;
			}
			
			if (solved == true) {
				g2d.drawImage(car.getCar(), carat, null);
			}
			if (cared == true) {
				g2d.drawImage(car.getCar(), carat, null);
				cared = false;
			}
			
			
			
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				int x = e.getX()/CSIZE;	
				int y = e.getY()/CSIZE;
				for (int i = -5; i<=5; i ++) {
					for (int j = -5; j <=5; j++) {
						Node current = map[x+i][y+j];
						if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
							current.setType(tool);
					}
				}
				Update();
			} catch(Exception z) {}
		}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			resetMap();	//RESET THE MAP WHENEVER CLICKED
			try {
				int x = e.getX()/CSIZE;	//GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
				int y = e.getY()/CSIZE;
				Node current = map[x][y];
				switch(tool ) {
					case 0: {	//START NODE
						if(current.getType()!=2) {	//IF NOT WALL
							if(startx > -1 && starty > -1) {	//IF START EXISTS SET IT TO EMPTY
								map[startx][starty].setType(3);
								map[startx][starty].setHops(-1);
							}
							current.setHops(0);
							startx = x;	//SET THE START X AND Y
							starty = y;
							current.setType(0);	//SET THE NODE CLICKED TO BE START
							car.initCar(current);
						}
						break;
					}
					case 1: {//FINISH NODE
						if(current.getType()!=2) {	//IF NOT WALL
							if(finishx > -1 && finishy > -1)	//IF FINISH EXISTS SET IT TO EMPTY
								map[finishx][finishy].setType(3);
							finishx = x;	//SET THE FINISH X AND Y
							finishy = y;
							current.setType(1);	//SET THE NODE CLICKED TO BE FINISH
							setFinishWall();
						}
						break;
					}
					case 2:{
						for (int i = -3; i<=3; i ++) {
							for (int j = -3; j <=3; j++) {
								Node cur = map[x+i][y+j];
								cur.setType(2);
							}
						}
					}
					default:
						if(current.getType() != 0 && current.getType() != 1)
							current.setType(tool);
						break;
				}
				Update();
			} catch(Exception z) {}	//EXCEPTION HANDLER
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
		private void setFinishWall() {
			if(finishAngle == Math.PI) {
				for(int i = -6; i<=6; i++) {
					map[finishx+i][finishy-6].setType(7);
					map[finishx+i][finishy+6].setType(7);
					map[finishx+6][finishy+i].setType(7);
					
				}
			}
			if(finishAngle == 0) {
				for (int i = -6; i<=6; i++) {
					map[finishx+i][finishy-6].setType(7);
					map[finishx+i][finishy+6].setType(7);
					map[finishx-6][finishy+i].setType(7);
				}
			}
			if(finishAngle == Math.PI/2) {
				for (int i = -6; i<=6; i++) {
					map[finishx+6][finishy+i].setType(7);
					map[finishx+i][finishy+6].setType(7);
					map[finishx-6][finishy+i].setType(7);
				}
			}
			if(finishAngle == -Math.PI/2) {
				for (int i = -6; i<=6; i++) {
					map[finishx+6][finishy+i].setType(7);
					map[finishx+i][finishy-6].setType(7);
					map[finishx-6][finishy+i].setType(7);
				}
			}
		}
	}
	
	class Algorithm {	
		
		//A STAR WORKS ESSENTIALLY THE SAME AS DIJKSTRA CREATING A PRIORITY QUE AND PROPAGATING OUTWARDS UNTIL IT FINDS THE END
		//HOWEVER ASTAR BUILDS IN A HEURISTIC OF DISTANCE FROM ANY NODE TO THE FINISH
		//THIS MEANS THAT NODES THAT ARE CLOSER TO THE FINISH WILL BE EXPLORED FIRST
		//THIS HEURISTIC IS BUILT IN BY SORTING THE QUE ACCORDING TO HOPS PLUS DISTANCE UNTIL THE FINISH
		public void AStar() {
			path = new ArrayList<>();
			ArrayList<Node> priority = new ArrayList<Node>();
			priority.add(map[startx][starty]);
			while(solving) {
				if(priority.size() <= 0) {
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;
				ArrayList<Node> explored = exploreNeighbors(priority.get(0),hops);
				if(explored.size() > 0) {
					priority.remove(0);
					priority.addAll(explored);
					Update();
					delay(delay);
				} else {
					priority.remove(0);
				}
				sortQue(priority);	//SORT THE PRIORITY QUE
			}
		}
		
		public ArrayList<Node> sortQue(ArrayList<Node> sort) {	//SORT PRIORITY QUE
			int c = 0;
			while(c < sort.size()) {
				int sm = c;
				for(int i = c+1; i < sort.size(); i++) {
					if(sort.get(i).getEuclidDist()+sort.get(i).getHops() < sort.get(sm).getEuclidDist()+sort.get(sm).getHops())
						sm = i;
				}
				if(c != sm) {
					Node temp = sort.get(c);
					sort.set(c, sort.get(sm));
					sort.set(sm, temp);
				}	
				c++;
			}
			return sort;
		}
		
		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//EXPLORE NEIGHBORS
			ArrayList<Node> explored = new ArrayList<Node>();	//LIST OF NODES THAT HAVE BEEN EXPLORED
			current.calAngle();
			
			int[][] pos = calNextPos(current.angle);
			
			
			for (int[] newPos: pos) {
				int xbound = current.getX()+newPos[0];
				int ybound = current.getY()+newPos[1];
			
				

				if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
					Node neighbor = map[xbound][ybound];
					
					neighbor.calAngle(current.getX(), current.getY());
					int[][] directionPos = calNextPos(neighbor.angle);
					boolean dir = true;
					for (int[] dirPos: directionPos) {
						for (int i = 5; i>=2; i--) {
							int xboundPlus = xbound+(car.getV()+car.getCar().getHeight()/(2*i))*dirPos[0];
							int yboundPlus = ybound+(car.getV()+car.getCar().getHeight()/(2*i))*dirPos[1];
							if ((xboundPlus <= -1 || xboundPlus >= cells) || (yboundPlus <= -1 || yboundPlus >= cells) ) {
								dir = false;
							}
							else {
								if(map[xboundPlus][yboundPlus].getType() ==2 ) {
									dir = false;
								}
							}
						}
						
					}
					if (dir == true) {
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2 && neighbor.getType()!=7) {	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
							explore(neighbor, current.getX(), current.getY(), hops);	//EXPLORE THE NODE
							explored.add(neighbor);	//ADD THE NODE TO THE LIST
						}
					}
					
				}
				
				
			}
			return explored;
		}
		
		public void explore(Node current, int lastx, int lasty, int hops) {	//EXPLORE A NODE
			if(current.getType()!=0 && current.getType() != 1)	//CHECK THAT THE NODE IS NOT THE START OR FINISH
				current.setType(4);	//SET IT TO EXPLORED
			current.setLastNode(lastx, lasty);	//KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
			current.setHops(hops);	//SET THE HOPS FROM THE START
			checks++;
			
			if(current.getType() == 1 ) {	//IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
				backtrack(current.getLastX(), current.getLastY(),hops);
			}
		}
		
		public void backtrack(int lx, int ly, int hops) {	//BACKTRACK
			length = hops;
			while(hops > 1) {	//BACKTRACK FROM THE END OF THE PATH TO THE START
				Node current = map[lx][ly];
				path.add(current);
				lx = current.getLastX();
				ly = current.getLastY();
				hops--;
			}
			Collections.reverse(path);
			for (Node node: path) {
				car.initCar(node);
				map[node.getLastX()][node.getLastY()].setType(6);
				node.setType(5);
				Update();
				delay(30);
			}
			solving = false;
		}
		public int[][] calNextPos(double angle) {
			
			if (angle == 0) {
				int[][] pos = {{1, -1}, {1, 0}, {1, 1}};
				return pos;
			}
			else if (angle == Math.PI) {
				int[][] pos = {{-1, -1}, {-1, 0}, {-1, 1}};
				return pos;
			}
			else if (angle == Math.PI/2) {
				int[][] pos = {{-1, 1}, {0, 1}, {1, 1}};
				return pos;
			}
			else if (angle == 3*Math.PI/2) {
				int[][] pos = {{-1, -1}, {0, -1}, {1, -1}};
				return pos;
			}
			else if (angle == Math.PI/4) {
				int[][] pos = {{1, 0}, {1, 1}, {0, 1}};
				return pos;
			}
			else if (angle == 3*Math.PI/4) {
				int[][] pos = {{-1, 0}, {-1, 1}, {0, 1}};
				return pos;
			}
			else if (angle == 5*Math.PI/4) {
				int[][] pos = {{-1, 0}, {-1, -1}, {0, -1}};
				return pos;
			}
			else {
				int[][] pos = {{1, 0}, {1, -1}, {0, -1}};
				return pos;
			}
			
			
		}
	}
	
	
	class Node {
		
		private int cellType = 0;
		private int hops;
		private int x;
		private int y;
		private int lastX;
		private int lastY;
		private double angle;
		private double dToEnd = 0;
		
		public Node(int type, int x, int y) {	//CONSTRUCTOR
			cellType = type;
			this.x = x;
			this.y = y;
			hops = -1;
		}
		
		public double getEuclidDist() {		//CALCULATES THE EUCLIDIAN DISTANCE TO THE FINISH NODE
			int xdif = Math.abs(x-finishx);
			int ydif = Math.abs(y-finishy);
			dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
			return dToEnd;
		}
		
		public void calAngle(int lastX, int lastY) {
			int subX = lastX-this.x;
			int subY = lastY-this.y;
		    if (subX == -1) {
		    	if (subY == -1)
		    		angle = Math.PI/4;
		    	else if(subY == 0)
		    		angle = 0;
		    	else if(subY == 1)
		    		angle = 7*Math.PI/4;
		    }
		    else if (subX == 0) {
		    	if (subY == -1)
		    		angle = Math.PI/2;
		    	else if (subY == 1)
		    		angle = 3*Math.PI/2;
		    }
		    else if (subX == 1) {
		    	if (subY == -1)
		    		angle = 3*Math.PI/4;
		    	else if (subY == 0)
		    		angle = Math.PI;
		    	else if (subY == 1)
		    		angle = 5*Math.PI/4;
		    }
		}
		public double getAngle() {
			calAngle();
			return angle;
		}
		public void calAngle() {
			int subX = lastX-x;
			int subY = lastY-y;
		    if (subX == -1) {
		    	if (subY == -1)
		    		angle = Math.PI/4;
		    	else if(subY == 0)
		    		angle = 0;
		    	else if(subY == 1)
		    		angle = 7*Math.PI/4;
		    }
		    else if (subX == 0) {
		    	if (subY == -1)
		    		angle = Math.PI/2;
		    	else if (subY == 1)
		    		angle = 3*Math.PI/2;
		    }
		    else if (subX == 1) {
		    	if (subY == -1)
		    		angle = 3*Math.PI/4;
		    	else if (subY == 0)
		    		angle = Math.PI;
		    	else if (subY == 1)
		    		angle = 5*Math.PI/4;
		    }
		}
		public int getX() {return x;}		//GET METHODS
		public int getY() {return y;}
		public int getLastX() {return lastX;}
		public int getLastY() {return lastY;}
		public int getType() {return cellType;}
		public int getHops() {return hops;}
		
		public void setType(int type) {cellType = type;}		//SET METHODS
		public void setLastNode(int x, int y) {lastX = x; lastY = y;}
		public void setHops(int hops) {this.hops = hops;}
	}

	
	class Car{
		//THE CENTER OF CAR's HEAD
		private int x;
		private int y;
		private int lastX;
		private int lastY;
		private int w; //WIDTH
		private int l; //LENGTH
		private BufferedImage car;
		private int v=1;
		
		private double angle;
		
		public Car(){
			try {
				this.car = ImageIO.read(getClass().getResourceAsStream("/car.png"));
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			this.angle = 0;
		}
		
		public void initCar(Node node) {
			this.x = node.getX();
			this.y = node.getY();
			this.lastX = node.getLastX();
			this.lastY = node.getLastY();
			
			calAngle();
		}
		
		public void calAngle() {
			int subX = lastX-x;
			int subY = lastY-y;
		    if (subX == -1) {
		    	if (subY == -1)
		    		angle = Math.PI/4;
		    	else if(subY == 0)
		    		angle = 0;
		    	else if(subY == 1)
		    		angle = 7*Math.PI/4;
		    }
		    else if (subX == 0) {
		    	if (subY == -1)
		    		angle = Math.PI/2;
		    	else if (subY == 1)
		    		angle = 3*Math.PI/2;
		    }
		    else if (subX == 1) {
		    	if (subY == -1)
		    		angle = 3*Math.PI/4;
		    	else if (subY == 0)
		    		angle = Math.PI;
		    	else if (subY == 1)
		    		angle = 5*Math.PI/4;
		    }
		}
		
		public AffineTransform transForm(AffineTransform at, int x, int y) {
			if(this.angle  == 0) {
				at.translate(x*CSIZE- at.getTranslateX()-car.getWidth()/2, y*CSIZE-at.getTranslateY()-car.getHeight()/2);
				at.rotate(0);
			}
			if(this.angle == Math.PI/2) {
				at.translate(x*CSIZE- at.getTranslateX()+car.getHeight()/2, y*CSIZE-at.getTranslateY()-car.getWidth()/2);
				at.rotate(Math.PI/2);
			}
			if(this.angle== Math.PI) {
				at.translate(x*CSIZE- at.getTranslateX()+car.getWidth()/2, y*CSIZE-at.getTranslateY()+car.getHeight()/2);
				at.rotate(Math.PI);
			}
			if(this.angle== 3*Math.PI/2) {
				at.translate(x*CSIZE- at.getTranslateX()-car.getHeight()/2, y*CSIZE+at.getTranslateY()+car.getWidth()/2);
				at.rotate(3*Math.PI/2);
			}
		
			
			if(this.angle==Math.PI/4) {
				at.translate(x*CSIZE- at.getTranslateX()+(car.getHeight() -car.getWidth())*Math.sqrt(2)/4, y*CSIZE-at.getTranslateY()-(car.getHeight() +car.getWidth())*Math.sqrt(2)/4);
				at.rotate(Math.PI/4);
			}
			if(this.angle== 3*Math.PI/4) {
				at.translate(x*CSIZE- at.getTranslateX()+(car.getHeight() +car.getWidth())*Math.sqrt(2)/4, y*CSIZE-at.getTranslateY()+(car.getHeight() -car.getWidth())*Math.sqrt(2)/4);
				at.rotate(3*Math.PI/4);
			}
			if(this.angle== 5*Math.PI/4) {
				at.translate(x*CSIZE- at.getTranslateX()-(car.getHeight() -car.getWidth())*Math.sqrt(2)/4, y*CSIZE-at.getTranslateY()+(car.getHeight() +car.getWidth())*Math.sqrt(2)/4);
				at.rotate(5*Math.PI/4);
			}
			if(this.angle== 7*Math.PI/4) {
				at.translate(x*CSIZE- at.getTranslateX()-(car.getHeight() +car.getWidth())*Math.sqrt(2)/4, y*CSIZE-at.getTranslateY()-(car.getHeight() -car.getWidth())*Math.sqrt(2)/4);
				at.rotate(7*Math.PI/4);
			}
			return at;
			
		}
		
		//Setter/Getter
		public int getLastX() {
			return lastX;
		}
		public void setLastX(int lastX) {
			this.lastX = lastX;
		}
		public int getLastY() {
			return lastY;
		}
		public void setLastY(int lastY) {
			this.lastY = lastY;
		}
		public BufferedImage getCar() {
			return car;
		}
		public void setCar(BufferedImage car) {
			this.car = car;
		}
		
		public int getX() {return x;}
		public int getY() {return y;}
		public int getW() {return w;}
		public int getL() {	return l;}
		
		public void setX(int x) {this.x = x;}
		public void setY(int y) {this.y = y;}
		public void setW(int w) {this.w = w;}
		public void setL(int l) {this.l = l;}

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
		}

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}
		
		
	}
	
}
