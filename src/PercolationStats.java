
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

	private static final double CONFIDENCE = 1.96;
	
	private int nooftrials;
	private double[] fracopensites;
	private double mean;
	private double stddev;
	

	
	public PercolationStats(int n, int trials) {
		if (n < 1 || trials < 1) {
			throw new IllegalArgumentException("Error: size of grid and number of trials must be at least 1");
		}
		int size = n; // size of grid
		nooftrials = trials; // number of trials in test
		mean = 0.0;
		stddev = 0.0;
		fracopensites = new double[nooftrials];
		for (int i = 0; i < nooftrials; i++) {
		    Percolation grid = new Percolation(size); // create new Percolation Grid
			while (!(grid.percolates())) {
				int row = StdRandom.uniform(1, size+1);
				int col = StdRandom.uniform(1, size+1);
				grid.open(row, col);
			}
			fracopensites[i] = (double) grid.numberOfOpenSites() / (double) nooftrials;
		}
		this.mean = StdStats.mean(fracopensites);
	    this.stddev = StdStats.stddev(fracopensites);
		
		
		
		
		
	}
	
	public double mean() {
		return mean;
	}
	
	public double stddev() {
		return stddev;
	}
	
	public double confidenceLo() {
		return (mean - (CONFIDENCE*stddev / Math.sqrt(nooftrials)));
	}
	
	public double confidenceHi() {
		
		return (mean + (CONFIDENCE*stddev / Math.sqrt(nooftrials)));
	}

	public static void main(String[] args) {
		int n = StdIn.readInt();
		int t = StdIn.readInt();
		PercolationStats stats = new PercolationStats(n, t);
		System.out.println("mean = " + stats.mean);
		System.out.println("stddev = " + stats.stddev);
		System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");
	}

}
