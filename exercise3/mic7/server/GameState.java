import java.io.Writer;
import java.util.Random;
import  java.util.concurrent.TimeUnit;

public class GameState implements Runnable {
	private static Random RANDGEN;
	static int MINVAL = 1;
	private boolean isFinished;
	private int guessAmount;
	private int targetValue;
	private long timeRemaining;
	private int mv;
	private Writer op;
	private boolean isChanged;
	GuessGameServerHandler ggs;
	private int lastGuess;
	private long timeLimit;

	public GameState (int mv, long tl,  GuessGameServerHandler ggsh){
		this.mv = mv;
		this.timeLimit = tl + TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
		this.isFinished = false;
		this.guessAmount = 0;
		RANDGEN = new Random();
		this.targetValue = RANDGEN.nextInt(mv)+1;
		// this.op = o;
		this.ggs = ggsh;
	}

	public boolean finished(){
		return isFinished;
	}

	public int getGuesses(){
		return guessAmount;
	}

	public int getTarget(){
		return targetValue;
	}

	public long getRemainingTime(){
		return timeRemaining;
	}

	public void guess(int g){
		this.lastGuess = g;
		guessAmount++;
	}

	public void run(){
		this.timeRemaining = timeLimit - TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	public String toString(){
		if(lastGuess > targetValue){
			return String.format("HIGH:%d:%d%n",getRemainingTime() ,guessAmount);
		} else if(lastGuess < targetValue){
			return String.format("LOW:%d:%d%n",getRemainingTime() ,guessAmount);
		} else if(lastGuess == targetValue){
			this.isFinished = true;
			return String.format("WIN:%d%n",guessAmount);
		} else {
			return String.format("LOSE:%d%n",guessAmount);
		}
	}	

}