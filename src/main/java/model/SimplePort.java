package model;

import java.util.HashMap;
import java.util.List;

import model.Status.CargoType;
import model.Status.FuelType;
import model.Status.ShipStatus;

public class SimplePort extends Port {
	public SimplePort(String name){
		super(name);
		PortOperator operator = new SimplePortOperator(name);
		this.operator = operator;
	}


	@Override
	public void addPortFacility(List<FuelType> fuelTypeList, CargoType loadingType, List<Double> bunkeringCapacityList, double loadingCapacity, double berthingFee, boolean bunkeringFlag){
		PortFacility facility = new SimplePortFacitliy(fuelTypeList, loadingType, bunkeringCapacityList, loadingCapacity, berthingFee, bunkeringFlag);
		facility.setPort(this);
		super.facilities.add(facility);
	}


	@Override
	public PortFacility checkBerthing(Ship ship) {
		for (PortFacility facility : this.facilities){
			if(facility.match(ship)){
				return facility;
			}
		}
		return null;
	}

	@Override
public int getTimeForReady(Ship ship) {
	// TODO Auto-generated method stub
	return 0;
}


@Override
public void departure(Ship ship) {
	for(PortFacility facility : facilities){
		if(facility.berthingShip == null){
			continue;
		}
		if(facility.berthingShip.equals(ship)){
			facility.berthingShip = null;
			facility.occupiedFlag = 0;
		}
	}

}

}
