import java.net.*;
import java.io.*;

public class GuessGameClient {
	
	public static void main(String[] args) throws IOException {
		try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {

			System.out.println("Connected to " + server.getInetAddress());
			BufferedReader in = new BufferedReader ( new InputStreamReader(server.getInputStream(), "UTF-8"));
			Writer out = new OutputStreamWriter(server.getOutputStream());
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			String guess = "";

			while(server.isConnected()){
				// System.out.println(in.readLine());
				String response = in.readLine();
				String [] responseFilter = response.split(":");
				if(responseFilter[0].equals("LOW")){
					System.out.println("Turn "+responseFilter[2]+": "+guess+" was LOW, "+ responseFilter[1] + " remaining");
				} else if(responseFilter[0].equals("HIGH")){
					System.out.println("Turn "+responseFilter[2]+": "+guess+" was HIGH, "+ responseFilter[1] + " remaining");
				} else if(responseFilter[0].equals("WIN")){
					System.out.println("Turn "+responseFilter[1]+": WIN");
				} else if(responseFilter[0].equals("LOSE")){
					System.out.println("Turn "+responseFilter[1]+": LOSE");
				} else if(responseFilter[0].equals("ERR")){
						System.out.println("Please enter a valid number");
				}
				System.out.print("Guess a number ");
				guess = stdin.readLine();
				out.write(String.format("%s%n",guess));
				out.flush();
			}
			System.out.println("Client shutdown");
			server.close();
		} catch (SocketException e){
			System.out.println("Connection closed");
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + args[1]);
			System.err.println(e);
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
	}

	public GuessGameClient(){

	}

}