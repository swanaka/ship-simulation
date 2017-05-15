package model;

import java.util.List;

import model.Port.PortFacility;
import model.Status.CargoType;
import model.Status.FuelType;
import model.Status.ShipStatus;
import util.Location;

public abstract class Ship {
	//Configuration of ship
	protected Hull hull;
	protected Engine engine;
	protected FuelTank fuelTank;
	protected Propeller propeller;
	protected List<Schedule> schedule;
	protected ShipOperator owner;
	protected CargoHold cargoHold;
	protected String name;
	private double operatingCost;

	//Status of ship
	protected Location loc;
	protected Port berthingPort;
	protected double amountOfFuel;
	protected double ratioOfAccident;
	protected double amountOfCargo;
	protected double remainingDistance;
	protected double cashFlow;
	protected double nox;
	protected double sox;
	protected double co2;
	protected ShipStatus status;
	protected int waitingTime;
	protected double speed;
	protected int time;
	protected double totalFuel;
	protected double totalDistance;
	protected double totalCargo;
	protected double acumCost;
	protected double totalCost;
	
	public Ship(){
		amountOfFuel = 0;
		ratioOfAccident = 0;
		amountOfCargo = 0;
		remainingDistance = 0;
		cashFlow = 0;
		nox = 0;
		sox = 0;
		co2 = 0;
		status = ShipStatus.WAIT;
		waitingTime = 0;
		time = 0;
		berthingPort = null;
	}
	//Function
	public void timeNext(){
		this.time++;
		switch(this.status){
			case TRANSPORT:
				if (remainingDistance > 0) transport();
				if (remainingDistance == 0) {
					Port port = this.schedule.get(0).getDestination();
					PortFacility facility = port.checkBerthing(this);
					if(facility == null) {
						setShipStatus(ShipStatus.WAIT);
						this.waitingTime ++;
					}
					if(facility != null){
						facility.accept(this);
						this.setBerthingPort(port);
						this.appropriateRevenue();
					}

				}
				break;
			case WAIT:
				Port port = this.schedule.get(0).getDestination();
				PortFacility facility = port.checkBerthing(this);
				if(facility == null){
					setShipStatus(ShipStatus.WAIT);
					this.waitingTime ++;
				}
				if(!(facility == null)){
					facility.accept(this);
					this.setBerthingPort(port);
					this.appropriateRevenue();
				}
				break;
			case BERTH:
				if(this.schedule.size() <= 1);
				else if(this.schedule.get(0).judgeEnd() && time >= this.schedule.get(1).startTime ){
					if(this.schedule.size() > 1){
						this.removeSchedule();
						this.berthingPort.departure(this);
						this.setBerthingPort(null);
						this.setShipStatus(ShipStatus.TRANSPORT);
						this.setRemainingDistance(PortNetwork.getDistance(this.getSchedule().from,this.getSchedule().to));

					}
				}
				break;
		}

	}
	public double getNox() {
		return nox;
	}
	public void setNox(double nox) {
		this.nox = nox;
	}
	public double getSox() {
		return sox;
	}
	public void setSox(double sox) {
		this.sox = sox;
	}
	public double getCo2() {
		return co2;
	}
	public void setCo2(double co2) {
		this.co2 = co2;
	}
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	public double getTotalFuel() {
		return totalFuel;
	}
	public void setTotalFuel(double totalFuel) {
		this.totalFuel = totalFuel;
	}
	public double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public double getTotalCargo() {
		return totalCargo;
	}
	public void setTotalCargo(double totalCargo) {
		this.totalCargo = totalCargo;
	}
	public double getAcumCost() {
		return acumCost;
	}
	public void setAcumCost(double acumCost) {
		this.acumCost = acumCost;
	}
	public abstract void transport();
	public abstract void appropriateRevenue();
	public abstract void addSchedule(int startTime, int endTime, Port departure, Port destination, double amount);
	public abstract void addContractToSchedule(double freight,double penalty);
	public abstract int getTime(double distance);
	public abstract double estimateFuelAmount(Port departure, Port destination);

