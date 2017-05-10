package exec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Demand;
import model.Fleet;
import model.Market;
import model.Freight;
import model.FuelPrice;
import model.OilPrice;
import model.Port;
import model.SimpleShip;
import model.SimpleShipOperator;
import model.Status.FuelType;
import model.Status.CargoType;
import model.PortNetwork;
import model.Ship;
import model.ShipOperator;
import model.SimpleDemand;


import model.SimplePort;
import simulation.SimpleSimulation;
import simulation.Simulation;
import util.CSVReader;

public class Main {

	public static void main(String[] args){
		
		int endTime = Integer.parseInt(args[0]);
		loadInitialPorts("./data/port_config.csv");
		loadMarketInfo("../../data/market_config.csv");
		loadInitialFleet("../../data/ship_config.csv");
		
		Simulation simulation = new SimpleSimulation(endTime);
		System.out.println("Simulation Start");
		simulation.execute();
		System.out.println("Simulation End");
		
	}
	
	private static void loadInitialFleet(String filePath){
		//List<String[]> data =CSVReader.forGeneral(filePath);
		double speed = 28;
		CargoType cargoType = CargoType.HFO;
		double cargoAmount = 300000;
		double foc = 1.24;
		double fuelCapacity = 5000;
		FuelType fuelType = FuelType.OIL;
		Port initialPort = PortNetwork.getPort("Japan");
		double cost = 0;
		
		Ship ship = new SimpleShip(speed, cargoType, cargoAmount, foc, fuelCapacity, fuelType, initialPort, cost);
		ShipOperator operator = new SimpleShipOperator("NYK");
		ship.setOwner(operator);
		Fleet.add(ship);
	}
	
	private static void loadInitialPorts(String configFilePath){
		List<String[]> data = CSVReader.forGeneral(configFilePath);
		List<Port> ports = new ArrayList<Port>();
		double[][] routeMatrix = null;
		int portCount = 0;
		for (int i=0;i<data.size();i++){
			if (data.get(i)[0].equals( "PortName")){
				String name = data.get(i+1)[0];
				String fuelType = data.get(i+3)[0];
				String loadingType = data.get(i+3)[1];
				String bunkeringCapacity = data.get(i+3)[2];
				String loadingCapacity = data.get(i+3)[3];
				String berthingFee = data.get(i+3)[4];
				int numOfPorts = Integer.parseInt(data.get(i+4)[0]);
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("FuelType", fuelType);
				param.put("LoadingType", loadingType);
				param.put("BunkeringCapacity", bunkeringCapacity);
				param.put("LoadingCapacity", loadingCapacity);
				param.put("BerthingFee", berthingFee);
				Port port = new SimplePort(name);
				port.addPortFacilities(param,numOfPorts);
				ports.add(port);
				i = i + 4;
				portCount ++;
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
	
	private static void loadMarketInfo(String filePath){
		CargoType cargoType = CargoType.HFO;
		double upforStandard = 1.037;
		double downforStandard = 0.964;
		double pforStandard = 0.688;
		double upforRate = 1.246;
		double downforRate = 0.89;
		double pforRate = 0.457;
		double initialStandard = 10.82;
		double initialRate = 1.84;
		Freight freight = new Freight(cargoType,upforStandard,downforStandard,pforStandard,upforRate,downforRate,pforRate,initialStandard,initialRate);
		//List<String[]> data = CSVReader.forGeneral(filePath);
		double initialPrice = 152;
		double upFactor = 1.124;
		double downFactor = 0.888;
		double probability = 0.539;
		FuelPrice oilprice = new OilPrice(initialPrice,upFactor,downFactor,probability);

		int limit = 300;
		double amount = 600000;
		int duration = 800;
		String departure = "Japan";
		String destination = "Los Angels";
		Demand demand = new SimpleDemand(cargoType,limit,amount,duration,departure,destination);

		Market.addDemand(demand);
		Market.addFuelPrice(oilprice);
		Market.addFreight(freight);
	}
	
	
}
