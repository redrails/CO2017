import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;

public class GuessGameServerHandler implements Runnable{

	static char ID = 'A';
	private GameState gs;
	Socket client;
	Writer out;
	BufferedReader in;
	private int mv;
	private long tl;
	public char id;

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