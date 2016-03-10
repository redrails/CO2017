package CO2017.exercise2.mic7;

import java.lang.Runnable;

public class Car implements Runnable {

/**
  * This class consists of creating a car object that will act as a
  * car and will allow to cross in the ZebraCrossing.
  * This class implements runnable which allows the class to be a thread itself.
  * @author mic7
  * @see 	Runnable
  * Add an ID that will correspond to the car Id.
  * The heading defines whether the car is going up or down, true = up; false = down;
  */

	static int ID = 0;
	private ZebraCrossing zebraCrossing;
	private int delay;
	private boolean heading;
	private int id;
	private String direction;

	public Car(ZebraCrossing zc, int d, boolean h) {

	/**
	  * Setting some variables for the class to use.
	  * static ID to increment for id.
	  * ZebraCrossing instance is required to execute the ped.
	  */

		this.id = ++Car.ID;
		this.zebraCrossing = zc;
		this.delay = d;
		this.heading = h;

	}

	/**
	  * @return heading of the car.
	  */
	public boolean getHeading() {
		return heading;
	}

	/**
	  * The run method will run the car thread. It will sleep the thread at each point of making the car move.
	  * The console will record the car actions.
	  */

	public void run() {
		// true - up, false - down
		if(heading){
			direction = "up";
		} else {
			direction = "down";
		}

		zebraCrossing.arrive(this);
		System.out.println(toString() + " has arrived to go " + direction);
		zebraCrossing.startCrossing(this);
		System.out.println(toString() + " has started to drive " + direction);
		try {
			Thread.sleep(150*delay);
		} catch (Exception e){
			System.out.println(e);
		}
		zebraCrossing.finishCrossing(this);
		System.out.println(toString() + " has finished driving " + direction);
	}

	/**
	  * @return Car heading and id
	*/

	public String toString() {
		String d;
		if(direction.equals("up")){
			d = "^";
		} else {
			d = "v";
		}
		return "C"+d+this.id;
	}

}