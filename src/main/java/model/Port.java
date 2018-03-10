package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Status.BunkeringStatus;
import model.Status.CargoType;
import model.Status.FuelType;
import model.Status.LoadingStatus;
import model.Status.CargoType;
import model.Status.MaintenanceStatus;
import model.Status.ShipStatus;
import util.Location;

public abstract class Port {
	
	//Configuration 
	private int capacity;
	protected List<PortFacility> facilities;
	private Location loc;
	protected PortOperator operator;
	protected int numOfBunkers;

	//Status
	//	List of ships whose destination or location is this ports.
	protected String name;
	protected int time;

	//Function
	public abstract PortFacility checkBerthing(Ship ship);
	public abstract void departure(Ship ship);

	public abstract void addPortFacility(List<FuelType> fuelTypeList, CargoType loadingType, List<Double> bunkeringCapacity, double loadingCapacity, double berthingFee, boolean bunkeringFlag);
	public abstract int getTimeForReady(Ship ship);
	
	public Port(String name){
		this.name = name;
		//waitingShips = new ArrayList<Ship>();
		facilities = new ArrayList<PortFacility>();
		numOfBunkers = -1;
	}
	
	public void timeNext(){
		this.time++;
		// 1. BERTH, BUNKERING, LOADING, UNLOADING, Port provide service,
		for (PortFacility facility : facilities){
			if (facility.berthingShip != null){
				facility.berthing();
			}
		}
	}
	public int getCapacity() {
		return capacity;
	}

	public PortOperator getOperator(){
		return this.operator;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getName(){
		return this.name;
	}

	public Location getLoc() {
		return loc;
	}


	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public void setNumOfBunkers(int num) {
		this.numOfBunkers = num;
	}

}
