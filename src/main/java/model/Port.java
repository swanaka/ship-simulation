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

	//Status
	//	List of ships whose destination or location is this ports.
	protected String name;
	protected int time;

	//Function
	public abstract PortFacility checkBerthing(Ship ship);
	public abstract void departure(Ship ship);

	public abstract void addPortFacility(HashMap<String, String> param);
	public abstract void addPortFacilities(HashMap<String, String> param, int num);
	public abstract void addPortFacilities(HashMap<String, String>[] params);
	public abstract int getTimeForReady(Ship ship);
	
	public Port(String name){
		this.name = name;
		//waitingShips = new ArrayList<Ship>();
		facilities = new ArrayList<PortFacility>();
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

	protected abstract class PortFacility{
		protected Ship berthingShip;
		protected FuelType fuelType;
		protected CargoType loadingType;
		protected int occupiedFlag;
		protected double bunkeringCapacity;
		protected double loadingCapacity;
		

		public abstract void accept(Ship ship);
		public abstract void berthing();
		public abstract void loading();
		public abstract void unloading();
		public abstract void bunkering();
		public abstract boolean match(Ship ship);

	}

}
