package CO2017.exercise2.mic7;

import java.util.Set;
import java.util.HashSet;


public class ZebraCrossing {

	protected Set<Car> _carsInCrossingDown;
	protected Set<Car> _carsInCrossingUp;
	protected int _carsWaitingDown;
	protected int _carsWaitingUp;
	protected volatile boolean _changed;
	protected Set<Person> _pedestriansInCrossing;
	protected int _pedsWaiting;

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

	public boolean isChanged() {
		return _changed;
	}

	public synchronized void arrive(Person p) {
		_pedsWaiting += 1;
		_changed = true;
	}

	public synchronized void arrive(Car c) {
		boolean h = c.getHeading();
		if(h){
			_carsWaitingUp++;
		} else {
			_carsWaitingDown++;
		}
		_changed = true;
	}

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

	public synchronized void finishCrossing(Person p) {

		_pedestriansInCrossing.remove(p);
		_changed = true;
		notifyAll();

	}

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