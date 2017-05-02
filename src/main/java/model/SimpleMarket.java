package model;



/*
 * Simple market model
 * Assumption: 1 fuel, 1 cargo.
 * @author Shinnosuke Wanaka
 */
//public class SimpleMarket extends Market {
//	public SimpleMarket(double upForStandard, double downForStandard, double pforStandard, double upForRate, double downForRate, double pforRate, double initialStandard, double initialRate){
//		Freight freight = new Freight(upForStandard,downForStandard, pforStandard, upForRate, downForRate, pforRate,initialStandard,initialRate);
//		this.freights.add(freight);
//	}
//	@Override
//	public void timeNext(int now){
//		super.timeNext(now);
//		for (Freight freight : this.freights){
//			freight.timeNext(now);
//		}
//	}
//	@Override
//	public boolean checkDemand() {
//		if (super.demands.size() == 0) return false;
//		else return true;
//	}
//
//	@Override
//	public void addContract() {
//		
//		for(Demand demand : super.demands){
//			//1.まずはdemandをほどく
//			int startTime = demand.getStartTime();
//			int endTime = demand.getEndTime();
//			LoadingType cargoType = demand.getCargoType();
//			double amount = demand.getAmountOfCargo();
//			String departure = demand.getDeparture();
//			String destination = demand.getDestination();
//			Port dep = PortNetwork.getPort(departure);
//			Port des = PortNetwork.getPort(destination);
//			//2. 間に合う船がいるかどうか調べる
//			List<Ship> ships = fleet.getShips();
//			double tmpFuel = 0;
//			Ship tmpShip = null;
//			List<Ship> assignedShip = new ArrayList<Ship>();
//			while(amount >0){
//				for (Ship ship : ships){
//					if (cargoType == ship.getCargoType()){
//						if (canTransport(ship,startTime,endTime,dep,des,portNetwork)){
//							//単位貨物あたりの燃料はいくらか?
//							double estimateFuelCost = estimateFuelCost(ship,startTime,endTime,dep,des,portNetwork,amount);
//							if (tmpFuel > estimateFuelCost){
//								tmpFuel = estimateFuelCost;
//								tmpShip = ship;
//							}
//						}
//					}
//					
//				}
//				assignedShip.add(tmpShip);
//				//スケジュールを設定する
//				double done = setScheduleToShip(tmpShip,startTime,endTime,dep,des,amount,portNetwork);
//				
//				//Reset
//				amount = amount -done;
//				tmpShip = null;
//			}
//			//3. 運賃を決める
//			double freight = decideFreight(ships,assignedShip,super.fuels,demand);
//			//4. Contractを作成する
//			makeContract(assignedShip,freight);
//			
//		}
//
//	}
//	private boolean canTransport(Ship ship, int startTime, int endTime, Port departure, Port destination, PortNetwork network){
//		//TO-DO actual behavior
//		//対象の船の最終予定時間と場所を取得
//		Port previousDestination = ship.getLastSchedule().getDestination();
//		int previousTime = ship.getLastSchedule().getEndTime();
//		//そこから出発地点まで来て、目的地まで最大船速で行く時間を計算(Loading、Bunkeringの時間を忘れない)
//		double preDistance = network.getDistance(previousDestination, departure);
//		double distance = network.getDistance(departure, destination);
//		int sumTime = ship.getTime(preDistance) + ship.getTime(distance) + departure.getTimeForReady(ship);
//		//その時間とendTimeとを比較する
//		if (endTime < previousTime + sumTime) return false;
//		else return true;
//	}
//	private double estimateFuelCost(Ship ship, int startTime, int endTime, Port departure, Port destination, PortNetwork network, double amount){
//		//TO-DO actual behavior
//		//対象の船の最終予定時間と場所を取得
//		Port previousDestination = ship.getLastSchedule().getDestination();
//		//そこから出発地点まで来て、目的地まで一定船速で行くまでの燃料を計算
//		double fuelamount = ship.estimateFuelAmount(previousDestination,departure,network) + ship.estimateFuelAmount(departure,previousDestination,network);
//		//今の燃料費にもとづいて総燃料費を計算
//		double sumFuelPrice = this.fuels.get(0).price * fuelamount;
//		//総燃料費を貨物量で割る(amountと最大貨物量の比較を忘れない)
//		if (amount >= ship.getAmountOfCargo()){
//			return sumFuelPrice / ship.getAmountOfCargo();
//		}else{
//			return sumFuelPrice / amount;
//		}
//	}
//	private double setScheduleToShip(Ship ship, int startTime, int endTime, Port departure, Port destination, double amount, PortNetwork network){
//		//対象の船の速度とポート間の距離を取得
//		double distance = PortNetwork.getDistance(departure, destination);
//		int time = ship.getTime(distance);
//		//endTimeからstartTimeを計算
//		int start = endTime - time;
//		//startTime, endTime, departure, destination,amountを設定
//		double done = 0;
//		if (amount >= ship.getAmountOfCargo()){
//			done = ship.getAmountOfCargo();
//		}else{
//			done =  amount;
//		}
//		ship.addSchedule(start, endTime, departure,destination,done);
//		return done;
//	}
//	private double decideFreight(List<Ship> ships, List<Ship> assignedShip,List<FuelPrice> fuels, Demand demand){
//		//TO-DO
//		//運賃率の計算(これはfuelPriceと相関)
//		//基準運賃の計算(これはfuelPriceと無相関)
//		//掛け算をして返す
//		return 0;
//	}
//	private void makeContract(List<Ship> ships, double freight){
//		for (Ship ship: ships){
//			ship.addFreightToSchedule(freight);
//		}
//	}
//	
//	public class Freight{
//		private double standardFreight;
//		private double freightRate;
//		
//		private double upforStandard;
//		private double downforStandard;
//		private double pforStandard;
//		
//		private double upforRate;
//		private double downforRate;
//		private double pforRate;
//		
//		public Freight(double upforStandard,double downforStandard,double pforStandard,double upforRate, double downforRate, double pforRate, double initialStandard, double initialRate){
//			this.upforStandard = upforStandard;
//			this.downforStandard = downforStandard;
//			this.pforStandard = pforStandard;
//			this.upforRate = upforRate;
//			this.downforRate = downforRate;
//			this.pforRate = pforRate;
//			
//			this.standardFreight = initialStandard;
//			this.freightRate = initialRate;
//		}
//		public void timeNext(int now){
//			double n = Math.random();
//			if (n >= pforStandard){
//				this.standardFreight = this.standardFreight * upforStandard;
//			}else{
//				this.standardFreight = this.standardFreight * downforStandard;
//			}
//			
//			double m = Math.random();
//			
//				double oilprice = fuels.get(0).price;
//				double preOilprice = fuels.get(0).getPastPrice(-1);
//			if (m >= pforRate){
//				this.freightRate = this.freightRate * upforRate;
//				this.freightRate = this.freightRate - (oilprice - preOilprice)/preOilprice * this.freightRate;
//			}else{
//				this.freightRate = this.freightRate * downforRate;
//				this.freightRate = this.freightRate - (oilprice - preOilprice)/preOilprice * this.freightRate;
//			}
//		}
//		public double getPrice(){
//			return this.standardFreight * this.freightRate;
//		}
//	}
//	
//
//}
