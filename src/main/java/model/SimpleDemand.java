package model;

import model.Status.CargoType;

/**
 * Continer demand that is constant and happens at regular timing
 * @author Shinnosuke Wanaka
 * 
 */
public class SimpleDemand extends Demand{
	
	private int counter;
	private int limit;
	private double amount;
	private int duration;

	
	
	public SimpleDemand(CargoType cargoType, int interval, double amount, int duration, String departure, String destination){
		super();
		setCargoType(cargoType);
		this.counter = 0;
		this.limit = interval;
		this.amount = amount;
		this.duration = duration;
		this.departure = departure;
		this.destination = destination;
	}

	@Override
	public void timeNext() {
		this.time++;
		if (counter == 1){
			setAmountOfCargo(amount);
			setStartTime(this.time);
			setEndTime(this.time + duration);
			setDeparture(departure);
			setDestination(destination);
			this.isdemand = true;
		}
		if (counter > this.limit){
			counter = 0;
		}
		counter ++;	
	}

	@Override
	public void reset() {
		this.isdemand = false;
	}
	
}