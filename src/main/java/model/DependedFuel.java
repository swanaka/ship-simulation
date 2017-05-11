package model;

public class DependedFuel extends FuelPrice {
	
	private FuelPrice dependedFuel;
	private double coeff;
	
	
	public void setDependFuel(FuelPrice fuel){
		this.dependedFuel = fuel;
	}
	
	@Override
	public void timeNext() {
		;

	}

	@Override
	public double getPastPrice(int past) {
		return this.dependedFuel.getPastPrice(past) * coeff;
	}
	@Override
	public double getPrice() {
		return this.dependedFuel.price * coeff;
	}
	
	public void setDependedFuel(FuelPrice fuel){
		this.dependedFuel = fuel;
	}
	
	public void setCoeff(double coeff){
		this.coeff = coeff;
	}
}
