package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.util.concurrent.*;

/**
  * This class will run the threads and execute the actual program.
  * The class will use static variables for ZebraCrossing and ThreadPoolExecutor.
  * @author mic7
  */
public class Controller implements Runnable {

	private static ZebraCrossing zc;
	private static ThreadPoolExecutor e;


	public Controller(){
	}

	/** 
	  * Check how many threads are running and if there are any then we print out current status of ZebraCrossing.
	  * Sleep the thread for 1 second and then recheck.
	  */
	public void run(){

		while(e.getActiveCount() > 0){
			if(zc.isChanged()){
				System.out.println(zc.toString());
			}
			try { 
				Thread.sleep(1000);
			} catch (InterruptedException e) { }
		}
	}

	/**
	  *	Record arguments for the files.
	  * args[0] pedestrian file to feed in.
	  * args[1] car up file to feed in.
	  * args[2] car down file to feed in.
	  */

	public static void main(String[] args) {

		String pedestrian = args[0];
		String headingUp = args[1];
		String headingDown = args[2];

		e = (ThreadPoolExecutor) Executors.newCachedThreadPool();				// Using the resource for the ThreadPoolExecutor to hold the threads.

		zc = new ZebraCrossing();												// ZebraCrossing resource instance to feed to other classes.
		// zc = new FairZebraCrossing();										// FairZebraCrossing execution.

		PedHandler pedHandler = new PedHandler(e, zc, pedestrian);				// Make a pedHandler for using the pedestrian file (args[0])
		e.execute(pedHandler);													// Using the ThreadPoolExecutor instance that is holding the threads, executute this handler thread.

		CarHandler carUpHandler = new CarHandler(e, zc, headingUp, true);		// Make a CarHandler for the Car up data (args[1])
		e.execute(carUpHandler);												// Same thing as before.

		CarHandler carDownHandler = new CarHandler(e, zc, headingDown, false); 	// Same for down file.
		e.execute(carDownHandler);


		Controller controller = new Controller();								// Controller instance to run.
		controller.run();														// Call run method for execution of threads and the printing, used to monitor.

		// while(e.getActiveCount() > 0){}

		try {
			e.shutdown();														// When all of the threads are terminated we can close the executor.
			e.awaitTermination(5L,TimeUnit.SECONDS);							// Await the threads to terminate.
		} catch(InterruptedException e) {
		}
		System.out.println("All threads have terminated.");						// Show that the threads have terminated. 

	}
}
