import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Map extends JPanel implements MouseListener, MouseMotionListener{	//MAP CLASS
	
	private int cells = 20;
	private int delay = 30;
	private final int MSIZE = 600;
	private int CSIZE = MSIZE/cells;
	private  int startx = -1;
	private  int starty = -1;
	private  int finishx = -1;
	private  int finishy = -1;
	private static int tool = 0;
	private int checks = 0;
	private int length = 0;
	private boolean solving = false;
	Algorithm alg = new Algorithm(); 
	
	public int getCells() {
		return cells;
	}

	public void setCells(int cells) {
		this.cells = cells;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
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

	Node[][] map;
	public Map() {
		addMouseListener(this);
		addMouseMotionListener(this);
		clearMap();
	}
	
	public void paintComponent(Graphics g) {	//REPAINT
		super.paintComponent(g);
		for(int x = 0; x < cells; x++) {	//PAINT EACH NODE IN THE GRID
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
					}
					break;
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
		repaint();
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
	
	public void reset() {	//RESET METHOD
		solving = false;
		length = 0;
		checks = 0;
	}
	public void Update() {
		CSIZE = MSIZE/cells;
		repaint();
	}
	public void delay() {	//DELAY METHOD
		try {
			Thread.sleep(delay);
		} catch(Exception e) {}
	}
	public void setTool(int tool) {
		this.tool= tool;
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

	class Algorithm implements Runnable{	
		public boolean isAStar;
		public boolean isAStar() {
			return isAStar;
		}

		public void setAStar(boolean isAStar) {
			this.isAStar = isAStar;
		}

		//DIJKSTRA WORKS BY PROPAGATING OUTWARDS UNTIL IT FINDS THE FINISH AND THEN WORKING ITS WAY BACK TO GET THE PATH
		//IT USES A PRIORITY QUE TO KEEP TRACK OF NODES THAT IT NEEDS TO EXPLORE
		//EACH NODE IN THE PRIORITY QUE IS EXPLORED AND ALL OF ITS NEIGHBORS ARE ADDED TO THE QUE
		//ONCE A NODE IS EXLPORED IT IS DELETED FROM THE QUE
		//AN ARRAYLIST IS USED TO REPRESENT THE PRIORITY QUE
		//A SEPERATE ARRAYLIST IS RETURNED FROM A METHOD THAT EXPLORES A NODES NEIGHBORS
		//THIS ARRAYLIST CONTAINS ALL THE NODES THAT WERE EXPLORED, IT IS THEN ADDED TO THE QUE
		//A HOPS VARIABLE IN EACH NODE REPRESENTS THE NUMBER OF NODES TRAVELED FROM THE START
		public void Dijkstra() {
			ArrayList<Node> priority = new ArrayList<Node>();	//CREATE A PRIORITY QUE
			priority.add(map[startx][starty]);	//ADD THE START TO THE QUE
			while(solving) {
				if(priority.size() <= 0) {	//IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;	//INCREMENT THE HOPS VARIABLE
				ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
				if(explored.size() > 0) {
					priority.remove(0);	//REMOVE THE NODE FROM THE QUE
					priority.addAll(explored);	//ADD ALL THE NEW NODES TO THE QUE
					Update();
					delay();
				} else {	//IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
					priority.remove(0);
				}
			}
		}
		
		//A STAR WORKS ESSENTIALLY THE SAME AS DIJKSTRA CREATING A PRIORITY QUE AND PROPAGATING OUTWARDS UNTIL IT FINDS THE END
		//HOWEVER ASTAR BUILDS IN A HEURISTIC OF DISTANCE FROM ANY NODE TO THE FINISH
		//THIS MEANS THAT NODES THAT ARE CLOSER TO THE FINISH WILL BE EXPLORED FIRST
		//THIS HEURISTIC IS BUILT IN BY SORTING THE QUE ACCORDING TO HOPS PLUS DISTANCE UNTIL THE FINISH
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
				sortQue(priority);	//SORT THE PRIORITY QUE
			}
		}
		
		public ArrayList<Node> sortQue(ArrayList<Node> sort) {	//SORT PRIORITY QUE
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
		
		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//EXPLORE NEIGHBORS
			ArrayList<Node> explored = new ArrayList<Node>();	//LIST OF NODES THAT HAVE BEEN EXPLORED
			for(int a = -1; a <= 1; a++) {
				for(int b = -1; b <= 1; b++) {
					int xbound = current.getX()+a;
					int ybound = current.getY()+b;
					if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
						Node neighbor = map[xbound][ybound];
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
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
			if(current.getType() == 1) {	//IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
				backtrack(current.getLastX(), current.getLastY(),hops);
			}
		}
		
		public void backtrack(int lx, int ly, int hops) {	//BACKTRACK
			length = hops;
			while(hops > 1) {	//BACKTRACK FROM THE END OF THE PATH TO THE START
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
			
		}


	}
}
