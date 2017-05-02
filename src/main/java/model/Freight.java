package model;

public class Freight{
	private double standardFreight;
	private double freightRate;
	
	private double upforStandard;
	private double downforStandard;
	private double pforStandard;
	
	private double upforRate;
	private double downforRate;
	private double pforRate;
	
	public Freight(double upforStandard,double downforStandard,double pforStandard,double upforRate, double downforRate, double pforRate, double initialStandard, double initialRate){
		this.upforStandard = upforStandard;
		this.downforStandard = downforStandard;
		this.pforStandard = pforStandard;
		this.upforRate = upforRate;
		this.downforRate = downforRate;
		this.pforRate = pforRate;
		
		this.standardFreight = initialStandard;
		this.freightRate = initialRate;
	}
	public void timeNext(int now){
		double n = Math.random();
		if (n >= pforStandard){
			this.standardFreight = this.standardFreight * upforStandard;
		}else{
			this.standardFreight = this.standardFreight * downforStandard;
		}
		
		double m = Math.random();
		
			double oilprice = Market.fuels.get(0).price;
			double preOilprice = Market.fuels.get(0).getPastPrice(-1);
		if (m >= pforRate){
			this.freightRate = this.freightRate * upforRate;
			this.freightRate = this.freightRate - (oilprice - preOilprice)/preOilprice * this.freightRate;
		}else{
			this.freightRate = this.freightRate * downforRate;
			this.freightRate = this.freightRate - (oilprice - preOilprice)/preOilprice * this.freightRate;
		}
	}
	public double getPrice(){
		return this.standardFreight * this.freightRate;
	}
}