package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.util.concurrent.*;

public class Controller implements Runnable {

	private static ZebraCrossing zc;
	private static ThreadPoolExecutor e;


	public Controller(){
	}

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

	public static void main(String[] args) {

		String pedestrian = args[0];
		String headingUp = args[1];
		String headingDown = args[2];

		e = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		zc = new ZebraCrossing();

		PedHandler pedHandler = new PedHandler(e, zc, pedestrian);
		e.execute(pedHandler);

		CarHandler carUpHandler = new CarHandler(e, zc, headingUp, true);
		e.execute(carUpHandler);

		CarHandler carDownHandler = new CarHandler(e, zc, headingDown, false);
		e.execute(carDownHandler);


		Controller controller = new Controller();
		controller.run();

		// while(e.getActiveCount() > 0){}

		try {
			e.shutdown();
			e.awaitTermination(5L,TimeUnit.SECONDS);
		} catch(InterruptedException e) {
		}
		System.out.println("All threads have terminated.");

	}
}
