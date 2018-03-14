package model;


/**
 * OilPrice model which is expressed by binomial model
 * @author Shinnosuke Wanaka
 * 
 */
public class BinomialPrice extends FuelPrice{
	private double upFactor;
	private double downFactor;
	private double upProbability;
	private double prePrice;
	
	private int counter = 0;

	@Override
	public void timeNext() {
		this.time++;
		counter++;
		if (counter > 24){
			prePrice = this.price;
			double n = Math.random();
			if (n >= upProbability){
				setPrice(this.price * upFactor);
			}else{
				setPrice(this.price * downFactor);
			}
			counter = 0;
		}
		
	}


	public BinomialPrice(double initialPrice, double upFactor, double downFactor, double upProbability){
		super();
		this.prePrice = initialPrice;
		this.price = initialPrice;
		this.upFactor = upFactor;
		this.downFactor = downFactor;
		this.upProbability = upProbability;
		
	}
	
	public double getPrePrice(){
		return prePrice;
	}

	@Override
	public double getPrice(Port port) {
		return this.price;
	}
	@Override
	public double getPastPrice(int past) {
		if(past == -1) return this.prePrice;
		else return 0;
	}

}