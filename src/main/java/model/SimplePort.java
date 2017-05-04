package model;

import java.util.HashMap;


import model.Status.FuelType;

import model.Status.CargoType;

import model.Status.ShipStatus;

public class SimplePort extends Port {
	
	public SimplePort(String name){
		super(name);
		PortOperator operator = new SimplePortOperator(name);
		this.operator = operator;
	}
	
	
	@Override
	public void addPortFacility(HashMap<String, String> param){
		System.out.println(param.get("FuelType"));
		FuelType fuelType = FuelType.valueOf(param.get("FuelType"));
		CargoType loadingType = CargoType.valueOf(param.get("LoadingType"));
		double bunkeringCapacity = Double.parseDouble(param.get("BunkeringCapacity"));
		double loadingCapacity = Double.parseDouble(param.get("LoadingCapacity"));
		double berthingFee = Double.parseDouble(param.get("BerthingFee"));
		PortFacility facility = new SimplePortFacitliy(fuelType, loadingType, bunkeringCapacity, loadingCapacity, berthingFee);
		super.facilities.add(facility);
	}
	
	@Override
	public void addPortFacilities(HashMap<String, String>param, int num){
		for (int i=0;i<num;i++){
			addPortFacility(param);
		}
	}
	
	@Override
	public void addPortFacilities(HashMap<String, String>[] paramList){
		for (HashMap<String, String> param : paramList){
			addPortFacility(param);
		}
	}

	@Override
	public PortFacility checkBerthing(Ship ship) {
		for (PortFacility facility : this.facilities){
			if(facility.match(ship)){
				return facility;
			}
		}
		return null;
	}

	//InnerClass
	private class SimplePortFacitliy extends PortFacility{
		
		private double berthingFee;

		public SimplePortFacitliy(FuelType fuelType, CargoType loadingType, double bunkeringCapacity, double loadingCapacity, 
				double berthingFee){
			super.occupiedFlag = 0;
			super.bunkeringCapacity = bunkeringCapacity;
			super.fuelType = fuelType;
			super.loadingType = loadingType;
			super.loadingCapacity = loadingCapacity;
			this.berthingFee = berthingFee;
		}

		public void accept(Ship ship){
			this.berthingShip = ship;
			ship.setShipStatus(ShipStatus.BERTH);
		}
		public void berthing(){
			if(berthingShip.getSchedule().isLoading)loading();
			if(berthingShip.getSchedule().isUnLoading) unloading();
			if(berthingShip.getSchedule().isBunkering) bunkering();
			berthingShip.owner.addCashFlow(-1*this.berthingFee);
			getOperator().addCashFlow(this.berthingFee);
				
			
		}
		public void loading(){
			
			this.berthingShip.setAmountOfCargo(this.berthingShip.getAmountOfCargo() + loadingCapacity);
			this.berthingShip.getSchedule().setLoadingAmount(berthingShip.getSchedule().getLoadingAmount()-loadingCapacity);
			if (this.berthingShip.getSchedule().getLoadingAmount() <= 0) this.berthingShip.getSchedule().setLoading(false);
		}

		public void unloading(){
			this.berthingShip.setAmountOfCargo(this.berthingShip.getAmountOfCargo() - loadingCapacity);
			this.berthingShip.getSchedule().setUnloadingAmount(berthingShip.getSchedule().getUnloadingAmount()-loadingCapacity);
			if (this.berthingShip.getSchedule().getUnloadingAmount() <= 0) this.berthingShip.getSchedule().setUnLoading(false);
		}

		public boolean match(Ship ship){
			if (super.occupiedFlag == 1) return false;
			if (super.fuelType != ship.getFuelType()) return false;
			if (super.loadingType != ship.getCargoType()) return false;
			return true;
		}

		@Override
		public void bunkering() {
			this.berthingShip.setAmountOfFuel(this.berthingShip.getAmountOfFuel() + bunkeringCapacity);
			if (berthingShip.getAmountOfFuel() == berthingShip.getFuelTank().getCapacity()){
				this.berthingShip.getSchedule().setBunkering(false);
			}
		}

	}

	@Override
	public int getTimeForReady(Ship ship) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void departure(Ship ship) {
		for(PortFacility facility : facilities){
			if(facility.berthingShip.equals(ship)){
				facility.berthingShip = null;
			}
		}
		
	}

}
