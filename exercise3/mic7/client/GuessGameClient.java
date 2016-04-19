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
			String responseFilter[];
			while(true) {
				// System.out.println(in.readLine());
				String response = in.readLine();
				responseFilter = response.split(":");
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
				System.out.print("Guess a number ");
				guess = stdin.readLine();
				out.write(String.format("%s%n",guess));
				out.flush();
			}
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