package exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Demand;
import model.DependedFuel;
import model.DiscretePrice;
import model.Fleet;
import model.Freight;
import model.FuelPrice;
import model.Market;
import model.BinomialPrice;
import model.Port;
import model.PortNetwork;
import model.Ship;
import model.ShipOperator;
import model.SimpleDemand;
import model.SimplePort;
import model.SimpleShip;
import model.SimpleShipOperator;
import model.Status.CargoType;
import model.Status.FuelType;
import model.TimeSeriesPrice;
import simulation.SimpleSimulation;
import simulation.Simulation;
import util.CSVReader;

public class Main {

	public static void main(String[] args){

		int endTime = Integer.parseInt(args[0]);
		String outputDir = null;
		loadInitialPorts("./data/port_config.csv");
		loadMarketInfoFromCSV("./data/freight_config.csv","./data/fuelprice_config.csv","./data/demand_config.csv");
		loadInitialFleetFromCSV("./data/fleet_config.csv");

		Simulation simulation = new SimpleSimulation(endTime,outputDir);
		System.out.println("Simulation Start");
		simulation.execute();
		System.out.println("Simulation End");

	}

	public static void loadInitialFleetFromCSV(String filePath){
		List<String[]> data =CSVReader.forGeneral(filePath);
		int shipCount = 0;
		ShipOperator operator = new SimpleShipOperator("NYK");
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals("shipName")){
				String name = data.get(i+1)[0];
				double speed = Double.parseDouble(data.get(i+3)[0]);
				CargoType cargoType = CargoType.valueOf(data.get(i+3)[1]);
				double cargoAmount = Double.parseDouble(data.get(i+3)[2]);
				double foc = Double.parseDouble(data.get(i+3)[3]);
				double fuelCapacity = Double.parseDouble(data.get(i+3)[4]);
				FuelType fuelType = FuelType.valueOf(data.get(i+3)[5]);
				Port initialPort = PortNetwork.getPort(data.get(i+3)[6]);
				Port bunkeringPort = PortNetwork.getPort("Singapore");
				double cost = Double.parseDouble(data.get(i+3)[7]);
				boolean scrubber;
				if (data.get(i+3)[8].equals("Yes")) {
					scrubber = true;
				}else {
					scrubber = false;
				}
				boolean gasdiesel;
				if (data.get(i+3)[9].equals("Yes")) {
					gasdiesel = true;
				}else {
					gasdiesel = false;
				}
				// Make instance of SimpleShip class using above input data
				Ship ship = new SimpleShip(speed, cargoType, cargoAmount, foc, fuelCapacity, fuelType, initialPort, cost,scrubber,gasdiesel);
				ship.setName(name);
				ship.setOwner(operator);
				ship.setBunkeringPort(bunkeringPort);
				Fleet.add(ship);
				shipCount ++;
			}
		}
		System.out.println("Total " + shipCount + " ships loaded.");
	}

	public static void loadInitialPorts(String configFilePath){
		List<String[]> data = CSVReader.forGeneral(configFilePath);
		List<Port> ports = new ArrayList<Port>();
		double[][] routeMatrix = null;
		int portCount = 0;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "PortName")){
				String name = data.get(i+1)[0];
				Port port = new SimplePort(name);
				boolean bunkeringFlag = true;
				int numOfFacility = Integer.parseInt(data.get(i+1)[1]);
				int numOfHFOfacility = Integer.parseInt(data.get(i+4)[0]);
				int numOfLNGfacility = Integer.parseInt(data.get(i+7)[0]);
				if(numOfLNGfacility > 0) {
					if(data.get(i+6)[5].equals("TRUE")) {
						bunkeringFlag = false;
						port.setNumOfBunkers(numOfLNGfacility);
						numOfLNGfacility = numOfHFOfacility;
					}
				}
				int numOfLSFOfacility = Integer.parseInt(data.get(i+10)[0]);
				
				for(int j = 0; j < numOfFacility; j++){
					List<FuelType> fuelTypeList = new ArrayList<FuelType>();
					List<Double> bunkeringCapacityList = new ArrayList<Double>();
					if(numOfHFOfacility > 0) {
						fuelTypeList.add(FuelType.HFO);
						bunkeringCapacityList.add(Double.parseDouble(data.get(i+3)[2]));
						numOfHFOfacility -= 1;
					}
					if(numOfLNGfacility > 0) {
						fuelTypeList.add(FuelType.LNG);
						bunkeringCapacityList.add(Double.parseDouble(data.get(i+6)[2]));
						numOfLNGfacility -= 1;
					}
					if(numOfLSFOfacility > 0) {
						fuelTypeList.add(FuelType.LSFO);
						bunkeringCapacityList.add(Double.parseDouble(data.get(i+9)[2]));
						numOfLSFOfacility -= 1;
					}
					CargoType loadingType = CargoType.valueOf(data.get(i+3)[1]);
					double loadingCapacity = Double.parseDouble(data.get(i+3+3*j)[3]);
					double berthingFee = Double.parseDouble(data.get(i+3+3*j)[4]);
					port.addPortFacility(fuelTypeList, loadingType, bunkeringCapacityList, loadingCapacity, berthingFee, bunkeringFlag);
				}
				portCount ++;
				ports.add(port);
			}
			if (data.get(i)[0].equals("RouteMatrix")){
				routeMatrix = new double[portCount][portCount];
				for (int j=1;j<1+portCount;j++){
					for(int k=1;k<1+portCount;k++){
						routeMatrix[j-1][k-1] = Double.parseDouble(data.get(i+1+j)[k]);
					}
				}
			}
		}
		System.out.println("Total " + portCount + " ports are loaded.");
		PortNetwork.setPortSettings(ports,routeMatrix);
	}

	public static void loadMarketInfoFromCSV(String freightFilePath, String fuelpriceFilePath, String demandFilePath){
		List<String[]> data = CSVReader.forGeneral(freightFilePath);
		Freight freight = null;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "freightName")){
				// Input freight data from freight data file "freight_config.csv"
				String name = data.get(i+1)[0];
				CargoType cargoType = CargoType.valueOf(data.get(i+3)[0]);
				double upforStandard = Double.parseDouble(data.get(i+3)[1]);
				double downforStandard = Double.parseDouble(data.get(i+3)[2]);
				double pforStandard = Double.parseDouble(data.get(i+3)[3]);
				double upforRate = Double.parseDouble(data.get(i+3)[4]);
				double downforRate = Double.parseDouble(data.get(i+3)[5]);
				double pforRate = Double.parseDouble(data.get(i+3)[6]);
				double initialStandard = Double.parseDouble(data.get(i+3)[7]);
				double initialRate = Double.parseDouble(data.get(i+3)[8]);
				freight = new Freight(
						cargoType,upforStandard,downforStandard,pforStandard,upforRate,
						downforRate,pforRate,initialStandard,initialRate);
			}
		}
		Market.addFreight(freight);

		data = null;
		double coeff = 1.75;
		data = CSVReader.forGeneral(fuelpriceFilePath);
		FuelPrice fuelprice = null;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "FuelpriceName")){
				// Input oilprice data from oilprice data file "oilprice_config.csv"
				String name = data.get(i+1)[0];
				FuelType fuelType = FuelType.valueOf(name);
				switch(fuelType){
					case LNG:
//						List<String> timeprices = Arrays.asList(data.get(i+3));
//						fuelprice = new TimeSeriesPrice(timeprices);
//						fuelprice.setFuelType(fuelType);
//						break;
					case HFO:
					case LSFO:
//						double initialPrice = Double.parseDouble(data.get(i+3)[0]);
//						double upFactor = Double.parseDouble(data.get(i+3)[1]);
//						double downFactor = Double.parseDouble(data.get(i+3)[2]);
//						double probability = Double.parseDouble(data.get(i+3)[3]);
//						fuelprice = new BinomialPrice(initialPrice,upFactor,downFactor,probability);
						double initialPrice = Double.parseDouble(data.get(i+3)[0]);
						String maxPrice = data.get(i+3)[1];
						String midPrice = data.get(i+3)[2];
						String minPrice = data.get(i+3)[3];
						List<String> prices = new ArrayList<String>();
						prices.add(maxPrice);
						prices.add(midPrice);
						prices.add(minPrice);
						fuelprice = new DiscretePrice(prices, initialPrice);
						fuelprice.setFuelType(fuelType);
						break;
//					case LSFO:
//						coeff = Double.parseDouble(data.get(i+3)[0]);
//						DependedFuel fuel = new DependedFuel();
//						fuel.setCoeff(coeff);
//						fuel.setFuelType(fuelType);
//						fuel.setDependedFuel("HFO");
//						fuelprice = (FuelPrice) fuel;
//						break;
					default:
						;
						
				}
				Market.addFuelPrice(fuelprice);
			}
		}
		

		data = CSVReader.forGeneral(demandFilePath);
		Demand demand = null;
		int demandCount = 0;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "demandName")){
				// Input demand data from demand data file "demand_config.csv"
				String name = data.get(i+1)[0];

				CargoType cargoType = CargoType.valueOf(data.get(i+3)[0]);
				int limit = Integer.parseInt(data.get(i+3)[1]);
				double amount = Double.parseDouble(data.get(i+3)[2]);
				int duration = Integer.parseInt(data.get(i+3)[3]);
				String departure = data.get(i+3)[4];
				String destination = data.get(i+3)[5];
				demand = new SimpleDemand(cargoType,limit,amount,duration,departure,destination);

				Market.addDemand(demand);
				int numOfDemands = Integer.parseInt(data.get(i+4)[0]);
				// Move plus 4 lines in Reading CSV files. (Read next data)
				i = i + 4;
				demandCount ++;
			}
		}
	}
}
