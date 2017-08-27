package model;

import java.util.ArrayList;
import java.util.List;

import model.Status.CargoType;
import model.Status.FuelType;

public class Market {
	protected static List<FuelPrice> fuels;
	protected static List<Demand> demands;
	protected static List<Freight> freights;

	private static Market market = new Market();

	private Market(){
		fuels = new ArrayList<FuelPrice>();
		demands = new ArrayList<Demand>();
		freights  = new ArrayList<Freight>();
	}
	public static void reset(){
		fuels = new ArrayList<FuelPrice>();
		demands = new ArrayList<Demand>();
		freights  = new ArrayList<Freight>();
	}
	public static Market getInstance(){
		return market;
	}

	public static void addFuelPrice(FuelPrice fuelPrice){
		fuels.add(fuelPrice);
	}

	public static void addDemand(Demand demand){
		demands.add(demand);
	}

	public static void addFreight(Freight freight){
		freights.add(freight);
	}

	public static void timeNext(){
		for(FuelPrice fuelPrice : fuels){
			fuelPrice.timeNext();
		}
		for (Demand demand : demands){
			demand.timeNext();
		}
		for (Freight freight : freights){
			freight.timeNext();
		}

	}

	public static double getPrice(FuelType type){
		for(FuelPrice fuel : fuels){
			if(type == fuel.getFuelType()){
				return fuel.getPrice();
			}
		}
		return 0;
	}
	public static List<FuelPrice> getFuels(){
		return fuels;
	}

	public List<Demand> getDemands(){
		return demands;
	}
	public static List<Freight> getFreight(){
		return freights;
	}


	public static boolean checkDemand() {
		for (Demand demand : demands){
			if(demand.isDemand()) return true;
		}
		return true;
	}

	public static void addContract() {

		for(Demand demand : demands){
			if (!demand.isDemand()) continue;
			else{//1.まずはdemandをほどく
				int startTime = demand.getStartTime();
				int endTime = demand.getEndTime();
				CargoType cargoType = demand.getCargoType();
				double amount = demand.getAmountOfCargo();
				String departure = demand.getDeparture();
				String destination = demand.getDestination();
				Port dep = PortNetwork.getPort(departure);
				Port des = PortNetwork.getPort(destination);

				//2. 間に合う船がいるかどうか調べる
				List<Ship> ships = Fleet.getShips();

				List<Ship> assignedShip = new ArrayList<Ship>();
				while(amount > 0){
					double tmpFuel = -1;
					Ship tmpShip = null;

					for (Ship ship : ships){
						if (cargoType == ship.getCargoType()){
							if (canTransport(ship,startTime,endTime,dep,des)){
								//単位貨物あたりの燃料はいくらか?
								double estimateFuelCost = estimateFuelCost(ship,startTime,endTime,dep,des,amount);
								if (tmpFuel == -1 || tmpFuel > estimateFuelCost){
									tmpFuel = estimateFuelCost;
									tmpShip = ship;
								}
							}
						}

					}
					if (tmpShip != null){
						assignedShip.add(tmpShip);
						//スケジュールを設定する
						double done = setScheduleToShip(tmpShip,startTime,endTime,dep,des,amount);

						//Reset
						amount = amount -done;
					}else{
						break;
					}

				}
				//3. 運賃を決める
				double freight = decideFreight(ships,assignedShip,fuels,demand);
				double penalty = 0;
				//4. Contractを作成する
				makeContract(assignedShip,freight,penalty);
				demand.reset();
			}

		}

	}
	private static boolean canTransport(Ship ship, int startTime, int endTime, Port departure, Port destination){
		//TO-DO actual behavior
		//対象の船の最終予定時間と場所を取得
		Port previousDestination = null;
		int previousTime = 0;
		if(ship.getLastSchedule() != null){
			previousDestination = ship.getLastSchedule().getDestination();
			previousTime = ship.getLastSchedule().getEndTime();
		}else{
			previousDestination = ship.getBerthingPort();
			previousTime = ship.time;
		}
		//そこから出発地点まで来て、目的地まで最大船速で行く時間を計算(Loading、Bunkeringの時間を忘れない)
		double preDistance = PortNetwork.getDistance(previousDestination, departure);
		double distance = PortNetwork.getDistance(departure, destination);
		int sumTime = ship.getTime(preDistance) + ship.getTime(distance) + departure.getTimeForReady(ship);
		//その時間とendTimeとを比較する
		if (endTime < previousTime + sumTime) return false;
		else return true;
	}

	private static double estimateFuelCost(Ship ship, int startTime, int endTime, Port departure, Port destination, double amount){
		//TO-DO actual behavior
		//対象の船の最終予定時間と場所を取得
		Port previousDestination = null;
		if(ship.getLastSchedule() == null) previousDestination = ship.getBerthingPort();
		else previousDestination = ship.getLastSchedule().getDestination();
		//そこから出発地点まで来て、目的地まで一定船速で行くまでの燃料を計算
		double fuelamount = ship.estimateFuelAmount(previousDestination,departure) + ship.estimateFuelAmount(departure,destination);
		//今の燃料費にもとづいて総燃料費を計算
		double fuelPrice = 0;
		for(FuelPrice fuel : fuels){
			if (fuel.getFuelType() == ship.getFuelType()){
				fuelPrice = fuel.getPrice();
			}
		}
		double sumFuelPrice = fuelPrice * fuelamount;
		//総燃料費を貨物量で割る(amountと最大貨物量の比較を忘れない)
		if (amount >= ship.getAmountOfCargo()){
			return sumFuelPrice / ship.getMaximumCargoAmount();
		}else{
			return sumFuelPrice / amount;
		}
	}
	private static double setScheduleToShip(Ship ship, int startTime, int endTime, Port departure, Port destination, double amount){
		//一つ前の終了時間を取得
		int previousEndTime = ship.getLastSchedule().getEndTime();
		//対象の船の速度とポート間の距離を取得
		double distance = PortNetwork.getDistance(departure, destination);
		int time = ship.getTime(distance);

		//endTimeからstartTimeを計算
		int start = previousEndTime + 24;
		int end = start + time;
		//startTime, endTime, departure, destination,amountを設定
		double done = 0;
		if (amount >= ship.getMaximumCargoAmount()){
			done = ship.getMaximumCargoAmount();
		}else{
			done =  amount;
		}
		ship.addSchedule(start, end, departure,destination,done);
		return done;
	}
	private static double decideFreight(List<Ship> ships, List<Ship> assignedShip,List<FuelPrice> fuels, Demand demand){
		CargoType cargoType = demand.getCargoType();
		for (Freight freight : freights){
			if (freight.getCargoType() == cargoType){
				return freight.getPrice();
			}
		}
		return -1;
	}
	private static void makeContract(List<Ship> ships, double freight, double penalty){
		for (Ship ship: ships){
			ship.addContractToSchedule(freight,penalty);
		}
	}




}