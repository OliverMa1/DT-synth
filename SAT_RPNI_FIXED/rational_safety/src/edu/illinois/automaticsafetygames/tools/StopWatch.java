package edu.illinois.automaticsafetygames.tools;

/**
 * Stop watch class.
 * 
 * @author Daniel Neider
 *
 */
public class StopWatch {

	/**
	 * Divide by this constant to obtain elapsed time in seconds.
	 */
	public static final long IN_SECONDS = 1000;

	/**
	 * Stores the current time when start is called.
	 */
	private long start;

	/**
	 * Stores the elapsed time.
	 */
	private long elapsedTime;

	/**
	 * Tracks whether the watch is running
	 */
	private boolean isRunning;

	/**
	 * Creates a new instance of the stop watch.
	 */
	public StopWatch() {

		// Initialize members
		start = elapsedTime = 0;
		isRunning = false;

	}

	/**
	 * Creates a new instance of the stop watch, where the parameter
	 * <code>started</code> indicates whether the watch is started immediately.
	 * 
	 * @param started
	 *            indicates whether the watch is started immediately.
	 */
	public StopWatch(boolean started) {

		// Initialize members
		elapsedTime = 0;
		
		if(started) {
			
			start = System.currentTimeMillis();
			isRunning = true;
			
		} else {
			
			start = 0;
			isRunning = false;
			
		}
		
	}

	/**
	 * Starts the stop watch.
	 */
	public void start() {

		if (!isRunning) {

			start = System.currentTimeMillis();
			isRunning = true;

		}

	}

	/**
	 * Stops the watch and returns the elapsed time.
	 * 
	 * @return Returns the lapsed time.
	 */
	public long stop() {

		if (isRunning) {

			elapsedTime += System.currentTimeMillis() - start;
			isRunning = false;

		}

		return elapsedTime;

	}

	/**
	 * Returns the elapsed time of this watch.
	 * 
	 * @return the elapsed time.
	 */
	public long getElapsedTime() {

		// Watch is running
		if (isRunning) {
			return elapsedTime + (System.currentTimeMillis() - start);
		}

		// Watch is not running
		else {
			return elapsedTime;
		}

	}

}
