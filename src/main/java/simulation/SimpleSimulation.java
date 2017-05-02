package simulation;

import java.util.List;

import model.Fleet;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Ship;

public class SimpleSimulation extends Simulation{

	public SimpleSimulation(int endTime) {
		super(endTime);
	}

	@Override
	public void save(int now) {
		//TO-DO
		List<Ship> ships = Fleet.getShips();
		for(Ship ship : ships){
			ship.getAmountOfCargo();
			ship.getAmountOfFuel();
			ship.getRemainingDistance();
			ship.getCargoType();
			ship.getShipStatus().name();
		}
		//this.portNetwork.save(now);
		double freight = Market.getFreight().get(0).getPrice();
		double fuelPrice = Market.getFuels().get(0).getPrice();
		System.out.println("Now: " + now + " Saved!");
		
	}

}
