package model;

import model.Status.LoadingType;

public abstract class Demand {
	private LoadingType cargoType;
	private int startTime;
	private int endTime;
	private double amountOfCargo;
	private String departure;
	private String destination;
	private boolean isdemand;
	
	public abstract void timeNext(int now);
	public boolean isDemand(){
		return isdemand;
	};

	public LoadingType getCargoType() {
		return cargoType;
	}

	public void setCargoType(LoadingType cargoType) {
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
