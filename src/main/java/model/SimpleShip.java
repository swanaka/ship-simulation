package model;



import java.util.ArrayList;

import model.Status.CargoType;
import model.Status.FuelType;
import model.Status.ShipStatus;


/*
 * Simple ship model.
 * Assumption: 1 speed.
 * @author Shinnosuke Wanaka
 */
public class SimpleShip extends Ship {

	
	
	public SimpleShip(double speed, CargoType cargoType, double cargoAmount, double foc, double fuelCapacity, FuelType fuelType, Port initialPort, double operatingCost){
		super();
		this.schedule = new ArrayList<Schedule>();
		this.speed = speed;
		this.fuelTank = new HFOTank();
		this.fuelTank.setCapacity(fuelCapacity);
		this.fuelTank.setFuelType(fuelType);
		this.setAmountOfFuel(fuelCapacity);
		this.cargoHold = new VLCCCargo();
		this.cargoHold.setCapacity(cargoAmount);
		this.cargoHold.setCargoType(cargoType);
		this.engine = new SimpleEngine(foc);
		Schedule initialSchedule = new SimpleSchedule(0,0,initialPort,initialPort);
		this.schedule.add(initialSchedule);
		this.setOperatingCost(operatingCost);
	}

	@Override
	public void transport() {

//		// 1. Get planned distance
//		int plannedTime = super.schedule.get(0).getEndTime();
//		double distance = super.remainingDistance; 
//		double plannedDistance = calcPlannedDistance(now, plannedTime, distance);

		// 2. Calculate ship speed
		double speed = this.speed;

		// 3. Calculate FOC
		double foc = this.engine.calcFOC(speed);

		// 4. Update remainng distance, fuel, gas emission
		double actualDis = speed;
		if(this.remainingDistance < actualDis){
			this.totalDistance += this.remainingDistance;
		}else{
			this.totalDistance += actualDis;
		}
		this.setRemainingDistance(this.remainingDistance - actualDis);
		if(this.amountOfFuel < foc){
			this.totalFuel += this.amountOfFuel;
		}else{
			this.totalFuel += foc;
		}
		this.setAmountOfFuel(this.amountOfFuel - foc);
		calcGasEmission(foc);
		this.acumCost += this.getOperatingCost();

	}
	
	@Override
	public void appropriateRevenue() {
		//TO-DO
		if(this.schedule.size() == 0) return;
		else {
			Schedule currentSchedule = this.schedule.get(0);
			if (currentSchedule.getDestination().equals(this.berthingPort)){
				double revenue = currentSchedule.getIncome();
				super.owner.addCashFlow(revenue);
				this.addCashFlow(revenue);
			}
		}
		
	}
	

//	private double calcActualDistance(double distance){
//		return distance;
//	}
//
//	private double calcPlannedDistance(int now, int plannedTime, double distance){
//		return distance / (plannedTime - now);
//	}

	private void calcGasEmission(double foc){
		double noxcoeff = 0;
		double soxcoeff = 0;
		double co2coeff = 0;
		if(this.getFuelType() == FuelType.LNG){
			noxcoeff = 9;
			soxcoeff = 0;
			
		}else if (this.getFuelType() == FuelType.HFO){
			noxcoeff = 9;
			soxcoeff = 2;
		}else if (this.getFuelType() == FuelType.LSFO){
			noxcoeff = 9;
			soxcoeff = 2;
		}
		this.nox += foc * noxcoeff;
		this.sox += foc * soxcoeff;
		this.co2 += foc * co2coeff;
	}
	
	private class SimpleEngine extends Engine{
		private double foc;
		
		private SimpleEngine(double foc){
			this.foc = foc;
		}
		
		public double calcFOC(double v){
			return foc;
		}
		
	
	}

	public class SimpleSchedule extends Schedule{

		private SimpleSchedule(int startTime, int endTime, Port from, Port to){
			this.setStartTime(startTime);
			this.setEndTime(endTime);
			this.setDeparture(from);
			this.setDestination(to);
			this.setBunkering(false);
			this.setLoading(false);
			this.setUnLoading(false);
			this.penalty = 0;
			this.fee = 0;
		}

		@Override
		public double getIncome() {
			double income = fee * getUnloadingAmount();
			double minus = acumCost;
			if (time > super.getEndTime()){
				 minus = penalty * (super.getEndTime() - time);
			}
			acumCost = 0;
			return income - minus;
		}

		@Override
		public boolean judgeEnd() {
			if(this.isBunkering == false && this.isLoading == false && this.isUnLoading == false) return true;
			return false;
		}
		
	}
	
	public class HFOTank extends FuelTank{}
	public class VLCCCargo extends CargoHold{}

	@Override
	public void addSchedule(int startTime, int endTime, Port departure, Port destination, double amount) {
		Port previousPort = null;
		if (this.getLastSchedule() == null) previousPort = this.berthingPort;
		else previousPort = this.getLastSchedule().getDestination();
		
		if(previousPort.equals(departure)){
			this.getLastSchedule().setLoading(true);
			this.getLastSchedule().setLoadingType(this.getCargoType());
			this.getLastSchedule().setLoadingAmount(amount);;
			this.getLastSchedule().setBunkering(true);
			this.getLastSchedule().setFuelType(this.getFuelType());
			
			Schedule schedule = new SimpleSchedule(startTime,endTime,departure,destination);
			schedule.setUnLoading(true);
			schedule.setUnloadingAmount(amount);
			schedule.setUnloadingType(getCargoType());
			schedule.setFee(-1);
			schedule.setPenalty(-1);
			this.schedule.add(schedule);
		}else{
			this.getLastSchedule().setBunkering(true);
			this.getLastSchedule().setFuelType(this.getFuelType());
			
			Schedule schedule = new SimpleSchedule(startTime,endTime,departure,destination);
			schedule.setLoading(true);
			schedule.setLoadingAmount(amount);
			schedule.setLoadingType(getCargoType());
			schedule.setBunkering(true);
			schedule.setFuelType(getFuelType());
			this.schedule.add(schedule);
			
			schedule = new SimpleSchedule(startTime,endTime,departure,destination);
			schedule.setUnLoading(true);
			schedule.setUnloadingAmount(amount);
			schedule.setUnloadingType(getCargoType());
			schedule.setFee(-1);
			schedule.setPenalty(-1);
			this.schedule.add(schedule);
		}

	}

	@Override
	public void addContractToSchedule(double freight,double penalty) {
		
		for (Schedule sch :this.schedule){
			if(sch.fee == -1) sch.setFee(freight);
			if(sch.penalty == -1) sch.setPenalty(penalty);
		}
		
	}

	@Override
	public int getTime(double distance) {
		int time = (int) Math.ceil(distance / this.speed);
		return time;
	}

	@Override
	public double estimateFuelAmount(Port departure, Port destination) {
		double foc = super.engine.calcFOC(this.speed);
		double distance = PortNetwork.getDistance(departure, destination);
		double time = distance / this.speed;
		return foc * time;
	}

	


}
