package simulation;

import model.Status.CargoType;
import model.Status.FuelType;

public interface SimulationData {
	
	final int endTime = 87600;
	
	final double speed = 28;
	final CargoType cargoType = CargoType.OIL;
	final double cargoAmount = 300000;
	final double fuelCapacity = 2000;
	final double focHFO = 0.124;
	final double focLSFO = 0.11;
	final double focLNG = 0.10;
	final double focHFOLNG = 0.10;
	final String initialPortName = "Japan";
	final double operatingCost = 15000;
	final boolean gasdieselFlag = false;
	
	
	final int numOfFacilities = 3;
	final double loadingCapacity = 3125;
	final double bunkeringCapacityOthers = 104;
	final double bunkeringCapacityShip = 94;
	final double bunkeringCapacityTruck = 21;
	final double berthingFee = 520;
	final double[][] routeMatrix = {{0, 12349.136, 6952.408},{12349.136, 0, 5981.96}, {6952.408, 5981.96, 0}};
	
	
	final double upforStandard = 1;
	double downforStandard = 1;
	double pforStandard = 0.522;
	double upforRate = 1;
	double downforRate = 1;
	double pforRate = 0.495;
	double initialStandard = 40;
	double initialRate = 0.683;

	final FuelType[] typelist = {FuelType.HFO, FuelType.LSFO, FuelType.LNG};
	final double initialLNG = 606;
	final String minLNG = "606";
	final String midLNG = "760";
	final String maxLNG = "925";
	
	final double initialHFO = 620;
	final String minHFO = "620";
	final String midHFO = "620";
	final String maxHFO = "620";
	
	final double initialLSFO = 1106;
	final String minLSFO = "1106";
	final String midLSFO = "1106";
	final String maxLSFO = "1106";
	
	final int limit = 100;
	final double amount = 10000000;
	final int duration = 500;
	final String departure = "Persian Gulf";
	final String destination = "Japan";
	
	final double persianLat = 26.884;
	final double persianLon = 50.082;
	final double japanLat = 35.596;
	final double japanLon = 139.78;
	final double singaporeLat = 1.1814;
	final double singaporeLon = 103.85;
}