	//Getter and Setter
	public double getRemainingDistance() {
		return remainingDistance;
	}
	public void setRemainingDistance(double remainingDistance) {
		this.remainingDistance = remainingDistance;
		if(this.remainingDistance < 0){
			this.remainingDistance = 0;
		}
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
		if (amountOfFuel > this.fuelTank.capacity){
			this.amountOfFuel = this.fuelTank.capacity;
		}else{
			this.amountOfFuel = amountOfFuel;
		}
		if (amountOfFuel < 0){
			this.amountOfFuel = 0;
			System.out.println("Short of Fuel!");
		}
	}
	public void addCashFlow(double cashFlow){
		this.cashFlow += cashFlow;
	}
	public double getCashFlow(){
		return this.cashFlow;
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
		if(amountOfCargo > this.cargoHold.getCapacity()){
			this.amountOfCargo = this.cargoHold.getCapacity();
		}else if (amountOfCargo < 0){
			this.amountOfCargo = 0;
		}else{
		this.amountOfCargo = amountOfCargo;
		}
	}
	public double getTotalCost(){
		return this.totalCost;
	}
	public void setShipStatus(ShipStatus status){
		this.status = status;
	}
	public ShipStatus getShipStatus(){
		return this.status;
	}
	public void setBerthingPort(Port port){
		this.berthingPort = port;
	}
	public Port getBerthingPort(){
		return this.berthingPort;
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
	
	public void setOperatingCost(double cost){
		this.operatingCost = cost;
	}
	public double getOperatingCost(){
		return this.operatingCost;
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

	public CargoType getCargoType(){
		return this.cargoHold.getCargoType();
	}
	public Schedule getLastSchedule(){
		if (this.schedule.size() == 0) return null;
		else return this.schedule.get(this.schedule.size() - 1);
	}
	public Schedule getSchedule(){
		if (this.schedule.size() == 0) return null;
		return this.schedule.get(0);
	}

	public void removeSchedule(){
		this.schedule.remove(0);
	}



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

	public abstract class Schedule{
		//When ~ When
		private int startTime;
		private int endTime;
		//From / To
		private Port from;
		private Port to;
		//Objective
		//loading property
		protected boolean isLoading;
		private double loadingAmount;
		private CargoType loadingType;
		//bunkering property
		protected boolean isBunkering;
		private double bunkeringAmount;
		private FuelType fuelType;
		//unloading property
		protected boolean isUnLoading;
		private double unloadingAmount;
		private CargoType unloadingType;
		//Contract property
		protected double fee;
		protected double penalty;


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
			return from;
		}
		public void setDeparture(Port departure) {
			this.from = departure;
		}
		public Port getDestination() {
			return to;
		}
		public void setDestination(Port destination) {
			this.to = destination;
		}


		public Port getFrom() {
			return from;
		}
		public void setFrom(Port from) {
			this.from = from;
		}
		public Port getTo() {
			return to;
		}
		public void setTo(Port to) {
			this.to = to;
		}
		public boolean isLoading() {
			return isLoading;
		}
		public void setLoading(boolean isLoading) {
			this.isLoading = isLoading;
		}
		public double getLoadingAmount() {
			return loadingAmount;
		}
		public void setLoadingAmount(double loadingAmount) {
			this.loadingAmount = loadingAmount;
		}
		public CargoType getLoadingType() {
			return loadingType;
		}
		public void setLoadingType(CargoType loadingType) {
			this.loadingType = loadingType;
		}
		public boolean isBunkering() {
			return isBunkering;
		}
		public void setBunkering(boolean isBunkering) {
			this.isBunkering = isBunkering;
		}
		public double getBunkeringAmount() {
			return bunkeringAmount;
		}
		public void setBunkeringAmount(double bunkeringAmount) {
			this.bunkeringAmount = bunkeringAmount;
		}
		public FuelType getFuelType() {
			return fuelType;
		}
		public void setFuelType(FuelType fuelType) {
			this.fuelType = fuelType;
		}
		public boolean isUnLoading() {
			return isUnLoading;
		}
		public void setUnLoading(boolean isUnLoading) {
			this.isUnLoading = isUnLoading;
		}
		public double getUnloadingAmount() {
			return unloadingAmount;
		}
		public void setUnloadingAmount(double unloadingAmount) {
			this.unloadingAmount = unloadingAmount;
		}
		public CargoType getUnloadingType() {
			return unloadingType;
		}
		public void setUnloadingType(CargoType unloadingType) {
			this.unloadingType = unloadingType;
		}
		public double getFee() {
			return fee;
		}
		public void setFee(double fee) {
			this.fee = fee;
		}
		public void setPenalty(double penalty) {
			this.penalty = penalty;
		}
		public double getPenalty(){
			return this.penalty;
		}
		public abstract double getIncome() ;
		public abstract boolean judgeEnd();


	}

	public abstract class CargoHold{
		private CargoType cargoType;
		private Double capacity;
		public CargoType getCargoType() {
			return cargoType;
		}
		public void setCargoType(CargoType cargoType) {
			this.cargoType = cargoType;
		}
		public Double getCapacity() {
			return capacity;
		}
		public void setCapacity(Double capacity) {
			this.capacity = capacity;
		}

	}

	public double getMaximumCargoAmount() {
		return this.cargoHold.getCapacity();
	}




}
