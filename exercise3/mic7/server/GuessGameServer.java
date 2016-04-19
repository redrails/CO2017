import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
  * @class GuessGameServer
  *	This class will open a server on a given port and accept connections.
  * Once a connection is made, it will instantiate a GuessGameServerHandler to handle each client.
  * This means we can have 1->* connections.
*/

public class GuessGameServer {

	private static ThreadPoolExecutor ex;	// To use for adding each handler to a thread for simultaneous running.
	private static BufferedReader in;		// Reading information into the server.

	public static void main(String[] args) throws IOException{
		int port = Integer.parseInt(args[0]);	// Specified port on running server.
		int max = Integer.parseInt(args[1]);	// Specified max value on running server.
		int time = Integer.parseInt(args[2]);	// Specified time limit on running server.
		ex = (ThreadPoolExecutor) Executors.newCachedThreadPool();	// ThreadPool instance.
		try (ServerSocket server = new ServerSocket(port)) {	// Start the server on the given port. 
			System.out.println("Starting GuessGame server ("+max+", "+time+") on port "+port); // Out message.
			System.out.println("Waiting for a connection...");	// Wait for a client to connect. 
			while (true) {							// Run the server and keep waiting for a client to connect. 
				Socket client = server.accept();	// When a client connect accept the connection.

				InetAddress clientAddress = client.getInetAddress();	// Address of the server host.

				GuessGameServerHandler ggsh = new GuessGameServerHandler(max, time, client);	// Create a new GuessGameServerHandler to handle the client.
				ex.execute(ggsh);	// Add that handler to the theadpool so that we can run it in a thread.
				System.out.printf("%s connection: %s%n", ggsh.id, clientAddress);	// Server output info including client id and address
				System.out.printf("%s start watching %n", ggsh.id);					// Server output into
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public GuessGameServer(){

	}

}