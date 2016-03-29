import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;

public class GuessGameServerHandler implements Runnable{

	static char ID = 'A';
	private GameState gs;
	private Socket client;
	private Writer out;
	private BufferedReader in;
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
		gs = new GameState(mv, tl, this);
	}

	public void run(){
		try{
			out.write(String.format("START:%d:%d%n", mv, tl));
			out.flush();
			System.out.println(this.id + " target is " + gs.getTarget());
			while(gs.finished() == false){
				try {
					// System.out.println("++++++++++++++++++++++"+gs.getRemainingTime());
					gs.guess(Integer.parseInt(in.readLine()));
					out.write(gs.toString());
					out.flush();
					System.out.print(this.id +" "+ gs.toString());
					if(gs.finished()){
						out.write("close\n");
						out.flush();
					}
				} catch(Exception e){
					out.write("Entered value was not a number, please try again.\n");
					out.flush();
				}
			}
			out.write("Game Over\n");
			out.flush();
			client.close();
			// System.out.println("Game Over");

		} catch (Exception e){
			e.printStackTrace();
		}

	}

} 