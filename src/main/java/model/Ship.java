package model;

import java.util.List;

import model.Status.BunkeringStatus;
import model.Status.FuelType;
import model.Status.LoadingStatus;
import model.Status.LoadingType;
import model.Status.MaintenanceStatus;
import model.Status.ShipStatus;
import util.Location;

public abstract class Ship {
	//Configuration of ship
	protected Hull hull;
	protected Engine engine;
	protected FuelTank fuelTank;
	protected Propeller propeller;
	protected List<Contract> schedule;
	protected ShipOperator owner;
	protected CargoHold cargoHold;
	protected String name;

	//Status of ship
	protected Location loc;
	protected double amountOfFuel;
	protected double ratioOfAccident;
	protected double amountOfCargo;
	protected double remainingDistance;
	protected double cashFlow;
	protected double emssionedGas;
	protected ShipStatus status;
	protected BunkeringStatus bStatus;
	protected LoadingStatus lStatus;
	protected MaintenanceStatus mStatus;
	protected int waitingTime;
	protected double speed;
	protected int time;
	
	//Function
	public void timeNext(){
		this.time++;
		switch(this.status){
		case TRANSPORT:
			transport();
		case WAIT:
			this.waitingTime ++;
		case BERTH:
			break;
		}

	}
	public abstract void transport();
	public abstract void appropriateRevenue();
	public abstract void addSchedule(int startTime, int endTime, Port departure, Port destination, double amount);
	public abstract void addFreightToSchedule(double freight);
	public abstract int getTime(double distance);

	//Getter and Setter
	public double getRemainingDistance() {
		return remainingDistance;
	}
	public void setRemainingDistance(double remainingDistance) {
		this.remainingDistance = remainingDistance;
	}
	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public double getAmountOfFuel() {
		return amountOfFuel;
	}

	public void setAmountOfFuel(double amountOfFuel) {
		this.amountOfFuel = amountOfFuel;
	}

	public double getRatioOfAccident() {
		return ratioOfAccident;
	}

	public void setRatioOfAccident(double rationOfAccident) {
		this.ratioOfAccident = rationOfAccident;
	}

	public double getAmountOfCargo() {
		return amountOfCargo;
	}

	public void setAmountOfCargo(double amountOfCargo) {
		this.amountOfCargo = amountOfCargo;
	}
	
	public void setShipStatus(ShipStatus status){
		this.status = status;
	}
	public ShipStatus getShipStatus(){
		return this.status;
	}

	public Hull getHull() {
		return hull;
	}

	public void setHull(Hull hull) {
		this.hull = hull;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public FuelTank getFuelTank() {
		return fuelTank;
	}

	public void setFuelTank(FuelTank fuelTank) {
		this.fuelTank = fuelTank;
	}

	public Propeller getPropeller() {
		return propeller;
	}

	public void setPropeller(Propeller propeller) {
		this.propeller = propeller;
	}


	public ShipOperator getOwner() {
		return owner;
	}
	public void setOwner(ShipOperator owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public FuelType getFuelType(){
		return this.fuelTank.getFuelType();
	}

	public LoadingType getCargoType(){
		return this.cargoHold.getCargoType();
	}
	public Contract getLastSchedule(){
		return this.schedule.get(this.schedule.size() - 1);
	}
	
	
	public Contract getContract(){
		return this.schedule.get(0);
	}
	
	public void removeSchedule(){
		this.schedule.remove(0);
	}
	public abstract double estimateFuelAmount(Port departure, Port destination);
	

	//Abstract inner class
	public abstract class Hull{

		public abstract double calcEHP(double v);
	}

	public abstract class Engine{
		public abstract double calcFOC(double v);
	}

	public abstract class FuelTank{
		private FuelType fuelType;
		private double capacity;

		public FuelType getFuelType(){
			return fuelType;
		}
		public void setFuelType(FuelType fuelType){
			this.fuelType = fuelType;
		}
		public double getCapacity() {
			return capacity;
		}
		public void setCapacity(double capacity) {
			this.capacity = capacity;
		}
		
	}

	public abstract class Propeller{
		public abstract double calcBHP(double v);
	}

	public abstract class Contract{
		private int startTime;
		private int endTime;
		private Port departure;
		private Port destination;
		private LoadingType cargoType;
		private double cargoAmount;
		protected double freightRate;

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
		public Port getDeparture() {
			return departure;
		}
		public void setDeparture(Port departure) {
			this.departure = departure;
		}
		public Port getDestination() {
			return destination;
		}
		public void setDestination(Port destination) {
			this.destination = destination;
		}
		
		public LoadingType getCargoType() {
			return cargoType;
		}
		public void setCargoType(LoadingType cargoType) {
			this.cargoType = cargoType;
		}
		public double getCargoAmount() {
			return cargoAmount;
		}
		public void setCargoAmount(double cargoAmount) {
			this.cargoAmount = cargoAmount;
		}
		public void setFreightRate(double freightRate){
			this.freightRate = freightRate;
		}
		public abstract double getIncome() ;
		public abstract double getPenalty(int time);
		
		
	}

	public abstract class CargoHold{
		private LoadingType cargoType;
		private Double capacity;
		public LoadingType getCargoType() {
			return cargoType;
		}
		public void setCargoType(LoadingType cargoType) {
			this.cargoType = cargoType;
		}
		public Double getCapacity() {
			return capacity;
		}
		public void setCapacity(Double capacity) {
			this.capacity = capacity;
		}

	}

	


}
