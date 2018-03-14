package model;

import java.util.Collections;
import java.util.List;

import model.Status.FuelType;

/**
 * OilPrice model which is expressed by binomial model
 * @author Shinnosuke Wanaka
 * 
 */
public class DiscretePrice extends FuelPrice{
	private List<String> prices;
	
	private int counter = 0;

	@Override
	public void timeNext() {
		this.time++;
		counter++;
		if (counter > 24){
			Collections.shuffle(prices);
			this.price = Double.parseDouble(prices.get(0));
			counter = 0;
		}
		
	}


	public DiscretePrice(List<String> prices, double initialprice){
		super();
		this.prices = prices;
		this.price = initialprice;
		
		
	}
	

	@Override
	public double getPrice(Port port) {
		if(this.fuelType == FuelType.LNG) {
			if(port.getName().equals("Singapore")) {
				return this.price * 0.9;
			}else if(port.getName().equals("Persian Gulf")) {
				return this.price * 0.8;
			}else if(port.getName().equals("Japan")) {
				return this.price * 1.2;
			}
		}
		return this.price;
	}
	@Override
	public double getPastPrice(int past) {
		 return this.price;
	}

}