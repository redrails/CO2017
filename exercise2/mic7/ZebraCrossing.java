package CO2017.exercise2.mic7;

import java.util.Set;
import java.util.HashSet;

/**
  * This class will be used to handle the ZebraCrossing which holds guards and conditions
  * on who can use the resource at which time.
  * The class handles the state of the crossing as well as the persons/cars in the crossing.
  * @author mic7
  */

public class ZebraCrossing {

	protected Set<Car> _carsInCrossingDown;
	protected Set<Car> _carsInCrossingUp;
	protected int _carsWaitingDown;
	protected int _carsWaitingUp;
	protected volatile boolean _changed;
	protected Set<Person> _pedestriansInCrossing;
	protected int _pedsWaiting;

	/**
	  * Set initial values in constructor.
	  * Instantiate HashSets for the appropriate sets.
	  * @see HashSet, Set
	*/

	public ZebraCrossing() {
		_carsInCrossingDown = new HashSet<>();
		_carsInCrossingUp = new HashSet<>();
		_pedestriansInCrossing = new HashSet<>();
		_carsWaitingDown = 0;
		_carsWaitingUp = 0;
		_changed = false;
		_pedsWaiting = 0;
		System.out.println(toString());
	}

	/**
	  * @return _changed, returns the state of the ZebraCrossing.
	*/
	public boolean isChanged() {
		return _changed;
	}

	/**
	  * @param p - Person that will arrive at the crossing.
	  * This will change the state of the crossing and also increment the amount of peds waiting.
	  */
	public synchronized void arrive(Person p) {
		_pedsWaiting += 1;
		_changed = true;
	}


	/**
	  * @param c - Car that will arrive at the crossing.
	  * The heading of the car will be checked and the appropriate counters will be incremented.
	  */
	public synchronized void arrive(Car c) {
		boolean h = c.getHeading();
		if(h){
			_carsWaitingUp++;
		} else {
			_carsWaitingDown++;
		}
		_changed = true;
	}

	/**
	  * @param p - Person that will start crossing.
	  * The guard condition suggests that if there are no cars crossing up ir down and there are less than 4 pedestrians
	  * wanting to cross then they can, otherwise the Thread should wait until this is met.
	  */
	public synchronized void startCrossing(Person p) {
		try{
			while(_carsInCrossingUp.size() != 0 
					|| _carsInCrossingDown.size() != 0 
					|| _pedestriansInCrossing.size() > 4){
				wait();
			} 
			_pedsWaiting-=1;
			_pedestriansInCrossing.add(p);
			_changed = true;
		} catch(Exception e) {}
	}

	/**
	  * @param c - Car that will start crossing.
	  * Check the heading of the car and appropriately deal with the sets and variables.
	  * The guard suggests that if there are no peds crossing and there is no car in the crossing
	  * headed to the same direction, then the car can cross. Otherwise it will wait.
	  * this changes the state of the crossing. 
	  */
	public synchronized void startCrossing(Car c) {

		boolean h = c.getHeading();

		try {
			if(h){
				while(!_pedestriansInCrossing.isEmpty() || !_carsInCrossingUp.isEmpty()){
					wait();
				}
				_carsWaitingUp-=1;
				_carsInCrossingUp.add(c);
			} else {
				while(!_pedestriansInCrossing.isEmpty() || !_carsInCrossingDown.isEmpty()){
					wait();
				}
				_carsWaitingDown-=1;
				_carsInCrossingDown.add(c);
			}
			_changed = true;
		} catch (Exception e) {}
	}

	/**
	  * @param p - Person that finishes crossing.
	  * Remove the pedestrian from the crossing and change the state of the crossing.
	  * notifyAll() will wake up all other threads waiting on this thread to complete.
	  * @see Object.notifyAll()
	  */
	public synchronized void finishCrossing(Person p) {

		_pedestriansInCrossing.remove(p);
		_changed = true;
		notifyAll();

	}

	/**
	  * @param c - Car that wants to finish crossing.
	  * See the heading of the car and remove appropriately from the crossing.
	  * notifyAll() will wake up all othe threads waiting on this thread to complete.
	  * @see Object.notifyAll()
	  */
	public synchronized void finishCrossing(Car c) {

		boolean h = c.getHeading();
		if(h){
			_carsInCrossingUp.remove(c);
		} else {
			_carsInCrossingDown.remove(c);
		}
		_changed = true;
		notifyAll();

	}

	/**
	  * @return - Data of all the variables that are being used.
	  * returns in the format: 
	  * Cars: ^[CARSINCROSSINGUPHERE] W=0 v[CARSINCROSSINGDOWNHERE] W=0 // W represents waiting cars.
	  * Peds: [PEDSCROSSINGHERE] W=0 // W represents waiting persons.
	  *
	  */
	public String toString() {
		_changed = false;
		return "Cars: ^" 
				+ _carsInCrossingUp.toString() 
				+ " W="		 + _carsWaitingUp
				+ " v" 		 + _carsInCrossingDown.toString()
				+ " W="		 + _carsWaitingDown
				+ "\nPeds: " + _pedestriansInCrossing.toString()
				+ " W=" 	 + _pedsWaiting;
	}

}