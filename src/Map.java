import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Map extends JPanel implements MouseListener, MouseMotionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int  cells = 20;
	private static int delay = 30;
	private static double density = cells*cells*0.5;
	
	private final int MSIZE = 600; //Map size
	
	private int CSIZE = MSIZE/cells; //Cell size
	
	//start and finish Node
	private  int startx = -1;
	private  int starty = -1;
	private  int finishx = -1;
	private  int finishy = -1;
	
	private static int tool = 0;
	private int checks = 0;
	private int length = 0;
	private boolean solving = false;
	Random r = new Random();
	Algorithm alg = new Algorithm();
	public JLabel lblChecked = new JLabel("Checked: " + checks);
	public JLabel lblPath = new JLabel("Path Length: " + length);
	
	Node[][] map;
	public Map() {
		addMouseListener(this);
		addMouseMotionListener(this);
		clearMap();
	}
	
	public void paintComponent(Graphics g) {	//repaint
		super.paintComponent(g);
		for(int x = 0; x < cells; x++) {	//paint each node in the grid
			for(int y = 0; y < cells; y++) {
				switch(map[x][y].getType()) {
					case 0:
						g.setColor(Color.GREEN);
						break;
					case 1:
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
						g.setColor(Color.YELLOW);
						break;
				}
				g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
				g.setColor(Color.BLACK);
				g.drawRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);

			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			int x = e.getX()/CSIZE;	
			int y = e.getY()/CSIZE;
			Node current = map[x][y];
			if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
				current.setType(tool);
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
		resetMap();	//reset the map whenever clicked
		try {
			int x = e.getX()/CSIZE;	
			int y = e.getY()/CSIZE;
			Node current = map[x][y];
			switch(tool ) {
				case 0: {	//start node
					if(current.getType()!=2) {	//if not wall
						if(startx > -1 && starty > -1) {	//if start exist set it to empty
							map[startx][starty].setType(3);
							map[startx][starty].setHops(-1);
						}
						current.setHops(0);
						startx = x;	//set the start node x and y
						starty = y;
						current.setType(0);	//set the clicked note to be start
					}
					break;
				}
				case 1: {
					if(current.getType()!=2) {	
						if(finishx > -1 && finishy > -1)	
							map[finishx][finishy].setType(3);
						finishx = x;	
						finishy = y;
						current.setType(1);
					}
					break;
				}
				default:
					if(current.getType() != 0 && current.getType() != 1)
						current.setType(tool);
					break;
			}
			Update();
		} catch(Exception z) {}	
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public void generateMap() {
		clearMap();	
		for(int i = 0; i < density; i++) {
			Node current;
			do {
				int x = r.nextInt(cells);
				int y = r.nextInt(cells);
				current = map[x][y];	//find a random not in the grid
			} while(current.getType()==2);	//if it is already a wall find a new one
			current.setType(2);	//set note to be wall
		}
	}
	
	public void clearMap() {	//clear map
		finishx = -1;	//reset start node and finish node
		finishy = -1;
		startx = -1;
		starty = -1;

		map = new Node[cells][cells];	//create new map of node
		for(int x = 0; x < cells; x++) {
			for(int y = 0; y < cells; y++) {
				map[x][y] = new Node(3,x,y);	//set to empty
			}
		}
		Update();
		reset();	
	}
	
	public void resetMap() {	//reset map
		for(int x = 0; x < cells; x++) {
			for(int y = 0; y < cells; y++) {
				Node current = map[x][y];
				if(current.getType() == 4 || current.getType() == 5)	
					map[x][y] = new Node(3,x,y);	//reset note to an empty node
			}
		}
		if(startx > -1 && starty > -1) {	
			map[startx][starty] = new Node(0,startx,starty);
			map[startx][starty].setHops(0);
		}
		if(finishx > -1 && finishy > -1)
			map[finishx][finishy] = new Node(1,finishx,finishy);
		reset();	
	}
	
	public void reset() {	
		solving = false;
		length = 0;
		checks = 0;
	}
	public void Update() {
		CSIZE = MSIZE/cells;
		lblChecked.setText("Checked: " + checks);
		lblPath.setText("Path Length: " + length);
		repaint();
	}
	public void delay() {	
		try {
			Thread.sleep(delay);
		} catch(Exception e) {}
	}
	public void setTool(int tool) {
		Map.tool= tool;
	}
	
	public Node[][] getMap() {
		return map;
		
	}
	public void setMap(Map newMap) {
		clearMap();
		startx = newMap.getStartx();
		starty = newMap.getStarty();
		finishx = newMap.getFinishx();
		finishy = newMap.getFinishy();
		for(int x = 0; x < cells; x++) {
			for(int y = 0; y < cells; y++) {
				Node current = map[x][y];
				current.setType(newMap.getMap()[x][y].getType());
			}
		}
		this.repaint();
	}
	public static double getDensity() {
		return density;
	}

	public static void setDensity(double density) {
		Map.density = density;
	}

	public int getCells() {
		return cells;
	}

	public static void setCells(int cells) {
		Map.cells = cells;
	}

	public int getDelay() {
		return delay;
	}

	public static void setDelay(int delay) {
		Map.delay = delay;
	}

	public int getCSIZE() {
		return CSIZE;
	}

	public void setCSIZE(int cSIZE) {
		CSIZE = cSIZE;
	}

	public int getStartx() {
		return startx;
	}

	public void setStartx(int startx) {
		this.startx = startx;
	}

	public int getStarty() {
		return starty;
	}

	public void setStarty(int starty) {
		this.starty = starty;
	}

	public int getFinishx() {
		return finishx;
	}

	public void setFinishx(int finishx) {
		this.finishx = finishx;
	}

	public int getFinishy() {
		return finishy;
	}

	public void setFinishy(int finishy) {
		this.finishy = finishy;
	}

	public int getChecks() {
		return checks;
	}

	public void setChecks(int checks) {
		this.checks = checks;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isSolving() {
		return solving;
	}

	public void setSolving(boolean solving) {
		this.solving = solving;
	}

	public int getMSIZE() {
		return MSIZE;
	}

	//Algorithm class extends Thread to run multi-threading with A star and Dijkstra at the same time
	class Algorithm extends Thread{	
		public boolean isAStar;
		public boolean isAStar() {
			return isAStar;
		}

		public void setAStar(boolean isAStar) {
			this.isAStar = isAStar;
		}

		//Dijkstra Algorithm Main Method
		public void Dijkstra() {
			ArrayList<Node> priority = new ArrayList<Node>();	//create a priority que
			priority.add(map[startx][starty]);	//add the start to the que
			while(solving) {
				if(priority.size() <= 0) {	//if the que is 0 then no path can be found
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;	//increment the hops variable
				ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//create an arraylist of nodes that were explored
				if(explored.size() > 0) {
					priority.remove(0);	//remove the node from the que
					priority.addAll(explored);	//add all the new nodes to the que
					Update();
					delay();
				} else {	//if no nodes were explored then just remove the node from the que
					priority.remove(0);
				}
			}
		}
		
		//A Star Algorithm Main Method
		public void AStar() {
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
					delay();
				} else {
					priority.remove(0);
				}
				sortQue(priority);	//sort the priority que
			}
		}
		
		
		public ArrayList<Node> sortQue(ArrayList<Node> sort) {	//sort priority que
			int c = 0;
			while(c < sort.size()) {
				int sm = c;
				for(int i = c+1; i < sort.size(); i++) {
					if(sort.get(i).getEuclidDist(finishx, finishy)+sort.get(i).getHops() < sort.get(sm).getEuclidDist(finishx, finishy)+sort.get(sm).getHops())
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
		
		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//explore neighbors
			ArrayList<Node> explored = new ArrayList<Node>();	//list of nodes that have been explored
			for(int a = -1; a <= 1; a++) {
				for(int b = -1; b <= 1; b++) {
					int xbound = current.getX()+a;
					int ybound = current.getY()+b;
					if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {	//makes sure the node is not outside the grid
						Node neighbor = map[xbound][ybound];
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	//checks if the node is not a wall and that it has not been explored
							explore(neighbor, current.getX(), current.getY(), hops);	//explore the node
							explored.add(neighbor);	//add the node to the list
						}
					}
				}
			}
			return explored;
		}
		
		public void explore(Node current, int lastx, int lasty, int hops) {	//explore a node
			if(current.getType()!=0 && current.getType() != 1)	//check that the node is not the start or finish
				current.setType(4);	//set it to explored
			current.setLastNode(lastx, lasty);	//keep track of the node that this node is explored from
			current.setHops(hops);	//set the hops from the start
			checks++;
			if(current.getType() == 1) {	//if the node is the finish then backtrack to get the path
				backtrack(current.getLastX(), current.getLastY(),hops);
			}
		}
		
		public void backtrack(int lx, int ly, int hops) {	//backtrack
			length = hops;
			while(hops > 1) {	//backtrack from the end of the path to the start
				Node current = map[lx][ly];
				current.setType(5);
				lx = current.getLastX();
				ly = current.getLastY();
				hops--;
			}
			solving = false;
		}

		@Override
		public void run() {
			if(isAStar) {
				AStar();	
			}
			else {
				Dijkstra();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

	}
}
