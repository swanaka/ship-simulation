package model;



import model.Status.FuelType;
import model.Status.LoadingType;
import model.Status.ShipStatus;


/*
 * Simple ship model.
 * Assumption: 1 speed.
 * @author Shinnosuke Wanaka
 */
public class SimpleShip extends Ship {

	
	
	public SimpleShip(double speed, LoadingType cargoType, double cargoAmount, double foc, double fuelCapacity, FuelType fuelType){
		super();
		this.speed = speed;
		this.fuelTank = new HFOTank();
		this.fuelTank.setCapacity(fuelCapacity);
		this.fuelTank.setFuelType(fuelType);
		this.cargoHold = new VLCCCargo();
		this.cargoHold.setCapacity(cargoAmount);
		this.cargoHold.setCargoType(cargoType);
		this.engine = new SimpleEngine(foc);
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
		double foc = super.engine.calcFOC(speed);

		// 4. Update remainng distance, fuel, gas emission
		double actualDis = speed;
		super.remainingDistance -= actualDis;
		super.amountOfFuel -= foc;
		super.emssionedGas += calcGasEmission(foc);
		
		//5. Update status
		if (remainingDistance < 0){
			this.status = ShipStatus.WAIT;
			this.schedule.get(0).getDestination().addWaitingShips(this);
		}

	}
	
	@Override
	public void appropriateRevenue() {
		Contract contract = super.schedule.get(0);
		double revenue = contract.getIncome() + contract.getPenalty(super.time);
		super.owner.addCashFlow(revenue);
		
	}
	

//	private double calcActualDistance(double distance){
//		return distance;
//	}
//
//	private double calcPlannedDistance(int now, int plannedTime, double distance){
//		return distance / (plannedTime - now);
//	}

	private double calcGasEmission(double foc){
		return foc;
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

	public class OContract extends Contract{
		
		
		private double penaltyRate;

		private OContract(int startTime, int endTime, Port departure, Port destination){
			super.setStartTime(startTime);
			super.setEndTime(endTime);
			super.setDeparture(departure);
			super.setDestination(destination);

		}

		@Override
		public double getIncome() {
			return freightRate * super.getCargoAmount();
		}

		@Override
		public double getPenalty(int time) {
			if (time > super.getEndTime()){
				return penaltyRate * (super.getEndTime() - time);
			}
			else return 0;
		}
		
	}
	
	public class HFOTank extends FuelTank{}
	public class VLCCCargo extends CargoHold{}

	@Override
	public void addSchedule(int startTime, int endTime, Port departure, Port destination, double amount) {
		Contract contract = new OContract(startTime,endTime,departure,destination);
		contract.setCargoAmount(amount);
		
	}

	@Override
	public void addFreightToSchedule(double freight) {
		this.schedule.get(this.schedule.size() - 1).setFreightRate(freight);
		
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
