import java.net.*;
import java.io.*;

public class GuessGameClient {
	
	public static void main(String[] args) throws IOException {
		try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {

			System.out.println("Connected to " + server.getInetAddress());
			BufferedReader in = new BufferedReader ( new InputStreamReader(server.getInputStream(), "UTF-8"));
			Writer out = new OutputStreamWriter(server.getOutputStream());
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			String guess;

			while(server.isConnected()){
				System.out.println(in.readLine());
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