package model;

import java.util.ArrayList;
import java.util.List;

import model.Status.FuelType;

public class Fleet {
	
	private static List<Ship> ships;
	private static Fleet fleet = new Fleet();
	
	private Fleet(){
		ships = new ArrayList<Ship>();
	}
	public static Fleet getInstance(){
		return fleet;
	}
	public static void timeNext(){
		for (Ship ship : ships){
			ship.timeNext();
		}
	}
	public static void add(Ship ship){
		ships.add(ship);
	}
	
	public static List<Ship> getShips(){
		return ships;
	}
	public static void reset(){
		ships = new ArrayList<Ship>();
	}
	public static void setDetour(boolean flag) {
		for(Ship ship : ships) {
			if(ship.getFuelType() == FuelType.LNG || ship.getFuelType() == FuelType.HFOLNG) {
				ship.setDetourFlag(flag);
			}
		}
	}

}
