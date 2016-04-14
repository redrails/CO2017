import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class GuessGameServer {

	private static ThreadPoolExecutor ex;
	private static BufferedReader in;

	public static void main(String[] args) throws IOException{
		int port = Integer.parseInt(args[0]);
		int max = Integer.parseInt(args[1]);
		int time = Integer.parseInt(args[2]);
		ex = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		try (ServerSocket server = new ServerSocket(port)) {
			System.out.println("Starting GuessGame server ("+max+", "+time+") on port "+port);
			System.out.println("Waiting for a connection...");
			while (true) {
				Socket client = server.accept();

				InetAddress clientAddress = client.getInetAddress();

				GuessGameServerHandler ggsh = new GuessGameServerHandler(max, time, client);
				ex.execute(ggsh);
				System.out.printf("%s connection: %s%n", ggsh.id, clientAddress);
				System.out.printf("%s start watching %n", ggsh.id);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public GuessGameServer(){

	}

}