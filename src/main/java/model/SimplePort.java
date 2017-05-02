package model;

import java.util.HashMap;

import model.Status.BunkeringStatus;
import model.Status.FuelType;
import model.Status.LoadingStatus;
import model.Status.LoadingType;
import model.Status.MaintenanceStatus;
import model.Status.ShipStatus;

public class SimplePort extends Port {
	
	public SimplePort(String name){
		super(name);
	}
	
	
	@Override
	public void addPortFacility(HashMap<String, String> param){
		String name = param.get("Name");
		super.name = name;
		System.out.println(param.get("FuelType"));
		FuelType fuelType = FuelType.valueOf(param.get("FuelType"));
		LoadingType loadingType = LoadingType.valueOf(param.get("LoadingType"));
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
	public void loading() {
		for (PortFacility facility : super.facilities){
			facility.loading();
		}

	}

	@Override
	public void unloading(Ship ship) {
		for (PortFacility facility : super.facilities){
			facility.unloading();
		}

	}

	@Override
	public void maintenance() {
		for (PortFacility facility : super.facilities){
			facility.maintenance();
		}

	}




	@Override
	public PortFacility checkBerthing(Ship ship) {
		for (PortFacility facility : super.facilities){
			if(facility.match(ship)){
				return facility;
			}
		}
		return null;
	}

	//InnerClass
	private class SimplePortFacitliy extends PortFacility{
		
		private double berthingFee;

		public SimplePortFacitliy(FuelType fuelType, LoadingType loadingType, double bunkeringCapacity, double loadingCapacity, 
				double berthingFee){
			super.occupiedFlag = 0;
			super.bunkeringCapacity = bunkeringCapacity;
			super.fuelType = fuelType;
			super.loadingType = loadingType;
			super.loadingCapacity = loadingCapacity;
			this.berthingFee = berthingFee;
		}

		public void accept(Ship ship){
			super.berthingShip = ship;
			ship.setShipStatus(ShipStatus.BERTH);
			ship.appropriateRevenue();
		}
		public void berthing(){
			loading();
			unloading();
			bunkering();
			maintenance();
			berthingShip.owner.addCashFlow(-1*this.berthingFee);
			getOperator().addCashFlow(this.berthingFee);
			if (berthingShip.lStatus==LoadingStatus.NO || berthingShip.bStatus == BunkeringStatus.NO || berthingShip.mStatus == MaintenanceStatus.NO){
				berthingShip.status = ShipStatus.TRANSPORT;
				if(!berthingShip.getContract().getDeparture().equals(this)) berthingShip.removeSchedule();
				if (this.equals(berthingShip.getContract().getDeparture())){
					//目的地を設定(RemainingDistanceを設定)
					//berthingShip.setRemainingDistance(PortNet;
				}else {
					//目的地を設定
				}
			}
			
		}
		public void loading(){
			switch(super.berthingShip.lStatus){
			case LOADING:
				super.berthingShip.setAmountOfCargo(super.berthingShip.getAmountOfCargo() + loadingCapacity);
			default:;
			}
			
			if (berthingShip.getContract().getCargoAmount() == berthingShip.getAmountOfCargo()) this.berthingShip.lStatus = LoadingStatus.NO;
		}

		public void unloading(){
			switch(super.berthingShip.lStatus){
			case UNLOADING:
				super.berthingShip.setAmountOfCargo(super.berthingShip.getAmountOfCargo() -  loadingCapacity);
			default:;
			}
			if(berthingShip.getAmountOfCargo()==0) berthingShip.lStatus = LoadingStatus.NO;
		}
		
		public void maintenance(){
			switch(super.berthingShip.mStatus){
			case YES:
				//TO-DO maintenance
			default:;
			}
			//To-Do maintenance;
		}

		public boolean match(Ship ship){
			if (super.occupiedFlag == 1) return false;
			if (super.fuelType != ship.getFuelType()) return false;
			if (super.loadingType != ship.getCargoType()) return false;
			return true;
		}

		@Override
		public void bunkering() {
			switch(super.berthingShip.bStatus){
			case YES:
				super.berthingShip.setAmountOfFuel(super.berthingShip.getAmountOfFuel() + bunkeringCapacity);
			default:;
			}
			if (berthingShip.getAmountOfFuel() == berthingShip.getFuelTank().getCapacity()){
				berthingShip.bStatus = BunkeringStatus.NO;
			}
		}

	}

	@Override
	public int getTimeForReady(Ship ship) {
		// TODO Auto-generated method stub
		return 0;
	}

}
