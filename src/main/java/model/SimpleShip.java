package model;



import java.util.ArrayList;

import model.Status.CargoType;
import model.Status.FuelType;


/*
 * Simple ship model.
 * Assumption: 1 speed.
 * @author Shinnosuke Wanaka
 */
public class SimpleShip extends Ship {
	public SimpleShip(double speed, CargoType cargoType, double cargoAmount, double foc, double fuelCapacity, FuelType fuelType, Port initialPort, double operatingCost, boolean scrubber, boolean gasDiesel){
		super();
		this.schedule = new ArrayList<Schedule>();
		this.speed = speed;
		this.fuelTank = new HFOTank();
		this.fuelTank.setCapacity(fuelCapacity);
		this.fuelTank.setFuelType(fuelType);
		if(fuelType == FuelType.HFOLNG) {
			this.fuelTank.setTmpType(FuelType.LNG);
			this.dualfuelFlag = true;
		}
		this.setAmountOfFuel(fuelCapacity);
		this.fuelTank.setSubAmount(fuelCapacity);
		this.cargoHold = new VLCCCargo();
		this.cargoHold.setCapacity(cargoAmount);
		this.cargoHold.setCargoType(cargoType);
		this.engine = new SimpleEngine(foc);
		Schedule initialSchedule = new SimpleSchedule(0,0,initialPort,initialPort);
		this.schedule.add(initialSchedule);
		this.setOperatingCost(operatingCost);
		this.scrubber = scrubber;
		this.gasdieselFlag = gasDiesel;
	}
	
	public void setBunkeringPort(Port port) {
		this.bunkeringPort = port;
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
			this.totalTonKm += this.remainingDistance * this.getAmountOfCargo();
		}else{
			this.totalDistance += actualDis;
			this.totalTonKm += actualDis * this.getSchedule().getCargoAmount();
		}
		this.setRemainingDistance(this.remainingDistance - actualDis);
		if(this.fuelTank.getAmount() < foc * speed){
			this.totalFuel += this.fuelTank.getAmount();
			this.totalFuelPrice += this.fuelTank.getAmount() *  this.fuelPrice;
		}else{
			this.totalFuel += foc * speed;
			this.totalFuelPrice += foc * speed *  this.fuelPrice;
		}
		this.setAmountOfFuel(this.fuelTank.getAmount() - foc * speed);
		calcGasEmission(foc * speed);
		this.acumCost += this.getOperatingCost();
		this.totalCost += this.getOperatingCost();

	}

	@Override
	public void appropriateRevenue() {
		if(this.schedule.size() == 0) return;
		else {
			Schedule currentSchedule = this.schedule.get(0);
			if (currentSchedule.getDestination().equals(this.berthingPort) && currentSchedule.fee != 0){
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
			if(this.gasdieselFlag) {
				noxcoeff = 0.00135;
			}else {
				noxcoeff = 0.0009;
			}
			soxcoeff = 0;
			co2coeff = 2.75;
			
		}else if (this.getFuelType() == FuelType.HFO){
			if(!scrubber) {
				noxcoeff = 0.0045;
				soxcoeff = 0.012;
				co2coeff = 3.1;
			}else {
				noxcoeff = 0.0045;
				soxcoeff = 0;
				co2coeff = 3.1;
			}
		}else if (this.getFuelType() == FuelType.LSFO){
			noxcoeff = 0.0045;
			soxcoeff = 0.0006;
			co2coeff = 3.2;
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
			if(dualfuelFlag) {
				if(getFuelType() == FuelType.LNG) {
					return foc;
				}else {
					return 0.124;
				}
			}else {
				return foc;
			}
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

	public class HFOTank extends FuelTank{
		public HFOTank() {
			this.amount = 0;
			this.subAmount = 0;
		}
	}
	public class VLCCCargo extends CargoHold{}

	@Override
	public void addSchedule(int startTime, int endTime, Port departure, Port destination, double amount) {
		
		Port previousPort = null;
		if (this.getLastSchedule() == null) previousPort = this.berthingPort;
		else previousPort = this.getLastSchedule().getDestination();
		
		if(detourFlag) {
			if(previousPort.equals(departure)) {
				this.getLastSchedule().setLoading(true);
				this.getLastSchedule().setLoadingType(this.getCargoType());
				this.getLastSchedule().setLoadingAmount(amount);
				this.getLastSchedule().setBunkering(true);
				
				Schedule schedule = new SimpleSchedule(startTime,endTime,departure,this.bunkeringPort);
				schedule.setUnLoading(false);
				schedule.setBunkering(true);
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
				
				schedule = new SimpleSchedule(startTime, endTime, this.bunkeringPort, destination);
				schedule.setUnLoading(true);
				schedule.setBunkering(false);
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
				
				
			}else {
				this.getLastSchedule().setBunkering(true);
				
				Schedule schedule = new SimpleSchedule(startTime, endTime, previousPort, this.bunkeringPort);
				schedule.setBunkering(true);
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
				
				schedule = new SimpleSchedule(startTime, endTime, this.bunkeringPort, departure);
				schedule.setBunkering(true);
				schedule.setLoading(true);
				schedule.setLoadingType(this.getCargoType());
				schedule.setLoadingAmount(amount);
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
				
				schedule = new SimpleSchedule(startTime, endTime, departure, this.bunkeringPort);
				schedule.setBunkering(true);
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);

				schedule = new SimpleSchedule(startTime, endTime, this.bunkeringPort, destination);
				schedule.setBunkering(false);
				schedule.setUnLoading(true);
				schedule.setUnloadingAmount(amount);
				schedule.setUnloadingType(getCargoType());
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
			}
		}else {
			if(previousPort.equals(departure)){
				this.getLastSchedule().setLoading(true);
				this.getLastSchedule().setLoadingType(this.getCargoType());
				this.getLastSchedule().setLoadingAmount(amount);;
				this.getLastSchedule().setBunkering(true);

				Schedule schedule = new SimpleSchedule(startTime,endTime,departure,destination);
				schedule.setUnLoading(true);
				schedule.setUnloadingAmount(amount);
				schedule.setUnloadingType(getCargoType());
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
			}else{
				this.getLastSchedule().setBunkering(true);

				// departureをprevious portに変更 KS
				Schedule schedule = new SimpleSchedule(startTime,endTime,previousPort,departure);
				schedule.setLoading(true);
				schedule.setLoadingAmount(amount);
				schedule.setLoadingType(getCargoType());
				schedule.setBunkering(true);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);

				schedule = new SimpleSchedule(startTime,endTime,departure,destination);
				schedule.setUnLoading(true);
				schedule.setUnloadingAmount(amount);
				schedule.setUnloadingType(getCargoType());
				schedule.setFee(-1);
				schedule.setPenalty(-1);
				schedule.setCargoAmount(amount);
				this.schedule.add(schedule);
			}
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
