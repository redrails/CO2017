package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

/**
  * This class consists of creating a person object that will act as a
  * pedestrian and will allow to cross in the ZebraCrossing.
  * This class implements runnable which allows the class to be a thread itself.
  * @author mic7
  * @see 	Runnable
  */

public class Person implements Runnable {

	/**
	  * Setting some variables for the class to use.
	  * static ID to increment for id.
	  * ZebraCrossing instance is required to execute the ped.
	  */

	static int ID = 0;
	private ZebraCrossing zebraCrossing;
	private int delay;
	private int id;

	/**
	* @param zc ZebraCrossing to place the person in.
	* @param d the delay that the pedestrian has.
	*/

	public Person(ZebraCrossing zc, int d) {

		this.zebraCrossing = zc;
		this.delay = d;
		this.id = ++Person.ID;

	}

	/**
	  * Record that the pedestrian is arriving and output to console.
	  * Record that the pedestrian starts crossing and output to console.
	  * Sleep the thread with the appropriate delay.
	*/

	public void run() {

		zebraCrossing.arrive(this);
		System.out.println(toString()+" has arrived");
		zebraCrossing.startCrossing(this);
		System.out.println(toString()+" has started to cross");
		try {
			Thread.sleep(500*delay);
		} catch (Exception e){
			System.out.println(e);
		}
		zebraCrossing.finishCrossing(this);
		System.out.println(toString()+" has finished crossing");

	}

	/**
	  * Print out the person id for use with printing.
	*/

	public String toString() {
		return "P"+id;
	}

}