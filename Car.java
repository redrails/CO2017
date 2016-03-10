package CO2017.exercise2.mic7;

import java.lang.Runnable;

public class Car implements Runnable {

	static int ID = 0;
	private ZebraCrossing zebraCrossing;
	private int delay;
	private boolean heading;
	private int id;
	private String direction;

	public Car(ZebraCrossing zc, int d, boolean h) {
		this.id = ++Car.ID;
		this.zebraCrossing = zc;
		this.delay = d;
		this.heading = h;

	}

	public boolean getHeading() {
		return heading;
	}

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