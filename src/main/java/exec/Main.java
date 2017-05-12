package exec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Demand;
import model.DependedFuel;
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
import simulation.SimpleSimulation;
import simulation.Simulation;
import util.CSVReader;

public class Main {

	public static void main(String[] args){

		int endTime = Integer.parseInt(args[0]);
		String outputDir = args[1];
		loadInitialPorts("./data/port_config.csv");
		//loadMarketInfo("./../data/market_config.csv");
		loadMarketInfoFromCSV("./data/freight_config.csv","./data/fuelprice_config.csv","./data/demand_config.csv");
		//loadInitialFleet("../../data/ship_config.csv");
		loadInitialFleetFromCSV("./data/fleet_config.csv");

		Simulation simulation = new SimpleSimulation(endTime,outputDir);
		System.out.println("Simulation Start");
		simulation.execute();
		System.out.println("Simulation End");

	}

//	private static void loadInitialFleet(String filePath){
//		//List<String[]> data =CSVReader.forGeneral(filePath);
//		double speed = 28;
//		CargoType cargoType = CargoType.HFO;
//		double cargoAmount = 300000;
//		double foc = 1.24;
//		double fuelCapacity = 5000;
//		FuelType fuelType = FuelType.OIL;
//		Port initialPort = PortNetwork.getPort("Japan");
//		double cost = 0;
//		
//		Ship ship = new SimpleShip(speed, cargoType, cargoAmount, foc, fuelCapacity, fuelType, initialPort, cost);
//		ShipOperator operator = new SimpleShipOperator("NYK");
//		ship.setOwner(operator);
//		Fleet.add(ship);
//	}

	private static void loadInitialFleetFromCSV(String filePath){
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
				double cost = Double.parseDouble(data.get(i+3)[7]);
				// Make instance of SimpleShip class using above input data
				Ship ship = new SimpleShip(speed, cargoType, cargoAmount, foc, fuelCapacity, fuelType, initialPort, cost);
				ship.setName(name);
				ship.setOwner(operator);
				Fleet.add(ship);
				shipCount ++;
			}
		}
		System.out.println(shipCount);
	}

	private static void loadInitialPorts(String configFilePath){
		List<String[]> data = CSVReader.forGeneral(configFilePath);
		List<Port> ports = new ArrayList<Port>();
		double[][] routeMatrix = null;
		int portCount = 0;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "PortName")){
				String name = data.get(i+1)[0];
				Port port = new SimplePort(name);
				int kindsOfFacility = Integer.parseInt(data.get(i+1)[1]);
				for(int j = 0; j < kindsOfFacility; j++){
					String fuelType = data.get(i+3+3*j)[0];
					String loadingType = data.get(i+3+3*j)[1];
					String bunkeringCapacity = data.get(i+3+3*j)[2];
					String loadingCapacity = data.get(i+3+3*j)[3];
					String berthingFee = data.get(i+3+3*j)[4];
					int numOfPortFacilities = Integer.parseInt(data.get(i+4+3*j)[0]);
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("FuelType", fuelType);
					param.put("LoadingType", loadingType);
					param.put("BunkeringCapacity", bunkeringCapacity);
					param.put("LoadingCapacity", loadingCapacity);
					param.put("BerthingFee", berthingFee);
					port.addPortFacilities(param,numOfPortFacilities);
				}
				portCount ++;
				ports.add(port);
				System.out.println(portCount);
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
		PortNetwork.setPortSettings(ports,routeMatrix);
	}

	private static void loadMarketInfoFromCSV(String freightFilePath, String fuelpriceFilePath, String demandFilePath){
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
					case HFO:
					case LNG:
						double initialPrice = Double.parseDouble(data.get(i+3)[0]);
						double upFactor = Double.parseDouble(data.get(i+3)[1]);
						double downFactor = Double.parseDouble(data.get(i+3)[2]);
						double probability = Double.parseDouble(data.get(i+3)[3]);
						fuelprice = new BinomialPrice(initialPrice,upFactor,downFactor,probability);
						fuelprice.setFuelType(fuelType);
						break;
					case LSFO:
						coeff = Double.parseDouble(data.get(i+3)[0]);
						DependedFuel fuel = new DependedFuel();
						fuel.setCoeff(coeff);
						fuel.setFuelType(fuelType);
						fuel.setDependedFuel("HFO");
						fuelprice = (FuelPrice) fuel;
						break;
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
				System.out.println(demandCount);
			}
		}
	}
}
