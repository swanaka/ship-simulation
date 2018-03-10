package util;

public class CAPEXCalculator4CESUN extends CAPEXCalculator{
private String typeOfFuel;
	private String typeOfEngine;
	private String scrubber;
	private String location;
	private String LNGbunkeringMethod;
	
	private double numOfShips = 20;
	private double numOfportfacilities = 10;
	
	private final double cost4HFOShip = 10.9275;
	private final double cost4LSFOShip = 0.1612;
	private final double cost4LNG4stroke = 18.3582;
	private final double cost4LNGdualfuel = 17.484;
	private final double cost4LNGgasdiesel = 13.5501;
	private final double cost4BunkeringShip = 50;
	private final double cost4LNGterminal = 940;
	private final double cost4LNGtruck = 0.5;
	private final double cost4LSFObunkering = 10;
	
	public CAPEXCalculator4CESUN(String typeOfFuel, String typeOfEngine, String scrubber, String location, String LNGbunkeringMethod) {
		this.typeOfFuel = typeOfFuel;
		this.typeOfEngine = typeOfEngine;
		this.scrubber = scrubber;
		this.location = location;
		this.LNGbunkeringMethod = LNGbunkeringMethod;
	}
	
	public double calculate() {
		double totalCost = 0;
		if(scrubber.equals("Yes")) {
			totalCost += cost4HFOShip * numOfShips;
		}
		if (typeOfFuel.equals("HFO")) {
			totalCost += 0;
		}else if(typeOfFuel.equals("LSFO")) {
			totalCost += cost4LSFOShip * numOfShips;
			totalCost += cost4LSFObunkering * numOfportfacilities;
		}else if(typeOfEngine.equals("Spark-ignited Leanburn Gas Engine")) {
			totalCost += cost4LNG4stroke * numOfShips;
		}else if(typeOfEngine.equals("Low-pressure Dual fuel Engine")) {
			totalCost += cost4LNGdualfuel * numOfShips;
		}else if(typeOfEngine.equals("Gas-diesel engine")) {
			totalCost += cost4LNGgasdiesel * numOfShips;
		}
		
		if(LNGbunkeringMethod.equals("Shore to Ship")) {
			totalCost += 2 * cost4LNGterminal;
		}else if(LNGbunkeringMethod.equals("Ship to Ship")) {
			totalCost += numOfportfacilities * cost4BunkeringShip;
		}else if(LNGbunkeringMethod.equals("Truck to Ship")) {
			totalCost += numOfportfacilities * cost4LNGtruck;
		}
		
		
		
		
		return totalCost;
	}
}
