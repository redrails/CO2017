import java.io.Writer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.*;

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

	public double getBySecond(long r){
		return r/1000;
	}

	public void guess(int g){
		this.lastGuess = g;
		if(g == targetValue || getRemainingTime()<=0){
			this.isFinished = true;
		}
		this.guessAmount++;
	}

	public void run(){
		Thread timerThread = new Thread(new Timer(this));
		timerThread.start();

		System.out.println(ggs.id + " target is " + this.getTarget());

		while(this.finished() == false){
			try {
				this.guess(Integer.parseInt(ggs.in.readLine()));
				ggs.out.write(this.toString());
				ggs.out.flush();
				// System.out.print(ggs.id +" "+ this.toString());

				String [] serverstatus = this.toString().split(":");
				if(serverstatus[0].equals("HIGH")){
					System.out.print(ggs.id +" "+ lastGuess +" (HIGH)-"+serverstatus[1]+"/"+serverstatus[2]);
				} else if(serverstatus[0].equals("LOW")){
					System.out.print(ggs.id +" "+ lastGuess +" (LOW)-"+serverstatus[1]+"/"+serverstatus[2]);
				} else if(serverstatus[0].equals("WIN")){
					System.out.print(ggs.id+" Game over");
					break;
				} else if(serverstatus[0].equals("LOSE")){
					System.out.println(ggs.id+" - LOSE -"+ ((double)getRemainingTime()/1000)+"s / "+serverstatus[1]);
				} else if(serverstatus[0].equals("ERR")){
					if(lastGuess > mv || lastGuess < 0){
						System.out.print(ggs.id+" "+ lastGuess +" **(ERR out of range) -"+ ((double)getRemainingTime()/1000)+"s / "+serverstatus[1]);
					}
				}

			} catch (NumberFormatException e){
				try{
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
		try {
			ggs.out.write("Game Over\n");
			ggs.out.flush();
			ggs.client.close();
		} catch (java.net.SocketException e){
			System.out.println("The connection from "+ggs.id+" was closed.");		
		} catch (IOException e){
			System.out.println("An unknown error ocurred");
		} catch (Exception e){
			System.out.println(e);
		}

	}

	public double getSecs(long time){
		return (double)time/1000;
	}

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

	class Timer implements Runnable {
		GameState gameState;
		public Timer(GameState g){
			this.gameState = g;
		}

		public void run(){
			while(getRemainingTime() > 0 && !finished()){
				timeRemaining = timeLimit - TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
			}
			if(getRemainingTime() <= 0){
				isFinished = true;
			}
		}

	}
}