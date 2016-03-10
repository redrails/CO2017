package CO2017.exercise2.mic7;

import java.lang.Runnable;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class PedHandler implements Runnable {

	private ThreadPoolExecutor traffic;
	private ZebraCrossing zebraCrossing;
	private String file;

	public PedHandler(ThreadPoolExecutor e, ZebraCrossing z, String f) {

		this.traffic = e;
		this.zebraCrossing = z;
		this.file = f;

	}

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
			Person person = new Person(zebraCrossing, delay);
			traffic.execute(person);

		}

	file.close();
	
	} catch (NoSuchFileException e) {
		System.exit(1);
	} catch (IOException e) {
		System.err.println(e);
		System.exit(1);
	} catch (Exception e) {

	}

}


}