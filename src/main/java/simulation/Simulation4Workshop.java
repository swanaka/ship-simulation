package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Demand;
import model.DiscretePrice;
import model.Fleet;
import model.Freight;
import model.FuelPrice;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Route;
import model.Ship;
import model.ShipOperator;
import model.SimpleDemand;
import model.SimplePort;
import model.SimpleShip;
import model.SimpleShipOperator;
import model.Status.CargoType;
import model.Status.FuelType;
import util.CAPEXCalculator4Workshop;
import util.CSVwriter;
import util.Location;

public class Simulation4Workshop extends Simulation implements SimulationData{
	
	private CAPEXCalculator4Workshop calculator;
	private static String OUTPUT_DIR = "./data/cases/";
	private String casenum;
	

	public Simulation4Workshop(int endTime, InputData data, String casenum) {
		super(endTime);
		this.casenum = casenum;
		Fleet.reset();
		PortNetwork.reset();
		Market.reset();
		
		List<Port> ports = new ArrayList<Port>();
		Port persian = settingForPort("Persian Gulf", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtPersianGulf()), data.getBunkeringMethodAtPersianGulf(), persianLat, persianLon);
		ports.add(persian);
		Port japan = settingForPort("Japan", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtJapan()), data.getBunkeringMethodAtJapan(), japanLat, japanLon);
		ports.add(japan);
		Port singapore = settingForPort("Singapore", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtSingapore()), data.getBunkeringMethodAtSingapore(), singaporeLat, singaporeLon);
		ports.add(singapore);
		List<Route> routes = new ArrayList<Route>();
		Route japan2persiangulf = new Route();
		japan2persiangulf.setOrigin(japan);
		japan2persiangulf.setDestination(persian);
		japan2persiangulf.importFromCSV("input/Japan2PersianGulf.csv");
		routes.add(japan2persiangulf);
		
		Route persiangulf2japan = new Route();
		persiangulf2japan.setOrigin(persian);
		persiangulf2japan.setDestination(japan);
		persiangulf2japan.importFromCSV("input/PersianGulf2Japan.csv");
		routes.add(persiangulf2japan);

		Route persiangulf2singapore = new Route();
		persiangulf2singapore.setOrigin(persian);
		persiangulf2singapore.setDestination(singapore);
		persiangulf2singapore.importFromCSV("input/PersianGulf2Singapore.csv");
		routes.add(persiangulf2singapore);
		
		Route singapore2persiangulf = new Route();
		singapore2persiangulf.setOrigin(singapore);
		singapore2persiangulf.setDestination(persian);
		singapore2persiangulf.importFromCSV("input/Singapore2PersianGulf.csv");
		routes.add(singapore2persiangulf);
		
		Route japan2singapore = new Route();
		japan2singapore.setOrigin(japan);
		japan2singapore.setDestination(singapore);
		japan2singapore.importFromCSV("input/Japan2Singapore.csv");
		routes.add(japan2singapore);
		
		Route singapore2japan = new Route();
		singapore2japan.setOrigin(singapore);
		singapore2japan.setDestination(japan);
		singapore2japan.importFromCSV("input/Singapore2Japan.csv");
		
		routes.add(singapore2japan);
		PortNetwork.setPortSettings(ports,routeMatrix, routes);
		
		settingForMarket();

		ShipOperator operator = new SimpleShipOperator("MOL");
		Port initialPort = PortNetwork.getPort(initialPortName);
		Port bunkeringPort = PortNetwork.getPort("Singapore");
		for(int i = 0; i < Integer.parseInt(data.getNumOfHFO()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focHFO, fuelCapacity, FuelType.HFO, initialPort, operatingCost, true, gasdieselFlag);
			ship.setName("HFO:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i< Integer.parseInt(data.getNumOfLSFO()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focLSFO, fuelCapacity, FuelType.LSFO, initialPort, operatingCost, false, gasdieselFlag);
			ship.setName("LFO:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i<Integer.parseInt(data.getNumOfLNG()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focLNG, fuelCapacity, FuelType.LNG, initialPort, operatingCost, false, gasdieselFlag);
			ship.setName("LNG:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i< Integer.parseInt(data.getNumOfHFOLNG());i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focHFOLNG, fuelCapacity, FuelType.HFOLNG, initialPort, operatingCost, true, gasdieselFlag);
			ship.setName("HFOLNG:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		if(Integer.parseInt(data.getNumOfBunkeringFacilitiesAtSingapore()) > 0) {
			Fleet.setDetour(true);
		}else {
			Fleet.setDetour(false);
		}
		this.calculator = new CAPEXCalculator4Workshop(data.getNumOfHFO(),data.getNumOfLSFO(), data.getNumOfLNG(), data.getNumOfHFOLNG(),data.getNumOfBunkeringFacilitiesAtPersianGulf(), data.getBunkeringMethodAtPersianGulf(),data.getNumOfBunkeringFacilitiesAtJapan(),data.getBunkeringMethodAtJapan(),data.getNumOfBunkeringFacilitiesAtSingapore(),data.getBunkeringMethodAtSingapore());
	}
	@Override
	public void execute(){
		save(-1);
		while(now <= endTime){
			timeNext();
			if(21899 < now && now < 26280) save(now);
			now++;
		}
		save();
	};
	@Override
	public void save(int now) {
		List<String> outputList = new ArrayList<String>();
		List<Ship> ships = Fleet.getShips();
		if(now == -1){
			for(Ship ship : ships){
				outputList.add("");
				outputList.add("Status");
				outputList.add("Fuel Type");
				outputList.add("Cargo Type");
				outputList.add("Amount of Cargo");
				outputList.add("Amount of Fuel");
				outputList.add("Total Fuel Cost");
				outputList.add("Remaining Desitance");
				outputList.add("Berthing Port");
				outputList.add("Lat");
				outputList.add("Lon");
			}
			outputList.add("");
			outputList.add("Freight");
			outputList.add("HFO Price");
			outputList.add("LSFO Price");
			outputList.add("LNG Price");
			CSVwriter.write(OUTPUT_DIR + casenum + ".csv", outputList, false);
		}
		//TO-DO
		else if(now % 4 == 0){
			outputList.add(String.valueOf(now - 21899));
			for(Ship ship : ships){
				outputList.add(ship.getShipStatus().name());
				outputList.add(String.valueOf(ship.getFuelType()));
				outputList.add(String.valueOf(ship.getCargoType()));
				outputList.add(String.valueOf(ship.getAmountOfCargo()));
				outputList.add(String.valueOf(ship.getAmountOfFuel()));
				outputList.add(String.valueOf(ship.getTotalFuelPricise()));
				outputList.add(String.valueOf(ship.getRemainingDistance()));
				if(ship.getBerthingPort()!=null){
					outputList.add(ship.getBerthingPort().getName());
				}else{
					outputList.add("");
				}
				outputList.add(String.valueOf(ship.getLoc().getLat()));
				outputList.add(String.valueOf(ship.getLoc().getLon()));
				outputList.add("");
			}
			//this.portNetwork.save(now);
			double freight = Market.getFreight().get(0).getPrice();
			outputList.add(String.valueOf(freight));
			double lngPrice = 0;
			double hfoPrice = 0;
			double lsfoPrice = 0;
			for(FuelPrice fuel : Market.getFuels()){
				switch(fuel.getFuelType()){
				case HFO:
					hfoPrice = fuel.getPrice();
					break;
				case LNG:
					lngPrice = fuel.getPrice();
					break;
				case LSFO:
					lsfoPrice = fuel.getPrice();
				}
			}
			outputList.add(String.valueOf(hfoPrice));
			outputList.add(String.valueOf(lsfoPrice));
			outputList.add(String.valueOf(lngPrice));
			CSVwriter.write(OUTPUT_DIR + casenum + ".csv", outputList, true);
		}

	}

	@Override
	public void save() {
		List<String> outputList = new ArrayList<String>();
		
		outputList.add("Fuel efficiency");
		outputList.add("Totall Transported Capability");
		outputList.add("Total CO2 Emission rate");
		outputList.add("Total NOx Emission rate");
		outputList.add("Total SOx Emission rate");
		outputList.add("Total Waiting Time rate");
		outputList.add("InitialCost");
		CSVwriter.write(OUTPUT_DIR + casenum+"_overall.csv", outputList, false);
		
		outputList = new ArrayList<String>();
		Map<String, String> result = getResult();
		outputList.add(result.get("fe"));
		outputList.add(result.get("tttk"));
		outputList.add(result.get("co"));
		outputList.add(result.get("no"));
		outputList.add(result.get("so"));
		outputList.add(result.get("wait"));
		outputList.add(result.get("ic"));
 		CSVwriter.write(OUTPUT_DIR + casenum+"_overall.csv", outputList, true);
 		CSVwriter.write(OUTPUT_DIR + "overall.csv", outputList, true);
	}
	
	public Map<String, String> getResult(){
		Map<String, String> result = new HashMap<String, String>();
		List<Ship> ships = Fleet.getShips();
		double TTTM = 0;
		double TECU = 0;
		double totalCo2 = 0;
		double totalNox = 0;
		double totalSox = 0;
		double totalWaitingTime = 0;


		double totalFuelCost = 0;
		for(Ship ship : ships){

			double totalCargo = ship.getTotalCargo();
			double totalDistance = ship.getTotalDistance();
			double totalFuel = ship.getTotalFuelPricise();
			double co2 = ship.getCo2();
			double sox = ship.getSox();
			double nox = ship.getNox();
			double waitingTime = ship.getWaitingTime();

			TTTM += ship.getTotalTonKm();
			if(totalCargo * totalDistance != 0) TECU += totalFuel / (totalCargo * totalDistance);
			totalFuelCost += ship.getTotalFuelPricise();
			totalCo2 += co2;
			totalSox += sox;
			totalNox += nox;
			totalWaitingTime += waitingTime;

		}
		result.put("tttk", String.valueOf(TTTM));
		result.put("fe", String.valueOf(totalFuelCost/TTTM));
		result.put("co", String.valueOf(totalCo2/TTTM));
		result.put("no", String.valueOf(totalNox/TTTM));
		result.put("so", String.valueOf(totalSox/TTTM));
		result.put("wait", (String.valueOf(totalWaitingTime/(this.getEndTime()-8760)/20*100)));
		result.put("ic", String.valueOf(this.calculator.calculate()));
		return result;
	}

	private Port settingForPort(String name, int numOfLNGfacility, String bunkeringMethod, double lat, double lon) {
		Port port = new SimplePort(name);
		Location loc = new Location(lat, lon);
		port.setLoc(loc);
		int numOfHFOfacility = numOfFacilities;
		int numOfLSFOfacility = numOfFacilities;
		boolean bunkeringFlag = true;
		double bunkeringCapacity = bunkeringCapacityOthers;
		double bunkeringCapacityLNG = bunkeringCapacityOthers;
		if(bunkeringMethod.equals("Shore to Ship")) {
			port.setNumOfBunkers(-1);
		}else {
			bunkeringFlag = false;
			port.setNumOfBunkers(numOfFacilities);
			numOfLNGfacility = numOfFacilities;
		}
		if(bunkeringMethod.equals("Truck to Ship")) {
			bunkeringCapacityLNG = bunkeringCapacityTruck;
		}
		if(bunkeringMethod.equals("Ship to Ship")) {
			bunkeringCapacityLNG = bunkeringCapacityShip;
		}
		for(int j = 0; j < numOfFacilities; j++){
			List<FuelType> fuelTypeList = new ArrayList<FuelType>();
			List<Double> bunkeringCapacityList = new ArrayList<Double>();
			if(numOfHFOfacility > 0) {
				fuelTypeList.add(FuelType.HFO);
				bunkeringCapacityList.add(bunkeringCapacity);
				numOfHFOfacility -= 1;
			}
			if(numOfLNGfacility > 0) {
				fuelTypeList.add(FuelType.LNG);
				bunkeringCapacityList.add(bunkeringCapacityLNG);
				numOfLNGfacility -= 1;
			}
			if(numOfLSFOfacility > 0) {
				fuelTypeList.add(FuelType.LSFO);
				bunkeringCapacityList.add(bunkeringCapacity);
				numOfLSFOfacility -= 1;
			}
			CargoType loadingType = cargoType;
			port.addPortFacility(fuelTypeList, loadingType, bunkeringCapacityList, loadingCapacity, berthingFee, bunkeringFlag);
		}
		return port;
	}
	
	private void settingForMarket() {
		// Input freight data from freight data file "freight_config.csv"
		
		Freight freight = new Freight(
				cargoType,upforStandard,downforStandard,pforStandard,upforRate,
				downforRate,pforRate,initialStandard,initialRate);
		Market.addFreight(freight);

		FuelPrice fuelprice = null;
		for (FuelType fuelType : typelist){
			// Input oilprice data from oilprice data file "oilprice_config.csv"
			List<String> prices = new ArrayList<String>();
			switch(fuelType){
				case LNG:
					prices.add(maxLNG);
					prices.add(midLNG);
					prices.add(minLNG);
					fuelprice = new DiscretePrice(prices, initialLNG);
					fuelprice.setFuelType(fuelType);
					break;
				case HFO:
					prices.add(maxHFO);
					prices.add(midHFO);
					prices.add(minHFO);
					fuelprice = new DiscretePrice(prices, initialHFO);
					fuelprice.setFuelType(fuelType);
					break;
				case LSFO:
					prices.add(maxLSFO);
					prices.add(midLSFO);
					prices.add(minLSFO);
					fuelprice = new DiscretePrice(prices, initialLSFO);
					fuelprice.setFuelType(fuelType);
					break;
				default:
					;
					
			}
			Market.addFuelPrice(fuelprice);
		}
		Demand demand = new SimpleDemand(cargoType,limit,amount,duration,departure,destination);

		Market.addDemand(demand);
	}
}
