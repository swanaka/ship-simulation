package model;

import model.Status.FuelType;

public class DependedFuel extends FuelPrice {
	
	private String dependedFuel;
	private double coeff;
	
	
	@Override
	public void timeNext() {
		;

	}

	@Override
	public double getPastPrice(int past) {
		for (FuelPrice fuel : Market.getFuels()){
			if(dependedFuel.equals(fuel.getFuelType().toString())){
				return fuel.getPastPrice(past) * this.coeff;
			}
		}
		return 0;
	}
	@Override
	public double getPrice() {
		for (FuelPrice fuel : Market.getFuels()){
			if(dependedFuel.equals(fuel.getFuelType().toString())){
				return fuel.getPrice()* coeff;
			}
		}
		return 0;
	}
	
	public void setDependedFuel(String fuel){
		this.dependedFuel = fuel;
	}
	
	public void setCoeff(double coeff){
		this.coeff = coeff;
	}
}
