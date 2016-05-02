package CO2017.exercise3.mic7.server;

import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;

/**	
  * This class will handle each client instance that runs.
  * This means that the server can handle many client instances.
  * Each client is assigned an ID, and then a GameState is created for the game instance itself.
*/

public class GuessGameServerHandler implements Runnable{
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	static char ID = 'A';	// Client unique ID.
	private GameState gs;	// GameState object.
	Socket client;			// The socket of the client.
	Writer out;				// output stream.
	BufferedReader in;		// input stream.
	private int mv;			// Maximum value i.e. upper range.
	private long tl;		// Time limit of the game.
	public char id;			// Client ID.

	/**
	  * @method GuessGameServerHandler contructor that takes the params given on connection from client.
	  *	@param mv - Maximum value of the randomly generated number.
	  * @param tl - Time limit of the game.
	  *	@param cl - Socket of the client that connection.
	  * This method instantiates the input/output buffers to transmit messages and also sets initial params.
	*/

	public GuessGameServerHandler(int mv, long tl, Socket cl){
		this.id = ID++;
		try {
			out = new OutputStreamWriter(cl.getOutputStream());
			in = new BufferedReader (new InputStreamReader(cl.getInputStream(), "UTF-8"));
			this.mv = mv;
			this.tl = tl;
			this.client = cl;

		} catch (IOException e) {
			System.err.printf("Failed to create Data streams to %s%n",
			cl.getInetAddress());
			System.err.println(e);
			System.exit(1);
		}
	}

	/**
	  * @method run - Concurrent thread to handle client
	  *	Creates new thread that the GameState will run in and sends an initial start message to
	  *	the client from the protocol specified.
	*/

	public void run(){
		try{
			Thread gs = new Thread(new GameState(mv, tl, out, this));
			out.write(String.format("START:%d:%d%n", mv, tl));
			out.flush();
			gs.start();
		} catch (Exception e){
			System.out.println("An unknown error occurred.");
		}

	}

} 