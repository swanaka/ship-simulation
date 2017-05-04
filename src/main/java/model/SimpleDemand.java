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
	private String departure;
	private String destination;
	
	public SimpleDemand(){
		super();
		setCargoType(CargoType.HFO);
		this.counter = 0;
		this.limit = 30;
		amount = 60000;
		duration = 720;
		departure = "Japan";
		destination = "Los Angels";
	}
	
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
		if (counter > this.limit){
			setAmountOfCargo(amount);
			setStartTime(super.time);
			setEndTime(super.time + duration);
			setDeparture(departure);
			setDestination(destination);
			this.isdemand = true;
			
			counter = 0;
		}
		counter ++;	
	}
	
}