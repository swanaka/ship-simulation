package model;

import java.util.Collections;
import java.util.List;

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
	public double getPastPrice(int past) {
		 return this.price;
	}

}