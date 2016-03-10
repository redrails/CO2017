package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class Person implements Runnable {

	static int ID = 0;
	private ZebraCrossing zebraCrossing;
	private int delay;
	private int id;

	public Person(ZebraCrossing zc, int d) {

		this.zebraCrossing = zc;
		this.delay = d;
		this.id = ++Person.ID;

	}

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

	public String toString() {
		return "P"+id;
	}

}