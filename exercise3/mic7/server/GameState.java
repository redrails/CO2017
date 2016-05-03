package CO2017.exercise3.mic7.server;

import java.io.Writer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.*;

/**
  * The GameState class will handle the game itself for each client.
  * The methods will be called from this class to handle each clients guesses.
  */

public class GameState implements Runnable {
	private static Random RANDGEN;	// Calling random class for generating random number.
	final static int MINVAL = 1;	// The mininum value i.e. the lower bound of random range to generate.
	private boolean isFinished;		// Status if the game is finished.
	private int guessAmount;		// The amount of guesses made by the user.
	private int targetValue;		// The correct number that should be guessed.
	private long timeRemaining;		// Time remaining for the guess to be made.
	private int mv;					// Maximum value i.e. the upper bound of random range to generate.
	private Writer op;				// The writer to stream outputs.
	// private boolean isChanged;		
	GuessGameServerHandler ggs;		// The handler object that this GameState belongs to.
	private int lastGuess;			// The last number that was guessed.
	private long timeLimit;			// Timelimit specified.

	/**
	  * @method GameState - The constructor sets default parameters.
	  * @param mv - maximum value.
	  * @param tl - timelimit.
	  * @param o - the writer that outputs protocols.
	  * @param ggsh - the GuessGameServerHandler object that this GameState belongs to.
	  */

	public GameState (int mv, long tl, Writer o, GuessGameServerHandler ggsh){
		this.mv = mv;
		this.timeLimit = tl + TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
		this.isFinished = false;
		this.guessAmount = 0;
		RANDGEN = new Random();
		this.targetValue = RANDGEN.nextInt(mv)+1;
		this.op = o;
		this.ggs = ggsh;
		this.timeRemaining = tl;
	}

	// Method to see if game is finished.
	public boolean finished(){
		return isFinished;
	}

	// Method to see how many guesses have been made.
	public int getGuesses(){
		return guessAmount;
	}

	// Method to check the target guess i.e. the correct guess.
	public int getTarget(){
		return targetValue;
	}

	// Method to see how much time is remaining.
	public long getRemainingTime(){
		return timeRemaining;
	}

	/**
	  * @method guess - takes a guess that a user makes and does some operations.
	  *	@param g - the guess that the user makes.
	  * Checks if the guess is correct, if it is then the game is finished. If time is up the game is finished.
	  * increments the guess amount respectively. 
	  */
	public void guess(int g){
		this.lastGuess = g;
		if(g == targetValue || getRemainingTime()<=0){
			this.isFinished = true;
		}
		this.guessAmount++;
	}

	/**
	  * @method run - runs the instance of the Game
	  * This method includes all the functionality of the game including mostly the protocols.
	  * The outputs on this method are the outputs on the client console. This methis also handles the timer Thread.
	  */
	public void run(){
		Thread timerThread = new Thread(new Timer(this));		// Instantiate the timer in another Thread for concurrency.
		timerThread.start();

		System.out.println(ggs.id + " target is " + this.getTarget());

		// While the game isn't finished we can keep asking for guesses and printing messages.
		while(this.finished() == false){
			try {
				this.guess(Integer.parseInt(ggs.in.readLine()));	// See if the entered number is actually a number
				ggs.out.write(this.toString());
				ggs.out.flush();


				String [] serverstatus = this.toString().split(":");	// From the server response, filter all the information that the protocol includes
				
				// The guards for the different game states possible, each prints a user message as required. 

				if(serverstatus[0].equals("HIGH")){
					System.out.print(ggs.id +" "+ lastGuess +" (HIGH)-"+serverstatus[1]+"/"+serverstatus[2]);
				} else if(serverstatus[0].equals("LOW")){
					System.out.print(ggs.id +" "+ lastGuess +" (LOW)-"+serverstatus[1]+"/"+serverstatus[2]);
				} else if(serverstatus[0].equals("WIN")){
					System.out.print(ggs.id+" "+ this.getTarget() + "(WIN)-"+ ((double)getRemainingTime()/1000)+"s / "+serverstatus[1]);
					break;
				} else if(serverstatus[0].equals("LOSE")){
					System.out.println(ggs.id+" - LOSE -"+ ((double)getRemainingTime()/1000)+"s / "+serverstatus[1]);
				} else if(serverstatus[0].equals("ERR")){
					if(lastGuess > mv || lastGuess < 0){
						System.out.print(ggs.id+" "+ lastGuess +" **(ERR out of range) -"+ ((double)getRemainingTime()/1000)+"s / "+serverstatus[1]);
					}
				}

			} catch (NumberFormatException e){	// If entered value wasn't a number
				try{	// send error message to client and server.
					System.out.println(ggs.id+" "+ lastGuess +" **(ERR non-integer) -"+ ((double)getRemainingTime()/1000)+"s / "+guessAmount);
					ggs.out.write(String.format("ERR:%d%n",guessAmount));
					ggs.out.flush();
				} catch (Exception ex){
					break;
				}
			} catch (IOException e){
				System.out.println("An error occurred.");
			}
		}
		try {	// After game is finished, print game over.
			ggs.out.write("Game Over\n");
			ggs.out.flush();
			ggs.client.close();
		} catch (java.net.SocketException e){	// Handle the client closing exception.
			System.out.println("The connection from "+ggs.id+" was closed.");		
		} catch (IOException e){
			System.out.println("An unknown error ocurred");
		} catch (Exception e){
			System.out.println(e);
		}

	}

	// Converting milliseconds to seconds.
	public double getSecs(long time){
		return (double)time/1000;
	}

	// Overriding the String.toString() method to provide out own protocol for the guards of the game states.
	public String toString(){
		if(timeRemaining <= 0){
			return String.format("LOSE:%d%n", guessAmount);

		}
		if(lastGuess < 0 || lastGuess > mv){
			return String.format("ERR:%d%n", guessAmount);
		} else if(lastGuess > targetValue){
			return String.format("HIGH:%.1fs:%d%n", getSecs(getRemainingTime()), guessAmount);
		} else if(lastGuess < targetValue){
			return String.format("LOW:%.1fs:%d%n", getSecs(getRemainingTime()), guessAmount);
		} else if(lastGuess == targetValue){
			return String.format("WIN:%d%n", guessAmount);
		} else { return ""; }
	}

	/**
	  * This class will run the timer thread, implements runnable so we can run it concurrently to the GameState object.
	  *	Takes the GameState object for output and checking time.
	  * Takes the current actual time and decreases the timelimit from it to set the time remaining. We earlier set
	  * timeRemaining to be timeLimit + currentTime, so we can keep decrementing the time remaining accordingly.
	  */
	class Timer implements Runnable {
		GameState gameState;
		public Timer(GameState g){
			this.gameState = g;
		}

		// The run method will contain the decrementing of time and making sure the time only runs until 0 by checking if the finished status is false.
		public void run(){
			while(getRemainingTime() > 0 && !finished()){
				timeRemaining = timeLimit - TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
			}
			// if time in fact is 0 then set finished to true.
			if(getRemainingTime() <= 0){
				System.out.println(gameState.ggs.id+" Game over");	// Send a game over message in the server when the timer run out.
				isFinished = true;
			}
		}

	}
}