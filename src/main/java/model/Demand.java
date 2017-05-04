package model;

import model.Status.CargoType;


public abstract class Demand {
	protected CargoType cargoType;
	protected int startTime;
	protected int endTime;
	protected double amountOfCargo;
	protected String departure;
	protected String destination;
	protected boolean isdemand;
	protected int time;
	
	public abstract void timeNext();
	public abstract void reset();
	public boolean isDemand(){
		return isdemand;
	};

	public CargoType getCargoType() {
		return cargoType;
	}

	public void setCargoType(CargoType cargoType) {
		this.cargoType = cargoType;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public double getAmountOfCargo() {
		return amountOfCargo;
	}
	public void setAmountOfCargo(double amountOfCargo) {
		this.amountOfCargo = amountOfCargo;
	}
	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	
	
}
