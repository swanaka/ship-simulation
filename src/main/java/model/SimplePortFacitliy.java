package model;

import java.util.List;

import model.Status.CargoType;
import model.Status.FuelType;
import model.Status.ShipStatus;

public class SimplePortFacitliy extends PortFacility{

	private double berthingFee;
	private double fuelPrice;
	private FuelType tmpFuelType;
	private double tmpBunkeringCapacity;
	//BunkeringFlag: TRUE => Ready to bunkering, FALSE => not ready to bunkering
	//To represent bunkering vessels or bunkering truck, this flag is installed.
	//If the method is Shore to ship, this flag is always true.
	private boolean bunkeringFlag;
	
	public SimplePortFacitliy(List<FuelType> fuelTypeList, CargoType loadingType, List<Double> bunkeringCapacityList, double loadingCapacity,
			double berthingFee, boolean bunkeringFlag){
		super.occupiedFlag = 0;
		super.bunkeringCapacityList = bunkeringCapacityList;
		super.fuelTypeList = fuelTypeList;
		super.loadingType = loadingType;
		super.loadingCapacity = loadingCapacity;
		this.berthingFee = berthingFee;
		this.bunkeringFlag = bunkeringFlag;
	}

	public void accept(Ship ship){
		this.berthingShip = ship;
		ship.setShipStatus(ShipStatus.BERTH);
		this.occupiedFlag = 1;
		this.tmpFuelType = ship.getFuelType();
		this.fuelPrice = Market.getPrice(this.tmpFuelType);
		this.berthingShip.setFuelPrice(fuelPrice);
		this.tmpBunkeringCapacity = super.bunkeringCapacityList.get(fuelTypeList.indexOf(tmpFuelType));
	}

	public void berthing(){
		if(berthingShip.getSchedule().isLoading)loading();
		if(berthingShip.getSchedule().isUnLoading) unloading();
		if(berthingShip.getSchedule().isBunkering) bunkering();
		if(super.port.time > 8760){
			berthingShip.owner.addCashFlow(-1*this.berthingFee);
			super.port.getOperator().addBerthingCash(this.berthingFee);
		}

	}

	public void loading(){
		this.berthingShip.setAmountOfCargo(this.berthingShip.getAmountOfCargo() + loadingCapacity);
		this.berthingShip.getSchedule().setLoadingAmount(berthingShip.getSchedule().getLoadingAmount()-loadingCapacity);
		if (this.berthingShip.getSchedule().getLoadingAmount() <= 0) this.berthingShip.getSchedule().setLoading(false);
	}

	public void unloading(){
		if(berthingShip.time > 8760) {
			if(this.berthingShip.getAmountOfCargo() < loadingCapacity){
				this.berthingShip.totalCargo += this.berthingShip.getAmountOfCargo();
			}else{
				this.berthingShip.totalCargo += loadingCapacity;
			}
		}
		this.berthingShip.setAmountOfCargo(this.berthingShip.getAmountOfCargo() - loadingCapacity);
		this.berthingShip.getSchedule().setUnloadingAmount(berthingShip.getSchedule().getUnloadingAmount()-loadingCapacity);
		if (this.berthingShip.getSchedule().getUnloadingAmount() <= 0) this.berthingShip.getSchedule().setUnLoading(false);
	}

	public boolean match(Ship ship){
		if (super.occupiedFlag == 1) return false;
		if (ship.getSchedule().isBunkering == false) return true;
		FuelType shipFuelType = null;
		if (ship.isDualfuelFlag() == false) {
			shipFuelType = ship.getFuelType();
		}else {
			double hfoPrice = Market.getPrice(FuelType.HFO);
			double lngPrice = Market.getPrice(FuelType.LNG);
			if (hfoPrice < lngPrice) {
				shipFuelType = FuelType.HFO;
			}else {
				shipFuelType = FuelType.LNG;
			}
			ship.getFuelTank().setTmpType(shipFuelType);
		}
		if (!super.fuelTypeList.contains(shipFuelType)) return false;
		if (super.loadingType != ship.getCargoType()) return false;
		return true;
	}

	@Override
	public void bunkering() {
		if(this.tmpFuelType == FuelType.LNG && this.bunkeringFlag == false) {
			if(super.port.numOfBunkers > 0) {
				super.port.numOfBunkers -= 1;
				this.bunkeringFlag = true;
			}else {
				return;
			}
		}
		this.berthingShip.setAmountOfFuel(this.berthingShip.getAmountOfFuel() + tmpBunkeringCapacity);
		double bunkeringAmount = 0;
		if ( this.berthingShip.getMaximumCargoAmount() < (this.berthingShip.getAmountOfCargo() + tmpBunkeringCapacity)){
			bunkeringAmount = this.berthingShip.getMaximumCargoAmount() - this.berthingShip.getAmountOfCargo();
		}else{
			bunkeringAmount = tmpBunkeringCapacity;
		}
		double fuelPrice = 0;
		if(this.berthingShip.time > 8760) {
			super.port.getOperator().addBunkeringCash(fuelPrice * bunkeringAmount / 1.2 * 0.2);
			this.berthingShip.addCashFlow(-1 * fuelPrice * bunkeringAmount);
		}
		if (berthingShip.getAmountOfFuel() == berthingShip.getFuelTank().getCapacity()){
			this.berthingShip.getSchedule().setBunkering(false);
			if(this.tmpFuelType == FuelType.LNG && super.port.numOfBunkers >= 0) {
				this.bunkeringFlag = false;
				this.port.numOfBunkers++;
			}
		}
	}
}


