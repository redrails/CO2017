package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

/**
  * This class will read the file for car data and make objects accordingly.
  * @author mic7
*/
public class CarHandler implements Runnable {

	private ThreadPoolExecutor traffic;
	private ZebraCrossing zebraCrossing;
	private String file;
	private boolean heading;

	/**
	  * @param e - ThreadPoolExecutor for holding the threads that will be used and executed.
	  * @param z - ZebraCrossing the ZebraCrossing resource being used.
	  * @param f - The filename as string.
	  * @param h - The heading of the car.
	  */
	public CarHandler(ThreadPoolExecutor e, ZebraCrossing z, String f, boolean h) {

		this.traffic = e;
		this.zebraCrossing = z;
		this.file = f;
		this.heading = h;

	}

	/**
	  * The run method which will read the file and make objects.
	  * @see Scanner
	  * By using Scanner we can read the files, the data is seperated by the ':' delimiter so we filter this
	  * At each line we read, we add a pause*100 and then create the person object with the appropriate params.
	  * It will be essential to execute these objects in the ThreadPoolExecutor because this will add it to the resource being used.
	  */

	public void run() {

		String fname = file;
		Path fpath = Paths.get(fname);
		try (Scanner file = new Scanner(fpath)) {

			int pause, delay;

			while (file.hasNextLine()) {

				Scanner line = new Scanner(file.nextLine());
				line.useDelimiter(":");
				pause = line.nextInt();
				delay = line.nextInt();
				line.close();
				Thread.sleep(pause*100);
				Car car = new Car(zebraCrossing, delay, heading);
				traffic.execute(car);	
			}
			file.close();

		} catch (NoSuchFileException e) {	// Catch missing file exceptions.
			System.exit(1);
		} catch (IOException e) {			// Catch input/output exceptions.
			System.err.println(e);
			System.exit(1);
		} catch (Exception e){
		}

	}

}
