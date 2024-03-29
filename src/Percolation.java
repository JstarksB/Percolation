/**
 *  Here we define the Percolation class, which models a percolation system using the UnionFind algorithms
 * @author Jordan Starks-Browning
 * @version 2019-12-6
 */
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
	
	private static final int opn = 1;
	private static final int contop = 2;
	private static final int conbot = 4;
    
	private final int size; // the length of each dimension of the board
	private final WeightedQuickUnionUF id; // array of weighted tree, id[j] is the next node further up the tree at j
	private int numberofopen; // number of open sites
	private int[] squareprops; // bitwise information on square in grid: 4 = connected to bottom; 2 = connected to top, 1 = open, 0 = closed. 
	private boolean percs; // boolean which is flipped when a square is opened that causes the system to percolate

	/**
	 * Constructor for the percolation grid, initialises a (n x n) grid with all blocked sites
	 * @param n - size of grid
	 */
	public Percolation(int n) {
		if (n < 1) { // n must be non-negative
			throw new IllegalArgumentException("Error: size of percolation table must be non-negative");
		}
		size = n; // set the size of the grid
		numberofopen = 0; // starts with no open squares
		percs = false; // system does not percolate at start
		squareprops = new int[(n*n)]; // create array of length n*n, showing properties of the square
		for (int i = 0; i < n*n; i++) {
		    squareprops[i] = 0; // initialise all squares as closed and not connected to top or bottom
		}
		for (int i = 0; i < n; i++) {
            squareprops[i] = (squareprops[i] | contop); // add property "connected to top" to top row squares
        }
		for (int i = (n-1)*n; i < (n*n); i++) {
		    squareprops[i] = (squareprops[i] | conbot); // add property "connected to bottom" to bottom row squares
		}
		id = new WeightedQuickUnionUF((n*n)); // create weighted UF tree
	}
	
	/**
	 * open - Opens the square at a given row and column value if it is not open already. If already open, the square remains open.
	 * @param row
	 * @param col
	 * @throws IllegalArgumentException
	 */
	public void open(int row, int col) {
		if (row < 1 || row > size || col < 1 || col > size) {
			throw new IllegalArgumentException("Error: row and column must be in range of table (between 1 and " + size);
		}
		int flatcoord = twoToOneDim(row, col); // convert the row/column coordinates into a "flat coordinate" to use in the 1D array
		if ((squareprops[flatcoord] & opn) == 0) {   // if 'open' bit is flipped, do nothing, otherwise continue 
		    squareprops[flatcoord] = (squareprops[flatcoord] | opn); // set square to be open
		    numberofopen++; // increase number of open sites
		    if (size != 1) { // if size == 1 then we are done
		        if (row == 1) { // for a square in the top row
	                validConnection(flatcoord, flatcoord+size); // connect to square underneath
	            } else if (row == size) { // for a square in the bottom row
	                validConnection(flatcoord, flatcoord - size); // connect to square above
	            } else { // for a square in the middle of the grid, connect to squares above and below
	                validConnection(flatcoord, flatcoord+size);
	                validConnection(flatcoord, flatcoord-size);
	            }
	            if (col == 1) { // for a square in the left edge
	                validConnection(flatcoord, flatcoord+1); // connect to square on right
	            } else if (col == size) { // for a square in the right edge
	                validConnection(flatcoord, flatcoord-1); // connect to square on left
	            } else { // for a square in the middle, connect to both sides
	                validConnection(flatcoord, flatcoord+1);
	                validConnection(flatcoord, flatcoord-1);
	            }
		    } else {
		        percs = true;
		    }
		}
	}
	
	/**
	 * isOpen - Determines whether a particular square in the grid is open
	 * @param row 
	 * @param col
	 * @return a boolean describing whether the square is open
	 * @throws IllegalArgumentException
	 */
	public boolean isOpen(int row, int col) {
		if (row < 1 || row > size || col < 1 || col > size) {
			throw new IllegalArgumentException("Error: row and column must be in range of table (between 1 and " + size);
		}
		int flatcoord = twoToOneDim(row, col);		
		return (squareprops[flatcoord] & opn) == opn;
	}
	
	/**
	 * isFull - Determines whether a particular square in the grid is full (connected to the top of the grid and open)
	 * @param row
	 * @param col
	 * @return a boolean - whether or not the square is full
	 * @throws IllegalArgumentException
	 */
	public boolean isFull(int row, int col) {
		if (row < 1 || row > size || col < 1 || col > size) {
			throw new IllegalArgumentException("Error: row and column must be in range of table (between 1 and " + size);
		}
		int flatcoord = twoToOneDim(row, col);
		return (squareprops[id.find(flatcoord)] & (opn + contop)) == (opn + contop);
	}
	
	/**
	 * numberOfOpenSites - returns the number of open squares in the grid
	 * @return (int) the number of open sqaures in the grid
	 */
	public int numberOfOpenSites() {
		return numberofopen;
	}
	
	/**
	 * percolates - returns whether the grid percolates (ie, whether the top row is connected to the bottom row)
	 * @return boolean - whether the grid percolates or not
	 */
	public boolean percolates() {
		return percs; // returns the percolates value, which is set to true if a square is opened which causes the system to percolate
	}
	
	/**
	 * Converts a 2D co-ordinate to a 1D index
	 * @param row
	 * @param col
	 * @return 1D index of coordinate
	 */
	private int twoToOneDim(int row, int col) {
		return (row-1)*size + (col-1);
	}
	
	
	/**
	 * Checks whether two points can be connected, then connects them, and shares over their statuses (connected to top / bottom)
	 * @param point1 (already known to be open before calling)
	 * @param point2 (point to check, may not be open)
	 */
	private void validConnection(int point1, int point2) {
	    if ((squareprops[point2] & opn) == opn) { // if point2 is open (point1 must be checked as open before using fn)
	        int mask = (squareprops[id.find(point1)] | squareprops[id.find(point2)]); // collect the status of the root of each point's tree
	        id.union(point1, point2); // connect the two points into a single tree
	        squareprops[id.find(point1)] = mask; // update the status of this single tree
	        if (mask == (opn + contop + conbot) && !percs) {
	            percs = true;
	        }
	    }
	}
	
	public static void main(String[] args) {
		// test space
	}
}
