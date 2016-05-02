package CO2017.exercise3.mic7.client;

import java.net.*;
import java.io.*;

/**
  * The GuessGameClient class will be the client class used to connect to the server.
  *	It will handle the transmission of guesses and server outputs from and to the client.
  */
public class GuessGameClient {
	
	public static void main(String[] args) throws IOException {
		// Try connecting to the server with given arguments, being server:port
		try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {

			// Upon connection there is an initial message.
			System.out.println("Connected to " + server.getInetAddress());

			// input reader for the server messages sent to the client.
			BufferedReader in = new BufferedReader ( new InputStreamReader(server.getInputStream(), "UTF-8"));

			// output to the server
			Writer out = new OutputStreamWriter(server.getOutputStream());

			// Input to the server from client
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			String guess = "";
			String responseFilter[]; // The response will be to split up for output of protocol correctly.
			
			// Keep asking for guesses until game is over.
			while(true) {
				String response = in.readLine();		// Read the outputs from server to client.
				responseFilter = response.split(":");	// Split the response using the : delimiter
				if(responseFilter[0].equals("START")){
					double time = (double)Integer.parseInt(responseFilter[2])/1000;
					System.out.println("START range is 1.." + responseFilter[1] + ", time allowed is "+ time + "s");
				}
				// All possible outputs from server to client. Handle them and output appropriately.
				if(responseFilter[0].equals("LOW")){	
					System.out.println("Turn "+responseFilter[2]+": "+guess+" was LOW, "+ responseFilter[1] + " remaining");
				} else if(responseFilter[0].equals("HIGH")){
					System.out.println("Turn "+responseFilter[2]+": "+guess+" was HIGH, "+ responseFilter[1] + " remaining");
				} else if(responseFilter[0].equals("WIN")){
					System.out.println("Turn "+responseFilter[1]+": WIN");
					System.out.println("Game Over - WIN");
					server.close();
					System.exit(1);
				} else if(responseFilter[0].equals("LOSE")){
					System.out.println("Turn "+responseFilter[1]+": LOSE");
					System.out.println("Game Over - LOSE");
					server.close();
					System.exit(1);
				} else if(responseFilter[0].equals("ERR")){
						System.out.println("Turn "+responseFilter[1]+": ERR");
				}
				// Ask for a guess from the client.
				System.out.print("Guess a number ");
				guess = stdin.readLine(); // Read guess into the server and send it.
				out.write(String.format("%s%n",guess));
				out.flush();
			}
		} catch (SocketException e){	// See if server closes.
			System.out.println("Connection closed");
		} catch (UnknownHostException e) {	// Catch exception if the host being connected to doesn't exist.
			System.err.println("Unknown host: " + args[1]);
			System.err.println(e);
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
	}

}