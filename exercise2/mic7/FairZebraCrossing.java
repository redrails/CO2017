package CO2017.exercise2.mic7;

public class FairZebraCrossing extends ZebraCrossing {

	/**
	  * This class will allow the ZebraCrossing to be more "fairer"
	  * The guards in this will check if there are more cars than peds, if there are then the cars can cross
	  * Otherwise the peds will be able to cross given that there are less cars waiting. 
	  * This isn't the most efficient way but will be able to be a little more efficient.
	  */
	public FairZebraCrossing() {
		super();
		// System.out.println("life isn't fair");
	}

	/**
	  * Guards that will check if total number of cars waiting is smaller than pedswaiting
	  * If there are then the peds will wait.
	  */
	@Override
	public void startCrossing(Person p) {
		while((_carsWaitingUp+_carsWaitingDown <= _pedsWaiting)
				|| !_carsInCrossingDown.isEmpty()
				|| !_carsInCrossingUp.isEmpty()
				|| _pedestriansInCrossing.size() > 4
				){
			try { 
				wait();
			} catch (Exception e) {}
		}
		_pedsWaiting-=1;
		_pedestriansInCrossing.add(p);
		_changed = true;
	}

	/**
	  * Guards that will check the opposite.
	  */
	@Override
	public void startCrossing(Car c) {
		while((_carsWaitingUp+_carsWaitingDown > _pedsWaiting) 
				|| !_pedestriansInCrossing.isEmpty()
				){
			try {
				wait();
			} catch (Exception e) {}
		}
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

}

