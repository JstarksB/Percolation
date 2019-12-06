/**
 *  Here we define the PercolationStats class, which creates a Percolation grid, repeatedly finds the percolation threshold value, then calculates the mean and standard deviation for those values
 * @author Jordan Starks-Browning
 * @version 2019-12-6
 */
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

	private static final double CONFIDENCE = 1.96;
	
	private final int nooftrials;
	private final double mean;
	private final double stddev;
	
	/**
	 * Constructor for a class which:
	 * 1) creates a percolation grid of size n with all blocked squares;
	 * 2) opens squares uniformly at random until the grid percolates, storing the proportion of sites open when percolates;
	 * 3) repeats this until there are "trials" values, then calculates the mean, standard deviation, and confidence interval for these trials
	 * @param n - the size of the grid
	 * @param trials - the number of trials
	 */
	public PercolationStats(int n, int trials) {
		if (n < 1 || trials < 1) {
			throw new IllegalArgumentException("Error: size of grid and number of trials must be at least 1");
		}
		int size = n; // size of grid
		nooftrials = trials; // number of trials in test
		double [] fracopensites = new double[nooftrials];
		for (int i = 0; i < nooftrials; i++) {
		    Percolation grid = new Percolation(size); // create new Percolation Grid
			while (!(grid.percolates())) {
				int row = StdRandom.uniform(1, size+1);
				int col = StdRandom.uniform(1, size+1);
				grid.open(row, col);
			}
			fracopensites[i] = (double) grid.numberOfOpenSites() / (double) (size * size);
		}
		this.mean = StdStats.mean(fracopensites);
	    this.stddev = StdStats.stddev(fracopensites);
	}
	
	/**
	 * Returns the (already stored and calculated) mean value
	 * @return the mean
	 */
	public double mean() {
		return mean;
	}
	
	/**
	 * Returns the (already calculated and stored) standard deviation value
	 * @return - standard deviation
	 */
	public double stddev() {
		return stddev;
	}
	
	/**
	 * Calculates the lower bound of the confidence interval (size of which is determined by the CONFIDENCE constant)
	 * @return - lower bound of confidence interval
	 */
	public double confidenceLo() {
		return (mean - (CONFIDENCE*stddev / Math.sqrt(nooftrials)));
	}
	
	/**
     * Calculates the upper bound of the confidence interval (size of which is determined by the CONFIDENCE constant)
     * @return - upper bound of confidence interval
     */
	public double confidenceHi() {
		
		return (mean + (CONFIDENCE*stddev / Math.sqrt(nooftrials)));
	}

	/**
	 * Main function - takes values for size of grid and number of trials from input, then creates a grid, and estimates the value of the percolation threshold value, printing the results
	 * @param args - command line
	 */
	public static void main(String[] args) {
		int n = StdIn.readInt();
		int t = StdIn.readInt();
		PercolationStats stats = new PercolationStats(n, t);
		System.out.println("mean = " + stats.mean);
		System.out.println("stddev = " + stats.stddev);
		System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");
	}
}
